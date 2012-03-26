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

import org.semanticweb.elk.reasoner.rules.SaturatedClassExpression;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;

/**
 * The engine to revert all inferences of not saturated contexts. It works by
 * applying all inferences starting from the root of the context, except that
 * the conclusions that are relevant to un-saturated contexts are deleted
 * instead of added.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <J>
 *            the type of the contexts with which this engine works
 */
public class SaturationCleaningEngine<J extends SaturatedClassExpression>
		implements InputProcessor<J> {

	// TODO: process input in batches similar to the main saturation engine
	/**
	 * The engine for revering inferences
	 */
	protected final RuleCleaningEngine ruleCleaningEngine;

	public SaturationCleaningEngine(RuleCleaningEngine ruleCleaningEngine) {
		this.ruleCleaningEngine = ruleCleaningEngine;
	}

	public void submit(J job) throws InterruptedException {
		ruleCleaningEngine.processContextCleaning(job);
	}

	public void process() throws InterruptedException {
		ruleCleaningEngine.process();
	}

	public boolean canProcess() {
		return ruleCleaningEngine.canProcess();
	}

}
