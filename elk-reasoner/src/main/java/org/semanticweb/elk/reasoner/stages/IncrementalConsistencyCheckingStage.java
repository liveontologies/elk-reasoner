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

/**
 * A {@link ReasonerStage} during which consistency of the current ontology is
 * checked
 * 
 * @author Pavel Klinov
 * 
 */
class IncrementalConsistencyCheckingStage extends ConsistencyCheckingStage {

	public IncrementalConsistencyCheckingStage(ReasonerStageManager manager) {
		super(manager);
	}

	@Override
	public String getName() {
		return "Incremental Consistency Checking";
	}

	@Override
	public List<ReasonerStage> getDependencies() {
		// TODO: what will happen with the taxonomy? Will it be correctly
		// updated after several (incremental) consistency tests?
		return Arrays.asList(manager.incrementalAdditionStage);
	}

	@Override
	void initComputation() {
		super.initComputation();

		this.computation_ = new ConsistencyChecking(
				reasoner.getProcessExecutor(), workerNo,
				reasoner.getProgressMonitor(), reasoner.ontologyIndex,
				reasoner.saturationState);
	}

}
