/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationFactory;
import org.semanticweb.elk.reasoner.saturation.DummyClassExpressionSaturationListener;
import org.semanticweb.elk.reasoner.saturation.ExtendedSaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationJob;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationFactory;
import org.semanticweb.elk.reasoner.saturation.tracing.RecursiveContextTracingFactory.Engine;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Uses a listener to get notified when a non-traced context is used to produce
 * a conclusion to a traced context and recursively submits the former for
 * tracing.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class RecursiveContextTracingFactory implements
		InputProcessorFactory<IndexedClassExpression, Engine> {
	
	private static final Logger LOGGER_ = LoggerFactory.getLogger(RecursiveContextTracingFactory.class);
	
	/**
	 * Factory for non-recursive context tracing.
	 */
	private final ClassExpressionSaturationFactory<SaturationJob<IndexedClassExpression>> tracingFactory_;
	
	private final Queue<SaturationJob<IndexedClassExpression>> toTraceQueue_;
	
	private final TracingSaturationState tracingSaturationState_;

	/**
	 * 
	 */
	public RecursiveContextTracingFactory(
			ExtendedSaturationState saturationState, TraceState traceState, int maxWorkers) {
		RuleApplicationFactory ruleTracingFactory = new ContextTracingFactory(saturationState, traceState, new RecursiveContextTracingListener());
		
		toTraceQueue_ = new ConcurrentLinkedQueue<SaturationJob<IndexedClassExpression>>();
		tracingSaturationState_ = traceState.getSaturationState();
		tracingFactory_ = new ClassExpressionSaturationFactory<SaturationJob<IndexedClassExpression>>(
				ruleTracingFactory, maxWorkers,
				new DummyClassExpressionSaturationListener<SaturationJob<IndexedClassExpression>>());
 
	}
	
	@Override
	public Engine getEngine() {
		return new Engine();
	}

	@Override
	public void finish() {
		tracingFactory_.finish();
	}
	
	/**
	 * Print statistics about the saturation
	 */
	public void printStatistics() {
		tracingFactory_.printStatistics();
	}

	public SaturationStatistics getRuleAndConclusionStatistics() {
		return tracingFactory_.getRuleAndConclusionStatistics();
	}	

	/**
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	public class Engine implements InputProcessor<IndexedClassExpression> {

		private final ClassExpressionSaturationFactory<SaturationJob<IndexedClassExpression>>.Engine tracingEngine_ = tracingFactory_.getEngine();
		
		@Override
		public void submit(IndexedClassExpression root) {
			LOGGER_.trace("{}: recursive context tracing started", root);

			Context context = tracingSaturationState_.getContext(root);
			
			if (context == null || !context.isSaturated()) {
				tracingEngine_.submit(new SaturationJob<IndexedClassExpression>(root));
			}
		}

		@Override
		public void process() throws InterruptedException {
			for (;;) {
				if (Thread.currentThread().isInterrupted())
					return;

				tracingEngine_.process();
				
				SaturationJob<IndexedClassExpression> nextJob = toTraceQueue_.poll();
				
				if (nextJob == null)
					break;
				
				tracingEngine_.submit(nextJob);
			}
		}

		@Override
		public void finish() {
			tracingEngine_.finish();
		}

	}

	/**
	 * Gets notifications when new yet-not-traced context need to be traced and submits new jobs to the context tracing queue.
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	private class RecursiveContextTracingListener implements ContextTracingListener {

		@Override
		public void notifyNonTraced(Context context) {
			/*
			 * this context hasn't yet been submitted for tracing but was used
			 * to produce a conclusion for a traced context. Thus we should
			 * trace it recursively.
			 */
			LOGGER_.trace("{}: recursively submitted for tracing", context);
			
			toTraceQueue_.add(new SaturationJob<IndexedClassExpression>(context.getRoot()));
		}
		
	}
	
}
