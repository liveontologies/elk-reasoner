/*
 * #%L
 * ELK OWL API Binding
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.owlapi.converter;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEntityVisitorEx;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

/**
 * Converting instances of {@link OWLEntity} to the corresponding instances of
 * {@link ElkEntity}
 * 
 * @author Yevgeny Kazakov
 * 
 */
public class ElkEntityConverterVisitor implements OWLEntityVisitorEx<ElkEntity> {

	private static ElkEntityConverterVisitor instance_ = new ElkEntityConverterVisitor();

	private ElkEntityConverterVisitor() {
	}
	
	public static ElkEntityConverterVisitor getInstance() {
		return instance_;
	}

	public ElkEntity visit(OWLClass cls) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkEntity visit(OWLObjectProperty property) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkEntity visit(OWLDataProperty property) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkEntity visit(OWLNamedIndividual individual) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkEntity visit(OWLDatatype datatype) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkEntity visit(OWLAnnotationProperty property) {
		// TODO Auto-generated method stub
		return null;
	}

}
