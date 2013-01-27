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
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.reasoner.incremental.IncrementalChangesInitialization;
import org.semanticweb.elk.reasoner.incremental.IncrementalStages;
import org.semanticweb.elk.reasoner.indexing.hierarchy.DifferentialIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexObjectConverter;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.SaturationState.ExtendedWriter;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.CountingConclusionVisitor;
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

	private static final Logger LOGGER_ = Logger
			.getLogger(BaseIncrementalChangesInitializationStage.class);

	static final boolean COLLECT_RULE_COUNTS = LOGGER_.isDebugEnabled();
	static final boolean COLLECT_RULE_TIMES = LOGGER_.isDebugEnabled();
	static final boolean COLLECT_CONCLUSION_COUNTS = LOGGER_.isDebugEnabled();

	protected IncrementalChangesInitialization initialization_ = null;

	protected final SaturationStatistics stageStatistics_ = new SaturationStatistics();

	BaseIncrementalChangesInitializationStage(AbstractReasonerState reasoner) {
		super(reasoner);
	}

	protected abstract IncrementalStages stage();

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
	
	protected ConclusionVisitor<?> getConclusionVisitor(
			ConclusionStatistics conclusionStatistics) {

		return COLLECT_CONCLUSION_COUNTS ? new CountingConclusionVisitor(
				conclusionStatistics.getProducedConclusionCounts())
				: ConclusionVisitor.DUMMY;
	}
	

	@Override
	public void printInfo() {
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
		ConclusionVisitor<?> conclusionVisitor = getConclusionVisitor(stageStatistics_
				.getConclusionStatistics());
		// first, create and init contexts for new classes
		final IndexObjectConverter converter = reasoner.objectCache_
				.getIndexObjectConverter();
		final ExtendedWriter writer = reasoner.saturationState.getExtendedWriter(conclusionVisitor);
		
		for (ElkClass newClass : reasoner.incrementalState.diffIndex.getAddedClasses()) {
			IndexedClass ic = (IndexedClass) converter.visit(newClass);
			
			if (ic.getContext() == null ) {
				writer.getCreateContext(ic);
			}
			else {
				//TODO Figure out why some added classes have contexts
				//This happens when class is removed and then re-added
				//throw new RuntimeException(ic + ": " + ic.getContext().getSubsumers());
			}
		}

		changedInitRules = diffIndex.getAddedContextInitRules();
		changedRulesByCE = diffIndex.getAddedContextRulesByClassExpressions();

		if (changedInitRules != null || !changedRulesByCE.isEmpty()) {
			inputs = Operations.split(reasoner.saturationState.getContexts(),
					128);
		}

		initialization_ = new IncrementalChangesInitialization(inputs,
				changedInitRules, changedRulesByCE, reasoner.saturationState,
				reasoner.getProcessExecutor(), ruleAppVisitor, conclusionVisitor, workerNo,
				reasoner.getProgressMonitor());
	}

	@Override
	protected void postExecute() {
		reasoner.incrementalState.diffIndex.commitAddedRules();
		reasoner.incrementalState.diffIndex.clearSignatureChanges();
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
		ConclusionVisitor<?> conclusionVisitor = getConclusionVisitor(stageStatistics_
				.getConclusionStatistics());
		
		changedInitRules = diffIndex.getRemovedContextInitRules();
		changedRulesByCE = diffIndex.getRemovedContextRulesByClassExpressions();

		if (changedInitRules != null || !changedRulesByCE.isEmpty()) {

			inputs = Operations.split(reasoner.saturationState.getContexts(),
					128);
		}

		initialization_ = new IncrementalChangesInitialization(inputs,
				changedInitRules, changedRulesByCE, reasoner.saturationState,
				reasoner.getProcessExecutor(), ruleAppVisitor, conclusionVisitor, workerNo,
				reasoner.getProgressMonitor());
	}

	@Override
	protected void postExecute() {
		//initializing contexts which will be removed
		ConclusionVisitor<?> conclusionVisitor = getConclusionVisitor(stageStatistics_
				.getConclusionStatistics());

		final ExtendedWriter writer = reasoner.saturationState
				.getExtendedWriter(conclusionVisitor);

		for (IndexedClassExpression ice : reasoner.incrementalState.diffIndex
				.getRemovedClassExpressions()) {

			if (ice.getContext() != null) {
				writer.initContext(ice.getContext());
				/*
				 * TODO This is only needed to clean the taxonomy afterwards
				 * It's better to delete these nodes from the taxonomy
				 * immediately and get rid of tracking the removed contexts in
				 * the saturation state
				 */
				writer.markForRemoval(ice.getContext());
			}
		}

		reasoner.incrementalState.diffIndex.clearDeletedRules();
	}

}