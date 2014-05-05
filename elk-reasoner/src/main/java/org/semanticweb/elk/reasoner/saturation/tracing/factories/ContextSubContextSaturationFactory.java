/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.factories;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationFactory;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationListener;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationAdditionFactory;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationInput;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Saturates sub-contexts triggering saturation of their parent contexts, if
 * they have not yet been saturated.
 * 
 * When a sub-context saturation job finishes, then factory guarantees that all
 * sub-conclusions, e.g., propagations, are computed and stored in the
 * sub-context.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ContextSubContextSaturationFactory<J extends SubContextSaturationJob<?>> implements InputProcessorFactory<J, ContextSubContextSaturationFactory<J>.Engine> {

	private static final Logger LOGGER_ = LoggerFactory.getLogger(ContextSubContextSaturationFactory.class);
	/**
	 * saturates contexts
	 */
	private final ClassExpressionSaturationFactory<J> contextSaturationFactory_;
	/**
	 * saturates sub-contexts
	 */
	private final SubContextSaturationFactory<J> subcontextSaturationFactory_;
	
	private final SaturationState<?> saturationState_;
	
	private final Queue<J> jobsWithSaturatedRoot_;
	
	public ContextSubContextSaturationFactory(SaturationState<?> saturationState, int maxWorkers, ClassExpressionSaturationListener<J> listener) {
		saturationState_ = saturationState;
		contextSaturationFactory_ = new ClassExpressionSaturationFactory<J>(new RuleApplicationAdditionFactory<RuleApplicationInput>(saturationState_), maxWorkers, new ContextSaturationListener());
		subcontextSaturationFactory_ = new SubContextSaturationFactory<J>(new RuleApplicationAdditionFactory<SubContextRuleApplicationInput>(saturationState_), maxWorkers, listener);
		jobsWithSaturatedRoot_ = new ConcurrentLinkedQueue<J>();
	}
	
	@Override
	public Engine getEngine() {
		return new Engine();
	}

	@Override
	public void finish() {
		contextSaturationFactory_.finish();
	}
	
	/**
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	class Engine implements InputProcessor<J> {

		private final ClassExpressionSaturationFactory<J>.Engine contextSaturationEngine_ = contextSaturationFactory_.getEngine();
		
		private final SubContextSaturationFactory<J>.Engine subcontextSaturationEngine_ = subcontextSaturationFactory_.getEngine();
		
		@Override
		public void process() throws InterruptedException {
			for (;;) {
				if (Thread.currentThread().isInterrupted())
					return;
				
				J jobWithSaturatedRoot = jobsWithSaturatedRoot_.poll();
				
				if (jobWithSaturatedRoot != null) {
					LOGGER_.trace("{}.{}: context is saturated, sub-context saturation started", jobWithSaturatedRoot.getInput(), jobWithSaturatedRoot.getSubRoot());
					
					subcontextSaturationEngine_.submit(jobWithSaturatedRoot);
					subcontextSaturationEngine_.process();
					continue;
				}
				// saturate the remaining unsaturated contexts
				contextSaturationEngine_.process();
				break;
			}
		}

		@Override
		public void finish() {
			contextSaturationEngine_.finish();
			subcontextSaturationEngine_.finish();
		}

		@Override
		public void submit(J job) {
			IndexedClassExpression root = job.getInput();

			LOGGER_.trace("{}.{}: sub-context saturation submitted", root, job.getSubRoot());

			Context context = saturationState_.getContext(root);
			
			if (context != null && context.isInitialized() 	&& context.isSaturated()) {
				// the root is already saturated, we can start working on the sub-context
				jobsWithSaturatedRoot_.add(job);
			} else {
				LOGGER_.trace("{}.{}: need to saturate the context first", root, job.getSubRoot());
				// need to saturate the root first
				contextSaturationEngine_.submit(job);
			}
		}
		
	}
	
	/**
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	private class ContextSaturationListener implements ClassExpressionSaturationListener<J> {

		@Override
		public void notifyFinished(J job) throws InterruptedException {
			LOGGER_.trace("{}.{}: context saturation finished", job.getInput(), job.getSubRoot());
			
			jobsWithSaturatedRoot_.add(job);
		}
		
	}

}
