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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturation;
import org.semanticweb.elk.reasoner.saturation.rules.RuleDeapplicationFactory;

/**
 * Reverts inferences
 * 
 * @author Pavel Klinov
 * 
 */
class IncrementalDeSaturationStage extends AbstractReasonerStage {

	// logger for this class
	// private static final Logger LOGGER_ = Logger.getLogger(IncrementalDeSaturationStage.class);

	private ClassExpressionSaturation<IndexedClassExpression> desaturation_ = null;

	public IncrementalDeSaturationStage(AbstractReasonerState reasoner) {
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
		if (desaturation_ == null) {
			initComputation();
		}
		
		try {
			// First, initialize changes, that is, take all concepts where superclasses have some registered changes
			// and put those superclasses in the ToDo queue
			
			// Second, run de-saturation

		} finally {
			progressMonitor.finish();
		}
		reasoner.doneContextReset = true;
	}
	
	

	@Override
	void initComputation() {
		super.initComputation();

		RuleDeapplicationFactory deappFactory = new RuleDeapplicationFactory(reasoner.ontologyIndex);
		
		desaturation_ = new ClassExpressionSaturation<IndexedClassExpression>(
				reasoner.incrementalState.classesToProcess_,
				reasoner.getProcessExecutor(),
				workerNo,
				reasoner.getProgressMonitor(),
				deappFactory);
	}

	@Override
	public void printInfo() {
		// TODO Auto-generated method stub
		
	}
}