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
 * A {@link ReasonerStageExecutor} which prints log messages about the executed
 * stages. If a stage has not been done, first, all its dependencies are
 * executed, and then this stage itself.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class LoggingStageExecutor extends SimpleInterrupter implements
		ReasonerStageExecutor {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(LoggingStageExecutor.class);

	@Override
	public void complete(ReasonerStage stage) throws ElkException {
		if (!stage.done()) {
			if (LOGGER_.isTraceEnabled())
				LOGGER_.trace(stage.getName() + " stage:");
			try {
				for (ReasonerStage dependentStage : stage.getDependencies()) {
					complete(dependentStage);
				}
			} catch (ElkException e) {
				LOGGER_.info(stage.getName() + ":" + e.getMessage());
				throw e;
			}
			Statistics.logOperationStart(stage.getName(), LOGGER_);
			registerCurrentThreadToInterrupt();
			try {
				stage.execute();
			} catch (ElkException e) {
				if (e instanceof ElkInterruptedException)
					if (LOGGER_.isInfoEnabled())
						LOGGER_.info(e.getMessage());
					else
						LOGGER_.debug(e.getMessage());
				throw new ElkInterruptedException(e);
			} finally {
				clearThreadToInterrupt();
				Statistics.logOperationFinish(stage.getName(), LOGGER_);
				Statistics.logMemoryUsage(LOGGER_);
				stage.printInfo();
				LOGGER_.debug(stage.getName() + " done.");
			}
		}
	}
}
