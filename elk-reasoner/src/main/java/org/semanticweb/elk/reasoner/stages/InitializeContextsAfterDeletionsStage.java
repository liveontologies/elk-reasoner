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

import org.semanticweb.elk.reasoner.incremental.IncrementalStages;

/**
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
class InitializeContextsAfterDeletionsStage extends
		AbstractIncrementalContextInitializationStage {

	public InitializeContextsAfterDeletionsStage(
			AbstractReasonerState reasoner, AbstractReasonerStage... preStages) {
		super(reasoner, preStages);
	}

	@Override
	protected IncrementalStages stage() {
		return IncrementalStages.CONTEXT_AFTER_DEL_INIT;
	}

	@Override
	public boolean preExecute() {
		if (!super.preExecute())
			return false;

		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace("Initializing contexts with deleted conclusions: "
					+ reasoner.saturationState.getNotSaturatedContexts());
		}

		this.todo = reasoner.saturationState.getNotSaturatedContexts()
				.iterator();
		this.maxContexts = reasoner.saturationState.getNotSaturatedContexts()
				.size();
		this.initContexts = 0;
		return true;
	}

	@Override
	public boolean postExecute() {
		if (!super.postExecute())
			return false;
		this.todo = null;
		return true;
	}

}
