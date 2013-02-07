/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.stages;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.util.concurrent.computation.SimpleInterrupter;

/**
 * A {@link ReasonerStageExecutor} which refuses to interrupt: it will restart
 * any interrupted stage. Used for unit tests.
 * 
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class RestartingTestStageExecutor extends SimpleInterrupter implements
		ReasonerStageExecutor {

	@Override
	public void complete(ReasonerStage stage) throws ElkException {
		if (!stage.isCompleted()) {

			for (ReasonerStage dependentStage : stage.getPreStages()) {
				complete(dependentStage);
			}
			registerCurrentThreadToInterrupt();
			for (;;) {
				try {
					stage.execute();
					break;
				} catch (ElkException e) {
					if (e instanceof ElkInterruptedException) {
						stage.clearInterrupt();
						continue;
					} else
						throw e;
				} finally {
					finish(stage);
				}
			}
			finish(stage);
		}
	}

	private void finish(ReasonerStage stage) {
		clearThreadToInterrupt();
	}
}
