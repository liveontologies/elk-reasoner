/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.exceptions.ElkException;
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

	private volatile ReasonerStage stageInProgress_;

	@Override
	public void complete(ReasonerStage stage) throws ElkException {
		if (!stage.isCompleted()) {

			// TODO: avoid recursive call, use accumulator
			for (ReasonerStage dependentStage : stage.getPreStages()) {
				complete(dependentStage);
			}
			try {
				stageInProgress_ = stage;
				execute(stage);
			} finally {
				stageInProgress_ = null;
			}
		}

	}

	@Override
	public synchronized void setInterrupt(boolean flag) {
		super.setInterrupt(flag);
		ReasonerStage interrupter = stageInProgress_;
		if (interrupter != null) {
			interrupter.setInterrupt(flag);
		}
	}

	protected abstract void execute(ReasonerStage stage) throws ElkException;
}
