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

import org.semanticweb.elk.loading.IncrementalOntologyProvider;
import org.semanticweb.elk.loading.LoadingException;

/**
 * The reasoner stage, during which the ontology changes are applied to the
 * reasoner. Loading of axioms is typically done in incremental way.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class OntologyLoadingStage extends AbstractReasonerStage {

	public OntologyLoadingStage(AbstractReasonerState reasoner) {
		super(reasoner);
	}

	@Override
	public String getName() {
		return "Loading of Axioms";
	}

	@Override
	public boolean done() {
		return reasoner.doneLoading && reasoner.doneChangeLoading;
	}

	@Override
	public Iterable<ReasonerStage> getDependencies() {
		return Collections.emptyList();
	}

	@Override
	public void execute() {
		initComputation();
		IncrementalOntologyProvider ontologyProvider = reasoner
				.getOntologyProvider();
		try {
			if (!reasoner.doneLoading) {
				ontologyProvider.accept(reasoner.ontologyIndex.getInserter());
				if (isInterrupted())
					return;
				reasoner.doneLoading = true;
			}
			if (!reasoner.doneChangeLoading) {
				ontologyProvider.accept(reasoner.ontologyIndex.getInserter(),
						reasoner.ontologyIndex.getDeleter());
				if (isInterrupted())
					return;
				reasoner.doneChangeLoading = true;
			}
		} catch (LoadingException e) {
			// TODO: Do something about it
			e.printStackTrace();
		}
	}

	@Override
	public void printInfo() {
	}
}
