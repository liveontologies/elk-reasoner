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

import org.semanticweb.elk.owl.interfaces.ElkAnnotationProperty;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkDataProperty;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.visitors.ElkEntityVisitor;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;

/**
 * Converter from ElkEntities to OWL API entities.
 * 
 * @author Markus Kroetzsch
 * 
 */
public final class ElkEntityConverter implements
		ElkEntityVisitor<OWLEntity> {
	
	final OWLDataFactory owlDataFactory = OWLManager.getOWLDataFactory();
	
	private static ElkEntityConverter INSTANCE_ = new ElkEntityConverter();
	
	private ElkEntityConverter() {
	}
	
	public static ElkEntityConverter getInstance() {
		return INSTANCE_;
	}
	

	@Override
	public OWLEntity visit(ElkAnnotationProperty elkAnnotationProperty) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OWLClass visit(ElkClass elkClass) {
		String iri = elkClass.getIri().asString();
		return owlDataFactory.getOWLClass(IRI.create(iri));
	}

	@Override
	public OWLEntity visit(ElkDataProperty elkDataProperty) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OWLEntity visit(ElkDatatype elkDatatype) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OWLEntity visit(ElkNamedIndividual elkNamedIndividual) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OWLEntity visit(ElkObjectProperty elkObjectProperty) {
		// TODO Auto-generated method stub
		return null;
	}

}
