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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.semanticweb.elk.loading.AbstractAxiomLoader;
import org.semanticweb.elk.loading.AxiomLoader;
import org.semanticweb.elk.loading.ElkLoadingException;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.owlapi.wrapper.OwlConverter;
import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An {@link AxiomLoader} that accumulates the {@link OWLOntologyChange} and
 * provides them by converting through {@link OwlConverter}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class OwlChangesLoader extends AbstractAxiomLoader implements
		AxiomLoader {

	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(OwlChangesLoader.class);

	private final ProgressMonitor progressMonitor;

	/** list to accumulate the unprocessed changes to the ontology */
	private final LinkedList<OWLOntologyChange> pendingChanges_;

	OwlChangesLoader(ProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
		this.pendingChanges_ = new LinkedList<OWLOntologyChange>();
	}

	@Override
	public synchronized void load(final ElkAxiomProcessor axiomInserter,
			final ElkAxiomProcessor axiomDeleter) throws ElkLoadingException {
		if (!pendingChanges_.isEmpty()) {
			String status = "Loading of Changes";
			progressMonitor.start(status);
			int changesCount = pendingChanges_.size();
			
			LOGGER_.trace("{}: {}", status, changesCount);
			
			int currentAxiom = 0;
			for (;;) {
				if (isInterrupted())
					break;
				OWLOntologyChange change = pendingChanges_.poll();
				if (change == null)
					break;
				if (!change.isAxiomChange()) {
					ElkLoadingException exception = new ElkLoadingException(
							"Cannot apply non-axiom change!");
					LOGGER_.error(exception.getMessage(), exception);
					throw exception;
				}

				OwlOntologyChangeProcessorVisitor loader = new OwlOntologyChangeProcessorVisitor(
						axiomInserter, axiomDeleter);

				change.accept(loader);
				ElkLoadingException error = loader.getError();

				if (error != null) {
					LOGGER_.error(error.getMessage(), error);
					throw error;
				}

				currentAxiom++;
				progressMonitor.report(currentAxiom, changesCount);
			}
			progressMonitor.finish();
		}
	}

	@Override
	public synchronized boolean isLoadingFinished() {
		return pendingChanges_.isEmpty();
	}

	synchronized void registerChange(OWLOntologyChange change) {
		LOGGER_.trace("Registering change: {}", change);

		pendingChanges_.add(change);
	}

	Set<OWLAxiom> getPendingAxiomAdditions() {
		Set<OWLAxiom> added = new HashSet<OWLAxiom>();
		for (OWLOntologyChange change : pendingChanges_) {
			if (change instanceof AddAxiom) {
				added.add(change.getAxiom());
			}
		}
		return added;
	}

	Set<OWLAxiom> getPendingAxiomRemovals() {
		Set<OWLAxiom> removed = new HashSet<OWLAxiom>();
		for (OWLOntologyChange change : pendingChanges_) {
			if (change instanceof RemoveAxiom) {
				removed.add(change.getAxiom());
			}
		}
		return removed;
	}

	List<OWLOntologyChange> getPendingChanges() {
		return pendingChanges_;
	}

}
