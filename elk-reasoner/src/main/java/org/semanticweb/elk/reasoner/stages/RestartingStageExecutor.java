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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ReasonerStageExecutor} which refuses to interrupt: it will restart
 * any interrupted stage.
 * 
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class RestartingStageExecutor extends SimpleStageExecutor {

	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(RestartingStageExecutor.class);

	@Override
	public void complete(ReasonerStage stage) throws ElkException {
		for (;;) {
			try {
				super.complete(stage);
			} catch (ElkInterruptedException e) {
				LOGGER_.info(stage.getName() + " restarted");
				stage.setInterrupt(false);
				continue;
			}
			break;
		}
	}

}
