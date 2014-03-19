/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.factories;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationFactory;
import org.semanticweb.elk.reasoner.saturation.DummyClassExpressionSaturationListener;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationAdditionFactory;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationInput;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO
 * 
 * When a sub-context saturation job finishes, then factory guarantees that all
 * sub-conclusions, e.g., propagation are computed and stored in the
 * sub-context.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class SubContextSaturationFactory<J extends SubContextSaturationJob> implements InputProcessorFactory<J, SubContextSaturationFactory<J>.Engine> {

	private static final Logger LOGGER_ = LoggerFactory.getLogger(SubContextSaturationFactory.class);
	
	//private final RuleApplicationFactory<?> ruleAppFactory_;
	
	private final ClassExpressionSaturationFactory<J> saturationFactory_;
	
	private final SaturationState<?> saturationState_;
	
	private final Queue<J> jobsToDo_;
	
	private final Queue<J> jobsWithSaturatedRoot_;
	
	public SubContextSaturationFactory(SaturationState<?> saturationState, int maxWorkers) {
		//ruleAppFactory_ = new RuleApplicationAdditionFactory(saturationState);
		saturationState_ = saturationState;
		saturationFactory_ = new ClassExpressionSaturationFactory<J>(new RuleApplicationAdditionFactory<RuleApplicationInput>(saturationState_), maxWorkers, new DummyClassExpressionSaturationListener<J>());
		jobsToDo_ = new ConcurrentLinkedQueue<J>();
		jobsWithSaturatedRoot_ = new ConcurrentLinkedQueue<J>();
	}
	
	@Override
	public Engine getEngine() {
		return new Engine();
	}

	@Override
	public void finish() {
		saturationFactory_.finish();
	}
	
	/**
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	class Engine implements InputProcessor<J> {

		private final ClassExpressionSaturationFactory<J>.Engine saturationEngine_ = saturationFactory_.getEngine();
		
		//private final InputProcessor<RuleApplicationInput> ruleAppEngine_ = ruleAppFactory_.getEngine(ContextCreationListener.DUMMY, 	ContextModificationListener.DUMMY);
		
		@Override
		public void process() throws InterruptedException {
			for (;;) {
				if (Thread.currentThread().isInterrupted())
					return;
				
				J jobWithSaturatedRoot = jobsWithSaturatedRoot_.poll();
				
				if (jobWithSaturatedRoot != null) {
					saturateSubContext(jobWithSaturatedRoot);
					continue;
				}
				
				saturationEngine_.process();
				break;
			}
		}

		private void saturateSubContext(J job) throws InterruptedException {
			// process the leftover from previously interrupted jobs
		}

		@Override
		public void finish() {
			saturationEngine_.finish();
		}

		@Override
		public void submit(J job) {
			IndexedClassExpression root = job.getInput();

			LOGGER_.trace("{}.{}: sub-context saturation started", root, job.getSubRoot());

			Context context = saturationState_.getContext(root);
			
			if (context != null && context.isInitialized() 	&& context.isSaturated()) {
				// the root is already saturated, we can start working on the sub-context
				jobsWithSaturatedRoot_.add(job);
			} else {
				// need to saturate root first
				saturationEngine_.submit(job);
			}
		}
		
	}

}
