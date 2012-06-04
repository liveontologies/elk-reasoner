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

import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.saturation.properties.RedundantCompositionsElimination;

public class RedundantCompositionsEliminationStage extends
		AbstractReasonerStage {

	/**
	 * the computation used for this stage
	 */
	private RedundantCompositionsElimination computation;

	/**
	 * the progress monitor used to report progress of this stage
	 */
	private final ProgressMonitor progressMonitor;

	public RedundantCompositionsEliminationStage(AbstractReasonerState reasoner) {
		super(reasoner);
		this.computation = null;
		this.progressMonitor = reasoner.getProgressMonitor();
	}

	@Override
	public String getName() {
		return "Redundant Compositions Elimination";
	}

	@Override
	public boolean done() {
		return reasoner.doneRedundantCompositionsElimination;
	}

	@Override
	public List<ReasonerStage> getDependencies() {
		return Arrays
				.asList((ReasonerStage) new ObjectPropertyCompositionsInitializationStage(
						reasoner));
	}

	@Override
	public void execute() {
		/*
		 * since the compositions can be computed only at the dependent stage,
		 * we need to initialize the computation the first time it is executed
		 */
		if (computation == null)
			computation = new RedundantCompositionsElimination(
					reasoner.compositions.entrySet(),
					reasoner.getStageExecutor(), progressMonitor);
		computation.process();
		if (isInterrupted())
			return;
		reasoner.doneRedundantCompositionsElimination = true;
	}

	@Override
	public void printInfo() {
	}

}
