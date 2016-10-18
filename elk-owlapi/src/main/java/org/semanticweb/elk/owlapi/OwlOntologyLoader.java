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

import org.semanticweb.elk.loading.AbstractAxiomLoader;
import org.semanticweb.elk.loading.AxiomLoader;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.owlapi.wrapper.OwlConverter;
import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.util.concurrent.computation.InterruptMonitor;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An {@link AxiomLoader} that loads a given {@link OWLOntology} through
 * {@link OwlConverter}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class OwlOntologyLoader extends AbstractAxiomLoader implements
		AxiomLoader {

	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(OwlOntologyLoader.class);

	/**
	 * the converter used to convert OWL axioms into ELK axioms
	 */
	private static final OwlConverter OWL_CONVERTER_ = OwlConverter
			.getInstance();
	/**
	 * the ontology to be loaded
	 */
	private final OWLOntology owlOntology_;
	/**
	 * the monitor to report progress of operations
	 */
	private final ProgressMonitor progressMonitor_;
	/**
	 * the status of the progress monitor
	 */
	private String status;

	/**
	 * the state of the iterator over ontologies in the import closure
	 */
	private Iterator<OWLOntology> importsClosureIterator_;
	/**
	 * the number of ontologies in the import closure
	 */
	private int importsClosureCount_;
	/**
	 * the current number of processed import closures
	 */
	private int importsClosureProcessed_;

	/**
	 * the state of the iterator over axioms of an ontology
	 */
	private Iterator<OWLAxiom> axiomsIterator_;
	/**
	 * the total number of axioms to iterate over
	 */
	private int axiomsCount_;
	/**
	 * the current number of processed axioms
	 */
	private int axiomsProcessed_;

	public OwlOntologyLoader(final InterruptMonitor interrupter,
			OWLOntology owlOntology, ProgressMonitor progressMonitor) {
		super(interrupter);
		this.owlOntology_ = owlOntology;
		this.progressMonitor_ = progressMonitor;
		initImportsClosure();
	}

	@Override
	public void load(ElkAxiomProcessor axiomInserter,
			ElkAxiomProcessor axiomDeleter) {
		progressMonitor_.start(status);
		
		LOGGER_.trace("{}", status);
			
		for (;;) {
			if (isInterrupted())
				break;
			if (!axiomsIterator_.hasNext()) {
				importsClosureProcessed_++;
				if (!importsClosureIterator_.hasNext())
					break;
				progressMonitor_.finish();
				updateStatus();
				progressMonitor_.start(status);
				
				LOGGER_.trace("{}", status);
				
				initAxioms(importsClosureIterator_.next());
				continue;
			}
			
			OWLAxiom axiom = axiomsIterator_.next();

			LOGGER_.trace("loading {}", axiom);
			
			if (OWL_CONVERTER_.isRelevantAxiom(axiom))
				axiomInserter.visit(OWL_CONVERTER_.convert(axiom));
			
			axiomsProcessed_++;
			progressMonitor_.report(axiomsProcessed_, axiomsCount_);
		}
		progressMonitor_.finish();
	}

	@Override
	public boolean isLoadingFinished() {
		return axiomsIterator_ != null && !axiomsIterator_.hasNext()
				&& importsClosureIterator_ != null
				&& !importsClosureIterator_.hasNext();
	}

	@Override
	public void dispose() {
		importsClosureIterator_ = null;
		importsClosureProcessed_ = 0;
		axiomsIterator_ = null;
		axiomsProcessed_ = 0;
	}

	private void initImportsClosure() {
		Set<OWLOntology> importsClosure = owlOntology_.getImportsClosure();
		importsClosureIterator_ = importsClosure.iterator();
		importsClosureCount_ = importsClosure.size();
		importsClosureProcessed_ = 0;
		updateStatus();
		if (importsClosureIterator_.hasNext())
			initAxioms(importsClosureIterator_.next());
		else
			axiomsIterator_ = Collections.<OWLAxiom> emptySet().iterator();
	}

	private void initAxioms(OWLOntology ontology) {
		Set<OWLAxiom> axioms = ontology.getAxioms();
		axiomsIterator_ = axioms.iterator();
		axiomsCount_ = axioms.size();
		axiomsProcessed_ = 0;
	}

	private void updateStatus() {
		if (importsClosureCount_ == 1)
			status = ReasonerProgressMonitor.LOADING;
		else
			status = ReasonerProgressMonitor.LOADING + " "
					+ (importsClosureProcessed_ + 1) + " of "
					+ importsClosureCount_;
	}

	public static class Factory implements AxiomLoader.Factory {

		private final OWLOntology owlOntology_;

		private final ProgressMonitor progressMonitor_;

		public Factory(final OWLOntology owlOntology,
				final ProgressMonitor progressMonitor) {
			this.owlOntology_ = owlOntology;
			this.progressMonitor_ = progressMonitor;
		}

		@Override
		public OwlOntologyLoader getAxiomLoader(
				final InterruptMonitor interrupter) {
			return new OwlOntologyLoader(interrupter, owlOntology_,
					progressMonitor_);
		}

	}

}
