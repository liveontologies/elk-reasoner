/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.semanticweb.elk.MutableBoolean;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.visitors.NoOpElkAxiomVisitor;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassEntity;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.ConclusionBaseFactory;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SaturationConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.inferences.AbstractClassInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.AbstractObjectPropertyInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.ObjectPropertyInference;
import org.semanticweb.elk.reasoner.stages.ReasonerStateAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A collection of utility methods to test correctness of saturation tracing.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class TracingTestUtils {

	protected static final Logger LOGGER_ = LoggerFactory
			.getLogger(TracingTestUtils.class);

	private static final UntracedConclusionListener UNTRACED_LISTENER = new UntracedConclusionListener() {

		@Override
		public void notifyUntraced(ClassConclusion conclusion) {
			fail(conclusion.getConclusionRoot() + ": " + conclusion
					+ ": conclusion was not traced");
		}

		@Override
		public void notifyUntraced(ObjectPropertyConclusion conclusion) {
			fail("Property conclusion " + conclusion + " was not traced");
		}
	};
	
	private static final SaturationConclusion.Factory FACTORY_ = new ConclusionBaseFactory();

	static ClassConclusion getConclusionToTrace(Context context,
			IndexedClassExpression subsumer) {
		if (context != null) {
			IndexedContextRoot root = context.getRoot();
			ClassConclusion contradiction = FACTORY_.getContradiction(root);
			if (context.containsConclusion(contradiction)) {
				return contradiction;
			}

			return FACTORY_.getComposedSubClassInclusion(root, subsumer);
		}

		throw new IllegalArgumentException("Context may not be null");
	}

	public static void checkTracingCompleteness(ElkClassExpression sub,
			ElkClassExpression sup, Reasoner reasoner) {
		IndexedClassExpression subsumee = ReasonerStateAccessor.transform(
				reasoner, sub);
		ClassConclusion subsumer = getConclusionToTrace(
				ReasonerStateAccessor.getContext(reasoner, subsumee),
				ReasonerStateAccessor.transform(reasoner, sup));
		final AtomicInteger conclusionCount = new AtomicInteger(0);
		TraceState traceState = ReasonerStateAccessor.getTraceState(reasoner);
		ClassInference.Visitor<IndexedContextRoot, Boolean> counter = new AbstractClassInferenceVisitor<IndexedContextRoot, Boolean>() {

			@Override
			protected Boolean defaultTracedVisit(ClassInference conclusion,
					IndexedContextRoot input) {
				conclusionCount.incrementAndGet();
				return true;
			}
		};

		TestTraceUnwinder explorer = new TestTraceUnwinder(traceState,
				UNTRACED_LISTENER);

		explorer.accept(subsumer, counter);
	}

	public static void checkTracingOfInconsistencyCompleteness(Reasoner reasoner) {
		IndexedClassEntity entity = ReasonerStateAccessor
				.getInconsistentEntity(reasoner);

		if (entity == null) {
			throw new IllegalStateException("The ontology is consistent");
		}

		final AtomicInteger conclusionCount = new AtomicInteger(0);
		TraceState traceState = ReasonerStateAccessor.getTraceState(reasoner);
		ClassInference.Visitor<IndexedContextRoot, Boolean> counter = new AbstractClassInferenceVisitor<IndexedContextRoot, Boolean>() {

			@Override
			protected Boolean defaultTracedVisit(ClassInference conclusion,
					IndexedContextRoot input) {
				conclusionCount.incrementAndGet();
				return true;
			}
		};

		TestTraceUnwinder explorer = new TestTraceUnwinder(traceState,
				UNTRACED_LISTENER);

		ClassConclusion contradiction = FACTORY_.getContradiction(entity);
		explorer.accept(contradiction, counter);
	}

	/*
	 * checking that the number of inferences for the given class subsumption is
	 * as expected
	 */
	public static void checkNumberOfInferences(ElkClassExpression sub,
			ElkClassExpression sup, Reasoner reasoner, int expected) {
		final IndexedClassExpression subsumee = ReasonerStateAccessor
				.transform(reasoner, sub);
		ClassConclusion conclusion = getConclusionToTrace(
				ReasonerStateAccessor.getContext(reasoner, subsumee),
				ReasonerStateAccessor.transform(reasoner, sup));
		int actual = 0;
		for (ClassInference ignore : ReasonerStateAccessor.getTraceState(
				reasoner).getClassInferences(conclusion)) {
			actual++;
		}
		assertEquals(expected, actual);
	}

	/*
	 * checking that the number of inferences for the given property subsumption
	 * is as expected
	 */
	public static void checkNumberOfInferences(ElkObjectProperty sub,
			ElkObjectProperty sup, Reasoner reasoner, int expected) {
		final IndexedObjectProperty subsumee = ReasonerStateAccessor.transform(
				reasoner, sub);
		final IndexedObjectProperty subsumer = ReasonerStateAccessor.transform(
				reasoner, sup);
		ObjectPropertyConclusion conclusion = FACTORY_.getSubPropertyChain(
				subsumee, subsumer);
		int actual = 0;
		for (ObjectPropertyInference ignore : ReasonerStateAccessor
				.getTraceState(reasoner)
				.getObjectPropertyInferences(conclusion)) {
			actual++;
		}
		assertEquals(expected, actual);
	}

	/*
	 * Either the class inference visitor or the property inference visitor
	 * should return true for some used inference in the trace.
	 * 
	 * TODO Generalize this to look for multiple inferences in the trace.
	 */
	public static void checkConditionOverUsedInferences(
			ElkClassExpression sub,
			ElkClassExpression sup,
			Reasoner reasoner,
			final ClassInference.Visitor<IndexedContextRoot, Boolean> classInferenceVisitor,
			final ObjectPropertyInference.Visitor<Void, Boolean> propertyInferenceVisitor) {
		final IndexedClassExpression subsumee = ReasonerStateAccessor
				.transform(reasoner, sub);
		ClassConclusion conclusion = getConclusionToTrace(
				ReasonerStateAccessor.getContext(reasoner, subsumee),
				ReasonerStateAccessor.transform(reasoner, sup));
		final MutableBoolean classInferenceCondition = new MutableBoolean(false);
		final MutableBoolean propertyInferenceCondition = new MutableBoolean(
				false);
		TraceState traceState = ReasonerStateAccessor.getTraceState(reasoner);

		new TestTraceUnwinder(traceState, UNTRACED_LISTENER)
				.accept(conclusion,
						new AbstractClassInferenceVisitor<IndexedContextRoot, Boolean>() {

							@Override
							protected Boolean defaultTracedVisit(
									ClassInference inference,
									IndexedContextRoot input) {
								classInferenceCondition.or(inference.accept(
										classInferenceVisitor, input));

								return true;
							}

						},
						new AbstractObjectPropertyInferenceVisitor<Void, Boolean>() {

							@Override
							protected Boolean defaultTracedVisit(
									ObjectPropertyInference inference,
									Void input) {
								propertyInferenceCondition.or(inference.accept(
										propertyInferenceVisitor, input));

								return true;
							}

						});

		assertTrue("The condition didn't succeed on any used class inference",
				classInferenceCondition.get());
		assertTrue(
				"The condition didn't succeed on any used property inference",
				propertyInferenceCondition.get());
	}

	/*
	 */
	public static Set<ElkAxiom> getSideConditions(ElkClassExpression sub,
			ElkClassExpression sup, Reasoner reasoner) {
		final Set<ElkAxiom> sideConditions = new HashSet<ElkAxiom>();
		final IndexedClassExpression subsumee = ReasonerStateAccessor
				.transform(reasoner, sub);
		ClassConclusion conclusion = getConclusionToTrace(
				ReasonerStateAccessor.getContext(reasoner, subsumee),
				ReasonerStateAccessor.transform(reasoner, sup));
		ClassInference.Visitor<Void, ?> sideConditionVisitor = SideConditions
				.getClassSideConditionVisitor(new NoOpElkAxiomVisitor<Void>() {

					@Override
					protected Void defaultLogicalVisit(ElkAxiom axiom) {
						sideConditions.add(axiom);
						return null;
					}

				});

		for (ClassInference inference : ReasonerStateAccessor.getTraceState(
				reasoner).getClassInferences(conclusion)) {
			inference.accept(sideConditionVisitor, null);
		}

		return sideConditions;
	}

	public static void visitClassInferences(ElkClassExpression sub,
			ElkClassExpression sup, Reasoner reasoner,
			ClassInference.Visitor<IndexedContextRoot, Boolean> visitor)
			throws ElkException {
		final IndexedClassExpression subsumee = ReasonerStateAccessor
				.transform(reasoner, sub);
		ClassConclusion conclusion = getConclusionToTrace(
				ReasonerStateAccessor.getContext(reasoner, subsumee),
				ReasonerStateAccessor.transform(reasoner, sup));
		TestTraceUnwinder traceUnwinder = new TestTraceUnwinder(
				ReasonerStateAccessor.getTraceState(reasoner),
				UNTRACED_LISTENER);

		reasoner.explainSubsumption(sub, sup);
		traceUnwinder.accept(conclusion, visitor);
	}

	public static void visitInferences(
			ElkClassExpression sub,
			ElkClassExpression sup,
			Reasoner reasoner,
			final ClassInference.Visitor<IndexedContextRoot, Boolean> classInferenceVisitor,
			final ObjectPropertyInference.Visitor<?, Boolean> propertyInferenceVisitor)
			throws ElkException {
		final IndexedClassExpression subsumee = ReasonerStateAccessor
				.transform(reasoner, sub);
		ClassConclusion conclusion = getConclusionToTrace(
				ReasonerStateAccessor.getContext(reasoner, subsumee),
				ReasonerStateAccessor.transform(reasoner, sup));
		TestTraceUnwinder traceUnwinder = new TestTraceUnwinder(
				ReasonerStateAccessor.getTraceState(reasoner),
				UNTRACED_LISTENER);

		reasoner.explainSubsumption(sub, sup);
		traceUnwinder.accept(conclusion, classInferenceVisitor,
				propertyInferenceVisitor);
	}

	public static void visitInferencesForInconsistency(
			Reasoner reasoner,
			final ClassInference.Visitor<IndexedContextRoot, Boolean> classInferenceVisitor,
			final ObjectPropertyInference.Visitor<?, Boolean> propertyInferenceVisitor)
			throws ElkException {
		IndexedClassEntity entity = ReasonerStateAccessor
				.getInconsistentEntity(reasoner);

		if (entity == null) {
			throw new IllegalStateException("The ontology is consistent");
		}

		TestTraceUnwinder traceUnwinder = new TestTraceUnwinder(
				ReasonerStateAccessor.getTraceState(reasoner),
				UNTRACED_LISTENER);

		reasoner.explainInconsistency();
		traceUnwinder.accept(FACTORY_.getContradiction(entity),
				classInferenceVisitor, propertyInferenceVisitor);
	}

	// //////////////////////////////////////////////////////////////////////
	// some dummy utility visitors
	// //////////////////////////////////////////////////////////////////////
	static final AbstractClassInferenceVisitor<IndexedContextRoot, Boolean> DUMMY_CLASS_INFERENCE_CHECKER = new AbstractClassInferenceVisitor<IndexedContextRoot, Boolean>() {

		@Override
		protected Boolean defaultTracedVisit(ClassInference conclusion,
				IndexedContextRoot input) {
			return true;
		}

	};

	static final AbstractObjectPropertyInferenceVisitor<Void, Boolean> DUMMY_PROPERTY_INFERENCE_CHECKER = new AbstractObjectPropertyInferenceVisitor<Void, Boolean>() {

		@Override
		protected Boolean defaultTracedVisit(ObjectPropertyInference inference,
				Void input) {
			return true;
		}
	};

}
