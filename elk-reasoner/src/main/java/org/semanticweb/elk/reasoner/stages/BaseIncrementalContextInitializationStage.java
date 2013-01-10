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

import java.util.Collections;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.incremental.IncrementalStages;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.util.collections.Operations;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
abstract class BaseIncrementalContextInitializationStage extends
		AbstractReasonerStage {

	// logger for this class
	static final Logger LOGGER_ = Logger
			.getLogger(BaseIncrementalContextInitializationStage.class);
	/**
	 * The counter for deleted contexts
	 */
	protected int initContexts_;
	/**
	 * The number of contexts
	 */
	protected int maxContexts_;
	
	/**
	 * The state of the iterator of the input to be processed
	 */
	protected Iterator<IndexedClassExpression> todo = null;

	public BaseIncrementalContextInitializationStage(AbstractReasonerState reasoner) {
		super(reasoner);
	}

	@Override
	public String getName() {
		return stage().toString();
	}


	@Override
	public boolean done() {
		return reasoner.incrementalState
				.getStageStatus(stage());
	}

	@Override
	public void execute() throws ElkInterruptedException {

		if (todo == null)
			initComputation();
		try {
			progressMonitor.start(getName());
			for (;;) {
				if (!todo.hasNext())
					break;
				IndexedClassExpression ice = todo.next();
				
				if (ice.getContext() != null) {
					reasoner.saturationState.getWriter().initContext(
							ice.getContext());
				}

				initContexts_++;
				progressMonitor.report(initContexts_, maxContexts_);

				if (interrupted()) {
					continue;
				}
			}
		} finally {
			progressMonitor.finish();
		}
		reasoner.doneContextReset = true;
	}

	@Override
	public void printInfo() {
		if (initContexts_ > 0 && LOGGER_.isDebugEnabled())
			LOGGER_.debug("Contexts init:" + initContexts_);
	}
	
	protected abstract IncrementalStages stage();
}

/**
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
class InitializeContextsAfterDesaturation extends BaseIncrementalContextInitializationStage {

	public InitializeContextsAfterDesaturation(AbstractReasonerState reasoner) {
		super(reasoner);
	}

	@Override
	protected IncrementalStages stage() {
		return IncrementalStages.CONTEXT_AFTER_DEL_INIT;
	}
	
	@Override
	void initComputation() {
		super.initComputation();
		
		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace("Initializing contexts with deleted conclusions: " + reasoner.saturationState.getNotSaturatedContexts());
			LOGGER_.trace("Initializing contexts which will be removed: " + reasoner.saturationState.getContextsToBeRemoved());
		}
		
		todo = Operations.concat(reasoner.saturationState.getNotSaturatedContexts(), reasoner.saturationState.getContextsToBeRemoved()).iterator();
		maxContexts_ = reasoner.saturationState.getNotSaturatedContexts().size() + reasoner.saturationState.getContextsToBeRemoved().size();	
		
		initContexts_ = 0;
	}

	@Override
	public Iterable<ReasonerStage> getDependencies() {
		return Collections.<ReasonerStage>singleton(new IncrementalDeSaturationStage(reasoner));
	}	
}

/**
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
class InitializeContextsAfterCleaning extends BaseIncrementalContextInitializationStage {

	public InitializeContextsAfterCleaning(AbstractReasonerState reasoner) {
		super(reasoner);
	}
	
	@Override
	protected IncrementalStages stage() {
		return IncrementalStages.CONTEXT_AFTER_CLEAN_INIT;
	}
	
	@Override
	void initComputation() {
		super.initComputation();
		
		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace("Cleaned contexts to be initialized: " + reasoner.saturationState.getNotSaturatedContexts());
		}
		
		todo = reasoner.saturationState.getNotSaturatedContexts().iterator();	
		maxContexts_ = reasoner.saturationState.getNotSaturatedContexts().size();
		initContexts_ = 0;
	}
	
	@Override
	public Iterable<ReasonerStage> getDependencies() {
		return Collections.<ReasonerStage>singleton(new IncrementalContextCleaningStage(reasoner));
	}
}