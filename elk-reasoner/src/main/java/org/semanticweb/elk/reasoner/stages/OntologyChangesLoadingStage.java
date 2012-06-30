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

import java.util.Arrays;

import org.semanticweb.elk.loading.LoadingException;
import org.semanticweb.elk.loading.OntologyChangesProvider;

/**
 * A {@link ReasonerStage} during which ontology changes are applied to the
 * reasoner.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class OntologyChangesLoadingStage extends AbstractReasonerStage {

	public OntologyChangesLoadingStage(AbstractReasonerState reasoner) {
		super(reasoner);
	}

	@Override
	public String getName() {
		return "Loading Changes";
	}

	@Override
	public boolean done() {
		return reasoner.doneChangeLoading;
	}

	@Override
	public Iterable<ReasonerStage> getDependencies() {
		return Arrays
				.asList((ReasonerStage) new OntologyLoadingStage(reasoner));
	}

	@Override
	public void execute() {
		initComputation();
		OntologyChangesProvider ontologyChangesProvider = reasoner
				.getOntologyChangesProvider();
		try {
			ontologyChangesProvider.accept(
					reasoner.ontologyIndex.getInserter(),
					reasoner.ontologyIndex.getDeleter());
			if (isInterrupted())
				return;
			reasoner.doneChangeLoading = true;
		} catch (LoadingException e) {
			// TODO: Do something about it
			e.printStackTrace();
		}
	}

	@Override
	public void printInfo() {
	}

}
