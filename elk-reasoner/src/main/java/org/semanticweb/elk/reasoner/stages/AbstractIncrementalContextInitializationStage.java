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

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.incremental.IncrementalStages;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.CountingConclusionVisitor;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
abstract class AbstractIncrementalContextInitializationStage extends
		AbstractReasonerStage {

	// logger for this class
	static final Logger LOGGER_ = Logger
			.getLogger(AbstractIncrementalContextInitializationStage.class);

	static final boolean COLLECT_CONCLUSION_COUNTS = LOGGER_.isDebugEnabled();

	protected final SaturationStatistics stageStatistics_ = new SaturationStatistics();

	/**
	 * The counter for deleted contexts
	 */
	protected int initContexts;
	/**
	 * The number of contexts
	 */
	protected int maxContexts;

	/**
	 * The state of the iterator of the input to be processed
	 */
	protected Iterator<IndexedClassExpression> todo = null;

	public AbstractIncrementalContextInitializationStage(
			ReasonerStageManager manager) {
		super(manager);
	}

	@Override
	public String getName() {
		return stage().toString();
	}

	@Override
	public boolean done() {
		return reasoner.incrementalState.getStageStatus(stage());
	}

	protected ConclusionVisitor<?> getConclusionVisitor(
			ConclusionStatistics conclusionStatistics) {

		return COLLECT_CONCLUSION_COUNTS ? new CountingConclusionVisitor(
				conclusionStatistics.getProducedConclusionCounts())
				: ConclusionVisitor.DUMMY;
	}

	@Override
	public void executeStage() throws ElkInterruptedException {

		final ConclusionVisitor<?> visitor = getConclusionVisitor(stageStatistics_
				.getConclusionStatistics());
		final SaturationState.ExtendedWriter writer = reasoner.saturationState
				.getExtendedWriter(visitor);

		try {
			progressMonitor.start(getName());
			for (;;) {
				if (!todo.hasNext())
					break;
				IndexedClassExpression ice = todo.next();

				if (ice.getContext() != null) {
					writer.initContext(ice.getContext());
				}

				initContexts++;
				progressMonitor.report(initContexts, maxContexts);

				if (interrupted()) {
					continue;
				}
			}
		} finally {
			progressMonitor.finish();
		}

		reasoner.doneContextReset = true;
		reasoner.ruleAndConclusionStats.add(stageStatistics_);
		todo = null;
	}

	@Override
	public void printInfo() {
		if (initContexts > 0 && LOGGER_.isDebugEnabled())
			LOGGER_.debug("Contexts init:" + initContexts);
	}

	protected abstract IncrementalStages stage();
}