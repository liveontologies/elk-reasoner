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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.incremental.IncrementalChangesInitialization;
import org.semanticweb.elk.reasoner.incremental.IncrementalStages;
import org.semanticweb.elk.reasoner.indexing.hierarchy.DifferentialIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.BasicCompositionRuleApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.ChainableRule;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationCounterVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationTimerVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.RuleStatistics;
import org.semanticweb.elk.util.collections.Operations;

/**
 * Reverts inferences
 * 
 * @author Pavel Klinov
 * @author "Yevgeny Kazakov"
 * 
 */
abstract class BaseIncrementalChangesInitializationStage extends
		AbstractReasonerStage {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(BaseIncrementalChangesInitializationStage.class);

	static final boolean COLLECT_RULE_COUNTS = LOGGER_.isDebugEnabled();
	static final boolean COLLECT_RULE_TIMES = LOGGER_.isDebugEnabled();

	protected IncrementalChangesInitialization initialization_ = null;

	protected final SaturationStatistics stageStatistics_ = new SaturationStatistics();

	BaseIncrementalChangesInitializationStage(AbstractReasonerState reasoner) {
		super(reasoner);
	}

	protected abstract IncrementalStages stage();

	/*
	 * private IncrementalStages stage() { return deletions_ ?
	 * IncrementalStages.DELETIONS_INIT : IncrementalStages.ADDITIONS_INIT; }
	 */

	@Override
	public String getName() {
		return stage().toString();
	}

	@Override
	public boolean done() {
		return reasoner.incrementalState.getStageStatus(stage());
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
		 * incremental part of the index is not needed anymore. The subclasses
		 * will commit it and clear it.
		 */
		postExecute();

		reasoner.incrementalState.setStageStatus(stage(), true);
		reasoner.ruleAndConclusionStats.add(stageStatistics_);
	}

	protected abstract void postExecute();

	protected RuleApplicationVisitor getRuleApplicationVisitor(
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

/**
 * 
 * 
 */
class IncrementalAdditionInitializationStage extends
		BaseIncrementalChangesInitializationStage {

	IncrementalAdditionInitializationStage(AbstractReasonerState reasoner) {
		super(reasoner);
	}

	@Override
	public Iterable<ReasonerStage> getDependencies() {
		return Collections
				.<ReasonerStage> singleton(new InitializeContextsAfterCleaning(
						reasoner));
	}

	@Override
	protected IncrementalStages stage() {
		return IncrementalStages.ADDITIONS_INIT;
	}

	@Override
	void initComputation() {
		super.initComputation();

		DifferentialIndex diffIndex = reasoner.incrementalState.diffIndex;
		ChainableRule<Context> changedInitRules = null;
		Map<IndexedClassExpression, ChainableRule<Context>> changedRulesByCE = null;
		Collection<Collection<Context>> inputs = Collections.emptyList();
		RuleApplicationVisitor ruleAppVisitor = getRuleApplicationVisitor(stageStatistics_
				.getRuleStatistics());

		changedInitRules = diffIndex.getAddedContextInitRules();
		changedRulesByCE = diffIndex.getAddedContextRulesByClassExpressions();

		if (changedInitRules != null || !changedRulesByCE.isEmpty()) {
			// inputs = Operations.split(
			// reasoner.ontologyIndex.getIndexedClassExpressions(), 128);
			inputs = Operations.split(reasoner.saturationState.getContexts(),
					128);
		}

		initialization_ = new IncrementalChangesInitialization(inputs,
				changedInitRules, changedRulesByCE, reasoner.saturationState,
				reasoner.getProcessExecutor(), ruleAppVisitor, workerNo,
				reasoner.getProgressMonitor());
	}

	@Override
	protected void postExecute() {
		reasoner.incrementalState.diffIndex.commitAddedRules();
	}

}

/**
 * 
 * 
 */
class IncrementalDeletionInitializationStage extends
		BaseIncrementalChangesInitializationStage {

	IncrementalDeletionInitializationStage(AbstractReasonerState reasoner) {
		super(reasoner);
	}

	@Override
	public Iterable<ReasonerStage> getDependencies() {
		return Collections
				.<ReasonerStage> singleton(new IncrementalCompletionStage(
						reasoner));
	}

	@Override
	protected IncrementalStages stage() {
		return IncrementalStages.DELETIONS_INIT;
	}

	@Override
	void initComputation() {
		super.initComputation();

		DifferentialIndex diffIndex = reasoner.incrementalState.diffIndex;
		ChainableRule<Context> changedInitRules = null;
		Map<IndexedClassExpression, ChainableRule<Context>> changedRulesByCE = null;
		Collection<Collection<Context>> inputs = Collections.emptyList();
		RuleApplicationVisitor ruleAppVisitor = getRuleApplicationVisitor(stageStatistics_
				.getRuleStatistics());

		changedInitRules = diffIndex.getRemovedContextInitRules();
		changedRulesByCE = diffIndex.getRemovedContextRulesByClassExpressions();

		if (changedInitRules != null || !changedRulesByCE.isEmpty()) {
			// inputs = Operations.split(
			// reasoner.ontologyIndex.getIndexedClassExpressions(), 128);
			inputs = Operations.split(reasoner.saturationState.getContexts(),
					128);
		}

		initialization_ = new IncrementalChangesInitialization(inputs,
				changedInitRules, changedRulesByCE, reasoner.saturationState,
				reasoner.getProcessExecutor(), ruleAppVisitor, workerNo,
				reasoner.getProgressMonitor());
	}

	@Override
	protected void postExecute() {
		SaturationState.Writer writer = reasoner.saturationState.getWriter();
		// Contexts for removed classes must also be properly cleaned to not
		// leave any broken backward links
		// TODO Perhaps its cleaner to do this thing inside the computation (to
		// make it interruptable, etc.)
		for (IndexedClassExpression removed : reasoner.incrementalState.diffIndex
				.getRemovedClassExpressions()) {
			if (removed.getContext() != null) {

				writer.markForRemoval(removed.getContext());
			}
		}

		reasoner.incrementalState.diffIndex.clearDeletedRules();
	}

}