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

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.util.concurrent.computation.SimpleInterrupter;
import org.semanticweb.elk.util.logging.Statistics;

/**
 * A {@link ReasonerStageExecutor} which refuses to interrupt: it will restart
 * any interrupted stage.
 * 
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class RestartingStageExecutor extends SimpleInterrupter implements
		ReasonerStageExecutor {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(RestartingStageExecutor.class);

	@Override
	public void complete(ReasonerStage stage) throws ElkException {
		if (!stage.done()) {
			LOGGER_.debug(stage.getName() + " stage:");
			for (ReasonerStage dependentStage : stage.getPreStages()) {
				complete(dependentStage);
			}
			Statistics.logOperationStart(stage.getName(), LOGGER_);
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
		}
	}

	private void finish(ReasonerStage stage) {
		clearThreadToInterrupt();
		Statistics.logOperationFinish(stage.getName(), LOGGER_);
		Statistics.logMemoryUsage(LOGGER_);
		stage.printInfo();
	}

}
