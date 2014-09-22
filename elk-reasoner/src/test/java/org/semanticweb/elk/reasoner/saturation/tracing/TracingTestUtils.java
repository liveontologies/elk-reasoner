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
import org.semanticweb.elk.MutableInteger;
import org.semanticweb.elk.owl.AbstractElkAxiomVisitor;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ContradictionImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.DecomposedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.AbstractConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionEqualityChecker;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.DummyConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ObjectPropertyConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.tracing.LocalTracingSaturationState.TracedContext;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ObjectPropertyInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.SubObjectProperty;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.AbstractClassInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.AbstractObjectPropertyInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ClassInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.GetInferenceTarget;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ObjectPropertyInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.PremiseVisitor;
import org.semanticweb.elk.reasoner.stages.ReasonerStateAccessor;
import org.semanticweb.elk.util.collections.HashListMultimap;
import org.semanticweb.elk.util.collections.Multimap;
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
		public void notifyUntraced(Conclusion conclusion,
				IndexedClassExpression contextRoot) {
			fail("Conclusion " + conclusion + " stored in " + contextRoot
					+ " was not traced");
		}

		@Override
		public void notifyUntraced(ObjectPropertyConclusion conclusion) {
			fail("Property conclusion " + conclusion + " was not traced");
		}
	};
	
	static Conclusion getConclusionToTrace(Context context, IndexedClassExpression subsumer) {
		if (context != null) {
			if (context.containsConclusion(ContradictionImpl.getInstance())) {
				return ContradictionImpl.getInstance();
			}
			
			return new DecomposedSubsumerImpl<IndexedClassExpression>(subsumer);
		}
		
		throw new IllegalArgumentException("Context may not be null");
	}

	public static int checkTracingCompleteness(ElkClassExpression sub,
			ElkClassExpression sup, Reasoner reasoner) {
		IndexedClassExpression subsumee = ReasonerStateAccessor.transform(
				reasoner, sub);
		Conclusion subsumer = getConclusionToTrace(ReasonerStateAccessor.getContext(reasoner, subsumee), 
				ReasonerStateAccessor.transform(reasoner, sup));
		final AtomicInteger conclusionCount = new AtomicInteger(0);
		TraceState traceState = ReasonerStateAccessor.getTraceState(reasoner);
		ConclusionVisitor<IndexedClassExpression, Boolean> counter = new AbstractConclusionVisitor<IndexedClassExpression, Boolean>() {

			@Override
			protected Boolean defaultVisit(Conclusion conclusion,
					IndexedClassExpression root) {
				conclusionCount.incrementAndGet();

				return true;
			}
		};

		TestTraceUnwinder explorer = new TestTraceUnwinder(traceState
				.getTraceStore().getReader(), UNTRACED_LISTENER);

		explorer.accept(subsumee, subsumer, counter);

		return conclusionCount.get();
	}

	public static void checkTracingMinimality(ElkClassExpression sub,
			ElkClassExpression sup, Reasoner reasoner) {
		IndexedClassExpression subsumee = ReasonerStateAccessor.transform(
				reasoner, sub);
		Conclusion subsumer = getConclusionToTrace(ReasonerStateAccessor.getContext(reasoner, subsumee), 
				ReasonerStateAccessor.transform(reasoner, sup));
		TracedContextsCollector collector = new TracedContextsCollector();
		TraceState traceState = ReasonerStateAccessor.getTraceState(reasoner);

		new TestTraceUnwinder(traceState.getTraceStore().getReader(),
				UNTRACED_LISTENER).accept(subsumee, subsumer, collector);

		for (Context traced : traceState.getTracedContexts()) {
			IndexedClassExpression root = traced.getRoot();

			assertTrue(root + " has been traced for no good reason", collector
					.getTracedRoots().contains(traced.getRoot()));
		}
	}

	/*
	 * checking that the number of inferences for the given class subsumption is as expected
	 */
	public static void checkNumberOfInferences(ElkClassExpression sub,
			ElkClassExpression sup, Reasoner reasoner, int expected) {
		final IndexedClassExpression subsumee = ReasonerStateAccessor
				.transform(reasoner, sub);
		Conclusion conclusion = getConclusionToTrace(ReasonerStateAccessor.getContext(reasoner, subsumee), 
				ReasonerStateAccessor.transform(reasoner, sup));
		final AtomicInteger inferenceCount = new AtomicInteger(0);
		ClassInferenceVisitor<IndexedClassExpression, Boolean> counter = new AbstractClassInferenceVisitor<IndexedClassExpression, Boolean>() {

			@Override
			protected Boolean defaultTracedVisit(ClassInference inference,
					IndexedClassExpression root) {

				LOGGER_.trace("{}: traced inference {}", subsumee, inference);

				inferenceCount.incrementAndGet();

				return true;
			}

		};

		ReasonerStateAccessor.getTraceState(reasoner).getTraceStore()
				.getReader().accept(subsumee, conclusion, counter);

		assertEquals(expected, inferenceCount.get());
	}
	
	/*
	 * checking that the number of inferences for the given property subsumption is as expected
	 */
	public static void checkNumberOfInferences(ElkObjectProperty sub,
			ElkObjectProperty sup, Reasoner reasoner, int expected) {
		final IndexedObjectProperty subsumee = ReasonerStateAccessor
				.transform(reasoner, sub);
		final IndexedObjectProperty subsumer = ReasonerStateAccessor
				.transform(reasoner, sup);
		ObjectPropertyConclusion conclusion = new SubObjectProperty(subsumee, subsumer);
		final AtomicInteger inferenceCount = new AtomicInteger(0);
		ObjectPropertyInferenceVisitor<Void, Boolean> counter = new AbstractObjectPropertyInferenceVisitor<Void, Boolean>() {

			@Override
			protected Boolean defaultTracedVisit(
					ObjectPropertyInference inference, Void input) {
				LOGGER_.trace("{}: traced inference {}", subsumee, inference);

				inferenceCount.incrementAndGet();
				return null;
			}

		};

		ReasonerStateAccessor.getTraceState(reasoner).getTraceStore()
				.getReader().accept(conclusion, counter);

		assertEquals(expected, inferenceCount.get());
	}
	
	/*
	 * Either the class inference visitor or the property inference visitor should return true for some used inference in the trace.
	 * 
	 * TODO Generalize this to look for multiple inferences in the trace.
	 */
	public static void checkConditionOverUsedInferences(
			ElkClassExpression sub,
			ElkClassExpression sup,
			Reasoner reasoner,
			final ClassInferenceVisitor<IndexedClassExpression, Boolean> classInferenceVisitor,
			final ObjectPropertyInferenceVisitor<Void, Boolean> propertyInferenceVisitor) {
		final IndexedClassExpression subsumee = ReasonerStateAccessor
				.transform(reasoner, sub);
		Conclusion conclusion = getConclusionToTrace(
				ReasonerStateAccessor.getContext(reasoner, subsumee),
				ReasonerStateAccessor.transform(reasoner, sup));
		final MutableBoolean classInferenceCondition = new MutableBoolean(false);
		final MutableBoolean propertyInferenceCondition = new MutableBoolean(false);
		TraceState traceState = ReasonerStateAccessor.getTraceState(reasoner);

		new TestTraceUnwinder(traceState.getTraceStore().getReader(),
				UNTRACED_LISTENER).accept(subsumee, conclusion,
				new DummyConclusionVisitor<IndexedClassExpression, Void>(), 
				new AbstractClassInferenceVisitor<IndexedClassExpression, Boolean>() {

					@Override
					protected Boolean defaultTracedVisit(ClassInference inference, IndexedClassExpression input) {
						classInferenceCondition.or(inference.acceptTraced(classInferenceVisitor, input));
						
						return classInferenceCondition.get();
					}
					
				},
				ObjectPropertyConclusionVisitor.DUMMY, 
				new AbstractObjectPropertyInferenceVisitor<Void, Boolean>() {

					@Override
					protected Boolean defaultTracedVisit(
							ObjectPropertyInference inference, Void input) {
						propertyInferenceCondition.or(inference.acceptTraced(propertyInferenceVisitor, input));
						
						return propertyInferenceCondition.get();
					}
					
				});		
		

		assertTrue("The condition didn't succeed on any used class inference", classInferenceCondition.get());
		assertTrue("The condition didn't succeed on any used property inference", propertyInferenceCondition.get());
	}
	
	/*
	 */
	public static Set<ElkAxiom> getSideConditions(ElkClassExpression sub,
			ElkClassExpression sup, Reasoner reasoner) {
		final Set<ElkAxiom> sideConditions = new HashSet<ElkAxiom>();
		final IndexedClassExpression subsumee = ReasonerStateAccessor
				.transform(reasoner, sub);
		Conclusion conclusion = getConclusionToTrace(ReasonerStateAccessor.getContext(reasoner, subsumee), 
				ReasonerStateAccessor.transform(reasoner, sup));
		ClassInferenceVisitor<IndexedClassExpression, ?> sideConditionVisitor = SideConditions.getClassSideConditionVisitor(new AbstractElkAxiomVisitor<Void>() {

			@Override
			protected Void defaultLogicalVisit(ElkAxiom axiom) {
				sideConditions.add(axiom);
				return null;
			}
			
		});

		ReasonerStateAccessor.getTraceState(reasoner).getTraceStore()
				.getReader().accept(subsumee, conclusion, sideConditionVisitor);

		return sideConditions;
	}	
	
	/*
	 * checks that the axiom visitor returns true for at least one side condition
	 */
	public static void checkSideConditions(ElkClassExpression sub,
			ElkClassExpression sup, Reasoner reasoner, final ElkAxiomVisitor<Boolean> checker) throws ElkException {
		final IndexedClassExpression subsumee = ReasonerStateAccessor
				.transform(reasoner, sub);
		final MutableBoolean checkPassed = new MutableBoolean(false);
		Conclusion conclusion = getConclusionToTrace(ReasonerStateAccessor.getContext(reasoner, subsumee), 
				ReasonerStateAccessor.transform(reasoner, sup));
		TraceStore.Reader inferenceReader = ReasonerStateAccessor.getTraceState(reasoner).getTraceStore().getReader();
		TestTraceUnwinder traceUnwinder = new TestTraceUnwinder(inferenceReader, UNTRACED_LISTENER);
		ClassInferenceVisitor<IndexedClassExpression, ?> sideConditionVisitor = SideConditions.getClassSideConditionVisitor(new AbstractElkAxiomVisitor<Void>() {

			@Override
			protected Void defaultLogicalVisit(ElkAxiom axiom) {
				checkPassed.or(axiom.accept(checker));
				return null;
			}
			
		});

		reasoner.explainSubsumption(sub, sup);
		traceUnwinder.accept(subsumee, conclusion, new DummyConclusionVisitor<IndexedClassExpression, Void>(), sideConditionVisitor);

		assertTrue("The condition is false for all used side conditions", checkPassed.get());
	}	
	
	public static void visitClassInferences(ElkClassExpression sub,
			ElkClassExpression sup, Reasoner reasoner, ClassInferenceVisitor<IndexedClassExpression, Void> visitor) throws ElkException {
		final IndexedClassExpression subsumee = ReasonerStateAccessor
				.transform(reasoner, sub);
		Conclusion conclusion = getConclusionToTrace(ReasonerStateAccessor.getContext(reasoner, subsumee), 
				ReasonerStateAccessor.transform(reasoner, sup));
		TraceStore.Reader inferenceReader = ReasonerStateAccessor.getTraceState(reasoner).getTraceStore().getReader();
		TestTraceUnwinder traceUnwinder = new TestTraceUnwinder(inferenceReader, UNTRACED_LISTENER);

		reasoner.explainSubsumption(sub, sup);
		traceUnwinder.accept(subsumee, conclusion, new DummyConclusionVisitor<IndexedClassExpression, Void>(), visitor);		
	}	
	
	public static void visitInferences(ElkClassExpression sub,
			ElkClassExpression sup, Reasoner reasoner, 
			ClassInferenceVisitor<IndexedClassExpression, ?> classInferenceVisitor,
			ObjectPropertyInferenceVisitor<?, ?> propertyInferenceVisitor) throws ElkException {
		final IndexedClassExpression subsumee = ReasonerStateAccessor
				.transform(reasoner, sub);
		Conclusion conclusion = getConclusionToTrace(ReasonerStateAccessor.getContext(reasoner, subsumee), 
				ReasonerStateAccessor.transform(reasoner, sup));
		TraceStore.Reader inferenceReader = ReasonerStateAccessor.getTraceState(reasoner).getTraceStore().getReader();
		TestTraceUnwinder traceUnwinder = new TestTraceUnwinder(inferenceReader, UNTRACED_LISTENER);

		reasoner.explainSubsumption(sub, sup);
		traceUnwinder.accept(subsumee, conclusion,
				new DummyConclusionVisitor<IndexedClassExpression, Void>(),
				classInferenceVisitor, ObjectPropertyConclusionVisitor.DUMMY,
				propertyInferenceVisitor);		
	}
	

	public static int checkInferenceAcyclicity(Reasoner reasoner) {
		final AtomicInteger conclusionCount = new AtomicInteger(0);
		final TraceState traceState = ReasonerStateAccessor
				.getTraceState(reasoner);
		final TraceStore.Reader traceReader = traceState.getTraceStore()
				.getReader();
		final SaturationState<TracedContext> tracingState = traceState
				.getSaturationState();
		final MutableInteger counter = new MutableInteger(0);

		for (final IndexedClassExpression root : traceReader.getContextRoots()) {

			traceReader.visitInferences(root,
					new AbstractClassInferenceVisitor<IndexedClassExpression, Void>() {

						@Override
						protected Void defaultTracedVisit(ClassInference inference,
								IndexedClassExpression ignored) {
							counter.increment();

							if (isInferenceCyclic(inference, root, traceReader)) {
								// isInferenceCyclic(inference, root,
								// traceReader);
								fail(inference + " saved in " + root
										+ " is cyclic ");
							}

							return null;
						}

					});
		}

		// now check if some blocked inferences are, in fact, acyclic
		counter.set(0);

		for (Context cxt : tracingState.getContexts()) {
			TracedContext context = (TracedContext) cxt;

			for (Conclusion premise : context.getBlockedInferences().keySet()) {
				for (ClassInference blocked : context.getBlockedInferences().get(
						premise)) {

					IndexedClassExpression targetRoot = blocked.acceptTraced(
							new GetInferenceTarget(), context);

					counter.increment();
					if (!isInferenceCyclic(blocked, targetRoot, traceReader)) {
						isInferenceCyclic(blocked, targetRoot, traceReader);

						fail(blocked + " blocked in " + context
								+ " by the premise " + premise + " is acyclic");
					}
				}
			}
		}

		return conclusionCount.get();
	}

	// the most straightforward (and slow) implementation
	public static boolean isInferenceCyclic(final ClassInference inference,
			final IndexedClassExpression inferenceTargetRoot,
			final TraceStore.Reader traceReader) {
		// first, create a map of premises to their inferences
		final Multimap<Conclusion, ClassInference> premiseInferenceMap = new HashListMultimap<Conclusion, ClassInference>();
		final IndexedClassExpression inferenceContextRoot = inference
				.getInferenceContextRoot(inferenceTargetRoot);

		inference.acceptTraced(new PremiseVisitor<Void, Void>(new AbstractConclusionVisitor<Void, Void>() {
			@Override
			protected Void defaultVisit(final Conclusion premise, Void ignored) {

				traceReader.accept(inferenceContextRoot, premise,
						new AbstractClassInferenceVisitor<IndexedClassExpression, Void>() {

							@Override
							protected Void defaultTracedVisit(
									ClassInference premiseInference, IndexedClassExpression ignored) {

								premiseInferenceMap.add(premise,
										premiseInference);

								return null;
							}

						});

				return null;
			}
		}), null);

		// now, let's see if there's an alternative inference for every premise
		for (Conclusion premise : premiseInferenceMap.keySet()) {
			final MutableBoolean isPremiseCyclic = new MutableBoolean(true);

			for (ClassInference premiseInference : premiseInferenceMap.get(premise)) {
				final MutableBoolean isPremiseInferenceCyclic = new MutableBoolean(
						false);
				// does it use the given conclusion as a premise?
				if (premiseInference.getInferenceContextRoot(inferenceContextRoot) == inferenceTargetRoot) {
					
					premiseInference.acceptTraced(
							new PremiseVisitor<Void, Void>(new AbstractConclusionVisitor<Void, Void>() {
								@Override
								protected Void defaultVisit(
										Conclusion premiseOfPremise,
										Void ignored) {

									if (premiseOfPremise.accept(
											new ConclusionEqualityChecker(),
											inference)) {
										// ok, this inference uses the given
										// conclusion
										isPremiseInferenceCyclic.set(true);
									}

									return null;
								}
							}), null);
				} else {
					// ok, the premise was inferred in a context different from
					// where the current inference was made.
				}
				// the premise is inferred only through the given conclusion
				// (i.e. is cyclic) iff ALL its inferences use the the given
				// conclusion
				isPremiseCyclic.and(isPremiseInferenceCyclic.get());
			}

			if (isPremiseCyclic.get()) {
				return true;
			}
		}

		return false;
	}
	
	////////////////////////////////////////////////////////////////////////
	// some dummy utility visitors
	////////////////////////////////////////////////////////////////////////
	static final AbstractClassInferenceVisitor<IndexedClassExpression, Boolean> DUMMY_CLASS_INFERENCE_CHECKER = new AbstractClassInferenceVisitor<IndexedClassExpression, Boolean>() {

		@Override
		protected Boolean defaultTracedVisit(ClassInference conclusion,
				IndexedClassExpression input) {
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
