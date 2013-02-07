package org.semanticweb.elk.reasoner.stages;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.semanticweb.elk.reasoner.incremental.IncrementalChangesInitialization;
import org.semanticweb.elk.reasoner.incremental.IncrementalStages;
import org.semanticweb.elk.reasoner.indexing.hierarchy.DifferentialIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.SaturationState.ExtendedWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ChainableRule;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationVisitor;
import org.semanticweb.elk.util.collections.Operations;

/**
 * 
 * 
 */
class IncrementalDeletionInitializationStage extends
		AbstractIncrementalChangesInitializationStage {

	public IncrementalDeletionInitializationStage(ReasonerStageManager manager) {
		super(manager);
	}

	@Override
	public Iterable<ReasonerStage> getDependencies() {
		return Collections
				.<ReasonerStage> singleton(manager.incrementalCompletionStage);
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
				reasoner.getProcessExecutor(), ruleAppVisitor,
				conclusionVisitor, workerNo, reasoner.getProgressMonitor());
	}

	@Override
	protected void postExecute() {
		// initializing contexts which will be removed
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