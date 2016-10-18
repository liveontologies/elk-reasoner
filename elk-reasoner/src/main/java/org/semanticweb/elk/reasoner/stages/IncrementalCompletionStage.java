/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.reasoner.incremental.IncrementalStages;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationNoInput;
import org.semanticweb.elk.reasoner.saturation.ContextModificationListener;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationAdditionFactory;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationInput;

/**
 * Completes saturation of all contexts which are not saturated at this point.
 * Useful, for example, to continue saturation after the ontology was proved
 * inconsistent and all workers have stopped, possibly in the middle of
 * saturation.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class IncrementalCompletionStage extends AbstractReasonerStage {

	// logger for this class
	// private static final Logger LOGGER_ =
	// LoggerFactory.getLogger(IncrementalCompletionStage.class);

	private ClassExpressionSaturationNoInput completion_ = null;

	public IncrementalCompletionStage(AbstractReasonerState reasoner,
			AbstractReasonerStage... preStages) {
		super(reasoner, preStages);
	}

	@Override
	public String getName() {
		return IncrementalStages.COMPLETION.toString();
	}

	@Override
	public boolean preExecute() {
		if (!super.preExecute())
			return false;
		completion_ = new ClassExpressionSaturationNoInput(
				reasoner.getProcessExecutor(), workerNo,
				new RuleApplicationAdditionFactory<RuleApplicationInput>(
						reasoner.getInterrupter(), reasoner.saturationState),
				ContextModificationListener.DUMMY);
		return true;
	}

	@Override
	public void executeStage() throws ElkException {
		completion_.process();
	}

	@Override
	public boolean postExecute() {
		if (!super.postExecute())
			return false;
		reasoner.ruleAndConclusionStats.add(completion_
				.getRuleAndConclusionStatistics());
		this.completion_ = null;
		return true;
	}

	@Override
	public void printInfo() {
		// TODO Auto-generated method stub
	}

}
