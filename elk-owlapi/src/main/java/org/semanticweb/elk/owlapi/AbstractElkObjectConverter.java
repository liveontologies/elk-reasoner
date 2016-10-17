package org.semanticweb.elk.owlapi;

/*-
 * #%L
 * ELK OWL API Binding
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.elk.exceptions.ElkRuntimeException;
import org.semanticweb.elk.owl.interfaces.ElkAnnotation;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationProperty;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationPropertyRangeAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationSubject;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationValue;
import org.semanticweb.elk.owl.interfaces.ElkAnonymousIndividual;
import org.semanticweb.elk.owl.interfaces.ElkAsymmetricObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDataAllValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkDataComplementOf;
import org.semanticweb.elk.owl.interfaces.ElkDataExactCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataExactCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkDataIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkDataMaxCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataMaxCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkDataMinCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataMinCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkDataOneOf;
import org.semanticweb.elk.owl.interfaces.ElkDataProperty;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyRangeAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataRange;
import org.semanticweb.elk.owl.interfaces.ElkDataSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkDataUnionOf;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkDatatypeDefinitionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDatatypeRestriction;
import org.semanticweb.elk.owl.interfaces.ElkDeclarationAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDifferentIndividualsAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointDataPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointUnionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentDataPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkFacetRestriction;
import org.semanticweb.elk.owl.interfaces.ElkFunctionalDataPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkFunctionalObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkHasKeyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkInverseObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkIrreflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkNegativeDataPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectAllValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectComplementOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectExactCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectExactCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasSelf;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasValue;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectInverseOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectMaxCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectMaxCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectMinCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectMinCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectOneOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyChain;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyRangeAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectUnionOf;
import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSWRLRule;
import org.semanticweb.elk.owl.interfaces.ElkSameIndividualAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubAnnotationPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubDataPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSymmetricObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkTransitiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.iris.ElkAbbreviatedIri;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;
import org.semanticweb.elk.owl.visitors.ElkSubObjectPropertyExpressionVisitor;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.vocab.OWLFacet;

/**
 * A prototype {@link ElkObjectVisitor} that converts {@link ElkObject}s to the
 * corresponding {@link OWLObject}s
 * 
 * @author Yevgeny Kazakov
 *
 */
public abstract class AbstractElkObjectConverter
		implements ElkObjectVisitor<OWLObject> {

	/**
	 * the factory used for creating {@link OWLObject}s
	 */
	private final OWLDataFactory owlFactory_;

	/**
	 * Creates an {@link AbstractElkObjectConverter} that uses the default
	 * {@link OWLDataFactory} for creating the corresponding {@link OWLObject}s
	 */
	public AbstractElkObjectConverter() {
		this(OWLManager.getOWLDataFactory());
	}

	/**
	 * Creates an {@link AbstractElkObjectConverter} that uses the given
	 * {@link OWLDataFactory} for creating the corresponding {@link OWLObject}s
	 * 
	 * @param owlFactory
	 *            the {@link OWLDataFactory} using which all corresponding
	 *            {@link OWLObject}s are constructed
	 */
	public AbstractElkObjectConverter(OWLDataFactory owlFactory) {
		this.owlFactory_ = owlFactory;
	}

	abstract OWLAnnotationProperty convert(ElkAnnotationProperty input);

	abstract OWLAnnotationSubject convert(ElkAnnotationSubject input);

	abstract OWLAnnotationValue convert(ElkAnnotationValue input);

	abstract OWLAxiom convert(ElkAxiom input);

	abstract OWLClass convert(ElkClass input);

	abstract OWLClassExpression convert(ElkClassExpression input);

	abstract OWLDataProperty convert(ElkDataProperty input);

	abstract OWLDataPropertyExpression convert(ElkDataPropertyExpression input);

	abstract OWLDataRange convert(ElkDataRange input);

	abstract OWLDatatype convert(ElkDatatype input);

	abstract OWLEntity convert(ElkEntity input);

	abstract OWLFacetRestriction convert(ElkFacetRestriction input);

	abstract OWLIndividual convert(ElkIndividual input);

	abstract IRI convert(ElkIri input);

	abstract OWLLiteral convert(ElkLiteral input);

	abstract OWLNamedIndividual convert(ElkNamedIndividual input);

	abstract OWLObjectProperty convert(ElkObjectProperty input);

	abstract OWLObjectPropertyExpression convert(
			ElkObjectPropertyExpression input);

	Set<OWLClassExpression> toClassExpressionSet(
			List<? extends ElkClassExpression> input) {
		Set<OWLClassExpression> result = new HashSet<OWLClassExpression>(
				input.size());
		for (ElkClassExpression next : input) {
			result.add(convert(next));
		}
		return result;
	}

	Set<OWLDataPropertyExpression> toDataPropertyExpressionSet(
			List<? extends ElkDataPropertyExpression> input) {
		Set<OWLDataPropertyExpression> result = new HashSet<OWLDataPropertyExpression>(
				input.size());
		for (ElkDataPropertyExpression next : input) {
			result.add(convert(next));
		}
		return result;
	}

	Set<OWLDataRange> toDataRangeSet(List<? extends ElkDataRange> input) {
		Set<OWLDataRange> result = new HashSet<OWLDataRange>(input.size());
		for (ElkDataRange next : input) {
			result.add(convert(next));
		}
		return result;
	}

	Set<OWLFacetRestriction> toFacetRestrictionSet(
			List<? extends ElkFacetRestriction> input) {
		Set<OWLFacetRestriction> result = new HashSet<OWLFacetRestriction>(
				input.size());
		for (ElkFacetRestriction next : input) {
			result.add(convert(next));
		}
		return result;
	}

	Set<OWLIndividual> toIndividualSet(List<? extends ElkIndividual> input) {
		Set<OWLIndividual> result = new HashSet<OWLIndividual>(input.size());
		for (ElkIndividual next : input) {
			result.add(convert(next));
		}
		return result;
	}

	Set<OWLLiteral> toLiteralSet(List<? extends ElkLiteral> input) {
		Set<OWLLiteral> result = new HashSet<OWLLiteral>(input.size());
		for (ElkLiteral next : input) {
			result.add(convert(next));
		}
		return result;
	}

	Set<OWLObjectPropertyExpression> toObjectPropertyExpressionSet(
			List<? extends ElkObjectPropertyExpression> input) {
		Set<OWLObjectPropertyExpression> result = new HashSet<OWLObjectPropertyExpression>(
				input.size());
		for (ElkObjectPropertyExpression next : input) {
			result.add(convert(next));
		}
		return result;
	}

	List<? extends OWLObjectPropertyExpression> toPropertyExpressionList(
			List<? extends ElkObjectPropertyExpression> input) {
		List<OWLObjectPropertyExpression> result = new ArrayList<OWLObjectPropertyExpression>(
				input.size());
		for (ElkObjectPropertyExpression next : input) {
			result.add(convert(next));
		}
		return result;
	}

	Set<? extends OWLPropertyExpression> toPropertyExpressionSet(
			List<? extends ElkObjectPropertyExpression> objectProperties,
			List<? extends ElkDataPropertyExpression> dataProperties) {
		Set<OWLPropertyExpression> result = new HashSet<OWLPropertyExpression>(
				objectProperties.size() + dataProperties.size());
		for (ElkObjectPropertyExpression property : objectProperties) {
			result.add(convert(property));
		}
		for (ElkDataPropertyExpression property : dataProperties) {
			result.add(convert(property));
		}
		return result;
	}

	@Override
	public IRI visit(ElkAbbreviatedIri iri) {
		return IRI.create(iri.getPrefix().getIri().toString(),
				iri.getLocalName());
	}

	@Override
	public OWLAnnotation visit(ElkAnnotation annotation) {
		return owlFactory_.getOWLAnnotation(convert(annotation.getProperty()),
				convert(annotation.getValue()));
	}

	@Override
	public OWLAnnotationAssertionAxiom visit(
			ElkAnnotationAssertionAxiom axiom) {
		return owlFactory_.getOWLAnnotationAssertionAxiom(
				convert(axiom.getProperty()), convert(axiom.getSubject()),
				convert(axiom.getValue()));
	}

	@Override
	public OWLAnnotationProperty visit(ElkAnnotationProperty expression) {
		return owlFactory_
				.getOWLAnnotationProperty(convert(expression.getIri()));
	}

	@Override
	public OWLAnnotationPropertyDomainAxiom visit(
			ElkAnnotationPropertyDomainAxiom axiom) {
		return owlFactory_.getOWLAnnotationPropertyDomainAxiom(
				convert(axiom.getProperty()), convert(axiom.getDomain()));
	}

	@Override
	public OWLAnnotationPropertyRangeAxiom visit(
			ElkAnnotationPropertyRangeAxiom axiom) {
		return owlFactory_.getOWLAnnotationPropertyRangeAxiom(
				convert(axiom.getProperty()), convert(axiom.getRange()));
	}

	@Override
	public OWLAnonymousIndividual visit(ElkAnonymousIndividual expression) {
		return owlFactory_.getOWLAnonymousIndividual(expression.getNodeId());
	}

	@Override
	public OWLAsymmetricObjectPropertyAxiom visit(
			ElkAsymmetricObjectPropertyAxiom axiom) {
		return owlFactory_.getOWLAsymmetricObjectPropertyAxiom(
				convert(axiom.getProperty()));
	}

	@Override
	public OWLClass visit(ElkClass expression) {
		return owlFactory_.getOWLClass(convert(expression.getIri()));
	}

	@Override
	public OWLClassAssertionAxiom visit(ElkClassAssertionAxiom axiom) {
		return owlFactory_.getOWLClassAssertionAxiom(
				convert(axiom.getClassExpression()),
				convert(axiom.getIndividual()));
	}

	@Override
	public OWLDataAllValuesFrom visit(ElkDataAllValuesFrom expression) {
		List<? extends ElkDataPropertyExpression> expressions = expression
				.getDataPropertyExpressions();
		if (expressions.size() > 0) {
			throw new IllegalArgumentException(
					"OWLAPI supports only one data property in OWLDataAllValuesFrom");
		}
		return owlFactory_.getOWLDataAllValuesFrom(convert(expressions.get(0)),
				convert(expression.getDataRange()));
	}

	@Override
	public OWLDataComplementOf visit(ElkDataComplementOf expression) {
		return owlFactory_.getOWLDataComplementOf(convert(expression));
	}

	@Override
	public OWLDataExactCardinality visit(
			ElkDataExactCardinalityQualified expression) {
		return owlFactory_.getOWLDataExactCardinality(
				expression.getCardinality(), convert(expression.getProperty()),
				convert(expression.getFiller()));
	}

	@Override
	public OWLDataExactCardinality visit(
			ElkDataExactCardinalityUnqualified expression) {
		return owlFactory_.getOWLDataExactCardinality(
				expression.getCardinality(), convert(expression.getProperty()));
	}

	@Override
	public OWLDataHasValue visit(ElkDataHasValue expression) {
		return owlFactory_.getOWLDataHasValue(convert(expression.getProperty()),
				convert(expression.getFiller()));
	}

	@Override
	public OWLDataIntersectionOf visit(ElkDataIntersectionOf expression) {
		return owlFactory_.getOWLDataIntersectionOf(
				toDataRangeSet(expression.getDataRanges()));
	}

	@Override
	public OWLDataMaxCardinality visit(
			ElkDataMaxCardinalityQualified expression) {
		return owlFactory_.getOWLDataMaxCardinality(expression.getCardinality(),
				convert(expression.getProperty()),
				convert(expression.getFiller()));
	}

	@Override
	public OWLDataMaxCardinality visit(
			ElkDataMaxCardinalityUnqualified expression) {
		return owlFactory_.getOWLDataMaxCardinality(expression.getCardinality(),
				convert(expression.getProperty()));
	}

	@Override
	public OWLDataMinCardinality visit(
			ElkDataMinCardinalityQualified expression) {
		return owlFactory_.getOWLDataMinCardinality(expression.getCardinality(),
				convert(expression.getProperty()),
				convert(expression.getFiller()));
	}

	@Override
	public OWLDataMinCardinality visit(
			ElkDataMinCardinalityUnqualified expression) {
		return owlFactory_.getOWLDataMinCardinality(expression.getCardinality(),
				convert(expression.getProperty()));
	}

	@Override
	public OWLDataOneOf visit(ElkDataOneOf expression) {
		return owlFactory_
				.getOWLDataOneOf(toLiteralSet(expression.getLiterals()));
	}

	@Override
	public OWLDataProperty visit(ElkDataProperty expression) {
		return owlFactory_.getOWLDataProperty(convert(expression.getIri()));
	}

	@Override
	public OWLDataPropertyAssertionAxiom visit(
			ElkDataPropertyAssertionAxiom axiom) {
		return owlFactory_.getOWLDataPropertyAssertionAxiom(
				convert(axiom.getProperty()), convert(axiom.getSubject()),
				convert(axiom.getObject()));
	}

	@Override
	public OWLDataPropertyDomainAxiom visit(ElkDataPropertyDomainAxiom axiom) {
		return owlFactory_.getOWLDataPropertyDomainAxiom(
				convert(axiom.getProperty()), convert(axiom.getDomain()));
	}

	@Override
	public OWLDataPropertyRangeAxiom visit(ElkDataPropertyRangeAxiom axiom) {
		return owlFactory_.getOWLDataPropertyRangeAxiom(
				convert(axiom.getProperty()), convert(axiom.getRange()));
	}

	@Override
	public OWLDataSomeValuesFrom visit(ElkDataSomeValuesFrom expression) {
		List<? extends ElkDataPropertyExpression> propertyExpressions = expression
				.getDataPropertyExpressions();
		if (propertyExpressions.size() > 0) {
			throw new IllegalArgumentException(
					"OWLAPI supports only one data property in OWLDataSomeValuesFrom");
		}
		return owlFactory_.getOWLDataSomeValuesFrom(
				convert(propertyExpressions.get(0)),
				convert(expression.getDataRange()));
	}

	@Override
	public OWLDatatype visit(ElkDatatype expression) {
		return owlFactory_.getOWLDatatype(convert(expression.getIri()));
	}

	@Override
	public OWLDatatypeDefinitionAxiom visit(ElkDatatypeDefinitionAxiom axiom) {
		return owlFactory_.getOWLDatatypeDefinitionAxiom(
				convert(axiom.getDatatype()), convert(axiom.getDataRange()));
	}

	@Override
	public OWLDatatypeRestriction visit(ElkDatatypeRestriction expression) {
		return owlFactory_.getOWLDatatypeRestriction(
				convert(expression.getDatatype()),
				toFacetRestrictionSet(expression.getFacetRestrictions()));
	}

	@Override
	public OWLDataUnionOf visit(ElkDataUnionOf expression) {
		return owlFactory_
				.getOWLDataUnionOf(toDataRangeSet(expression.getDataRanges()));
	}

	@Override
	public OWLDeclarationAxiom visit(ElkDeclarationAxiom axiom) {
		return owlFactory_.getOWLDeclarationAxiom(convert(axiom.getEntity()));
	}

	@Override
	public OWLDifferentIndividualsAxiom visit(
			ElkDifferentIndividualsAxiom axiom) {
		return owlFactory_.getOWLDifferentIndividualsAxiom(
				toIndividualSet(axiom.getIndividuals()));
	}

	@Override
	public OWLDisjointClassesAxiom visit(ElkDisjointClassesAxiom axiom) {
		return owlFactory_.getOWLDisjointClassesAxiom(
				toClassExpressionSet(axiom.getClassExpressions()));
	}

	@Override
	public OWLDisjointDataPropertiesAxiom visit(
			ElkDisjointDataPropertiesAxiom axiom) {
		return owlFactory_
				.getOWLDisjointDataPropertiesAxiom(toDataPropertyExpressionSet(
						axiom.getDataPropertyExpressions()));
	}

	@Override
	public OWLDisjointObjectPropertiesAxiom visit(
			ElkDisjointObjectPropertiesAxiom axiom) {
		return owlFactory_.getOWLDisjointObjectPropertiesAxiom(
				toObjectPropertyExpressionSet(
						axiom.getObjectPropertyExpressions()));
	}

	@Override
	public OWLDisjointUnionAxiom visit(ElkDisjointUnionAxiom axiom) {
		return owlFactory_.getOWLDisjointUnionAxiom(
				convert(axiom.getDefinedClass()),
				toClassExpressionSet(axiom.getClassExpressions()));
	}

	@Override
	public OWLEquivalentClassesAxiom visit(ElkEquivalentClassesAxiom axiom) {
		return owlFactory_.getOWLEquivalentClassesAxiom(
				toClassExpressionSet(axiom.getClassExpressions()));
	}

	@Override
	public OWLEquivalentDataPropertiesAxiom visit(
			ElkEquivalentDataPropertiesAxiom axiom) {
		return owlFactory_.getOWLEquivalentDataPropertiesAxiom(
				toDataPropertyExpressionSet(
						axiom.getDataPropertyExpressions()));
	}

	@Override
	public OWLEquivalentObjectPropertiesAxiom visit(
			ElkEquivalentObjectPropertiesAxiom axiom) {
		return owlFactory_.getOWLEquivalentObjectPropertiesAxiom(
				toObjectPropertyExpressionSet(
						axiom.getObjectPropertyExpressions()));
	}

	@Override
	public OWLFacetRestriction visit(ElkFacetRestriction restriction) {
		return owlFactory_.getOWLFacetRestriction(
				OWLFacet.getFacet(convert(restriction.getConstrainingFacet())),
				convert(restriction.getRestrictionValue()));
	}

	@Override
	public IRI visit(ElkFullIri iri) {
		return IRI.create(iri.getFullIriAsString());
	}

	@Override
	public OWLFunctionalDataPropertyAxiom visit(
			ElkFunctionalDataPropertyAxiom axiom) {
		return owlFactory_.getOWLFunctionalDataPropertyAxiom(
				convert(axiom.getProperty()));
	}

	@Override
	public OWLFunctionalObjectPropertyAxiom visit(
			ElkFunctionalObjectPropertyAxiom axiom) {
		return owlFactory_.getOWLFunctionalObjectPropertyAxiom(
				convert(axiom.getProperty()));
	}

	@Override
	public OWLHasKeyAxiom visit(ElkHasKeyAxiom axiom) {
		return owlFactory_.getOWLHasKeyAxiom(
				convert(axiom.getClassExpression()),
				toPropertyExpressionSet(axiom.getObjectPropertyExpressions(),
						axiom.getDataPropertyExpressions()));
	}

	@Override
	public OWLInverseFunctionalObjectPropertyAxiom visit(
			ElkInverseFunctionalObjectPropertyAxiom axiom) {
		return owlFactory_.getOWLInverseFunctionalObjectPropertyAxiom(
				convert(axiom.getProperty()));
	}

	@Override
	public OWLInverseObjectPropertiesAxiom visit(
			ElkInverseObjectPropertiesAxiom axiom) {
		return owlFactory_.getOWLInverseObjectPropertiesAxiom(
				convert(axiom.getFirstObjectPropertyExpression()),
				convert(axiom.getSecondObjectPropertyExpression()));
	}

	@Override
	public OWLIrreflexiveObjectPropertyAxiom visit(
			ElkIrreflexiveObjectPropertyAxiom axiom) {
		return owlFactory_.getOWLIrreflexiveObjectPropertyAxiom(
				convert(axiom.getProperty()));
	}

	@Override
	public OWLLiteral visit(ElkLiteral expression) {
		return owlFactory_.getOWLLiteral(expression.getLexicalForm(),
				convert(expression.getDatatype()));
	}

	@Override
	public OWLNamedIndividual visit(ElkNamedIndividual expression) {
		return owlFactory_.getOWLNamedIndividual(convert(expression.getIri()));
	}

	@Override
	public OWLNegativeDataPropertyAssertionAxiom visit(
			ElkNegativeDataPropertyAssertionAxiom axiom) {
		return owlFactory_.getOWLNegativeDataPropertyAssertionAxiom(
				convert(axiom.getProperty()), convert(axiom.getSubject()),
				convert(axiom.getObject()));
	}

	@Override
	public OWLNegativeObjectPropertyAssertionAxiom visit(
			ElkNegativeObjectPropertyAssertionAxiom axiom) {
		return owlFactory_.getOWLNegativeObjectPropertyAssertionAxiom(
				convert(axiom.getProperty()), convert(axiom.getSubject()),
				convert(axiom.getObject()));
	}

	@Override
	public OWLObjectAllValuesFrom visit(ElkObjectAllValuesFrom expression) {
		return owlFactory_.getOWLObjectAllValuesFrom(
				convert(expression.getProperty()),
				convert(expression.getFiller()));
	}

	@Override
	public OWLObjectComplementOf visit(ElkObjectComplementOf expression) {
		return owlFactory_.getOWLObjectComplementOf(
				convert(expression.getClassExpression()));
	}

	@Override
	public OWLObjectExactCardinality visit(
			ElkObjectExactCardinalityQualified expression) {
		return owlFactory_.getOWLObjectExactCardinality(
				expression.getCardinality(), convert(expression.getProperty()));
	}

	@Override
	public OWLObjectExactCardinality visit(
			ElkObjectExactCardinalityUnqualified expression) {
		return owlFactory_.getOWLObjectExactCardinality(
				expression.getCardinality(), convert(expression.getProperty()));
	}

	@Override
	public OWLObjectHasSelf visit(ElkObjectHasSelf expression) {
		return owlFactory_
				.getOWLObjectHasSelf(convert(expression.getProperty()));
	}

	@Override
	public OWLObjectHasValue visit(ElkObjectHasValue expression) {
		return owlFactory_.getOWLObjectHasValue(
				convert(expression.getProperty()),
				convert(expression.getFiller()));
	}

	@Override
	public OWLObjectIntersectionOf visit(ElkObjectIntersectionOf expression) {
		return owlFactory_.getOWLObjectIntersectionOf(
				toClassExpressionSet(expression.getClassExpressions()));
	}

	@Override
	public OWLObjectInverseOf visit(ElkObjectInverseOf expression) {
		return owlFactory_
				.getOWLObjectInverseOf(convert(expression.getObjectProperty()));
	}

	@Override
	public OWLObjectMaxCardinality visit(
			ElkObjectMaxCardinalityQualified expression) {
		return owlFactory_.getOWLObjectMaxCardinality(
				expression.getCardinality(), convert(expression.getProperty()),
				convert(expression.getFiller()));
	}

	@Override
	public OWLObjectMaxCardinality visit(
			ElkObjectMaxCardinalityUnqualified expression) {
		return owlFactory_.getOWLObjectMaxCardinality(
				expression.getCardinality(), convert(expression.getProperty()));
	}

	@Override
	public OWLObjectMinCardinality visit(
			ElkObjectMinCardinalityQualified expression) {
		return owlFactory_.getOWLObjectMinCardinality(
				expression.getCardinality(), convert(expression.getProperty()),
				convert(expression.getFiller()));
	}

	@Override
	public OWLObjectMinCardinality visit(
			ElkObjectMinCardinalityUnqualified expression) {
		return owlFactory_.getOWLObjectMinCardinality(
				expression.getCardinality(), convert(expression.getProperty()));
	}

	@Override
	public OWLObjectOneOf visit(ElkObjectOneOf expression) {
		return owlFactory_.getOWLObjectOneOf(
				toIndividualSet(expression.getIndividuals()));
	}

	@Override
	public OWLObjectProperty visit(ElkObjectProperty expression) {
		return owlFactory_.getOWLObjectProperty(convert(expression.getIri()));
	}

	@Override
	public OWLObjectPropertyAssertionAxiom visit(
			ElkObjectPropertyAssertionAxiom axiom) {
		return owlFactory_.getOWLObjectPropertyAssertionAxiom(
				convert(axiom.getProperty()), convert(axiom.getSubject()),
				convert(axiom.getObject()));
	}

	@Override
	public OWLObject visit(ElkObjectPropertyChain expression) {
		throw new ElkRuntimeException(
				"ElkObjectPropertyChain cannot convert to OWLObject");
	}

	@Override
	public OWLObjectPropertyDomainAxiom visit(
			ElkObjectPropertyDomainAxiom axiom) {
		return owlFactory_.getOWLObjectPropertyDomainAxiom(
				convert(axiom.getProperty()), convert(axiom.getDomain()));
	}

	@Override
	public OWLObjectPropertyRangeAxiom visit(
			ElkObjectPropertyRangeAxiom axiom) {
		return owlFactory_.getOWLObjectPropertyRangeAxiom(
				convert(axiom.getProperty()), convert(axiom.getRange()));
	}

	@Override
	public OWLObjectSomeValuesFrom visit(ElkObjectSomeValuesFrom expression) {
		return owlFactory_.getOWLObjectSomeValuesFrom(
				convert(expression.getProperty()),
				convert(expression.getFiller()));
	}

	@Override
	public OWLObjectUnionOf visit(ElkObjectUnionOf expression) {
		return owlFactory_.getOWLObjectUnionOf(
				toClassExpressionSet(expression.getClassExpressions()));
	}

	@Override
	public OWLReflexiveObjectPropertyAxiom visit(
			ElkReflexiveObjectPropertyAxiom axiom) {
		return owlFactory_.getOWLReflexiveObjectPropertyAxiom(
				convert(axiom.getProperty()));
	}

	@Override
	public OWLSameIndividualAxiom visit(ElkSameIndividualAxiom axiom) {
		return owlFactory_.getOWLSameIndividualAxiom(
				toIndividualSet(axiom.getIndividuals()));
	}

	@Override
	public OWLSubAnnotationPropertyOfAxiom visit(
			ElkSubAnnotationPropertyOfAxiom axiom) {
		return owlFactory_.getOWLSubAnnotationPropertyOfAxiom(
				convert(axiom.getSubAnnotationProperty()),
				convert(axiom.getSuperAnnotationProperty()));
	}

	@Override
	public OWLObject visit(ElkSubClassOfAxiom axiom) {
		return owlFactory_.getOWLSubClassOfAxiom(
				convert(axiom.getSubClassExpression()),
				convert(axiom.getSuperClassExpression()));
	}

	@Override
	public OWLSubDataPropertyOfAxiom visit(ElkSubDataPropertyOfAxiom axiom) {
		return owlFactory_.getOWLSubDataPropertyOfAxiom(
				convert(axiom.getSubDataPropertyExpression()),
				convert(axiom.getSuperDataPropertyExpression()));
	}

	@Override
	public OWLObjectPropertyAxiom visit(
			final ElkSubObjectPropertyOfAxiom axiom) {
		return axiom.getSubObjectPropertyExpression().accept(
				new ElkSubObjectPropertyExpressionVisitor<OWLObjectPropertyAxiom>() {

					OWLSubObjectPropertyOfAxiom defaultVisit(
							ElkObjectPropertyExpression subExpression) {
						return owlFactory_.getOWLSubObjectPropertyOfAxiom(
								convert(subExpression), convert(axiom
										.getSuperObjectPropertyExpression()));
					}

					@Override
					public OWLSubObjectPropertyOfAxiom visit(
							ElkObjectInverseOf expression) {
						return defaultVisit(expression);
					}

					@Override
					public OWLSubObjectPropertyOfAxiom visit(
							ElkObjectProperty expression) {
						return defaultVisit(expression);
					}

					@Override
					public OWLSubPropertyChainOfAxiom visit(
							ElkObjectPropertyChain subExpression) {
						return owlFactory_.getOWLSubPropertyChainOfAxiom(
								toPropertyExpressionList(subExpression
										.getObjectPropertyExpressions()),
								convert(axiom
										.getSuperObjectPropertyExpression()));
					}

				});
	}

	@Override
	public OWLObject visit(ElkSWRLRule axiom) {
		throw new ElkRuntimeException("SWRL rules not supported by ELK");
	}

	@Override
	public OWLSymmetricObjectPropertyAxiom visit(
			ElkSymmetricObjectPropertyAxiom axiom) {
		return owlFactory_.getOWLSymmetricObjectPropertyAxiom(
				convert(axiom.getProperty()));
	}

	@Override
	public OWLTransitiveObjectPropertyAxiom visit(
			ElkTransitiveObjectPropertyAxiom axiom) {
		return owlFactory_.getOWLTransitiveObjectPropertyAxiom(
				convert(axiom.getProperty()));
	}

}
