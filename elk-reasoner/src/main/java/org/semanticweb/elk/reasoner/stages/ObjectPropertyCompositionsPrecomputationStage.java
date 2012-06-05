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

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.saturation.classes.RuleRoleComposition;
import org.semanticweb.elk.reasoner.saturation.properties.ObjectPropertyCompositionsPrecomputation;

/**
 * The reasoner stage whose purpose is to set up multimaps for fast look-up of
 * object property compositions to be used in {@link RuleRoleComposition}.
 * 
 * @author Frantisek Simancik
 * 
 */
public class ObjectPropertyCompositionsPrecomputationStage extends
		AbstractReasonerStage {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(ObjectPropertyCompositionsPrecomputationStage.class);

	/**
	 * the computation used for this stage
	 */
	private final ObjectPropertyCompositionsPrecomputation computation;
	/**
	 * the number of workers used in the computation for this stage
	 */
	private final int workerNo;
	/**
	 * the progress monitor used to report progress of this stage
	 */
	private final ProgressMonitor progressMonitor;

	public ObjectPropertyCompositionsPrecomputationStage(
			AbstractReasonerState reasoner) {
		super(reasoner);
		this.workerNo = reasoner.getNumberOfWorkers();
		this.progressMonitor = reasoner.getProgressMonitor();
		this.computation = new ObjectPropertyCompositionsPrecomputation(
				reasoner.getStageExecutor(), workerNo, progressMonitor,
				reasoner.ontologyIndex);
	}

	@Override
	public String getName() {
		return "Object Property Compositions Precomputation";
	}

	@Override
	public boolean done() {
		return reasoner.doneObjectPropertyCompositionsPrecomputation;
	}

	@Override
	public List<ReasonerStage> getDependencies() {
		return Arrays
				.asList((ReasonerStage) new ObjectPropertyHierarchyComputationStage(
						reasoner));
	}

	@Override
	public void execute() {
		if (LOGGER_.isInfoEnabled())
			LOGGER_.info(getName() + " using " + workerNo + " workers");
		computation.process();
		if (isInterrupted())
			return;
		reasoner.doneObjectPropertyCompositionsPrecomputation = true;
		reasoner.doneReset = false;
	}

	@Override
	public void printInfo() {
	}

}
