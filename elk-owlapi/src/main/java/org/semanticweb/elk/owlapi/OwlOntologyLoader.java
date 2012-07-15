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

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.elk.loading.Loader;
import org.semanticweb.elk.loading.OntologyLoader;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.owlapi.wrapper.OwlConverter;
import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;

/**
 * A {@link Loader} that loads a given {@link OWLOntology} through
 * {@link OwlConverter} into an {@link ElkAxiomProcessor}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class OwlOntologyLoader implements OntologyLoader {

	/**
	 * the converter used to convert OWL axioms into ELK axioms
	 */
	private static final OwlConverter OWL_CONVERTER_ = OwlConverter
			.getInstance();
	/**
	 * the ontology to be loaded
	 */
	private final OWLOntology owlOntology;
	/**
	 * the monitor to report progress of operations
	 */
	private final ProgressMonitor progressMonitor;
	/**
	 * the status of the progress monitor
	 */
	private String status;

	/**
	 * the state of the iterator over ontologies in the import closure
	 */
	private Iterator<OWLOntology> importsClosureIterator;
	/**
	 * the number of ontologies in the import closure
	 */
	private int importsClosureCount;
	/**
	 * the current number of processed import closures
	 */
	private int importsClosureProcessed;

	/**
	 * the state of the iterator over axioms of an ontology
	 */
	private Iterator<OWLAxiom> axiomsIterator;
	/**
	 * the total number of axioms to iterate over
	 */
	private int axiomsCount;
	/**
	 * the current number of processed axioms
	 */
	private int axiomsProcessed;

	public OwlOntologyLoader(OWLOntology owlOntology,
			ProgressMonitor progressMonitor) {
		this.owlOntology = owlOntology;
		this.progressMonitor = progressMonitor;
		initImportsClosure();
	}

	@Override
	public Loader getLoader(final ElkAxiomProcessor axiomLoader) {
		return new Loader() {
			@Override
			public void load() {
				progressMonitor.start(status);
				for (;;) {
					if (Thread.currentThread().isInterrupted())
						break;
					if (!axiomsIterator.hasNext()) {
						if (!importsClosureIterator.hasNext())
							break;
						progressMonitor.finish();
						updateStatus();
						progressMonitor.start(status);
						initAxioms(importsClosureIterator.next());
					}
					OWLAxiom axiom = axiomsIterator.next();
					if (OWL_CONVERTER_.isRelevantAxiom(axiom))
						axiomLoader.visit(OWL_CONVERTER_.convert(axiom));
					axiomsProcessed++;
					progressMonitor.report(axiomsProcessed, axiomsCount);
				}
				progressMonitor.finish();
			}

			@Override
			public void dispose() {
				OwlOntologyLoader.this.dispose();
			}
		};
	}

	private void initImportsClosure() {
		Set<OWLOntology> importsClosure = owlOntology.getImportsClosure();
		importsClosureIterator = importsClosure.iterator();
		importsClosureCount = importsClosure.size();
		importsClosureProcessed = 0;
		updateStatus();
		if (importsClosureIterator.hasNext())
			initAxioms(importsClosureIterator.next());
		else
			axiomsIterator = Collections.<OWLAxiom> emptySet().iterator();
	}

	private void initAxioms(OWLOntology ontology) {
		Set<OWLAxiom> axioms = ontology.getAxioms();
		axiomsIterator = axioms.iterator();
		axiomsCount = axioms.size();
		axiomsProcessed = 0;
	}

	private void updateStatus() {
		if (importsClosureCount == 1)
			status = ReasonerProgressMonitor.LOADING;
		else
			status = ReasonerProgressMonitor.LOADING + " "
					+ (importsClosureProcessed + 1) + " of "
					+ importsClosureCount;
	}

	private void dispose() {
		importsClosureIterator = null;
		importsClosureProcessed = 0;
		axiomsIterator = null;
		axiomsProcessed = 0;
	}

}
