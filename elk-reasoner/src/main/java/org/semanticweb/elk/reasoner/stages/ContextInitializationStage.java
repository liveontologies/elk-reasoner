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
import java.util.List;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;

public class ContextInitializationStage extends AbstractReasonerStage {

	public ContextInitializationStage(ReasonerState reasoner) {
		super(reasoner);
	}

	@Override
	public String getName() {
		return "Context Initialization";
	}

	@Override
	public boolean done() {
		return reasoner.doneContextInitialization;
	}

	@Override
	public List<ReasonerStage> getDependencies() {
		return Collections.emptyList();
	}

	@Override
	public void execute() {
		for (IndexedClassExpression ice : reasoner.ontologyIndex
				.getIndexedClassExpressions())
			ice.resetContext();
		if (isInterrupted())
			return;
		reasoner.doneContextInitialization = true;
	}

	@Override
	public void printInfo() {
	}

}
