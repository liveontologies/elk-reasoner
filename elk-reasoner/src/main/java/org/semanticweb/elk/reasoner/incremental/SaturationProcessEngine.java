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
package org.semanticweb.elk.reasoner.incremental;

import org.semanticweb.elk.util.concurrent.computation.InputProcessor;

/**
 * The engine to perform inferences of the respective
 * {@link IncrementalRuleApplicationEngine}, which can be initialized by either
 * {@link SaturationChangesInitEngine} or
 * {@link IncrementalRuleApplicationEngine}. This engine has only dummy submit
 * method just to make sure that the workers start processing inferences through
 * the {@link IncrementalRuleApplicationEngine}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class SaturationProcessEngine implements InputProcessor<Void> {

	/**
	 * The engine for revering inferences
	 */
	protected final IncrementalRuleApplicationEngine incrementalRuleApplicationEngine;

	public SaturationProcessEngine(
			IncrementalRuleApplicationEngine incrementalRuleApplicationEngine) {
		this.incrementalRuleApplicationEngine = incrementalRuleApplicationEngine;
	}

	public void submit(Void job) throws InterruptedException {
		// dummy method just to make sure that the workers start
		// processing inferences
	}

	public void process() throws InterruptedException {
		incrementalRuleApplicationEngine.process();
	}

	public boolean canProcess() {
		return incrementalRuleApplicationEngine.canProcess();
	}

}
