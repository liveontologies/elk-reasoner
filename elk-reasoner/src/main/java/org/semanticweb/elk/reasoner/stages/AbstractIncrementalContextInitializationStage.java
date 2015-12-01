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

import org.semanticweb.elk.reasoner.incremental.IncrementalStages;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.SaturationUtils;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.SaturationConclusionBaseFactory;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ContextInitialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
abstract class AbstractIncrementalContextInitializationStage extends
		AbstractReasonerStage {

	// logger for this class
	static final Logger LOGGER_ = LoggerFactory
			.getLogger(AbstractIncrementalContextInitializationStage.class);

	protected SaturationStatistics stageStatistics_ = null;

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
	protected Iterator<IndexedContextRoot> todo = null;

	private SaturationStateWriter<?> writer_ = null;
	
	private ContextInitialization.Factory factory_ = new SaturationConclusionBaseFactory();

	public AbstractIncrementalContextInitializationStage(
			AbstractReasonerState reasoner, AbstractReasonerStage... preStages) {
		super(reasoner, preStages);
	}

	@Override
	public String getName() {
		return stage().toString();
	}

	@Override
	public boolean preExecute() {
		if (!super.preExecute())
			return false;
		this.stageStatistics_ = new SaturationStatistics();
		this.writer_ = SaturationUtils.getStatsAwareWriter(
				reasoner.saturationState.getContextCreatingWriter(),
				stageStatistics_);
		return true;
	}

	@Override
	public void executeStage() throws ElkInterruptedException {
		for (;;) {
			if (isInterrupted())
				return;
			if (!todo.hasNext())
				break;
			IndexedContextRoot root = todo.next();

			ClassConclusion init = factory_.getContextInitialization(root,
					reasoner.saturationState.getOntologyIndex());

			if (reasoner.saturationState.getContext(root) != null) {
				writer_.produce(init);
			}

			initContexts++;
			progressMonitor.report(initContexts, maxContexts);

		}
	}

	@Override
	public boolean postExecute() {
		if (!super.postExecute())
			return false;
		reasoner.ruleAndConclusionStats.add(stageStatistics_);
		this.stageStatistics_ = null;
		this.todo = null;
		this.writer_ = null;
		return true;
	}

	@Override
	public void printInfo() {
		if (initContexts > 0 && LOGGER_.isDebugEnabled())
			LOGGER_.debug("Contexts init:" + initContexts);
	}

	protected abstract IncrementalStages stage();
}
