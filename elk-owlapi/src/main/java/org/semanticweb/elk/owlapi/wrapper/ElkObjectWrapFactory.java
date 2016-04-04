package org.semanticweb.elk.owlapi.wrapper;

/*
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

import org.semanticweb.elk.owl.implementation.ElkSWRLRuleImpl;
import org.semanticweb.elk.owl.interfaces.ElkAnnotation;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationProperty;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationPropertyRangeAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationSubject;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationValue;
import org.semanticweb.elk.owl.interfaces.ElkAnonymousIndividual;
import org.semanticweb.elk.owl.interfaces.ElkAsymmetricObjectPropertyAxiom;
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
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSymmetricObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkTransitiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.owl.visitors.ElkSubObjectPropertyExpressionVisitor;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
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
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLFacet;

/**
 * An {@link ElkObject.Factory} that creates {@link ElkObjectWrap} expression.
 * It is required that all {@link ElkObject}s given as arguments must be
 * instances of {@link ElkObjectWrap}.
 * 
 * @author Yevgeny Kazakov
 */
public class ElkObjectWrapFactory implements ElkObject.Factory {

	// the factory for constructing wrapped OWL objects
	private final OWLDataFactory owlFactory_;

	public ElkObjectWrapFactory(OWLDataFactory owlFactory) {
		this.owlFactory_ = owlFactory;
	}

	IRI convert(ElkIri iri) {
		return IRI.create(iri.getFullIriAsString());
	}

	OWLEntity convert(ElkEntity input) {
		return ((ElkEntityWrap<?>) input).owlObject;
	}

	OWLObjectProperty convert(ElkObjectProperty input) {
		return ((ElkObjectPropertyWrap<?>) input).owlObject;
	}

	OWLObjectPropertyExpression convert(ElkObjectPropertyExpression input) {
		return (OWLObjectPropertyExpression) ((ElkObjectWrap<?>) input).owlObject;
	}

	List<? extends OWLObjectPropertyExpression> convert(
			ElkObjectPropertyChain input) {
		return ((ElkObjectPropertyChainWrap<?>) input).owlObject;
	}

	OWLDataProperty convert(ElkDataProperty input) {
		return ((ElkDataPropertyWrap<?>) input).owlObject;
	}

	OWLDataPropertyExpression convert(ElkDataPropertyExpression input) {
		return ((ElkDataPropertyExpressionWrap<?>) input).owlObject;
	}

	OWLClass convert(ElkClass input) {
		return ((ElkClassWrap<?>) input).owlObject;
	}

	OWLClassExpression convert(ElkClassExpression input) {
		return (OWLClassExpression) ((ElkObjectWrap<?>) input).owlObject;
	}

	OWLIndividual convert(ElkIndividual input) {
		return ((ElkIndividualWrap<?>) input).owlObject;
	}

	OWLAnnotationProperty convert(ElkAnnotationProperty input) {
		return ((ElkAnnotationPropertyWrap<?>) input).owlObject;
	}
	
	OWLAnnotationValue convert(ElkAnnotationValue input) {
		return (OWLAnnotationValue) ((ElkObjectWrap<?>) input).owlObject;
	}

	OWLLiteral convert(ElkLiteral input) {
		return ((ElkLiteralWrap<?>) input).owlObject;
	}

	OWLFacetRestriction convert(ElkFacetRestriction input) {
		return ((ElkFacetRestrictionWrap<?>) input).owlObject;
	}

	Set<OWLClassExpression> toSet(ElkClassExpression first,
			ElkClassExpression second, ElkClassExpression... other) {
		Set<OWLClassExpression> result = new HashSet<OWLClassExpression>(
				other.length + 2);
		result.add(convert(first));
		result.add(convert(second));
		for (int i = 0; i < other.length; i++) {
			result.add(convert(other[i]));
		}
		return result;
	}

	Set<OWLClassExpression> toClassExpressionSet(
			List<? extends ElkClassExpression> input) {
		Set<OWLClassExpression> result = new HashSet<OWLClassExpression>(
				input.size());
		for (ElkClassExpression next : input) {
			result.add(convert(next));
		}
		return result;
	}

	Set<OWLObjectPropertyExpression> toSet(ElkObjectPropertyExpression first,
			ElkObjectPropertyExpression second,
			ElkObjectPropertyExpression... other) {
		Set<OWLObjectPropertyExpression> result = new HashSet<OWLObjectPropertyExpression>(
				other.length + 2);
		result.add(convert(first));
		result.add(convert(second));
		for (int i = 0; i < other.length; i++) {
			result.add(convert(other[i]));
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

	List<? extends OWLObjectPropertyExpression> toObjectPropertyExpressionList(
			List<? extends ElkObjectPropertyExpression> input) {
		List<OWLObjectPropertyExpression> result = new ArrayList<OWLObjectPropertyExpression>(
				input.size());
		for (ElkObjectPropertyExpression next : input) {
			result.add(convert(next));
		}
		return result;
	}

	Set<? extends OWLPropertyExpression> toPropertyExpressionSet(
			List<? extends ElkObjectPropertyExpression> objectPEs,
			List<? extends ElkDataPropertyExpression> dataPEs) {
		Set<OWLPropertyExpression> result = new HashSet<OWLPropertyExpression>(
				objectPEs.size() + dataPEs.size());
		for (ElkObjectPropertyExpression property : objectPEs) {
			result.add(convert(property));
		}
		for (ElkDataPropertyExpression property : dataPEs) {
			result.add(convert(property));
		}
		return result;
	}

	Set<OWLIndividual> toSet(ElkIndividual first, ElkIndividual... other) {
		Set<OWLIndividual> result = new HashSet<OWLIndividual>(
				other.length + 1);
		result.add(convert(first));
		for (int i = 0; i < other.length; i++) {
			result.add(convert(other[i]));
		}
		return result;
	}

	Set<OWLIndividual> toSet(ElkIndividual first, ElkIndividual second,
			ElkIndividual... other) {
		Set<OWLIndividual> result = new HashSet<OWLIndividual>(
				other.length + 2);
		result.add(convert(first));
		result.add(convert(second));
		for (int i = 0; i < other.length; i++) {
			result.add(convert(other[i]));
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

	Set<OWLLiteral> toSet(ElkLiteral first, ElkLiteral... other) {
		Set<OWLLiteral> result = new HashSet<OWLLiteral>(other.length + 1);
		result.add(convert(first));
		for (int i = 0; i < other.length; i++) {
			result.add(convert(other[i]));
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

	Set<OWLDataPropertyExpression> toSet(ElkDataPropertyExpression first,
			ElkDataPropertyExpression... other) {
		Set<OWLDataPropertyExpression> result = new HashSet<OWLDataPropertyExpression>(
				other.length + 1);
		result.add(convert(first));
		for (int i = 0; i < other.length; i++) {
			result.add(convert(other[i]));
		}
		return result;
	}

	Set<OWLDataPropertyExpression> toSet(ElkDataPropertyExpression first,
			ElkDataPropertyExpression second,
			ElkDataPropertyExpression... other) {
		Set<OWLDataPropertyExpression> result = new HashSet<OWLDataPropertyExpression>(
				other.length + 2);
		result.add(convert(first));
		result.add(convert(second));
		for (int i = 0; i < other.length; i++) {
			result.add(convert(other[i]));
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

	Set<OWLFacetRestriction> toFacetRestrictionSet(
			List<? extends ElkFacetRestriction> input) {
		Set<OWLFacetRestriction> result = new HashSet<OWLFacetRestriction>(
				input.size());
		for (ElkFacetRestriction next : input) {
			result.add(convert(next));
		}
		return result;
	}

	OWLDatatype convert(ElkDatatype input) {
		return ((ElkDatatypeWrap<?>) input).owlObject;
	}

	OWLDataRange convert(ElkDataRange input) {
		return ((ElkDataRangeWrap<?>) input).owlObject;
	}

	Set<OWLDataRange> toSet(ElkDataRange first, ElkDataRange second,
			ElkDataRange... other) {
		Set<OWLDataRange> result = new HashSet<OWLDataRange>(other.length + 2);
		result.add(convert(first));
		result.add(convert(second));
		for (int i = 0; i < other.length; i++) {
			result.add(convert(other[i]));
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

	@Override
	public ElkAnnotation getAnnotation(ElkAnnotationProperty property,
			ElkAnnotationValue value) {
		return new ElkAnnotationWrap<OWLAnnotation>(owlFactory_
				.getOWLAnnotation(convert(property), convert(value)));
	}

	@Override
	public ElkAnnotationProperty getAnnotationProperty(ElkIri iri) {
		return new ElkAnnotationPropertyWrap<OWLAnnotationProperty>(
				owlFactory_.getOWLAnnotationProperty(convert(iri)));
	}

	@Override
	public ElkAnnotationAssertionAxiom getAnnotationAssertionAxiom(
			ElkAnnotationProperty property, ElkAnnotationSubject subject,
			ElkAnnotationValue value) {
		return new ElkAnnotationAssertionAxiomWrap<OWLAnnotationAssertionAxiom>(
				owlFactory_.getOWLAnnotationAssertionAxiom(convert(property),
						convert((ElkIri) subject), convert((ElkIri) value)));
	}

	@Override
	public ElkAnnotationPropertyDomainAxiom getAnnotationPropertyDomainAxiom(
			ElkAnnotationProperty property, ElkIri domain) {
		return new ElkAnnotationPropertyDomainAxiomWrap<OWLAnnotationPropertyDomainAxiom>(
				owlFactory_.getOWLAnnotationPropertyDomainAxiom(
						convert(property), convert(domain)));
	}

	@Override
	public ElkAnnotationPropertyRangeAxiom getAnnotationPropertyRangeAxiom(
			ElkAnnotationProperty property, ElkIri range) {
		return new ElkAnnotationPropertyRangeAxiomWrap<OWLAnnotationPropertyRangeAxiom>(
				owlFactory_.getOWLAnnotationPropertyRangeAxiom(
						convert(property), convert(range)));
	}

	@Override
	public ElkAnonymousIndividual getAnonymousIndividual(String nodeId) {
		return new ElkAnonymousIndividualWrap<OWLAnonymousIndividual>(
				owlFactory_.getOWLAnonymousIndividual(nodeId));
	}

	@Override
	public ElkAsymmetricObjectPropertyAxiom getAsymmetricObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression) {
		return new ElkAsymmetricObjectPropertyAxiomWrap<OWLAsymmetricObjectPropertyAxiom>(
				owlFactory_.getOWLAsymmetricObjectPropertyAxiom(
						convert(objectPropertyExpression)));
	}

	@Override
	public ElkClass getClass(ElkIri iri) {
		return new ElkClassWrap<OWLClass>(
				owlFactory_.getOWLClass(convert(iri)));
	}

	@Override
	public ElkClassAssertionAxiom getClassAssertionAxiom(
			ElkClassExpression classExpression, ElkIndividual individual) {
		return new ElkClassAssertionAxiomWrap<OWLClassAssertionAxiom>(
				owlFactory_.getOWLClassAssertionAxiom(convert(classExpression),
						convert(individual)));
	}

	@Override
	public ElkDataAllValuesFrom getDataAllValuesFrom(ElkDataRange range,
			ElkDataPropertyExpression first,
			ElkDataPropertyExpression... other) {
		if (other.length > 0) {
			throw new IllegalArgumentException(
					"OWLAPI supports only one data property in OWLDataAllValuesFrom");
		}
		return new ElkDataAllValuesFromWrap<OWLDataAllValuesFrom>(owlFactory_
				.getOWLDataAllValuesFrom(convert(first), convert(range)));
	}

	@Override
	public ElkDataAllValuesFrom getDataAllValuesFrom(
			List<? extends ElkDataPropertyExpression> properties,
			ElkDataRange range) {
		if (properties.size() > 1) {
			throw new IllegalArgumentException(
					"OWLAPI supports only one data property in OWLDataAllValuesFrom");
		}
		return new ElkDataAllValuesFromWrap<OWLDataAllValuesFrom>(
				owlFactory_.getOWLDataAllValuesFrom(convert(properties.get(0)),
						convert(range)));
	}

	@Override
	public ElkDataComplementOf getDataComplementOf(ElkDataRange range) {
		return new ElkDataComplementOfWrap<OWLDataComplementOf>(
				owlFactory_.getOWLDataComplementOf(convert(range)));
	}

	@Override
	public ElkDataExactCardinalityUnqualified getDataExactCardinalityUnqualified(
			ElkDataPropertyExpression property, int cardinality) {
		return new ElkDataExactCardinalityUnqualifiedWrap<OWLDataExactCardinality>(
				owlFactory_.getOWLDataExactCardinality(cardinality,
						convert(property)));
	}

	@Override
	public ElkDataExactCardinalityQualified getDataExactCardinalityQualified(
			ElkDataPropertyExpression property, int cardinality,
			ElkDataRange range) {
		return new ElkDataExactCardinalityQualifiedWrap<OWLDataExactCardinality>(
				owlFactory_.getOWLDataExactCardinality(cardinality,
						convert(property), convert(range)));
	}

	@Override
	public ElkDataHasValue getDataHasValue(ElkDataPropertyExpression property,
			ElkLiteral value) {
		return new ElkDataHasValueWrap<OWLDataHasValue>(owlFactory_
				.getOWLDataHasValue(convert(property), convert(value)));
	}

	@Override
	public ElkDataIntersectionOf getDataIntersectionOf(ElkDataRange first,
			ElkDataRange second, ElkDataRange... other) {
		return new ElkDataIntersectionOfWrap<OWLDataIntersectionOf>(owlFactory_
				.getOWLDataIntersectionOf(toSet(first, second, other)));
	}

	@Override
	public ElkDataIntersectionOf getDataIntersectionOf(
			List<? extends ElkDataRange> ranges) {
		return new ElkDataIntersectionOfWrap<OWLDataIntersectionOf>(
				owlFactory_.getOWLDataIntersectionOf(toDataRangeSet(ranges)));
	}

	@Override
	public ElkDataMaxCardinalityUnqualified getDataMaxCardinalityUnqualified(
			ElkDataPropertyExpression property, int cardinality) {
		return new ElkDataMaxCardinalityUnqualifiedWrap<OWLDataMaxCardinality>(
				owlFactory_.getOWLDataMaxCardinality(cardinality,
						convert(property)));
	}

	@Override
	public ElkDataMaxCardinalityQualified getDataMaxCardinalityQualified(
			ElkDataPropertyExpression property, int cardinality,
			ElkDataRange range) {
		return new ElkDataMaxCardinalityQualifiedWrap<OWLDataMaxCardinality>(
				owlFactory_.getOWLDataMaxCardinality(cardinality,
						convert(property), convert(range)));
	}

	@Override
	public ElkDataMinCardinalityUnqualified getDataMinCardinalityUnqualified(
			ElkDataPropertyExpression property, int cardinality) {
		return new ElkDataMinCardinalityUnqualifiedWrap<OWLDataMinCardinality>(
				owlFactory_.getOWLDataMinCardinality(cardinality,
						convert(property)));
	}

	@Override
	public ElkDataMinCardinalityQualified getDataMinCardinalityQualified(
			ElkDataPropertyExpression property, int cardinality,
			ElkDataRange range) {
		return new ElkDataMinCardinalityQualifiedWrap<OWLDataMinCardinality>(
				owlFactory_.getOWLDataMinCardinality(cardinality,
						convert(property), convert(range)));
	}

	@Override
	public ElkDataOneOf getDataOneOf(ElkLiteral first, ElkLiteral... other) {
		return new ElkDataOneOfWrap<OWLDataOneOf>(
				owlFactory_.getOWLDataOneOf(toSet(first, other)));
	}

	@Override
	public ElkDataOneOf getDataOneOf(List<? extends ElkLiteral> members) {
		return new ElkDataOneOfWrap<OWLDataOneOf>(
				owlFactory_.getOWLDataOneOf(toLiteralSet(members)));
	}

	@Override
	public ElkDataProperty getDataProperty(ElkIri iri) {
		return new ElkDataPropertyWrap<OWLDataProperty>(
				owlFactory_.getOWLDataProperty(convert(iri)));
	}

	@Override
	public ElkDataPropertyAssertionAxiom getDataPropertyAssertionAxiom(
			ElkDataPropertyExpression property, ElkIndividual subject,
			ElkLiteral object) {
		return new ElkDataPropertyAssertionAxiomWrap<OWLDataPropertyAssertionAxiom>(
				owlFactory_.getOWLDataPropertyAssertionAxiom(convert(property),
						convert(subject), convert(object)));
	}

	@Override
	public ElkDataPropertyDomainAxiom getDataPropertyDomainAxiom(
			ElkDataPropertyExpression property, ElkClassExpression domain) {
		return new ElkDataPropertyDomainAxiomWrap<OWLDataPropertyDomainAxiom>(
				owlFactory_.getOWLDataPropertyDomainAxiom(convert(property),
						convert(domain)));
	}

	@Override
	public ElkDataPropertyRangeAxiom getDataPropertyRangeAxiom(
			ElkDataPropertyExpression property, ElkDataRange range) {
		return new ElkDataPropertyRangeAxiomWrap<OWLDataPropertyRangeAxiom>(
				owlFactory_.getOWLDataPropertyRangeAxiom(convert(property),
						convert(range)));
	}

	@Override
	public ElkDataSomeValuesFrom getDataSomeValuesFrom(ElkDataRange range,
			ElkDataPropertyExpression first,
			ElkDataPropertyExpression... other) {
		if (other.length > 0) {
			throw new IllegalArgumentException(
					"OWLAPI supports only one data property in OWLDataSomeValuesFrom");
		}
		return new ElkDataSomeValuesFromWrap<OWLDataSomeValuesFrom>(owlFactory_
				.getOWLDataSomeValuesFrom(convert(first), convert(range)));
	}

	@Override
	public ElkDataSomeValuesFrom getDataSomeValuesFrom(
			List<? extends ElkDataPropertyExpression> properties,
			ElkDataRange range) {
		if (properties.size() > 1) {
			throw new IllegalArgumentException(
					"OWLAPI supports only one data property in OWLDataSomeValuesFrom");
		}
		return new ElkDataSomeValuesFromWrap<OWLDataSomeValuesFrom>(
				owlFactory_.getOWLDataSomeValuesFrom(convert(properties.get(0)),
						convert(range)));
	}

	@Override
	public ElkDatatype getDatatype(ElkIri iri) {
		return new ElkDatatypeWrap<OWLDatatype>(
				owlFactory_.getOWLDatatype(convert(iri)));
	}

	@Override
	public ElkDatatype getDatatypeRdfPlainLiteral() {
		return new ElkDatatypeWrap<OWLDatatype>(
				owlFactory_.getRDFPlainLiteral());
	}

	@Override
	public ElkDatatypeRestriction getDatatypeRestriction(ElkDatatype datatype,
			List<ElkFacetRestriction> restrictions) {
		return new ElkDatatypeRestrictionWrap<OWLDatatypeRestriction>(
				owlFactory_.getOWLDatatypeRestriction(convert(datatype),
						toFacetRestrictionSet(restrictions)));
	}

	@Override
	public ElkDataUnionOf getDataUnionOf(ElkDataRange first,
			ElkDataRange second, ElkDataRange... other) {
		return new ElkDataUnionOfWrap<OWLDataUnionOf>(
				owlFactory_.getOWLDataUnionOf(toSet(first, second, other)));
	}

	@Override
	public ElkDataUnionOf getDataUnionOf(List<? extends ElkDataRange> ranges) {
		return new ElkDataUnionOfWrap<OWLDataUnionOf>(
				owlFactory_.getOWLDataUnionOf(toDataRangeSet(ranges)));
	}

	@Override
	public ElkDeclarationAxiom getDeclarationAxiom(ElkEntity entity) {
		return new ElkDeclarationAxiomWrap<OWLDeclarationAxiom>(
				owlFactory_.getOWLDeclarationAxiom(convert(entity)));
	}

	@Override
	public ElkDifferentIndividualsAxiom getDifferentIndividualsAxiom(
			ElkIndividual first, ElkIndividual second, ElkIndividual... other) {
		return new ElkDifferentIndividualsAxiomWrap<OWLDifferentIndividualsAxiom>(
				owlFactory_.getOWLDifferentIndividualsAxiom(
						toSet(first, second, other)));
	}

	@Override
	public ElkDifferentIndividualsAxiom getDifferentIndividualsAxiom(
			List<? extends ElkIndividual> individuals) {
		return new ElkDifferentIndividualsAxiomWrap<OWLDifferentIndividualsAxiom>(
				owlFactory_.getOWLDifferentIndividualsAxiom(
						toIndividualSet(individuals)));
	}

	@Override
	public ElkDisjointClassesAxiom getDisjointClassesAxiom(
			ElkClassExpression first, ElkClassExpression second,
			ElkClassExpression... other) {
		return new ElkDisjointClassesAxiomWrap<OWLDisjointClassesAxiom>(
				owlFactory_.getOWLDisjointClassesAxiom(
						toSet(first, second, other)));
	}

	@Override
	public ElkDisjointClassesAxiom getDisjointClassesAxiom(
			List<? extends ElkClassExpression> disjointClassExpressions) {
		return new ElkDisjointClassesAxiomWrap<OWLDisjointClassesAxiom>(
				owlFactory_.getOWLDisjointClassesAxiom(
						toClassExpressionSet(disjointClassExpressions)));
	}

	@Override
	public ElkDisjointDataPropertiesAxiom getDisjointDataPropertiesAxiom(
			ElkDataPropertyExpression first, ElkDataPropertyExpression second,
			ElkDataPropertyExpression... other) {
		return new ElkDisjointDataPropertiesAxiomWrap<OWLDisjointDataPropertiesAxiom>(
				owlFactory_.getOWLDisjointDataPropertiesAxiom(
						toSet(first, second, other)));
	}

	@Override
	public ElkDisjointDataPropertiesAxiom getDisjointDataPropertiesAxiom(
			List<? extends ElkDataPropertyExpression> disjointDataPropertyExpressions) {
		return new ElkDisjointDataPropertiesAxiomWrap<OWLDisjointDataPropertiesAxiom>(
				owlFactory_.getOWLDisjointDataPropertiesAxiom(
						toDataPropertyExpressionSet(
								disjointDataPropertyExpressions)));
	}

	@Override
	public ElkDisjointObjectPropertiesAxiom getDisjointObjectPropertiesAxiom(
			ElkObjectPropertyExpression first,
			ElkObjectPropertyExpression second,
			ElkObjectPropertyExpression... other) {
		return new ElkDisjointObjectPropertiesAxiomWrap<OWLDisjointObjectPropertiesAxiom>(
				owlFactory_.getOWLDisjointObjectPropertiesAxiom(
						toSet(first, second, other)));
	}

	@Override
	public ElkDisjointObjectPropertiesAxiom getDisjointObjectPropertiesAxiom(
			List<? extends ElkObjectPropertyExpression> disjointObjectPropertyExpressions) {
		return new ElkDisjointObjectPropertiesAxiomWrap<OWLDisjointObjectPropertiesAxiom>(
				owlFactory_.getOWLDisjointObjectPropertiesAxiom(
						toObjectPropertyExpressionSet(
								disjointObjectPropertyExpressions)));
	}

	@Override
	public ElkDisjointUnionAxiom getDisjointUnionAxiom(ElkClass definedClass,
			ElkClassExpression first, ElkClassExpression second,
			ElkClassExpression... other) {
		return new ElkDisjointUnionAxiomWrap<OWLDisjointUnionAxiom>(
				owlFactory_.getOWLDisjointUnionAxiom(convert(definedClass),
						toSet(first, second, other)));
	}

	@Override
	public ElkDisjointUnionAxiom getDisjointUnionAxiom(ElkClass definedClass,
			List<? extends ElkClassExpression> disjointClassExpressions) {
		return new ElkDisjointUnionAxiomWrap<OWLDisjointUnionAxiom>(
				owlFactory_.getOWLDisjointUnionAxiom(convert(definedClass),
						toClassExpressionSet(disjointClassExpressions)));
	}

	@Override
	public ElkEquivalentClassesAxiom getEquivalentClassesAxiom(
			ElkClassExpression first, ElkClassExpression second,
			ElkClassExpression... other) {
		return new ElkEquivalentClassesAxiomWrap<OWLEquivalentClassesAxiom>(
				owlFactory_.getOWLEquivalentClassesAxiom(
						toSet(first, second, other)));
	}

	@Override
	public ElkEquivalentClassesAxiom getEquivalentClassesAxiom(
			List<? extends ElkClassExpression> equivalentClassExpressions) {
		return new ElkEquivalentClassesAxiomWrap<OWLEquivalentClassesAxiom>(
				owlFactory_.getOWLEquivalentClassesAxiom(
						toClassExpressionSet(equivalentClassExpressions)));
	}

	@Override
	public ElkEquivalentDataPropertiesAxiom getEquivalentDataPropertiesAxiom(
			ElkDataPropertyExpression first, ElkDataPropertyExpression second,
			ElkDataPropertyExpression... other) {
		return new ElkEquivalentDataPropertiesAxiomWrap<OWLEquivalentDataPropertiesAxiom>(
				owlFactory_.getOWLEquivalentDataPropertiesAxiom(
						toSet(first, second, other)));
	}

	@Override
	public ElkEquivalentDataPropertiesAxiom getEquivalentDataPropertiesAxiom(
			List<? extends ElkDataPropertyExpression> equivalentDataPropertyExpressions) {
		return new ElkEquivalentDataPropertiesAxiomWrap<OWLEquivalentDataPropertiesAxiom>(
				owlFactory_.getOWLEquivalentDataPropertiesAxiom(
						toDataPropertyExpressionSet(
								equivalentDataPropertyExpressions)));
	}

	@Override
	public ElkEquivalentObjectPropertiesAxiom getEquivalentObjectPropertiesAxiom(
			ElkObjectPropertyExpression first,
			ElkObjectPropertyExpression second,
			ElkObjectPropertyExpression... other) {
		return new ElkEquivalentObjectPropertiesAxiomWrap<OWLEquivalentObjectPropertiesAxiom>(
				owlFactory_.getOWLEquivalentObjectPropertiesAxiom(
						toSet(first, second, other)));
	}

	@Override
	public ElkEquivalentObjectPropertiesAxiom getEquivalentObjectPropertiesAxiom(
			List<? extends ElkObjectPropertyExpression> equivalentObjectPropertyExpressions) {
		return new ElkEquivalentObjectPropertiesAxiomWrap<OWLEquivalentObjectPropertiesAxiom>(
				owlFactory_.getOWLEquivalentObjectPropertiesAxiom(
						toObjectPropertyExpressionSet(
								equivalentObjectPropertyExpressions)));
	}

	@Override
	public ElkFunctionalDataPropertyAxiom getFunctionalDataPropertyAxiom(
			ElkDataPropertyExpression property) {
		return new ElkFunctionalDataPropertyAxiomWrap<OWLFunctionalDataPropertyAxiom>(
				owlFactory_
						.getOWLFunctionalDataPropertyAxiom(convert(property)));
	}

	@Override
	public ElkFunctionalObjectPropertyAxiom getFunctionalObjectPropertyAxiom(
			ElkObjectPropertyExpression property) {
		return new ElkFunctionalObjectPropertyAxiomWrap<OWLFunctionalObjectPropertyAxiom>(
				owlFactory_.getOWLFunctionalObjectPropertyAxiom(
						convert(property)));
	}

	@Override
	public ElkInverseFunctionalObjectPropertyAxiom getInverseFunctionalObjectPropertyAxiom(
			ElkObjectPropertyExpression property) {
		return new ElkInverseFunctionalObjectPropertyAxiomWrap<OWLInverseFunctionalObjectPropertyAxiom>(
				owlFactory_.getOWLInverseFunctionalObjectPropertyAxiom(
						convert(property)));
	}

	@Override
	public ElkInverseObjectPropertiesAxiom getInverseObjectPropertiesAxiom(
			ElkObjectPropertyExpression first,
			ElkObjectPropertyExpression second) {
		return new ElkInverseObjectPropertiesAxiomWrap<OWLInverseObjectPropertiesAxiom>(
				owlFactory_.getOWLInverseObjectPropertiesAxiom(convert(second),
						convert(second)));
	}

	@Override
	public ElkIrreflexiveObjectPropertyAxiom getIrreflexiveObjectPropertyAxiom(
			ElkObjectPropertyExpression property) {
		return new ElkIrreflexiveObjectPropertyAxiomWrap<OWLIrreflexiveObjectPropertyAxiom>(
				owlFactory_.getOWLIrreflexiveObjectPropertyAxiom(
						convert(property)));
	}

	@Override
	public ElkLiteral getLiteral(String lexicalForm, ElkDatatype datatype) {
		return new ElkLiteralWrap<OWLLiteral>(
				owlFactory_.getOWLLiteral(lexicalForm, convert(datatype)));
	}

	@Override
	public ElkNamedIndividual getNamedIndividual(ElkIri iri) {
		return new ElkNamedIndividualWrap<OWLNamedIndividual>(
				owlFactory_.getOWLNamedIndividual(convert(iri)));
	}

	@Override
	public ElkNegativeDataPropertyAssertionAxiom getNegativeDataPropertyAssertionAxiom(
			ElkDataPropertyExpression property, ElkIndividual subject,
			ElkLiteral object) {
		return new ElkNegativeDataPropertyAssertionAxiomWrap<OWLNegativeDataPropertyAssertionAxiom>(
				owlFactory_.getOWLNegativeDataPropertyAssertionAxiom(
						convert(property), convert(subject), convert(object)));
	}

	@Override
	public ElkNegativeObjectPropertyAssertionAxiom getNegativeObjectPropertyAssertionAxiom(
			ElkObjectPropertyExpression property, ElkIndividual subject,
			ElkIndividual object) {
		return new ElkNegativeObjectPropertyAssertionAxiomWrap<OWLNegativeObjectPropertyAssertionAxiom>(
				owlFactory_.getOWLNegativeObjectPropertyAssertionAxiom(
						convert(property), convert(subject), convert(object)));
	}

	@Override
	public ElkObjectAllValuesFrom getObjectAllValuesFrom(
			ElkObjectPropertyExpression property, ElkClassExpression filler) {
		return new ElkObjectAllValuesFromWrap<OWLObjectAllValuesFrom>(
				owlFactory_.getOWLObjectAllValuesFrom(convert(property),
						convert(filler)));
	}

	@Override
	public ElkObjectComplementOf getObjectComplementOf(
			ElkClassExpression negated) {
		return new ElkObjectComplementOfWrap<OWLObjectComplementOf>(
				owlFactory_.getOWLObjectComplementOf(convert(negated)));
	}

	@Override
	public ElkObjectExactCardinalityUnqualified getObjectExactCardinalityUnqualified(
			ElkObjectPropertyExpression property, int cardinality) {
		return new ElkObjectExactCardinalityUnqualifiedWrap<OWLObjectExactCardinality>(
				owlFactory_.getOWLObjectExactCardinality(cardinality,
						convert(property)));
	}

	@Override
	public ElkObjectExactCardinalityQualified getObjectExactCardinalityQualified(
			ElkObjectPropertyExpression property, int cardinality,
			ElkClassExpression filler) {
		return new ElkObjectExactCardinalityQualifiedWrap<OWLObjectExactCardinality>(
				owlFactory_.getOWLObjectExactCardinality(cardinality,
						convert(property)));
	}

	@Override
	public ElkObjectHasSelf getObjectHasSelf(
			ElkObjectPropertyExpression property) {
		return new ElkObjectHasSelfWrap<OWLObjectHasSelf>(
				owlFactory_.getOWLObjectHasSelf(convert(property)));
	}

	@Override
	public ElkObjectHasValue getObjectHasValue(
			ElkObjectPropertyExpression property, ElkIndividual value) {
		return new ElkObjectHasValueWrap<OWLObjectHasValue>(owlFactory_
				.getOWLObjectHasValue(convert(property), convert(value)));
	}

	@Override
	public ElkObjectIntersectionOf getObjectIntersectionOf(
			ElkClassExpression first, ElkClassExpression second,
			ElkClassExpression... other) {
		return new ElkObjectIntersectionOfWrap<OWLObjectIntersectionOf>(
				owlFactory_.getOWLObjectIntersectionOf(
						toSet(first, second, other)));
	}

	@Override
	public ElkObjectIntersectionOf getObjectIntersectionOf(
			List<? extends ElkClassExpression> members) {
		return new ElkObjectIntersectionOfWrap<OWLObjectIntersectionOf>(
				owlFactory_.getOWLObjectIntersectionOf(
						toClassExpressionSet(members)));
	}

	@Override
	public ElkObjectInverseOf getObjectInverseOf(ElkObjectProperty property) {
		return new ElkObjectInverseOfWrap<OWLObjectProperty>(owlFactory_
				.getOWLObjectInverseOf(convert(property)).getNamedProperty());
	}

	@Override
	public ElkObjectMaxCardinalityUnqualified getObjectMaxCardinalityUnqualified(
			ElkObjectPropertyExpression property, int cardinality) {
		return new ElkObjectMaxCardinalityUnqualifiedWrap<OWLObjectMaxCardinality>(
				owlFactory_.getOWLObjectMaxCardinality(cardinality,
						convert(property)));
	}

	@Override
	public ElkObjectMaxCardinalityQualified getObjectMaxCardinalityQualified(
			ElkObjectPropertyExpression property, int cardinality,
			ElkClassExpression filler) {
		return new ElkObjectMaxCardinalityQualifiedWrap<OWLObjectMaxCardinality>(
				owlFactory_.getOWLObjectMaxCardinality(cardinality,
						convert(property), convert(filler)));
	}

	@Override
	public ElkObjectMinCardinalityUnqualified getObjectMinCardinalityUnqualified(
			ElkObjectPropertyExpression property, int cardinality) {
		return new ElkObjectMinCardinalityUnqualifiedWrap<OWLObjectMinCardinality>(
				owlFactory_.getOWLObjectMinCardinality(cardinality,
						convert(property)));
	}

	@Override
	public ElkObjectMinCardinalityQualified getObjectMinCardinalityQualified(
			ElkObjectPropertyExpression property, int cardinality,
			ElkClassExpression filler) {
		return new ElkObjectMinCardinalityQualifiedWrap<OWLObjectMinCardinality>(
				owlFactory_.getOWLObjectMinCardinality(cardinality,
						convert(property), convert(filler)));
	}

	@Override
	public ElkObjectOneOf getObjectOneOf(ElkIndividual first,
			ElkIndividual... other) {
		return new ElkObjectOneOfWrap<OWLObjectOneOf>(
				owlFactory_.getOWLObjectOneOf(toSet(first, other)));
	}

	@Override
	public ElkObjectOneOf getObjectOneOf(
			List<? extends ElkIndividual> members) {
		return new ElkObjectOneOfWrap<OWLObjectOneOf>(
				owlFactory_.getOWLObjectOneOf(toIndividualSet(members)));
	}

	@Override
	public ElkObjectProperty getObjectProperty(ElkIri iri) {
		return new ElkObjectPropertyWrap<OWLObjectProperty>(
				owlFactory_.getOWLObjectProperty(convert(iri)));
	}

	@Override
	public ElkObjectPropertyAssertionAxiom getObjectPropertyAssertionAxiom(
			ElkObjectPropertyExpression property, ElkIndividual subject,
			ElkIndividual object) {
		return new ElkObjectPropertyAssertionAxiomWrap<OWLObjectPropertyAssertionAxiom>(
				owlFactory_.getOWLObjectPropertyAssertionAxiom(
						convert(property), convert(subject), convert(object)));
	}

	@Override
	public ElkObjectPropertyChain getObjectPropertyChain(
			List<? extends ElkObjectPropertyExpression> properties) {
		return new ElkObjectPropertyChainWrap<List<? extends OWLObjectPropertyExpression>>(
				toObjectPropertyExpressionList(properties));
	}

	@Override
	public ElkObjectPropertyDomainAxiom getObjectPropertyDomainAxiom(
			ElkObjectPropertyExpression property, ElkClassExpression domain) {
		return new ElkObjectPropertyDomainAxiomWrap<OWLObjectPropertyDomainAxiom>(
				owlFactory_.getOWLObjectPropertyDomainAxiom(convert(property),
						convert(domain)));
	}

	@Override
	public ElkObjectPropertyRangeAxiom getObjectPropertyRangeAxiom(
			ElkObjectPropertyExpression property, ElkClassExpression range) {
		return new ElkObjectPropertyRangeAxiomWrap<OWLObjectPropertyRangeAxiom>(
				owlFactory_.getOWLObjectPropertyRangeAxiom(convert(property),
						convert(range)));
	}

	@Override
	public ElkObjectSomeValuesFrom getObjectSomeValuesFrom(
			ElkObjectPropertyExpression property, ElkClassExpression filler) {
		return new ElkObjectSomeValuesFromWrap<OWLObjectSomeValuesFrom>(
				owlFactory_.getOWLObjectSomeValuesFrom(convert(property),
						convert(filler)));
	}

	@Override
	public ElkObjectUnionOf getObjectUnionOf(ElkClassExpression first,
			ElkClassExpression second, ElkClassExpression... other) {
		return new ElkObjectUnionOfWrap<OWLObjectUnionOf>(
				owlFactory_.getOWLObjectUnionOf(toSet(first, second, other)));
	}

	@Override
	public ElkObjectUnionOf getObjectUnionOf(
			List<? extends ElkClassExpression> members) {
		return new ElkObjectUnionOfWrap<OWLObjectUnionOf>(
				owlFactory_.getOWLObjectUnionOf(toClassExpressionSet(members)));
	}

	@Override
	public ElkDataProperty getOwlBottomDataProperty() {
		return new ElkDataPropertyWrap<OWLDataProperty>(
				owlFactory_.getOWLBottomDataProperty());
	}

	@Override
	public ElkObjectProperty getOwlBottomObjectProperty() {
		return new ElkObjectPropertyWrap<OWLObjectProperty>(
				owlFactory_.getOWLBottomObjectProperty());
	}

	@Override
	public ElkClass getOwlNothing() {
		return new ElkClassWrap<OWLClass>(owlFactory_.getOWLNothing());
	}

	@Override
	public ElkClass getOwlThing() {
		return new ElkClassWrap<OWLClass>(owlFactory_.getOWLThing());
	}

	@Override
	public ElkDataProperty getOwlTopDataProperty() {
		return new ElkDataPropertyWrap<OWLDataProperty>(
				owlFactory_.getOWLTopDataProperty());
	}

	@Override
	public ElkObjectProperty getOwlTopObjectProperty() {
		return new ElkObjectPropertyWrap<OWLObjectProperty>(
				owlFactory_.getOWLTopObjectProperty());
	}

	@Override
	public ElkReflexiveObjectPropertyAxiom getReflexiveObjectPropertyAxiom(
			ElkObjectPropertyExpression property) {
		return new ElkReflexiveObjectPropertyAxiomWrap<OWLReflexiveObjectPropertyAxiom>(
				owlFactory_
						.getOWLReflexiveObjectPropertyAxiom(convert(property)));
	}

	@Override
	public ElkSameIndividualAxiom getSameIndividualAxiom(ElkIndividual first,
			ElkIndividual second, ElkIndividual... other) {
		return new ElkSameIndividualAxiomWrap<OWLSameIndividualAxiom>(
				owlFactory_.getOWLSameIndividualAxiom(
						toSet(first, second, other)));
	}

	@Override
	public ElkSameIndividualAxiom getSameIndividualAxiom(
			List<? extends ElkIndividual> individuals) {
		return new ElkSameIndividualAxiomWrap<OWLSameIndividualAxiom>(
				owlFactory_.getOWLSameIndividualAxiom(
						toIndividualSet(individuals)));
	}

	@Override
	public ElkSubAnnotationPropertyOfAxiom getSubAnnotationPropertyOfAxiom(
			ElkAnnotationProperty subAnnotationProperty,
			ElkAnnotationProperty superAnnotationProperty) {
		return new ElkSubAnnotationPropertyOfAxiomWrap<OWLSubAnnotationPropertyOfAxiom>(
				owlFactory_.getOWLSubAnnotationPropertyOfAxiom(
						convert(subAnnotationProperty),
						convert(superAnnotationProperty)));
	}

	@Override
	public ElkSubClassOfAxiom getSubClassOfAxiom(
			ElkClassExpression subClassExpression,
			ElkClassExpression superClassExpression) {
		return new ElkSubClassOfAxiomWrap<OWLSubClassOfAxiom>(
				owlFactory_.getOWLSubClassOfAxiom(convert(subClassExpression),
						convert(superClassExpression)));
	}

	@Override
	public ElkSubDataPropertyOfAxiom getSubDataPropertyOfAxiom(
			ElkDataPropertyExpression subProperty,
			ElkDataPropertyExpression superProperty) {
		return new ElkSubDataPropertyOfAxiomWrap<OWLSubDataPropertyOfAxiom>(
				owlFactory_.getOWLSubDataPropertyOfAxiom(convert(subProperty),
						convert(superProperty)));
	}

	@Override
	public ElkSubObjectPropertyOfAxiom getSubObjectPropertyOfAxiom(
			ElkSubObjectPropertyExpression subProperty,
			final ElkObjectPropertyExpression superProperty) {
		return subProperty.accept(
				new ElkSubObjectPropertyExpressionVisitor<ElkSubObjectPropertyOfAxiom>() {

					ElkSubObjectPropertyOfAxiom defaultVisit(
							ElkObjectPropertyExpression expression) {
						return new ElkSubObjectPropertyOfAxiomWrap<OWLSubObjectPropertyOfAxiom>(
								owlFactory_.getOWLSubObjectPropertyOfAxiom(
										convert(expression),
										convert(superProperty)));
					}

					@Override
					public ElkSubObjectPropertyOfAxiom visit(
							ElkObjectPropertyChain expression) {
						return new ElkSubObjectPropertyChainOfAxiomWrap<OWLSubPropertyChainOfAxiom>(
								owlFactory_.getOWLSubPropertyChainOfAxiom(
										convert(expression),
										convert(superProperty)));
					}

					@Override
					public ElkSubObjectPropertyOfAxiom visit(
							ElkObjectInverseOf expression) {
						return defaultVisit(expression);
					}

					@Override
					public ElkSubObjectPropertyOfAxiom visit(
							ElkObjectProperty expression) {
						return defaultVisit(expression);
					}

				});

	}

	@Override
	public ElkSymmetricObjectPropertyAxiom getSymmetricObjectPropertyAxiom(
			ElkObjectPropertyExpression property) {
		return new ElkSymmetricObjectPropertyAxiomWrap<OWLSymmetricObjectPropertyAxiom>(
				owlFactory_
						.getOWLSymmetricObjectPropertyAxiom(convert(property)));
	}

	@Override
	public ElkTransitiveObjectPropertyAxiom getTransitiveObjectPropertyAxiom(
			ElkObjectPropertyExpression property) {
		return new ElkTransitiveObjectPropertyAxiomWrap<OWLTransitiveObjectPropertyAxiom>(
				owlFactory_.getOWLTransitiveObjectPropertyAxiom(
						convert(property)));
	}

	@Override
	public ElkHasKeyAxiom getHasKeyAxiom(ElkClassExpression object,
			List<? extends ElkObjectPropertyExpression> objectPropertyKeys,
			List<? extends ElkDataPropertyExpression> dataPropertyKeys) {
		return new ElkHasKeyAxiomWrap<OWLHasKeyAxiom>(owlFactory_
				.getOWLHasKeyAxiom(convert(object), toPropertyExpressionSet(
						objectPropertyKeys, dataPropertyKeys)));
	}

	@Override
	public ElkDatatypeDefinitionAxiom getDatatypeDefinitionAxiom(
			ElkDatatype datatype, ElkDataRange dataRange) {
		return new ElkDatatypeDefinitionAxiomWrap<OWLDatatypeDefinitionAxiom>(
				owlFactory_.getOWLDatatypeDefinitionAxiom(convert(datatype),
						convert(dataRange)));
	}

	@Override
	public ElkFacetRestriction getFacetRestriction(ElkIri iri,
			ElkLiteral literal) {
		return new ElkFacetRestrictionWrap<OWLFacetRestriction>(
				owlFactory_.getOWLFacetRestriction(
						OWLFacet.getFacet(convert(iri)), convert(literal)));
	}

	@Override
	public ElkSWRLRule getSWRLRule() {
		return new ElkSWRLRuleImpl();
	}

	@Override
	public ElkDatatype getOwlReal() {
		return new ElkDatatypeWrap<OWLDatatype>(
				owlFactory_.getOWLDatatype(OWL2Datatype.OWL_REAL.getIRI()));
	}

	@Override
	public ElkDatatype getOwlRational() {
		return new ElkDatatypeWrap<OWLDatatype>(
				owlFactory_.getOWLDatatype(OWL2Datatype.OWL_RATIONAL.getIRI()));
	}

	@Override
	public ElkDatatype getXsdDecimal() {
		return new ElkDatatypeWrap<OWLDatatype>(
				owlFactory_.getOWLDatatype(OWL2Datatype.XSD_DECIMAL.getIRI()));
	}

	@Override
	public ElkDatatype getXsdInteger() {
		return new ElkDatatypeWrap<OWLDatatype>(
				owlFactory_.getOWLDatatype(OWL2Datatype.XSD_INTEGER.getIRI()));
	}

	@Override
	public ElkDatatype getXsdNonNegativeInteger() {
		return new ElkDatatypeWrap<OWLDatatype>(owlFactory_.getOWLDatatype(
				OWL2Datatype.XSD_NON_NEGATIVE_INTEGER.getIRI()));
	}

	@Override
	public ElkDatatype getXsdNonPositiveInteger() {
		return new ElkDatatypeWrap<OWLDatatype>(owlFactory_.getOWLDatatype(
				OWL2Datatype.XSD_NON_POSITIVE_INTEGER.getIRI()));
	}

	@Override
	public ElkDatatype getXsdPositiveInteger() {
		return new ElkDatatypeWrap<OWLDatatype>(owlFactory_
				.getOWLDatatype(OWL2Datatype.XSD_POSITIVE_INTEGER.getIRI()));
	}

	@Override
	public ElkDatatype getXsdNegativeInteger() {
		return new ElkDatatypeWrap<OWLDatatype>(owlFactory_
				.getOWLDatatype(OWL2Datatype.XSD_NEGATIVE_INTEGER.getIRI()));
	}

	@Override
	public ElkDatatype getXsdLong() {
		return new ElkDatatypeWrap<OWLDatatype>(
				owlFactory_.getOWLDatatype(OWL2Datatype.XSD_LONG.getIRI()));
	}

	@Override
	public ElkDatatype getXsdInt() {
		return new ElkDatatypeWrap<OWLDatatype>(
				owlFactory_.getOWLDatatype(OWL2Datatype.XSD_INT.getIRI()));
	}

	@Override
	public ElkDatatype getXsdShort() {
		return new ElkDatatypeWrap<OWLDatatype>(
				owlFactory_.getOWLDatatype(OWL2Datatype.XSD_SHORT.getIRI()));
	}

	@Override
	public ElkDatatype getXsdByte() {
		return new ElkDatatypeWrap<OWLDatatype>(
				owlFactory_.getOWLDatatype(OWL2Datatype.XSD_BYTE.getIRI()));
	}

	@Override
	public ElkDatatype getXsdUnsignedLong() {
		return new ElkDatatypeWrap<OWLDatatype>(owlFactory_
				.getOWLDatatype(OWL2Datatype.XSD_UNSIGNED_LONG.getIRI()));
	}

	@Override
	public ElkDatatype getXsdUnsignedInt() {
		return new ElkDatatypeWrap<OWLDatatype>(owlFactory_
				.getOWLDatatype(OWL2Datatype.XSD_UNSIGNED_INT.getIRI()));
	}

	@Override
	public ElkDatatype getXsdUnsignedShort() {
		return new ElkDatatypeWrap<OWLDatatype>(owlFactory_
				.getOWLDatatype(OWL2Datatype.XSD_UNSIGNED_SHORT.getIRI()));
	}

	@Override
	public ElkDatatype getXsdUnsignedByte() {
		return new ElkDatatypeWrap<OWLDatatype>(owlFactory_
				.getOWLDatatype(OWL2Datatype.XSD_UNSIGNED_BYTE.getIRI()));
	}

	@Override
	public ElkDatatype getXsdDouble() {
		return new ElkDatatypeWrap<OWLDatatype>(
				owlFactory_.getOWLDatatype(OWL2Datatype.XSD_DOUBLE.getIRI()));
	}

	@Override
	public ElkDatatype getXsdFloat() {
		return new ElkDatatypeWrap<OWLDatatype>(
				owlFactory_.getOWLDatatype(OWL2Datatype.XSD_FLOAT.getIRI()));
	}

	@Override
	public ElkDatatype getXsdString() {
		return new ElkDatatypeWrap<OWLDatatype>(
				owlFactory_.getOWLDatatype(OWL2Datatype.XSD_STRING.getIRI()));
	}

	@Override
	public ElkDatatype getXsdNormalizedString() {
		return new ElkDatatypeWrap<OWLDatatype>(owlFactory_
				.getOWLDatatype(OWL2Datatype.XSD_NORMALIZED_STRING.getIRI()));
	}

	@Override
	public ElkDatatype getXsdToken() {
		return new ElkDatatypeWrap<OWLDatatype>(
				owlFactory_.getOWLDatatype(OWL2Datatype.XSD_TOKEN.getIRI()));
	}

	@Override
	public ElkDatatype getXsdLanguage() {
		return new ElkDatatypeWrap<OWLDatatype>(
				owlFactory_.getOWLDatatype(OWL2Datatype.XSD_LANGUAGE.getIRI()));
	}

	@Override
	public ElkDatatype getXsdName() {
		return new ElkDatatypeWrap<OWLDatatype>(
				owlFactory_.getOWLDatatype(OWL2Datatype.XSD_NAME.getIRI()));
	}

	@Override
	public ElkDatatype getXsdNCName() {
		return new ElkDatatypeWrap<OWLDatatype>(
				owlFactory_.getOWLDatatype(OWL2Datatype.XSD_NCNAME.getIRI()));
	}

	@Override
	public ElkDatatype getXsdNMTOKEN() {
		return new ElkDatatypeWrap<OWLDatatype>(
				owlFactory_.getOWLDatatype(OWL2Datatype.XSD_NMTOKEN.getIRI()));
	}

	@Override
	public ElkDatatype getXsdHexBinary() {
		return new ElkDatatypeWrap<OWLDatatype>(owlFactory_
				.getOWLDatatype(OWL2Datatype.XSD_HEX_BINARY.getIRI()));
	}

	@Override
	public ElkDatatype getXsdBase64Binary() {
		return new ElkDatatypeWrap<OWLDatatype>(owlFactory_
				.getOWLDatatype(OWL2Datatype.XSD_BASE_64_BINARY.getIRI()));
	}

	@Override
	public ElkDatatype getXsdAnyUri() {
		return new ElkDatatypeWrap<OWLDatatype>(
				owlFactory_.getOWLDatatype(OWL2Datatype.XSD_ANY_URI.getIRI()));
	}

	@Override
	public ElkDatatype getXsdDateTime() {
		return new ElkDatatypeWrap<OWLDatatype>(owlFactory_
				.getOWLDatatype(OWL2Datatype.XSD_DATE_TIME.getIRI()));
	}

	@Override
	public ElkDatatype getXsdDateTimeStamp() {
		return new ElkDatatypeWrap<OWLDatatype>(owlFactory_
				.getOWLDatatype(OWL2Datatype.XSD_DATE_TIME_STAMP.getIRI()));
	}

	@Override
	public ElkDatatype getRdfXMLLiteral() {
		return new ElkDatatypeWrap<OWLDatatype>(owlFactory_
				.getOWLDatatype(OWL2Datatype.RDF_XML_LITERAL.getIRI()));
	}

	@Override
	public ElkDatatype getRdfsLiteral() {
		return new ElkDatatypeWrap<OWLDatatype>(
				owlFactory_.getOWLDatatype(OWL2Datatype.RDFS_LITERAL.getIRI()));
	}

}
