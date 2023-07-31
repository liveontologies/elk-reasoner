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

import java.util.Collections;

import org.semanticweb.elk.reasoner.completeness.Feature;
import org.semanticweb.elk.reasoner.completeness.OccurrencesInOntology;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassEntity;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturation;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationAdditionFactory;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ReasonerStage} during which consistency of the current ontology is
 * checked
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
class ConsistencyCheckingStage extends AbstractReasonerStage {
	
	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ConsistencyCheckingStage.class);

	/**
	 * the computation used for this stage
	 */
	protected ClassExpressionSaturation<IndexedClassEntity> computation = null;

	public ConsistencyCheckingStage(AbstractReasonerState reasoner,
			AbstractReasonerStage... preStages) {
		super(reasoner, preStages);
	}

	@Override
	public String getName() {
		return "Consistency Checking";
	}

	@Override
	public boolean preExecute() {
		if (!super.preExecute())
			return false;		
		this.computation = new ClassExpressionSaturation<IndexedClassEntity>(
				isTriviallyConsistent() ? Collections.emptyList()
						: reasoner.consistencyCheckingState.getTestEntitites(),
				reasoner.getProcessExecutor(), workerNo,
				reasoner.getProgressMonitor(),
				new RuleApplicationAdditionFactory<RuleApplicationInput>(
						reasoner.getInterrupter(), reasoner.saturationState));
		return true;
	}
	
	boolean isTriviallyConsistent() {
		OccurrencesInOntology occurrences = reasoner.getOccurrencesInOntology();
		if (
		occurrences.getOccurrenceCount(Feature.OWL_NOTHING_POSITIVE) == 0
				&& occurrences.getOccurrenceCount(Feature.DIFFERENT_INDIVIDUALS) == 0
				&& occurrences.getOccurrenceCount(Feature.DISJOINT_CLASSES) == 0	
				&& occurrences.getOccurrenceCount(Feature.DISJOINT_UNION) == 0
				&& occurrences.getOccurrenceCount(
						Feature.OBJECT_COMPLEMENT_OF_POSITIVE) == 0) {
			LOGGER_.debug("The ontology is trivially consistent");
			return true;
		}
		// else
		return false;
	}

	@Override
	public void executeStage() throws ElkInterruptedException {
		computation.process();
	}

	@Override
	public boolean postExecute() {
		if (!super.postExecute())
			return false;
		reasoner.ruleAndConclusionStats
				.add(computation.getRuleAndConclusionStatistics());
		this.computation = null;
		// prunes consistency checking
		reasoner.consistencyCheckingState.getTestEntitites(); 
		return true;
	}

	@Override
	public void printInfo() {
		if (computation != null)
			computation.printStatistics();
	}

}
