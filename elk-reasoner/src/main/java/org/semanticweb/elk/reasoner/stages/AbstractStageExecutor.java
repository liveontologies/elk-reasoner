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
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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
