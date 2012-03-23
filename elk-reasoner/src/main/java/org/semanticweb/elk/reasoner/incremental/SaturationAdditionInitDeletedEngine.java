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
 * The engine to initialize re-application of inferences in contexts. It works
 * by re-applying inferences in the contexts in which inferences have been
 * reverted. It is intended to be executed after the stage using
 * {@link SaturationDeletionEngine} for contexts which became non-saturated.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <J>
 *            the type of the contexts with which this engine works
 */
public class SaturationAdditionInitDeletedEngine<J extends SaturatedClassExpression>
		implements InputProcessor<J> {

	/**
	 * The engine for re-applying the inferences
	 */
	protected final RuleReApplicationEngine ruleReApplicationEngine;

	public SaturationAdditionInitDeletedEngine(
			RuleReApplicationEngine ruleReApplicationEngine) {
		this.ruleReApplicationEngine = ruleReApplicationEngine;
	}

	public void submit(J job) throws InterruptedException {
		ruleReApplicationEngine.processContext(job);
	}

	public void process() throws InterruptedException {
		// nothing to do here
	}

	public boolean canProcess() {
		// the jobs are immediately processed, so there is nothing to process
		return false;
	}

}
