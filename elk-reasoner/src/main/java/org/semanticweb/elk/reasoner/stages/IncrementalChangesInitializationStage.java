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

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.incremental.IncrementalChangesInitialization;
import org.semanticweb.elk.reasoner.incremental.IncrementalStages;
import org.semanticweb.elk.reasoner.indexing.hierarchy.DifferentialIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.RuleAndConclusionStatistics;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.BasicCompositionRuleApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.ChainableRule;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationCounterVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationTimerVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.RuleStatistics;

/**
 * Reverts inferences
 * 
 * @author Pavel Klinov
 * @author "Yevgeny Kazakov"
 * 
 */
class IncrementalChangesInitializationStage extends AbstractReasonerStage {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(IncrementalChangesInitializationStage.class);
	static final boolean COLLECT_RULE_COUNTS = LOGGER_.isDebugEnabled();
	static final boolean COLLECT_RULE_TIMES = LOGGER_.isDebugEnabled();
	private final ReasonerStage dependency_;

	private IncrementalChangesInitialization initialization_ = null;

	private final boolean deletions_;

	private final RuleAndConclusionStatistics stageStatistics_ = new RuleAndConclusionStatistics();

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

		/*
		 * if this stage is completed successfully, the corresponding
		 * incremental part of the index is not needed anymore
		 */
		if (deletions_)
			reasoner.incrementalState.diffIndex.clearDeletedRules();
		else
			reasoner.incrementalState.diffIndex.commitAddedRules();
		reasoner.incrementalState.setStageStatus(stage(), true);
		reasoner.ruleAndConclusionStats.add(stageStatistics_);
	}

	@Override
	void initComputation() {
		super.initComputation();

		DifferentialIndex diffIndex = reasoner.incrementalState.diffIndex;
		ChainableRule<Context> changedInitRules = null;
		Map<IndexedClassExpression, ChainableRule<Context>> changedRulesByCE = null;
		Collection<IndexedClassExpression> inputs = Collections.emptyList();
		RuleApplicationVisitor ruleAppVisitor = getRuleApplicationVisitor(stageStatistics_
				.getRuleStatistics());

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

		initialization_ = new IncrementalChangesInitialization(inputs,
				changedInitRules, changedRulesByCE, reasoner.saturationState,
				reasoner.getProcessExecutor(), ruleAppVisitor, workerNo,
				reasoner.getProgressMonitor());
	}

	private RuleApplicationVisitor getRuleApplicationVisitor(
			RuleStatistics ruleStatistics) {
		RuleApplicationVisitor ruleAppVisitor = new BasicCompositionRuleApplicationVisitor();

		if (COLLECT_RULE_COUNTS) {
			ruleAppVisitor = new RuleApplicationCounterVisitor(ruleAppVisitor,
					ruleStatistics.ruleCounter);
		}

		if (COLLECT_RULE_TIMES) {
			ruleAppVisitor = new RuleApplicationTimerVisitor(ruleAppVisitor,
					ruleStatistics.ruleTimer);
		}

		return ruleAppVisitor;
	}

	@Override
	public void printInfo() {
		// TODO Auto-generated method stub
	}
}