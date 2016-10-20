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
import org.semanticweb.elk.owl.interfaces.ElkAnnotationProperty;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationSubject;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationValue;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDataProperty;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkDataRange;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.interfaces.ElkFacetRestriction;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.owlapi.wrapper.ElkAnnotationPropertyWrap;
import org.semanticweb.elk.owlapi.wrapper.ElkAxiomWrap;
import org.semanticweb.elk.owlapi.wrapper.ElkClassExpressionWrap;
import org.semanticweb.elk.owlapi.wrapper.ElkClassWrap;
import org.semanticweb.elk.owlapi.wrapper.ElkDataPropertyExpressionWrap;
import org.semanticweb.elk.owlapi.wrapper.ElkDataPropertyWrap;
import org.semanticweb.elk.owlapi.wrapper.ElkDataRangeWrap;
import org.semanticweb.elk.owlapi.wrapper.ElkDatatypeWrap;
import org.semanticweb.elk.owlapi.wrapper.ElkEntityWrap;
import org.semanticweb.elk.owlapi.wrapper.ElkFacetRestrictionWrap;
import org.semanticweb.elk.owlapi.wrapper.ElkIndividualWrap;
import org.semanticweb.elk.owlapi.wrapper.ElkLiteralWrap;
import org.semanticweb.elk.owlapi.wrapper.ElkNamedIndividualWrap;
import org.semanticweb.elk.owlapi.wrapper.ElkObjectPropertyExpressionWrap;
import org.semanticweb.elk.owlapi.wrapper.ElkObjectPropertyWrap;
import org.semanticweb.elk.reasoner.ElkFreshEntitiesException;
import org.semanticweb.elk.reasoner.ElkInconsistentOntologyException;
import org.semanticweb.elk.reasoner.stages.ElkInterruptedException;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.reasoner.FreshEntitiesException;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import org.semanticweb.owlapi.reasoner.ReasonerInternalException;
import org.semanticweb.owlapi.reasoner.ReasonerInterruptedException;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNode;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLNamedIndividualNode;
import org.semanticweb.owlapi.reasoner.impl.OWLNamedIndividualNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLObjectPropertyNode;
import org.semanticweb.owlapi.reasoner.impl.OWLObjectPropertyNodeSet;

/**
 * Facade class for conversion from ELK objects to OWL API objects.
 * 
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 * @author Peter Skocovsky
 */
public class ElkConverter extends AbstractElkObjectConverter {

	private static ElkConverter INSTANCE_ = new ElkConverter();

	public static ElkConverter getInstance() {
		return INSTANCE_;
	}

	private ElkConverter() {
	}

	@Override
	public OWLAnnotationProperty convert(ElkAnnotationProperty input) {
		if (input instanceof ElkAnnotationPropertyWrap<?>) {
			return ((ElkAnnotationPropertyWrap<?>) input).getOwlObject();
		}
		// else
		return visit(input);
	}

	@Override
	public OWLAnnotationSubject convert(ElkAnnotationSubject input) {
		return (OWLAnnotationSubject) input.accept(this);
	}

	@Override
	public OWLAnnotationValue convert(ElkAnnotationValue input) {
		return (OWLAnnotationValue) input.accept(this);
	}

	@Override
	public OWLAxiom convert(ElkAxiom input) {
		if (input instanceof ElkAxiomWrap<?>) {
			return ((ElkAxiomWrap<?>) input).getOwlObject();
		}
		// else
		return (OWLAxiom) input.accept(this);
	}

	@Override
	public OWLClass convert(ElkClass input) {
		if (input instanceof ElkClassWrap<?>) {
			return ((ElkClassWrap<?>) input).getOwlObject();
		}
		// else
		return visit(input);
	}

	@Override
	public OWLClassExpression convert(ElkClassExpression input) {
		if (input instanceof ElkClassExpressionWrap<?>) {
			return ((ElkClassExpressionWrap<?>) input).getOwlObject();
		}
		// else
		return (OWLClassExpression) input.accept(this);
	}

	@Override
	public OWLDataProperty convert(ElkDataProperty input) {
		if (input instanceof ElkDataPropertyWrap<?>) {
			return ((ElkDataPropertyWrap<?>) input).getOwlObject();
		}
		// else
		return visit(input);
	}

	@Override
	public OWLDataPropertyExpression convert(ElkDataPropertyExpression input) {
		if (input instanceof ElkDataPropertyExpressionWrap<?>) {
			return ((ElkDataPropertyExpressionWrap<?>) input).getOwlObject();
		}
		// else
		return (OWLDataPropertyExpression) input.accept(this);
	}

	@Override
	public OWLDataRange convert(ElkDataRange input) {
		if (input instanceof ElkDataRangeWrap<?>) {
			return ((ElkDataRangeWrap<?>) input).getOwlObject();
		}
		// else
		return (OWLDataRange) input.accept(this);
	}

	@Override
	public OWLDatatype convert(ElkDatatype input) {
		if (input instanceof ElkDatatypeWrap<?>) {
			return ((ElkDatatypeWrap<?>) input).getOwlObject();
		}
		// else
		return visit(input);
	}

	@Override
	public OWLEntity convert(ElkEntity input) {
		if (input instanceof ElkEntityWrap<?>) {
			return ((ElkEntityWrap<?>) input).getOwlObject();
		}
		// else
		return (OWLEntity) input.accept(this);
	}

	// TODO: perhaps convert using some visitor
	public OWLRuntimeException convert(ElkException e) {
		if (e instanceof ElkFreshEntitiesException)
			return convert((ElkFreshEntitiesException) e);
		else if (e instanceof ElkInconsistentOntologyException)
			return convert((ElkInconsistentOntologyException) e);
		else if (e instanceof ElkInterruptedException)
			return convert((ElkInterruptedException) e);
		else
			return new ReasonerInterruptedException(e);
	}

	@Override
	public OWLFacetRestriction convert(ElkFacetRestriction input) {
		if (input instanceof ElkFacetRestrictionWrap<?>) {
			return ((ElkFacetRestrictionWrap<?>) input).getOwlObject();
		}
		// else
		return visit(input);
	}

	public FreshEntitiesException convert(ElkFreshEntitiesException e) {
		HashSet<OWLEntity> owlEntities = new HashSet<OWLEntity>();
		for (ElkEntity elkEntity : e.getEntities()) {
			owlEntities.add(convert(elkEntity));
		}
		return new FreshEntitiesException(owlEntities);
	}

	public InconsistentOntologyException convert(
			ElkInconsistentOntologyException e) {
		return new InconsistentOntologyException();
	}

	@Override
	public OWLIndividual convert(ElkIndividual input) {
		if (input instanceof ElkIndividualWrap<?>) {
			return ((ElkIndividualWrap<?>) input).getOwlObject();
		}
		// else
		return (OWLIndividual) input.accept(this);
	}

	public ReasonerInterruptedException convert(ElkInterruptedException e) {
		return new ReasonerInterruptedException((ElkInterruptedException) e);
	}

	@Override
	public IRI convert(ElkIri input) {
		return (IRI) input.accept(this);
	}

	@Override
	public OWLLiteral convert(ElkLiteral input) {
		if (input instanceof ElkLiteralWrap<?>) {
			return ((ElkLiteralWrap<?>) input).getOwlObject();
		}
		// else
		return visit(input);
	}

	@Override
	public OWLNamedIndividual convert(ElkNamedIndividual input) {
		if (input instanceof ElkNamedIndividualWrap<?>) {
			return ((ElkNamedIndividualWrap<?>) input).getOwlObject();
		}
		// else
		return visit(input);
	}

	@Override
	public OWLObjectProperty convert(ElkObjectProperty input) {
		if (input instanceof ElkObjectPropertyWrap<?>) {
			return ((ElkObjectPropertyWrap<?>) input).getOwlObject();
		}
		// else
		return visit(input);
	}

	@Override
	public OWLObjectPropertyExpression convert(
			ElkObjectPropertyExpression input) {
		if (input instanceof ElkObjectPropertyExpressionWrap<?>) {
			return ((ElkObjectPropertyExpressionWrap<?>) input).getOwlObject();
		}
		// else
		return (OWLObjectPropertyExpression) input.accept(this);
	}

	public OWLRuntimeException convert(ElkRuntimeException e) {
		return new ReasonerInternalException(e);
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

	public OWLObjectPropertyNode convertObjectPropertyNode(
			final Node<ElkObjectProperty> node) {
		final Set<OWLObjectPropertyExpression> owlObjectProps = new HashSet<OWLObjectPropertyExpression>();
		for (final ElkObjectProperty cls : node) {
			owlObjectProps.add(convert(cls));
		}
		return new OWLObjectPropertyNode(owlObjectProps);
	}

	public OWLObjectPropertyNodeSet convertObjectPropertyNodes(
			final Iterable<? extends Node<ElkObjectProperty>> nodes) {
		Set<org.semanticweb.owlapi.reasoner.Node<OWLObjectPropertyExpression>> owlNodes = new HashSet<org.semanticweb.owlapi.reasoner.Node<OWLObjectPropertyExpression>>();
		for (final Node<ElkObjectProperty> node : nodes) {
			owlNodes.add(convertObjectPropertyNode(node));
		}
		return new OWLObjectPropertyNodeSet(owlNodes);
	}

}
