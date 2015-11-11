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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * For now just checks that all classes have themselves as subsumers in their contexts.
 * 
 * Also checks for emptiness of ToDo for all contexts
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class CheckContextInvariants extends BasePostProcessingStage {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(CheckContextInvariants.class);

	private final AbstractReasonerState reasoner_;

	public CheckContextInvariants(AbstractReasonerState reasoner) {
		reasoner_ = reasoner;
	}

	@Override
	public String getName() {
		return "Checking subsumers of classes for modified taxonomy nodes";
	}

	@Override
	public void execute() throws ElkException {
		// check roots for all contexts now
		for (Context context : reasoner_.saturationState.getContexts()) {
			if (!context.getComposedSubsumers().contains(context.getRoot())) {
				LOGGER_.error(context.getRoot() + (context.isSaturated() ? " [saturated]" : " [modified]") + ": not a subsumer of itself");
			}
			
			ClassConclusion conclusion = context.takeToDo(); 
			
			if (conclusion != null) {
				LOGGER_.error(context.getRoot() + ": non-empty TODO: " + conclusion);
			}
		}
	}

}
