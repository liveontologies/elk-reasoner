/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.atomic.AtomicInteger;

import org.semanticweb.elk.MutableBoolean;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.BaseBooleanConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.BaseConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionEqualityChecker;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.tracing.util.TracingUtils;
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
 * pavel.klinov@uni-ulm.de
 */
public class TracingTestUtils {

	protected static final Logger LOGGER_ = LoggerFactory	.getLogger(TracingTestUtils.class);
	
	private static final UntracedConclusionListener UNTRACED_LISTENER = new UntracedConclusionListener() {
		
		@Override
		public void notifyUntraced(Conclusion conclusion, Context context) {
			fail("Conclusion " + conclusion + " stored in " + context + " was not traced");
		}
	};
	
	public static int checkTracingCompleteness(ElkClassExpression sub, ElkClassExpression sup, Reasoner reasoner) {
		Context cxt = ReasonerStateAccessor.transform(reasoner, sub).getContext();
		Conclusion conclusion = TracingUtils.getSubsumerWrapper(ReasonerStateAccessor.transform(reasoner, sup));
		final AtomicInteger conclusionCount = new AtomicInteger(0);
		TraceState traceState = ReasonerStateAccessor.getTraceState(reasoner);
		ConclusionVisitor<Boolean, Context> counter = new BaseConclusionVisitor<Boolean, Context>() {

			@Override
			protected Boolean defaultVisit(Conclusion conclusion, Context cxt) {
				conclusionCount.incrementAndGet();
				
				return true;
			}
		};
		
		RecursiveTraceExplorer explorer = new RecursiveTraceExplorer(traceState.getTraceStore().getReader(), traceState.getSaturationState(), UNTRACED_LISTENER);
		
		explorer.accept(cxt, conclusion, counter);
		
		return conclusionCount.get();
	}
	
	public static void checkTracingMinimality(ElkClassExpression sub, ElkClassExpression sup, Reasoner reasoner) {
		Context cxt = ReasonerStateAccessor.transform(reasoner, sub).getContext();
		Conclusion conclusion = TracingUtils.getSubsumerWrapper(ReasonerStateAccessor.transform(reasoner, sup));
		TracedContextsCollector collector = new TracedContextsCollector();
		TraceState traceState = ReasonerStateAccessor.getTraceState(reasoner);
		
		new RecursiveTraceExplorer(traceState.getTraceStore().getReader(), traceState.getSaturationState(), UNTRACED_LISTENER).accept(cxt, conclusion, collector);

		for (Context traced : traceState.getSaturationState().getTracedContexts()) {
			IndexedClassExpression root = traced.getRoot();
			
			assertTrue(root + " has been traced for no good reason", collector.getTracedRoots().contains(traced.getRoot()));
		}
	}
	
	public static void checkNumberOfInferences(ElkClassExpression sub, ElkClassExpression sup, Reasoner reasoner, int expected) {
		final IndexedClassExpression subsumee = ReasonerStateAccessor.transform(reasoner, sub);
		Conclusion conclusion = TracingUtils.getSubsumerWrapper(ReasonerStateAccessor.transform(reasoner, sup));
		final AtomicInteger inferenceCount = new AtomicInteger(0);
		TracedConclusionVisitor<Boolean, Context> counter = new BaseTracedConclusionVisitor<Boolean, Context>() {

			@Override
			protected Boolean defaultTracedVisit(TracedConclusion conclusion, Context context) {
				
				LOGGER_.trace("{}: traced inference {}", subsumee, conclusion);
				
				inferenceCount.incrementAndGet();
				
				return true;
			}
			
		};
		
		ReasonerStateAccessor.getTraceState(reasoner).getTraceStore().getReader().accept(subsumee, conclusion, counter);
		
		assertEquals(expected, inferenceCount.get());
	}
	
	public static int checkInferenceAcyclicity(ElkClassExpression sub, ElkClassExpression sup, Reasoner reasoner) {
		Context cxt = ReasonerStateAccessor.transform(reasoner, sub).getContext();
		Conclusion conclusion = TracingUtils.getSubsumerWrapper(ReasonerStateAccessor.transform(reasoner, sup));
		final AtomicInteger conclusionCount = new AtomicInteger(0);
		final TraceState traceState = ReasonerStateAccessor.getTraceState(reasoner);
		RecursiveTraceExplorer explorer = new RecursiveTraceExplorer(traceState.getTraceStore().getReader(), traceState.getSaturationState(), UNTRACED_LISTENER);
		
		explorer.accept(cxt, conclusion, new BaseBooleanConclusionVisitor<Context>(), new BaseTracedConclusionVisitor<Void, Context>() {

			@Override
			protected Void defaultTracedVisit(TracedConclusion inference, Context inferenceContext) {
				assertFalse(inference + " made in " + inferenceContext + " is cyclic", isInferenceCyclic(inference, inferenceContext, traceState.getTraceStore().getReader()));
				
				return null;
			}
			
		});
		
		return conclusionCount.get();
	}
	
	
	//the most straightforward (and slow) implementation
	public static boolean isInferenceCyclic(final TracedConclusion inference, final Context inferenceContext, final TraceStore.Reader traceReader) {
		//first, create a map of premises to their inferences
		final Multimap<Conclusion, TracedConclusion> premiseInferenceMap = new HashListMultimap<Conclusion, TracedConclusion>();
		
		inference.acceptTraced(new PremiseVisitor<Void, Void>() {

			@Override
			protected Void defaultVisit(final Conclusion premise, Void ignored) {
				
				traceReader.accept(inferenceContext.getRoot(), premise, new BaseTracedConclusionVisitor<Void, Void>() {

					@Override
					protected Void defaultTracedVisit(TracedConclusion premiseInference, Void ignored) {
						
						premiseInferenceMap.add(premise, premiseInference);
						
						return null;
					}
					
				});
				
				return null;
			}
			
		}, null);
		
		//now, let's see if there's an alternative inference for every premise
		for (Conclusion premise : premiseInferenceMap.keySet()) {
			final MutableBoolean isCyclic = new MutableBoolean(true);
			
			for (TracedConclusion premiseInference : premiseInferenceMap.get(premise)) {
				//does it use the given conclusion as a premise?
				if (premiseInference.getInferenceContext(inferenceContext) != inferenceContext) {
					//ok, the premise was inferred in a context different from where the current inference was made.
					isCyclic.set(false);
				}
				
				final MutableBoolean initInference = new MutableBoolean(true); 
				
				premiseInference.acceptTraced(new PremiseVisitor<Void, Void>() {

					@Override
					protected Void defaultVisit(Conclusion premiseOfPremise, Void ignored) {
						initInference.set(false);
						
						if (!premiseOfPremise.accept(new ConclusionEqualityChecker(), inference)) {
							isCyclic.set(false);
						}
						
						return null;
					}
					
				}, null);
				
				if (initInference.get()) {
					isCyclic.set(false);
				}
			}
			
			if (isCyclic.get()) {
				return true;
			}
		}
		
		return false;
	}
}
