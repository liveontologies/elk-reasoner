/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.atomic.AtomicInteger;

import org.semanticweb.elk.MutableBoolean;
import org.semanticweb.elk.MutableInteger;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.BaseConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionEqualityChecker;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.tracing.LocalTracingSaturationState.TracedContext;
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
		InferenceVisitor<Boolean, Context> counter = new BaseInferenceVisitor<Boolean, Context>() {

			@Override
			protected Boolean defaultTracedVisit(Inference conclusion, Context context) {
				
				LOGGER_.trace("{}: traced inference {}", subsumee, conclusion);
				
				inferenceCount.incrementAndGet();
				
				return true;
			}
			
		};
		
		ReasonerStateAccessor.getTraceState(reasoner).getTraceStore().getReader().accept(subsumee, conclusion, counter);
		
		assertEquals(expected, inferenceCount.get());
	}
	
	public static int checkInferenceAcyclicity(Reasoner reasoner) {
		final AtomicInteger conclusionCount = new AtomicInteger(0);
		final TraceState traceState = ReasonerStateAccessor.getTraceState(reasoner);
		final TraceStore.Reader traceReader = traceState.getTraceStore().getReader();
		final SaturationState tracingState = traceState.getSaturationState();
		final MutableInteger counter = new MutableInteger(0);
		
		for (IndexedClassExpression root : traceReader.getContextRoots()) {
			final Context cxt = tracingState.getContext(root);
			
			traceReader.visitInferences(root, new BaseInferenceVisitor<Void, Void>() {

				@Override
				protected Void defaultTracedVisit(Inference inference, Void ignored) {
					counter.increment();
					
					if (isInferenceCyclic(inference, cxt, traceReader)) {
						isInferenceCyclic(inference, cxt, traceReader);
						
						fail(inference + " saved in " + cxt + " is cyclic ");
					}
					
					return null;
				}
				
			});
		}
		
		//System.err.println(counter + " saved inferences checked for acyclicity");
		
		//now check if some blocked inferences are, in fact, acyclic
		counter.set(0);
		
		for (Context cxt : tracingState.getContexts()) {
			TracedContext context = (TracedContext) cxt;
			
			for (Conclusion premise : context.getBlockedInferences().keySet()) {
				for (Inference blocked : context.getBlockedInferences().get(premise)) {
				
					Context target = tracingState.getContext(blocked.acceptTraced(new GetInferenceTarget(), context));
					
					counter.increment();
					if (!isInferenceCyclic(blocked, target, traceReader)) {
						isInferenceCyclic(blocked, target, traceReader);
						
						fail(blocked + " blocked in " + context + " by the premise " + premise + " is acyclic");
					}
				}
			}
		}
		
		//System.err.println(counter + " blocked inferences checked for acyclicity");
		
		return conclusionCount.get();
	}
	
	
	//the most straightforward (and slow) implementation
	public static boolean isInferenceCyclic(final Inference inference, final Context inferenceTarget, final TraceStore.Reader traceReader) {
		//first, create a map of premises to their inferences
		final Multimap<Conclusion, Inference> premiseInferenceMap = new HashListMultimap<Conclusion, Inference>();
		final Context inferenceContext = inference.getInferenceContext(inferenceTarget);
		
		inference.acceptTraced(new PremiseVisitor<Void, Void>() {

			@Override
			protected Void defaultVisit(final Conclusion premise, Void ignored) {
				
				traceReader.accept(inferenceContext.getRoot(), premise, new BaseInferenceVisitor<Void, Void>() {

					@Override
					protected Void defaultTracedVisit(Inference premiseInference, Void ignored) {
						
						premiseInferenceMap.add(premise, premiseInference);
						
						return null;
					}
					
				});
				
				return null;
			}
			
		}, null);
		
		//now, let's see if there's an alternative inference for every premise
		for (Conclusion premise : premiseInferenceMap.keySet()) {
			final MutableBoolean isPremiseCyclic = new MutableBoolean(true);
			
			for (Inference premiseInference : premiseInferenceMap.get(premise)) {
				final MutableBoolean isPremiseInferenceCyclic = new MutableBoolean(false);
				//does it use the given conclusion as a premise?
				if (premiseInference.getInferenceContext(inferenceContext).getRoot() == inferenceTarget.getRoot()) {
					premiseInference.acceptTraced(new PremiseVisitor<Void, Void>() {

						@Override
						protected Void defaultVisit(Conclusion premiseOfPremise, Void ignored) {
							
							if (premiseOfPremise.accept(new ConclusionEqualityChecker(), inference)) {
								//ok, this inference uses the given conclusion
								isPremiseInferenceCyclic.set(true);
							}
							
							return null;
						}
						
					}, null);
				}
				else {
					//ok, the premise was inferred in a context different from where the current inference was made.
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
}
