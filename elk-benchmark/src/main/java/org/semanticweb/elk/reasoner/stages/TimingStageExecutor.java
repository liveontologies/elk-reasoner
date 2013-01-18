/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.reasoner.stages.AbstractStageExecutor;
import org.semanticweb.elk.reasoner.stages.ReasonerStage;
import org.semanticweb.elk.util.logging.ElkTimer;

/**
 * A simple executor which measures the time spent on stage execution
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TimingStageExecutor extends AbstractStageExecutor {

	private final AbstractStageExecutor executor_;
	
	public TimingStageExecutor(final AbstractStageExecutor executor) {
		executor_ = executor;
	}

	@Override
	public void execute(ReasonerStage stage) throws ElkException {
		ElkTimer timer = ElkTimer.getNamedTimer(stage.getName());
		
		timer.start();
		executor_.execute(stage);
		timer.stop();
	}

}
