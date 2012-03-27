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
 * The engine that can be used to initialize changes in the saturation after
 * incremental index update. It should be used either for index additions or
 * index deletions. The appropriate mode is determined by the
 * {@link IncrementalRuleApplicationEngine} used in this engine.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <J>
 *            the type of the contexts which this engine can accept
 */
public class SaturationChangesInitEngine<J extends SaturatedClassExpression>
		implements InputProcessor<J> {

	/**
	 * The rule application engine used for initialization of the saturation
	 * changes
	 */
	protected final IncrementalRuleApplicationEngine incrementalRuleApplicationEngine;

	public SaturationChangesInitEngine(
			IncrementalRuleApplicationEngine incrementalRuleApplicationEngine) {
		this.incrementalRuleApplicationEngine = incrementalRuleApplicationEngine;
	}

	public void submit(J job) throws InterruptedException {
		incrementalRuleApplicationEngine.initChanges(job);
	}

	public void process() throws InterruptedException {
		// nothing to do here; this engine should be used only for
		// initialization, not for processing
	}

	public boolean canProcess() {
		// the jobs are immediately processed, so there is nothing to process
		return false;
	}

}
