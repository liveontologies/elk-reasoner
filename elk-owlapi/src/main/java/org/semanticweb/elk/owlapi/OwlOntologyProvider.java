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
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.elk.loading.OntologyProvider;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.owlapi.wrapper.OwlConverter;
import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.util.logging.Statistics;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;

/**
 * An {@link OntologyProvider} that converts a given {@link OWLOntology} through
 * {@link OwlConverter}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class OwlOntologyProvider implements OntologyProvider {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(OwlOntologyProvider.class);

	private static final OwlConverter OWL_CONVERTER_ = OwlConverter
			.getInstance();

	private final ProgressMonitor progressMonitor;

	private final OWLOntology owlOntology;

	/** list to accumulate the unprocessed changes to the ontology */
	protected final List<OWLOntologyChange> pendingChanges;

	public OwlOntologyProvider(OWLOntology owlOntology,
			ProgressMonitor progressMonitor) {
		this.owlOntology = owlOntology;
		this.progressMonitor = progressMonitor;
		this.pendingChanges = new ArrayList<OWLOntologyChange>();
	}

	@Override
	public void accept(ElkAxiomProcessor ontologyVisitor) {
		Set<OWLOntology> importsClosure = owlOntology.getImportsClosure();
		int ontCount = importsClosure.size();
		int currentOntology = 0;
		for (OWLOntology ont : importsClosure) {
			currentOntology++;
			String status;
			if (ontCount == 1)
				status = ReasonerProgressMonitor.LOADING;
			else
				status = ReasonerProgressMonitor.LOADING + " "
						+ currentOntology + " of " + ontCount;
			Statistics.logOperationStart(status, LOGGER_);
			progressMonitor.start(status);
			Set<OWLAxiom> axioms = ont.getAxioms();
			int axiomCount = axioms.size();
			int currentAxiom = 0;
			for (OWLAxiom axiom : axioms) {
				currentAxiom++;
				if (OwlAxiomConverter.isRelevantAxiom(axiom))
					ontologyVisitor.visit(OWL_CONVERTER_.convert(axiom));
				progressMonitor.report(currentAxiom, axiomCount);
			}
			progressMonitor.finish();
			Statistics.logOperationFinish(status, LOGGER_);
		}
	}
}
