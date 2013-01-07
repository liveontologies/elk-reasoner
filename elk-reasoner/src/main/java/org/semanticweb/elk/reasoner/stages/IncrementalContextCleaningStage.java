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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.reasoner.incremental.IncrementalStages;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturation;
import org.semanticweb.elk.reasoner.saturation.rules.ContextCleaningFactory;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationFactory;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class IncrementalContextCleaningStage extends AbstractReasonerStage
		implements PostProcessingReasonerStage {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(IncrementalContextCleaningStage.class);

	private ClassExpressionSaturation<IndexedClassExpression> cleaning_ = null;

	public IncrementalContextCleaningStage(AbstractReasonerState reasoner) {
		super(reasoner);
	}

	@Override
	public String getName() {
		return IncrementalStages.CONTEXT_CLEANING.toString();
	}

	@Override
	public boolean done() {
		return reasoner.incrementalState
				.getStageStatus(IncrementalStages.CONTEXT_CLEANING);
	}

	@Override
	public List<ReasonerStage> getDependencies() {
		return Arrays
				.asList((ReasonerStage) new IncrementalContextInitializationStage(
						reasoner, new IncrementalDeSaturationStage(reasoner)));
	}

	@Override
	public void execute() throws ElkInterruptedException {
		if (cleaning_ == null) {
			initComputation();
		}

		progressMonitor.start(getName());

		try {
			for (;;) {
				cleaning_.process();
				if (!interrupted())
					break;
			}
		} finally {
			progressMonitor.finish();
		}

		reasoner.incrementalState.setStageStatus(
				IncrementalStages.CONTEXT_CLEANING, true);
	}

	@Override
	void initComputation() {
		super.initComputation();

		RuleApplicationFactory cleaningFactory = new ContextCleaningFactory(
				reasoner.saturationState);

		if (LOGGER_.isDebugEnabled()) {
			LOGGER_.debug("Contexts to be cleaned: "
					+ reasoner.saturationState.getNotSaturatedContexts());
		}

		cleaning_ = new ClassExpressionSaturation<IndexedClassExpression>(
				reasoner.getProcessExecutor(), workerNo,
				reasoner.getProgressMonitor(), cleaningFactory);
	}

	@Override
	public void printInfo() {
		if (cleaning_ != null)
			cleaning_.printStatistics();
	}

	@Override
	public Collection<ReasonerStage> getPostProcessingStages() {
		return Collections.<ReasonerStage> singleton(new CheckCleaningStage());
	}

	/**
	 * Used to check that all unsaturated contexts have been cleaned
	 */
	private class CheckCleaningStage extends BaseReasonerStage {

		@Override
		public String getName() {
			return "Checking that unsaturated contexts are clean";
		}

		@Override
		public void execute() throws ElkException {
			for (IndexedClassExpression ice : reasoner.saturationState.getNotSaturatedContexts()) {
				if (ice.getContext().getSubsumers().size() > 0) {
					LOGGER_.error("Context not cleaned: " + ice.toString() + "!" + "\n"
							+ ice.getContext().getSubsumers().size()
							+ " subsumers: " + ice.getContext().getSubsumers());
				}
			}
		}
	}
}