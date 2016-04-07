/*
 * #%L
 * ELK OWL API
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
/**
 * @author Yevgeny Kazakov, Jul 1, 2011
 */
package org.semanticweb.elk.owlapi;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.exceptions.ElkRuntimeException;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNode;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLNamedIndividualNode;
import org.semanticweb.owlapi.reasoner.impl.OWLNamedIndividualNodeSet;

/**
 * Facade class for conversion from ELK objects to OWL API objects.
 * 
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 */
public class ElkConverter {

	private static ElkConverter INSTANCE_ = new ElkConverter();

	private ElkConverter() {
	}

	public static ElkConverter getInstance() {
		return INSTANCE_;
	}

	protected static ElkEntityConverter ELK_ENTITY_CONVERTER = ElkEntityConverter
			.getInstance();

	protected static ElkExceptionConverter ELK_EXCEPTION_CONVERTER = ElkExceptionConverter
			.getInstance();

	@SuppressWarnings("static-method")
	public OWLClass convert(ElkClass cls) {
		return ELK_ENTITY_CONVERTER.visit(cls);
	}

	@SuppressWarnings("static-method")
	public OWLNamedIndividual convert(ElkNamedIndividual ind) {
		return ELK_ENTITY_CONVERTER.visit(ind);
	}

	public OWLClassNode convertClassNode(Node<ElkClass> node) {
		Set<OWLClass> owlClasses = new HashSet<OWLClass>();
		for (ElkClass cls : node) {
			owlClasses.add(convert(cls));
		}
		return new OWLClassNode(owlClasses);
	}

	public OWLClassNodeSet convertClassNodes(
			Iterable<? extends Node<ElkClass>> nodes) {
		Set<org.semanticweb.owlapi.reasoner.Node<OWLClass>> owlNodes = new HashSet<org.semanticweb.owlapi.reasoner.Node<OWLClass>>();
		for (Node<ElkClass> node : nodes) {
			owlNodes.add(convertClassNode(node));
		}
		return new OWLClassNodeSet(owlNodes);
	}

	public OWLNamedIndividualNode convertIndividualNode(
			Node<ElkNamedIndividual> node) {
		Set<OWLNamedIndividual> owlIndividuals = new HashSet<OWLNamedIndividual>();
		for (ElkNamedIndividual ind : node) {
			owlIndividuals.add(convert(ind));
		}
		return new OWLNamedIndividualNode(owlIndividuals);
	}

	public OWLNamedIndividualNodeSet convertIndividualNodes(
			Iterable<? extends Node<ElkNamedIndividual>> nodes) {
		Set<org.semanticweb.owlapi.reasoner.Node<OWLNamedIndividual>> owlNodes = new HashSet<org.semanticweb.owlapi.reasoner.Node<OWLNamedIndividual>>();
		for (Node<ElkNamedIndividual> node : nodes) {
			owlNodes.add(convertIndividualNode(node));
		}
		return new OWLNamedIndividualNodeSet(owlNodes);
	}

	@SuppressWarnings("static-method")
	public OWLRuntimeException convert(ElkException e) {
		return ELK_EXCEPTION_CONVERTER.convert(e);
	}

	@SuppressWarnings("static-method")
	public OWLRuntimeException convert(ElkRuntimeException e) {
		return ELK_EXCEPTION_CONVERTER.convert(e);
	}

}
