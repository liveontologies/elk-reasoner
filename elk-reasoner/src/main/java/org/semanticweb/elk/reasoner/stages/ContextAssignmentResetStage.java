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

import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ReasonerStage} ensuring that no context is assigned to
 * {@link IndexedClassExpression}s of the current ontology.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
class ContextAssignmentResetStage extends AbstractReasonerStage {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ContextAssignmentResetStage.class);

	/**
	 * The number of contexts before the stage
	 */
	private int contextCountBefore_ = 0;

	public ContextAssignmentResetStage(AbstractReasonerState reasoner,
			AbstractReasonerStage... preStages) {
		super(reasoner, preStages);
	}

	@Override
	public String getName() {
		return "Context Reset";
	}

	@Override
	public boolean preExecute() {
		if (!super.preExecute())
			return false;
		contextCountBefore_ = reasoner.saturationState.getContexts().size();
		return true;
	}

	@Override
	public void executeStage() throws ElkInterruptedException {
		SaturationStateWriter<?> writer = reasoner.saturationState
				.getContextModifyingWriter();
		writer.resetContexts();
	}

	@Override
	public boolean postExecute() {
		if (!super.postExecute())
			return false;
		return true;
	}

	@Override
	public void printInfo() {
		int contexResetCount = reasoner.saturationState.getContexts().size()
				- contextCountBefore_;
		if (contexResetCount > 0)
			LOGGER_.debug("Contexts deleted: {}", contexResetCount);
	}

}
