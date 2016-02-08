/**
 * 
 */
package org.semanticweb.elk.reasoner.tracing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.semanticweb.elk.MutableBoolean;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.visitors.DummyElkAxiomVisitor;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.indexing.model.IndexedAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassEntity;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.SaturationConclusionBaseFactory;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SaturationConclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
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
		public void notifyUntraced(Conclusion conclusion) {
			fail(conclusion + ": conclusion was not traced");
		}

	};

	private static final SaturationConclusion.Factory FACTORY_ = new SaturationConclusionBaseFactory();

	static ClassConclusion getConclusionToTrace(Context context,
			IndexedClassExpression subsumer) {
		if (context != null) {
			IndexedContextRoot root = context.getRoot();
			ClassConclusion contradiction = FACTORY_.getContradiction(root);
			if (context.containsConclusion(contradiction)) {
				return contradiction;
			}

			return FACTORY_.getSubClassInclusionComposed(root, subsumer);
		}

		throw new IllegalArgumentException("Context may not be null");
	}

	public static void checkTracingCompleteness(ElkClassExpression sub,
			ElkClassExpression sup, Reasoner reasoner) {
		IndexedClassExpression subsumee = ReasonerStateAccessor
				.transform(reasoner, sub);
		ClassConclusion subsumer = getConclusionToTrace(
				ReasonerStateAccessor.getContext(reasoner, subsumee),
				ReasonerStateAccessor.transform(reasoner, sup));
		final AtomicInteger conclusionCount = new AtomicInteger(0);
		TraceState traceState = ReasonerStateAccessor.getTraceState(reasoner);
		Inference.Visitor<Boolean> counter = new DummyInferenceChecker() {

			@Override
			protected Boolean defaultVisit(Inference inference) {
				conclusionCount.incrementAndGet();
				return true;
			}
		};

		TestTraceUnwinder explorer = new TestTraceUnwinder(traceState,
				UNTRACED_LISTENER);

		explorer.accept(subsumer, counter);
	}

	public static void checkTracingOfInconsistencyCompleteness(
			Reasoner reasoner) {
		IndexedClassEntity entity = ReasonerStateAccessor
				.getInconsistentEntity(reasoner);

		if (entity == null) {
			throw new IllegalStateException("The ontology is consistent");
		}

		final AtomicInteger conclusionCount = new AtomicInteger(0);
		TraceState traceState = ReasonerStateAccessor.getTraceState(reasoner);
		Inference.Visitor<Boolean> counter = new DummyInferenceChecker() {

			@Override
			protected Boolean defaultVisit(Inference inference) {
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
		for (Inference ignore : ReasonerStateAccessor.getTraceState(reasoner)
				.getInferences(conclusion)) {
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
		final IndexedObjectProperty subsumee = ReasonerStateAccessor
				.transform(reasoner, sub);
		final IndexedObjectProperty subsumer = ReasonerStateAccessor
				.transform(reasoner, sup);
		ObjectPropertyConclusion conclusion = FACTORY_
				.getSubPropertyChain(subsumee, subsumer);
		int actual = 0;
		for (Inference ignore : ReasonerStateAccessor.getTraceState(reasoner)
				.getInferences(conclusion)) {
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
	public static void checkConditionOverUsedInferences(ElkClassExpression sub,
			ElkClassExpression sup, Reasoner reasoner,
			final Inference.Visitor<Boolean> inferenceVisitor) {
		final IndexedClassExpression subsumee = ReasonerStateAccessor
				.transform(reasoner, sub);
		SaturationConclusion conclusion = getConclusionToTrace(
				ReasonerStateAccessor.getContext(reasoner, subsumee),
				ReasonerStateAccessor.transform(reasoner, sup));
		final MutableBoolean inferenceCondition = new MutableBoolean(false);
		TraceState traceState = ReasonerStateAccessor.getTraceState(reasoner);

		new TestTraceUnwinder(traceState, UNTRACED_LISTENER).accept(conclusion,
				new DummyInferenceChecker() {

					@Override
					protected Boolean defaultVisit(Inference inference) {
						inferenceCondition
								.or(inference.accept(inferenceVisitor));

						return true;
					}

				});

		assertTrue("The condition didn't succeed on any used inference",
				inferenceCondition.get());
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
		Inference.Visitor<?> sideConditionVisitor = new InferencePremiseVisitor<Void>(
				new DummyConclusionVisitor<Void>() {
					@Override
					protected Void defaultVisit(IndexedAxiom newConclusion) {
						sideConditions.add(newConclusion.getOriginalAxiom());
						return null;
					}
				},
				new DummyElkAxiomVisitor<Void>());

		for (Inference inference : ReasonerStateAccessor.getTraceState(reasoner)
				.getInferences(conclusion)) {
			inference.accept(sideConditionVisitor);
		}

		return sideConditions;
	}

	public static void visitInferences(ElkClassExpression sub,
			ElkClassExpression sup, Reasoner reasoner,
			final Inference.Visitor<Boolean> inferenceVisitor)
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
		traceUnwinder.accept(conclusion, inferenceVisitor);
	}

	public static void visitInferencesForInconsistency(Reasoner reasoner,
			final Inference.Visitor<Boolean> inferenceVisitor)
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
				inferenceVisitor);
	}

	// //////////////////////////////////////////////////////////////////////
	// some dummy utility visitors
	// //////////////////////////////////////////////////////////////////////
	static class DummyInferenceChecker extends DummyInferenceVisitor<Boolean> {

		@Override
		protected Boolean defaultVisit(Inference inference) {
			return true;
		}

	}

	static final Inference.Visitor<Boolean> DUMMY_INFERENCE_CHECKER = new DummyInferenceChecker();

}
