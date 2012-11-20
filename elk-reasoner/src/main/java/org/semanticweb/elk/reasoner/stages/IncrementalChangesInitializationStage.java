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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.semanticweb.elk.reasoner.incremental.IncrementalChangesInitialization;
import org.semanticweb.elk.reasoner.incremental.IncrementalStages;
import org.semanticweb.elk.reasoner.indexing.hierarchy.DifferentialIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ChainableRule;

/**
 * Reverts inferences
 * 
 * @author Pavel Klinov
 * 
 */
class IncrementalChangesInitializationStage extends AbstractReasonerStage {

	// logger for this class
	// private static final Logger LOGGER_ =
	// Logger.getLogger(IncrementalDeSaturationStage.class);
	private final ReasonerStage dependency_;

	private IncrementalChangesInitialization initialization_ = null;

	private final boolean deletions_;

	IncrementalChangesInitializationStage(AbstractReasonerState reasoner,
			boolean deletions) {
		super(reasoner);
		deletions_ = deletions;
		dependency_ = null;
	}

	IncrementalChangesInitializationStage(AbstractReasonerState reasoner,
			boolean deletions, ReasonerStage dependency) {
		super(reasoner);
		deletions_ = deletions;
		dependency_ = dependency;
	}

	private IncrementalStages stage() {
		return deletions_ ? IncrementalStages.DELETIONS_INIT
				: IncrementalStages.ADDITIONS_INIT;
	}

	@Override
	public String getName() {
		return stage().toString();
	}

	@Override
	public boolean done() {
		return reasoner.incrementalState.getStageStatus(stage());
	}

	@Override
	public List<ReasonerStage> getDependencies() {
		return dependency_ != null ? Arrays.asList(dependency_) : Collections
				.<ReasonerStage> emptyList();
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

		reasoner.incrementalState.setStageStatus(stage(), true);
	}

	@Override
	void initComputation() {
		super.initComputation();

		DifferentialIndex diffIndex = reasoner.incrementalState.diffIndex;
		ChainableRule<Context> changedInitRules = null;
		Map<IndexedClassExpression, ChainableRule<Context>> changedRulesByCE = null;
		Collection<IndexedClassExpression> inputs = Collections.emptyList();

		if (deletions_) {
			changedInitRules = diffIndex.getRemovedContextInitRules();
			changedRulesByCE = diffIndex
					.getRemovedContextRulesByClassExpressions();
		} else {
			changedInitRules = diffIndex.getAddedContextInitRules();
			changedRulesByCE = diffIndex
					.getAddedContextRulesByClassExpressions();
		}

		if (changedInitRules != null || !changedRulesByCE.isEmpty()) {
			inputs = reasoner.ontologyIndex.getIndexedClassExpressions();
		}
		
		System.out.println("Input size " + inputs.size());

		initialization_ = new IncrementalChangesInitialization(inputs,
				changedInitRules, changedRulesByCE, reasoner.saturationState,
				deletions_, reasoner.getProcessExecutor(), workerNo,
				reasoner.getProgressMonitor());
	}

	@Override
	public void printInfo() {
		// TODO Auto-generated method stub
	}
}