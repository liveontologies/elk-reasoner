/**
 * 
 */
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

import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturation;
import org.semanticweb.elk.reasoner.saturation.context.ContextRootCollection;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationAdditionPruningFactory;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationFactory;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationInput;

/**
 * Prunes the set of deleted conclusions by re-deriving those having alternative
 * derivations (name taken from the original paper on the DRed algorithm). Uses
 * a {@link RuleApplicationAdditionPruningFactory} which "fills gaps" in the set of
 * conclusions for each context by deriving the missing conclusions.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class IncrementalOverdeletionPruningStage extends AbstractReasonerStage {

	private ClassExpressionSaturation<IndexedContextRoot> completion_ = null;

	public IncrementalOverdeletionPruningStage(AbstractReasonerState reasoner,
			AbstractReasonerStage... preStages) {
		super(reasoner, preStages);
	}

	@Override
	public String getName() {
		return "Incremental Overdeletion Pruning";
	}

	@Override
	public void printInfo() {
		if (completion_ != null) {
			completion_.printStatistics();
		}
	}

	@Override
	public boolean preExecute() {
		if (!super.preExecute()) {
			return false;
		}

		RuleApplicationFactory<?, RuleApplicationInput> ruleAppFactory = new RuleApplicationAdditionPruningFactory(
				reasoner.saturationState);
		Collection<IndexedContextRoot> inputs = new ContextRootCollection(
				reasoner.saturationState.getNotSaturatedContexts());

		completion_ = new ClassExpressionSaturation<IndexedContextRoot>(
				inputs, reasoner.getProcessExecutor(), workerNo,
				reasoner.getProgressMonitor(), ruleAppFactory);

		return true;
	}

	@Override
	void executeStage() throws ElkException {
		completion_.process();
	}

	@Override
	public boolean postExecute() {
		if (!super.postExecute()) {
			return false;
		}
		reasoner.ruleAndConclusionStats.add(completion_
				.getRuleAndConclusionStatistics());
		this.completion_ = null;
		return true;
	}

	@Override
	public synchronized void setInterrupt(boolean flag) {
		super.setInterrupt(flag);
		setInterrupt(completion_, flag);
	}

}
