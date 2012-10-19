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
import org.semanticweb.elk.reasoner.incremental.ContextModificationListener;
import org.semanticweb.elk.reasoner.incremental.IncrementalChangesInitialization;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.rules.RuleDeapplicationFactory;

/**
 * Reverts inferences
 * 
 * @author Pavel Klinov
 * 
 */
class IncrementalChangesInitializationStage extends AbstractReasonerStage {

	// logger for this class
	//private static final Logger LOGGER_ = Logger.getLogger(IncrementalDeSaturationStage.class);

	private IncrementalChangesInitialization initialization_ = null;
	private final ContextModificationListener<IndexedClassExpression> listener_ = new ContextModificationListener<IndexedClassExpression>();

	public IncrementalChangesInitializationStage(AbstractReasonerState reasoner) {
		super(reasoner);
	}

	@Override
	public String getName() {
		return "Incremental Context Desaturation";
	}

	@Override
	public boolean done() {
		// TODO new constant?
		return reasoner.doneContextReset;
	}

	@Override
	public List<ReasonerStage> getDependencies() {
		return Arrays.asList((ReasonerStage) new ChangesLoadingStage(reasoner));
	}

	@Override
	public void execute() throws ElkInterruptedException {
		if (initialization_ == null) {
			initComputation();
		}
		
		progressMonitor.start(getName());

		try {
			for (;;) {
				initialization_.process();
				if (!interrupted())
					break;
			}
		} finally {
			progressMonitor.finish();
		}
		// TODO new constant
		reasoner.doneContextReset = true;
		// save for future processing
		reasoner.incrementalState.classesToProcess_ = listener_.getModifiedClassExpressions();
	}
	
	

	@Override
	void initComputation() {
		super.initComputation();
		// this factory will be shared between the initialization and (de)saturation computations
		RuleDeapplicationFactory deappFactory = new RuleDeapplicationFactory(reasoner.ontologyIndex);
		
		initialization_ = new IncrementalChangesInitialization(
				reasoner.ontologyIndex.getIndexedClassExpressions(),
				reasoner.incrementalState.diffIndex_.getIndexDeletions(),
				deappFactory,
				reasoner.getProcessExecutor(),
				workerNo,
				reasoner.getProgressMonitor(),
				listener_);
	}

	@Override
	public void printInfo() {
		// TODO Auto-generated method stub
		
	}
}