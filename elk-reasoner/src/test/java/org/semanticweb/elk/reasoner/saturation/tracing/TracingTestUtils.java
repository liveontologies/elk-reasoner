/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.atomic.AtomicInteger;

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.BaseConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.tracing.util.TracingUtils;
import org.semanticweb.elk.reasoner.stages.ReasonerStateAccessor;
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
	
}
