/*
 * #%L
 * ELK OWL API Binding
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
package org.semanticweb.elk.owlapi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.elk.loading.LoadingException;
import org.semanticweb.elk.loading.OntologyChangesProvider;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.owlapi.wrapper.OwlConverter;
import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.util.logging.Statistics;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;

/**
 * An {@link OntologyChangesProvider} that accumulates the
 * {@link OWLOntologyChange} and provides them by converting through
 * {@link OwlConverter}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class OwlOntologyChangesProvider implements OntologyChangesProvider {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(OwlOntologyChangesProvider.class);

	private static final OwlConverter OWL_CONVERTER_ = OwlConverter
			.getInstance();

	private final ProgressMonitor progressMonitor;

	/** list to accumulate the unprocessed changes to the ontology */
	protected final List<OWLOntologyChange> pendingChanges;

	OwlOntologyChangesProvider(ProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
		this.pendingChanges = new ArrayList<OWLOntologyChange>();
	}

	@Override
	public void accept(ElkAxiomProcessor axiomInserter,
			ElkAxiomProcessor axiomDeleter) throws LoadingException {
		if (!pendingChanges.isEmpty()) {
			String status = ReasonerProgressMonitor.LOADING;
			Statistics.logOperationStart(status, LOGGER_);
			progressMonitor.start(status);
			int axiomCount = pendingChanges.size();
			int currentAxiom = 0;
			for (OWLOntologyChange change : pendingChanges) {
				if (change instanceof AddAxiom)
					axiomInserter.visit(OWL_CONVERTER_.convert(change
							.getAxiom()));
				else if (change instanceof RemoveAxiom) {
					axiomDeleter
							.visit(OWL_CONVERTER_.convert(change.getAxiom()));
				}
				currentAxiom++;
				progressMonitor.report(currentAxiom, axiomCount);
			}
			progressMonitor.finish();
			Statistics.logOperationFinish(status, LOGGER_);
			pendingChanges.clear();
		}

	}

	void registerChange(OWLOntologyChange change) {
		OWLAxiom axiom = change.getAxiom();
		if (OwlAxiomConverter.isRelevantAxiom(axiom))
			pendingChanges.add(change);
	}

	Set<OWLAxiom> getPendingAxiomAdditions() {
		Set<OWLAxiom> added = new HashSet<OWLAxiom>();
		for (OWLOntologyChange change : pendingChanges) {
			if (change instanceof AddAxiom) {
				added.add(change.getAxiom());
			}
		}
		return added;
	}

	Set<OWLAxiom> getPendingAxiomRemovals() {
		Set<OWLAxiom> removed = new HashSet<OWLAxiom>();
		for (OWLOntologyChange change : pendingChanges) {
			if (change instanceof RemoveAxiom) {
				removed.add(change.getAxiom());
			}
		}
		return removed;
	}

	List<OWLOntologyChange> getPendingChanges() {
		return pendingChanges;
	}

}
