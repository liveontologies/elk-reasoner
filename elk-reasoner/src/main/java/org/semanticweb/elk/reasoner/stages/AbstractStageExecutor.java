/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.util.concurrent.computation.SimpleInterrupter;

/**
 * An abstract base class which implements a very simple logic of executing
 * stages: first check the completion flag, if false - first complete all
 * dependencies, and finally execute the stage.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public abstract class AbstractStageExecutor extends SimpleInterrupter implements
		ReasonerStageExecutor {

	@Override
	public void complete(ReasonerStage stage) throws ElkException {
		if (!stage.done()) {

			for (ReasonerStage dependentStage : stage.getDependencies()) {
				complete(dependentStage);
			}

			registerCurrentThreadToInterrupt();

			try {
				execute(stage);
			} finally {
				clearThreadToInterrupt();
			}
		}

	}

	// FIXME Perhaps should be protected but then it won't be possible to create
	// a wrapper around this interface in another package
	public abstract void execute(ReasonerStage stage) throws ElkException;
}
