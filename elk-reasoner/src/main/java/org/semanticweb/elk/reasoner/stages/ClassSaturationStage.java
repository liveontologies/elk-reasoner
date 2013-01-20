package org.semanticweb.elk.reasoner.stages;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
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

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturation;

/**
 * A {@link ReasonerStage} which computes saturation for every class of the
 * ontology
 * 
 * @author "Yevgeny Kazakov"
 */
public class ClassSaturationStage extends AbstractReasonerStage {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(ClassSaturationStage.class);

	/**
	 * the computation used for this stage
	 */
	private ClassExpressionSaturation<IndexedClass> computation_ = null;

	public ClassSaturationStage(AbstractReasonerState reasoner) {
		super(reasoner);
	}

	@Override
	public String getName() {
		return "Class Saturation";
	}

	@Override
	public boolean done() {
		return reasoner.doneClassSaturation;
	}

	@Override
	public List<ReasonerStage> getDependencies() {
		return Arrays.asList((ReasonerStage) new ConsistencyCheckingStage(
				reasoner));
	}

	@Override
	public void execute() throws ElkInterruptedException {
		if (computation_ == null)
			initComputation();
		progressMonitor.start(getName());
		try {
			for (;;) {
				computation_.process();
				if (!interrupted())
					break;
			}
		} finally {
			progressMonitor.finish();
		}
		
		reasoner.doneClassSaturation = true;
		reasoner.ruleAndConclusionStats.add(computation_.getRuleAndConclusionStatistics());
	}

	@Override
	void initComputation() {
		super.initComputation();
		this.computation_ = new ClassExpressionSaturation<IndexedClass>(
				reasoner.ontologyIndex.getIndexedClasses(),
				reasoner.getProcessExecutor(), workerNo,
				reasoner.getProgressMonitor(), reasoner.saturationState);
		if (LOGGER_.isInfoEnabled())
			LOGGER_.info(getName() + " using " + workerNo + " workers");
	}

	@Override
	public void printInfo() {
		if (computation_ != null)
			computation_.printStatistics();
	}

}
