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

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.incremental.IncrementalChangesInitialization;
import org.semanticweb.elk.reasoner.incremental.IncrementalStages;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.CountingConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.BasicCompositionRuleApplicationVisitor;
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
abstract class AbstractIncrementalChangesInitializationStage extends
		AbstractReasonerStage {

	private static final Logger LOGGER_ = Logger
			.getLogger(AbstractIncrementalChangesInitializationStage.class);

	static final boolean COLLECT_RULE_COUNTS = LOGGER_.isDebugEnabled();
	static final boolean COLLECT_RULE_TIMES = LOGGER_.isDebugEnabled();
	static final boolean COLLECT_CONCLUSION_COUNTS = LOGGER_.isDebugEnabled();

	protected IncrementalChangesInitialization initialization_ = null;

	protected final SaturationStatistics stageStatistics_ = new SaturationStatistics();

	public AbstractIncrementalChangesInitializationStage(
			AbstractReasonerState reasoner, AbstractReasonerStage... preStages) {
		super(reasoner, preStages);
	}

	protected abstract IncrementalStages stage();

	@Override
	public String getName() {
		return stage().toString();
	}

	@Override
	public void executeStage() throws ElkInterruptedException {
		for (;;) {
			initialization_.process();
			if (!spuriousInterrupt())
				break;
		}
	}

	@Override
	boolean postExecute() {
		if (!super.postExecute())
			return false;
		/*
		 * if this stage is completed successfully, the corresponding
		 * incremental part of the index is not needed anymore. The subclasses
		 * will commit it and clear it.
		 */
		reasoner.incrementalState.setStageStatus(stage(), true);
		reasoner.ruleAndConclusionStats.add(stageStatistics_);
		return true;
	}

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