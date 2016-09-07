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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.semanticweb.elk.MutableBoolean;
import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.visitors.DummyElkAxiomVisitor;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.indexing.model.IndexedAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.DerivedClassConclusionDummyVisitor;
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

	public static void checkTracingCompleteness(ElkClassExpression subClass,
			ElkClassExpression superClass, final Reasoner reasoner)
			throws ElkException {
		final List<ClassConclusion> conclusions = new ArrayList<ClassConclusion>();
		reasoner.visitDerivedConclusionsForSubsumption(subClass, superClass,
				new DerivedClassConclusionDummyVisitor() {

					@Override
					protected boolean defaultVisit(ClassConclusion conclusion) {
						conclusions.add(conclusion);
						return true;
					}

				});

		if (conclusions.isEmpty()) {			
			fail(subClass + " ⊑ " + superClass + " not provable!");
		}
		reasoner.explainConclusions(conclusions);
		final AtomicInteger conclusionCount = new AtomicInteger(0);
		TraceState traceState = ReasonerStateAccessor.getTraceState(reasoner);
		TracingInference.Visitor<Boolean> counter = new DummyInferenceChecker() {

			@Override
			protected Boolean defaultVisit(TracingInference inference) {
				conclusionCount.incrementAndGet();
				return true;
			}
		};

		TestTraceUnwinder explorer = new TestTraceUnwinder(traceState,
				UNTRACED_LISTENER);
		for (ClassConclusion conclusion: conclusions) {
			explorer.accept(conclusion, counter);				
		}
	}

	public static void checkTracingCompleteness(ClassConclusion conclusion,
			Reasoner reasoner) {
		final AtomicInteger conclusionCount = new AtomicInteger(0);
		TraceState traceState = ReasonerStateAccessor.getTraceState(reasoner);
		TracingInference.Visitor<Boolean> counter = new DummyInferenceChecker() {

			@Override
			protected Boolean defaultVisit(TracingInference inference) {
				conclusionCount.incrementAndGet();
				return true;
			}
		};

		TestTraceUnwinder explorer = new TestTraceUnwinder(traceState,
				UNTRACED_LISTENER);

		explorer.accept(conclusion, counter);
	}

	/*
	 * checking that the number of inferences for the given class subsumption is
	 * as expected
	 */
	public static void checkNumberOfInferences(ClassConclusion conclusion,
			Reasoner reasoner, int expected) {
		int actual = 0;
		for (@SuppressWarnings("unused")
		TracingInference ignore : ReasonerStateAccessor.getTraceState(reasoner)
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
		for (TracingInference ignore : ReasonerStateAccessor
				.getTraceState(reasoner).getInferences(conclusion)) {
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
			final TracingInference.Visitor<Boolean> inferenceVisitor) {
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
					protected Boolean defaultVisit(TracingInference inference) {
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
		TracingInference.Visitor<?> sideConditionVisitor = new TracingInferencePremiseVisitor<Void>(
				new DummyConclusionVisitor<Void>() {
					@Override
					protected Void defaultVisit(IndexedAxiom newConclusion) {
						sideConditions.add(newConclusion.getOriginalAxiom());
						return null;
					}
				}, new DummyElkAxiomVisitor<Void>());

		for (TracingInference inference : ReasonerStateAccessor
				.getTraceState(reasoner).getInferences(conclusion)) {
			inference.accept(sideConditionVisitor);
		}

		return sideConditions;
	}

	public static String print(TracingInferenceSet inferences, Conclusion conclusion) {
		StringBuilder builder = new StringBuilder();		
		print(inferences, conclusion, builder, new HashSet<Conclusion>(), 0);
		return builder.toString();
	}

	private static void printIndent(StringBuilder builder, int depth) {
		for (int i = 0; i < depth; i++) {
			builder.append("   ");
		}
	}
	
	private static void print(final TracingInferenceSet inferences,
			Conclusion conclusion, final StringBuilder builder,
			final Set<Conclusion> done, final int depth) {
		
		printIndent(builder, depth);

		builder.append(conclusion);

		if (done.add(conclusion)) {
			builder.append('\n');
		} else {
			builder.append("*\n");
			return;
		}

		for (TracingInference inf : inferences.getInferences(conclusion)) {
			for (int i = 0; i < depth + 1; i++) {
				builder.append("   ");
			}

			builder.append(inf.getClass().getSimpleName()).append('\n');

			inf.accept(new TracingInferencePremiseVisitor<Void>(
					new ConclusionBaseFactory(),
					new DummyConclusionVisitor<Void>() {
						@Override
						protected Void defaultVisit(Conclusion premise) {
							print(inferences, premise, builder, done,
									depth + 2);
							return null;
						}
					}, new DummyElkAxiomVisitor<Void>() {
						@Override
						protected Void defaultVisit(ElkAxiom axiom) {
							printIndent(builder, depth + 2);
							builder.append(": ");
							builder.append(axiom);
							builder.append('\n');
							return null;
						}
					}));
		}
	}
	
	// //////////////////////////////////////////////////////////////////////
	// some dummy utility visitors
	// //////////////////////////////////////////////////////////////////////
	static class DummyInferenceChecker
			extends TracingInferenceDummyVisitor<Boolean> {

		@Override
		protected Boolean defaultVisit(TracingInference inference) {
			return true;
		}

	}

	static final TracingInference.Visitor<Boolean> DUMMY_INFERENCE_CHECKER = new DummyInferenceChecker();

}
