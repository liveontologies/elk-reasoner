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

import org.semanticweb.elk.reasoner.incremental.ContextModificationListener;
import org.semanticweb.elk.reasoner.incremental.IncrementalStages;
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
	private final ContextModificationListener listener_ = new ContextModificationListener();

	public IncrementalDeSaturationStage(AbstractReasonerState reasoner) {
		super(reasoner);
	}

	@Override
	public String getName() {
		return IncrementalStages.DESATURATION.toString();
	}

	@Override
	public boolean done() {
		return reasoner.incrementalState.getStageStatus(IncrementalStages.DESATURATION);
	}

	@Override
	public List<ReasonerStage> getDependencies() {
		return Arrays.asList((ReasonerStage) new IncrementalChangesInitializationStage(reasoner, true));
	}

	@Override
	public void execute() throws ElkInterruptedException {
		if (desaturation_ == null) {
			initComputation();
		}
		
		listener_.reset();
		progressMonitor.start(getName());
		
		try {
			for (;;) {
				desaturation_.process();
				if (!interrupted())
					break;
			}
		} finally {
			progressMonitor.finish();
		}
		
		reasoner.incrementalState.setStageStatus(IncrementalStages.DESATURATION, true);
		// save for future processing
		reasoner.incrementalState.classesToProcess = listener_.getModifiedClassExpressions();
	}
	
	

	@Override
	void initComputation() {
		super.initComputation();

		RuleDeapplicationFactory deappFactory = new RuleDeapplicationFactory(reasoner.saturationState);
		
		desaturation_ = new ClassExpressionSaturation<IndexedClassExpression>(
				reasoner.incrementalState.classesToProcess,
				reasoner.getProcessExecutor(),
				workerNo,
				reasoner.getProgressMonitor(),
				deappFactory,
				listener_);
	}

	@Override
	public void printInfo() {
		if (desaturation_ != null)
			desaturation_.printStatistics();
	}
}