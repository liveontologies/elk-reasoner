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

import java.util.Arrays;
import java.util.List;

import org.semanticweb.elk.reasoner.consistency.ConsistencyChecking;

public class ConsistencyCheckingStage extends AbstractReasonerStage {

	public ConsistencyCheckingStage(ReasonerState reasoner) {
		super(reasoner);
	}

	@Override
	public String getName() {
		return "Consistency Checking";
	}

	@Override
	public boolean done() {
		return reasoner.doneConsistencyCheck;
	}

	@Override
	public List<ReasonerStage> getDependencies() {
		return Arrays.asList((ReasonerStage) new ObjectPropertySaturationStage(
				reasoner));
	}

	@Override
	public void execute() {
		reasoner.consistentOntology = (new ConsistencyChecking(
				reasoner.stageExecutor, reasoner.executor, reasoner.workerNo,
				reasoner.progressMonitor, reasoner.ontologyIndex))
				.checkConsistent();
		if (isInterrupted())
			return;
		reasoner.doneConsistencyCheck = true;
	}

	@Override
	public void printInfo() {
	}

}
