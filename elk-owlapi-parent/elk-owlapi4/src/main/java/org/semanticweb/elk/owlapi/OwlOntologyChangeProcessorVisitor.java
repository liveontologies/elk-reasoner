package org.semanticweb.elk.owlapi;
/*
 * #%L
 * ELK OWL API Binding
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

import org.semanticweb.elk.loading.ElkLoadingException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.owlapi.wrapper.OwlConverter;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.AddOntologyAnnotation;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeVisitor;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.model.RemoveImport;
import org.semanticweb.owlapi.model.RemoveOntologyAnnotation;
import org.semanticweb.owlapi.model.SetOntologyID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class OwlOntologyChangeProcessorVisitor implements
		OWLOntologyChangeVisitor {

	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(OwlOntologyChangeProcessorVisitor.class);

	private static final OwlConverter OWL_CONVERTER_ = OwlConverter
			.getInstance();

	private final ElkAxiomProcessor axiomInserter_, axiomDeleter_;
	
	private ElkLoadingException error_ = null;

	OwlOntologyChangeProcessorVisitor(ElkAxiomProcessor axiomInserter,
			ElkAxiomProcessor axiomDeleter) {
		this.axiomInserter_ = axiomInserter;
		this.axiomDeleter_ = axiomDeleter;
	}

	protected void defaultVisit(OWLOntologyChange change) {
		error_ = new ElkLoadingException(
				"Ontology change " + change.toString() + " is not supported");
	}
	
	public ElkLoadingException getError() {
		return error_;
	}

	@Override
	public void visit(RemoveAxiom arg) {
		ElkAxiom elkAxiom = OWL_CONVERTER_.convert(arg.getAxiom());

		axiomDeleter_.visit(elkAxiom);

		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("removing " + arg.getAxiom());

	}

	@Override
	public void visit(AddAxiom arg) {
		ElkAxiom elkAxiom = OWL_CONVERTER_.convert(arg.getAxiom());

		axiomInserter_.visit(elkAxiom);

		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("adding " + arg.getAxiom());

	}

	@Override
	public void visit(SetOntologyID change) {
		defaultVisit(change);
	}

	@Override
	public void visit(AddImport change) {
		defaultVisit(change);
	}

	@Override
	public void visit(RemoveImport change) {
		defaultVisit(change);
	}

	@Override
	public void visit(AddOntologyAnnotation change) {
		defaultVisit(change);
	}

	@Override
	public void visit(RemoveOntologyAnnotation change) {
		defaultVisit(change);
	}

}
