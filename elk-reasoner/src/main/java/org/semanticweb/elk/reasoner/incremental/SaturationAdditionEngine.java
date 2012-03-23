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
 * The engine to perform re-application of inferences within contexts. It is
 * indented to be executed after the initialization stage using
 * {@link SaturationAdditionInitEngine}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class SaturationAdditionEngine implements InputProcessor<Void> {

	/**
	 * The engine for re-applying the inferences
	 */
	protected final RuleReApplicationEngine ruleReApplicationEngine;

	public SaturationAdditionEngine(
			RuleReApplicationEngine ruleReApplicationEngine) {
		this.ruleReApplicationEngine = ruleReApplicationEngine;
	}

	public void submit(Void job) throws InterruptedException {
		// nothing to do here
	}

	public void process() throws InterruptedException {
		ruleReApplicationEngine.process();
	}

	public boolean canProcess() {
		return ruleReApplicationEngine.canProcess();
	}

}
