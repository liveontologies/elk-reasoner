package org.semanticweb.elk.owl.interfaces;

/*
 * #%L
 * ELK OWL Object Interfaces
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

import java.util.List;

import org.semanticweb.elk.owl.iris.ElkIri;

/**
 * An {@link ElkObjectFactory} that delegates all calls to the provided
 * {@code ElkObjectFactory}. Useful as a prototype for factories that perform
 * additional actions in addition to creation of the {@link ElkObject}s.
 * 
 * @author Yevgeny Kazakov
 *
 */
public class ElkObjectDelegatingFactory implements ElkObjectFactory {

	private final ElkObjectFactory factory_;

	public ElkObjectDelegatingFactory(ElkObjectFactory factory) {
		this.factory_ = factory;
	}

	protected <C extends ElkObject> C filter(C candidate) {
		// can be overridden in subclasses
		return candidate;
	}

	@Override
	public ElkAnnotation getAnnotation(ElkAnnotationProperty property,
			ElkAnnotationValue value) {
		return filter(factory_.getAnnotation(property, value));
	}

	@Override
	public ElkAnnotationProperty getAnnotationProperty(ElkIri iri) {
		return filter(factory_.getAnnotationProperty(iri));
	}

	@Override
	public ElkAnnotationAssertionAxiom getAnnotationAssertionAxiom(
			ElkAnnotationProperty property, ElkAnnotationSubject subject,
			ElkAnnotationValue value) {
		return filter(
				factory_.getAnnotationAssertionAxiom(property, subject, value));
	}

	@Override
	public ElkAnnotationPropertyDomainAxiom getAnnotationPropertyDomainAxiom(
			ElkAnnotationProperty property, ElkIri domain) {
		return filter(
				factory_.getAnnotationPropertyDomainAxiom(property, domain));
	}

	@Override
	public ElkAnnotationPropertyRangeAxiom getAnnotationPropertyRangeAxiom(
			ElkAnnotationProperty property, ElkIri range) {
		return filter(
				factory_.getAnnotationPropertyRangeAxiom(property, range));
	}

	@Override
	public ElkAnonymousIndividual getAnonymousIndividual(String nodeId) {
		return filter(factory_.getAnonymousIndividual(nodeId));
	}

	@Override
	public ElkAsymmetricObjectPropertyAxiom getAsymmetricObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression) {
		return filter(factory_
				.getAsymmetricObjectPropertyAxiom(objectPropertyExpression));
	}

	@Override
	public ElkClass getClass(ElkIri iri) {
		return filter(factory_.getClass(iri));
	}

	@Override
	public ElkClassAssertionAxiom getClassAssertionAxiom(
			ElkClassExpression classExpression, ElkIndividual individual) {
		return filter(
				factory_.getClassAssertionAxiom(classExpression, individual));
	}

	@Override
	public ElkDataAllValuesFrom getDataAllValuesFrom(ElkDataRange dataRange,
			ElkDataPropertyExpression dpe1, ElkDataPropertyExpression... dpe) {
		return filter(factory_.getDataAllValuesFrom(dataRange, dpe1, dpe));
	}

	@Override
	public ElkDataAllValuesFrom getDataAllValuesFrom(ElkDataRange dataRange,
			List<? extends ElkDataPropertyExpression> dpList) {
		return filter(factory_.getDataAllValuesFrom(dataRange, dpList));
	}

	@Override
	public ElkDataComplementOf getDataComplementOf(ElkDataRange dataRange) {
		return filter(factory_.getDataComplementOf(dataRange));
	}

	@Override
	public ElkDataExactCardinalityUnqualified getDataExactCardinalityUnqualified(
			ElkDataPropertyExpression dataPropertyExpression, int cardinality) {
		return filter(factory_.getDataExactCardinalityUnqualified(
				dataPropertyExpression, cardinality));
	}

	@Override
	public ElkDataExactCardinalityQualified getDataExactCardinalityQualified(
			ElkDataPropertyExpression dataPropertyExpression, int cardinality,
			ElkDataRange dataRange) {
		return filter(factory_.getDataExactCardinalityQualified(
				dataPropertyExpression, cardinality, dataRange));
	}

	@Override
	public ElkDataHasValue getDataHasValue(
			ElkDataPropertyExpression dataPropertyExpression,
			ElkLiteral literal) {
		return filter(
				factory_.getDataHasValue(dataPropertyExpression, literal));
	}

	@Override
	public ElkDataIntersectionOf getDataIntersectionOf(
			ElkDataRange firstDataRange, ElkDataRange secondDataRange,
			ElkDataRange... otherDataRanges) {
		return filter(factory_.getDataIntersectionOf(firstDataRange,
				secondDataRange, otherDataRanges));
	}

	@Override
	public ElkDataIntersectionOf getDataIntersectionOf(
			List<? extends ElkDataRange> dataRanges) {
		return filter(factory_.getDataIntersectionOf(dataRanges));
	}

	@Override
	public ElkDataMaxCardinalityUnqualified getDataMaxCardinalityUnqualified(
			ElkDataPropertyExpression dataPropertyExpression, int cardinality) {
		return filter(factory_.getDataMaxCardinalityUnqualified(
				dataPropertyExpression, cardinality));
	}

	@Override
	public ElkDataMaxCardinalityQualified getDataMaxCardinalityQualified(
			ElkDataPropertyExpression dataPropertyExpression, int cardinality,
			ElkDataRange dataRange) {
		return filter(factory_.getDataMaxCardinalityQualified(
				dataPropertyExpression, cardinality, dataRange));
	}

	@Override
	public ElkDataMinCardinalityUnqualified getDataMinCardinalityUnqualified(
			ElkDataPropertyExpression dataPropertyExpression, int cardinality) {
		return filter(factory_.getDataMinCardinalityUnqualified(
				dataPropertyExpression, cardinality));
	}

	@Override
	public ElkDataMinCardinalityQualified getDataMinCardinalityQualified(
			ElkDataPropertyExpression dataPropertyExpression, int cardinality,
			ElkDataRange dataRange) {
		return filter(factory_.getDataMinCardinalityQualified(
				dataPropertyExpression, cardinality, dataRange));
	}

	@Override
	public ElkDataOneOf getDataOneOf(ElkLiteral firstLiteral,
			ElkLiteral... otherLiterals) {
		return filter(factory_.getDataOneOf(firstLiteral, otherLiterals));
	}

	@Override
	public ElkDataOneOf getDataOneOf(List<? extends ElkLiteral> literals) {
		return filter(factory_.getDataOneOf(literals));
	}

	@Override
	public ElkDataProperty getDataProperty(ElkIri iri) {
		return filter(factory_.getDataProperty(iri));
	}

	@Override
	public ElkDataPropertyAssertionAxiom getDataPropertyAssertionAxiom(
			ElkDataPropertyExpression dataPropertyExpression,
			ElkIndividual individual, ElkLiteral literal) {
		return filter(factory_.getDataPropertyAssertionAxiom(
				dataPropertyExpression, individual, literal));
	}

	@Override
	public ElkDataPropertyDomainAxiom getDataPropertyDomainAxiom(
			ElkDataPropertyExpression dataPropertyExpression,
			ElkClassExpression classExpression) {
		return filter(factory_.getDataPropertyDomainAxiom(
				dataPropertyExpression, classExpression));
	}

	@Override
	public ElkDataPropertyRangeAxiom getDataPropertyRangeAxiom(
			ElkDataPropertyExpression dataPropertyExpression,
			ElkDataRange dataRange) {
		return filter(factory_.getDataPropertyRangeAxiom(dataPropertyExpression,
				dataRange));
	}

	@Override
	public ElkDataSomeValuesFrom getDataSomeValuesFrom(ElkDataRange dataRange,
			ElkDataPropertyExpression firstDataPropertyExpression,
			ElkDataPropertyExpression... otherDataPropertyExpressions) {
		return filter(factory_.getDataSomeValuesFrom(dataRange,
				firstDataPropertyExpression, otherDataPropertyExpressions));
	}

	@Override
	public ElkDataSomeValuesFrom getDataSomeValuesFrom(ElkDataRange dataRange,
			List<? extends ElkDataPropertyExpression> dpList) {
		return filter(factory_.getDataSomeValuesFrom(dataRange, dpList));
	}

	@Override
	public ElkDatatype getDatatype(ElkIri iri) {
		return filter(factory_.getDatatype(iri));
	}

	@Override
	public ElkDatatype getDatatypeRdfPlainLiteral() {
		return filter(factory_.getDatatypeRdfPlainLiteral());
	}

	@Override
	public ElkDatatypeRestriction getDatatypeRestriction(ElkDatatype datatype,
			List<ElkFacetRestriction> facetRestrictions) {
		return filter(
				factory_.getDatatypeRestriction(datatype, facetRestrictions));
	}

	@Override
	public ElkDataUnionOf getDataUnionOf(ElkDataRange firstDataRange,
			ElkDataRange secondDataRange, ElkDataRange... otherDataRanges) {
		return filter(factory_.getDataUnionOf(firstDataRange, secondDataRange,
				otherDataRanges));
	}

	@Override
	public ElkDataUnionOf getDataUnionOf(
			List<? extends ElkDataRange> dataRanges) {
		return filter(factory_.getDataUnionOf(dataRanges));
	}

	@Override
	public ElkDeclarationAxiom getDeclarationAxiom(ElkEntity entity) {
		return filter(factory_.getDeclarationAxiom(entity));
	}

	@Override
	public ElkDifferentIndividualsAxiom getDifferentIndividualsAxiom(
			ElkIndividual firstIndividual, ElkIndividual secondIndividual,
			ElkIndividual... otherIndividuals) {
		return filter(factory_.getDifferentIndividualsAxiom(firstIndividual,
				secondIndividual, otherIndividuals));
	}

	@Override
	public ElkDifferentIndividualsAxiom getDifferentIndividualsAxiom(
			List<? extends ElkIndividual> individuals) {
		return filter(factory_.getDifferentIndividualsAxiom(individuals));
	}

	@Override
	public ElkDisjointClassesAxiom getDisjointClassesAxiom(
			ElkClassExpression firstClassExpression,
			ElkClassExpression secondClassExpression,
			ElkClassExpression... otherClassExpressions) {
		return filter(factory_.getDisjointClassesAxiom(firstClassExpression,
				secondClassExpression, otherClassExpressions));
	}

	@Override
	public ElkDisjointClassesAxiom getDisjointClassesAxiom(
			List<? extends ElkClassExpression> disjointClassExpressions) {
		return filter(
				factory_.getDisjointClassesAxiom(disjointClassExpressions));
	}

	@Override
	public ElkDisjointDataPropertiesAxiom getDisjointDataPropertiesAxiom(
			ElkDataPropertyExpression firstDataPropertyExpression,
			ElkDataPropertyExpression secondDataPropertyExpression,
			ElkDataPropertyExpression... otherDataPropertyExpressions) {
		return filter(factory_.getDisjointDataPropertiesAxiom(
				firstDataPropertyExpression, secondDataPropertyExpression,
				otherDataPropertyExpressions));
	}

	@Override
	public ElkDisjointDataPropertiesAxiom getDisjointDataPropertiesAxiom(
			List<? extends ElkDataPropertyExpression> disjointDataPropertyExpressions) {
		return filter(factory_.getDisjointDataPropertiesAxiom(
				disjointDataPropertyExpressions));
	}

	@Override
	public ElkDisjointObjectPropertiesAxiom getDisjointObjectPropertiesAxiom(
			ElkObjectPropertyExpression firstObjectPropertyExpression,
			ElkObjectPropertyExpression secondObjectPropertyExpression,
			ElkObjectPropertyExpression... otherObjectPropertyExpressions) {
		return filter(factory_.getDisjointObjectPropertiesAxiom(
				firstObjectPropertyExpression, secondObjectPropertyExpression,
				otherObjectPropertyExpressions));
	}

	@Override
	public ElkDisjointObjectPropertiesAxiom getDisjointObjectPropertiesAxiom(
			List<? extends ElkObjectPropertyExpression> disjointObjectPropertyExpressions) {
		return filter(factory_.getDisjointObjectPropertiesAxiom(
				disjointObjectPropertyExpressions));
	}

	@Override
	public ElkDisjointUnionAxiom getDisjointUnionAxiom(ElkClass definedClass,
			ElkClassExpression firstClassExpression,
			ElkClassExpression secondClassExpression,
			ElkClassExpression... otherClassExpressions) {
		return filter(factory_.getDisjointUnionAxiom(definedClass,
				firstClassExpression, secondClassExpression,
				otherClassExpressions));
	}

	@Override
	public ElkDisjointUnionAxiom getDisjointUnionAxiom(ElkClass definedClass,
			List<? extends ElkClassExpression> disjointClassExpressions) {
		return filter(factory_.getDisjointUnionAxiom(definedClass,
				disjointClassExpressions));
	}

	@Override
	public ElkEquivalentClassesAxiom getEquivalentClassesAxiom(
			ElkClassExpression firstClassExpression,
			ElkClassExpression secondClassExpression,
			ElkClassExpression... otherClassExpressions) {
		return filter(factory_.getEquivalentClassesAxiom(firstClassExpression,
				secondClassExpression, otherClassExpressions));
	}

	@Override
	public ElkEquivalentClassesAxiom getEquivalentClassesAxiom(
			List<? extends ElkClassExpression> equivalentClassExpressions) {
		return filter(
				factory_.getEquivalentClassesAxiom(equivalentClassExpressions));
	}

	@Override
	public ElkEquivalentDataPropertiesAxiom getEquivalentDataPropertiesAxiom(
			ElkDataPropertyExpression firstDataPropertyExpression,
			ElkDataPropertyExpression secondDataPropertyExpression,
			ElkDataPropertyExpression... otherDataPropertyExpressions) {
		return filter(factory_.getEquivalentDataPropertiesAxiom(
				firstDataPropertyExpression, secondDataPropertyExpression,
				otherDataPropertyExpressions));
	}

	@Override
	public ElkEquivalentDataPropertiesAxiom getEquivalentDataPropertiesAxiom(
			List<? extends ElkDataPropertyExpression> equivalentDataPropertyExpressions) {
		return filter(factory_.getEquivalentDataPropertiesAxiom(
				equivalentDataPropertyExpressions));
	}

	@Override
	public ElkEquivalentObjectPropertiesAxiom getEquivalentObjectPropertiesAxiom(
			ElkObjectPropertyExpression firstObjectPropertyExpression,
			ElkObjectPropertyExpression secondObjectPropertyExpression,
			ElkObjectPropertyExpression... otherObjectPropertyExpressions) {
		return filter(factory_.getEquivalentObjectPropertiesAxiom(
				firstObjectPropertyExpression, secondObjectPropertyExpression,
				otherObjectPropertyExpressions));
	}

	@Override
	public ElkEquivalentObjectPropertiesAxiom getEquivalentObjectPropertiesAxiom(
			List<? extends ElkObjectPropertyExpression> equivalentObjectPropertyExpressions) {
		return filter(factory_.getEquivalentObjectPropertiesAxiom(
				equivalentObjectPropertyExpressions));
	}

	@Override
	public ElkFunctionalDataPropertyAxiom getFunctionalDataPropertyAxiom(
			ElkDataPropertyExpression dataPropertyExpression) {
		return filter(factory_
				.getFunctionalDataPropertyAxiom(dataPropertyExpression));
	}

	@Override
	public ElkFunctionalObjectPropertyAxiom getFunctionalObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression) {
		return filter(factory_
				.getFunctionalObjectPropertyAxiom(objectPropertyExpression));
	}

	@Override
	public ElkInverseFunctionalObjectPropertyAxiom getInverseFunctionalObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression) {
		return filter(factory_.getInverseFunctionalObjectPropertyAxiom(
				objectPropertyExpression));
	}

	@Override
	public ElkInverseObjectPropertiesAxiom getInverseObjectPropertiesAxiom(
			ElkObjectPropertyExpression firstObjectPropertyExpression,
			ElkObjectPropertyExpression secondObjectPropertyExpression) {
		return filter(factory_.getInverseObjectPropertiesAxiom(
				firstObjectPropertyExpression, secondObjectPropertyExpression));
	}

	@Override
	public ElkIrreflexiveObjectPropertyAxiom getIrreflexiveObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression) {
		return filter(factory_
				.getIrreflexiveObjectPropertyAxiom(objectPropertyExpression));
	}

	@Override
	public ElkLiteral getLiteral(String lexicalForm, ElkDatatype datatype) {
		return filter(factory_.getLiteral(lexicalForm, datatype));
	}

	@Override
	public ElkNamedIndividual getNamedIndividual(ElkIri iri) {
		return filter(factory_.getNamedIndividual(iri));
	}

	@Override
	public ElkNegativeDataPropertyAssertionAxiom getNegativeDataPropertyAssertionAxiom(
			ElkDataPropertyExpression dataPropertyExpression,
			ElkIndividual individual, ElkLiteral literal) {
		return filter(factory_.getNegativeDataPropertyAssertionAxiom(
				dataPropertyExpression, individual, literal));
	}

	@Override
	public ElkNegativeObjectPropertyAssertionAxiom getNegativeObjectPropertyAssertionAxiom(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkIndividual firstIndividual, ElkIndividual secondIndividual) {
		return filter(factory_.getNegativeObjectPropertyAssertionAxiom(
				objectPropertyExpression, firstIndividual, secondIndividual));
	}

	@Override
	public ElkObjectAllValuesFrom getObjectAllValuesFrom(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkClassExpression classExpression) {
		return filter(factory_.getObjectAllValuesFrom(objectPropertyExpression,
				classExpression));
	}

	@Override
	public ElkObjectComplementOf getObjectComplementOf(
			ElkClassExpression classExpression) {
		return filter(factory_.getObjectComplementOf(classExpression));
	}

	@Override
	public ElkObjectExactCardinalityUnqualified getObjectExactCardinalityUnqualified(
			ElkObjectPropertyExpression objectPropertyExpression,
			int cardinality) {
		return filter(factory_.getObjectExactCardinalityUnqualified(
				objectPropertyExpression, cardinality));
	}

	@Override
	public ElkObjectExactCardinalityQualified getObjectExactCardinalityQualified(
			ElkObjectPropertyExpression objectPropertyExpression,
			int cardinality, ElkClassExpression classExpression) {
		return filter(factory_.getObjectExactCardinalityQualified(
				objectPropertyExpression, cardinality, classExpression));
	}

	@Override
	public ElkObjectHasSelf getObjectHasSelf(
			ElkObjectPropertyExpression objectPropertyExpression) {
		return filter(factory_.getObjectHasSelf(objectPropertyExpression));
	}

	@Override
	public ElkObjectHasValue getObjectHasValue(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkIndividual individual) {
		return filter(factory_.getObjectHasValue(objectPropertyExpression,
				individual));
	}

	@Override
	public ElkObjectIntersectionOf getObjectIntersectionOf(
			ElkClassExpression firstClassExpression,
			ElkClassExpression secondClassExpression,
			ElkClassExpression... otherClassExpressions) {
		return filter(factory_.getObjectIntersectionOf(firstClassExpression,
				secondClassExpression, otherClassExpressions));
	}

	@Override
	public ElkObjectIntersectionOf getObjectIntersectionOf(
			List<? extends ElkClassExpression> classExpressions) {
		return filter(factory_.getObjectIntersectionOf(classExpressions));
	}

	@Override
	public ElkObjectInverseOf getObjectInverseOf(
			ElkObjectProperty objectProperty) {
		return filter(factory_.getObjectInverseOf(objectProperty));
	}

	@Override
	public ElkObjectMaxCardinalityUnqualified getObjectMaxCardinalityUnqualified(
			ElkObjectPropertyExpression objectPropertyExpression,
			int cardinality) {
		return filter(factory_.getObjectMaxCardinalityUnqualified(
				objectPropertyExpression, cardinality));
	}

	@Override
	public ElkObjectMaxCardinalityQualified getObjectMaxCardinalityQualified(
			ElkObjectPropertyExpression objectPropertyExpression,
			int cardinality, ElkClassExpression classExpression) {
		return filter(factory_.getObjectMaxCardinalityQualified(
				objectPropertyExpression, cardinality, classExpression));
	}

	@Override
	public ElkObjectMinCardinalityUnqualified getObjectMinCardinalityUnqualified(
			ElkObjectPropertyExpression objectPropertyExpression,
			int cardinality) {
		return filter(factory_.getObjectMinCardinalityUnqualified(
				objectPropertyExpression, cardinality));
	}

	@Override
	public ElkObjectMinCardinalityQualified getObjectMinCardinalityQualified(
			ElkObjectPropertyExpression objectPropertyExpression,
			int cardinality, ElkClassExpression classExpression) {
		return filter(factory_.getObjectMinCardinalityQualified(
				objectPropertyExpression, cardinality, classExpression));
	}

	@Override
	public ElkObjectOneOf getObjectOneOf(ElkIndividual firstIndividual,
			ElkIndividual... otherIndividuals) {
		return filter(
				factory_.getObjectOneOf(firstIndividual, otherIndividuals));
	}

	@Override
	public ElkObjectOneOf getObjectOneOf(
			List<? extends ElkIndividual> individuals) {
		return filter(factory_.getObjectOneOf(individuals));
	}

	@Override
	public ElkObjectProperty getObjectProperty(ElkIri iri) {
		return filter(factory_.getObjectProperty(iri));
	}

	@Override
	public ElkObjectPropertyAssertionAxiom getObjectPropertyAssertionAxiom(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkIndividual firstIndividual, ElkIndividual secondIndividual) {
		return filter(factory_.getObjectPropertyAssertionAxiom(
				objectPropertyExpression, firstIndividual, secondIndividual));
	}

	@Override
	public ElkObjectPropertyChain getObjectPropertyChain(
			List<? extends ElkObjectPropertyExpression> objectPropertyExpressions) {
		return filter(
				factory_.getObjectPropertyChain(objectPropertyExpressions));
	}

	@Override
	public ElkObjectPropertyDomainAxiom getObjectPropertyDomainAxiom(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkClassExpression classExpression) {
		return filter(factory_.getObjectPropertyDomainAxiom(
				objectPropertyExpression, classExpression));
	}

	@Override
	public ElkObjectPropertyRangeAxiom getObjectPropertyRangeAxiom(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkClassExpression classExpression) {
		return filter(factory_.getObjectPropertyRangeAxiom(
				objectPropertyExpression, classExpression));
	}

	@Override
	public ElkObjectSomeValuesFrom getObjectSomeValuesFrom(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkClassExpression classExpression) {
		return filter(factory_.getObjectSomeValuesFrom(objectPropertyExpression,
				classExpression));
	}

	@Override
	public ElkObjectUnionOf getObjectUnionOf(
			ElkClassExpression firstClassExpression,
			ElkClassExpression secondClassExpression,
			ElkClassExpression... otherClassExpressions) {
		return filter(factory_.getObjectUnionOf(firstClassExpression,
				secondClassExpression, otherClassExpressions));
	}

	@Override
	public ElkObjectUnionOf getObjectUnionOf(
			List<? extends ElkClassExpression> classExpressions) {
		return filter(factory_.getObjectUnionOf(classExpressions));
	}

	@Override
	public ElkDataProperty getOwlBottomDataProperty() {
		return filter(factory_.getOwlBottomDataProperty());
	}

	@Override
	public ElkObjectProperty getOwlBottomObjectProperty() {
		return filter(factory_.getOwlBottomObjectProperty());
	}

	@Override
	public ElkClass getOwlNothing() {
		return filter(factory_.getOwlNothing());
	}

	@Override
	public ElkClass getOwlThing() {
		return filter(factory_.getOwlThing());
	}

	@Override
	public ElkDataProperty getOwlTopDataProperty() {
		return filter(factory_.getOwlTopDataProperty());
	}

	@Override
	public ElkObjectProperty getOwlTopObjectProperty() {
		return filter(factory_.getOwlTopObjectProperty());
	}

	@Override
	public ElkReflexiveObjectPropertyAxiom getReflexiveObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression) {
		return filter(factory_
				.getReflexiveObjectPropertyAxiom(objectPropertyExpression));
	}

	@Override
	public ElkSameIndividualAxiom getSameIndividualAxiom(
			ElkIndividual firstIndividual, ElkIndividual secondIndividual,
			ElkIndividual... otherIndividuals) {
		return filter(factory_.getSameIndividualAxiom(firstIndividual,
				secondIndividual, otherIndividuals));
	}

	@Override
	public ElkSameIndividualAxiom getSameIndividualAxiom(
			List<? extends ElkIndividual> individuals) {
		return filter(factory_.getSameIndividualAxiom(individuals));
	}

	@Override
	public ElkSubAnnotationPropertyOfAxiom getSubAnnotationPropertyOfAxiom(
			ElkAnnotationProperty subAnnotationProperty,
			ElkAnnotationProperty superAnnotationProperty) {
		return filter(factory_.getSubAnnotationPropertyOfAxiom(
				subAnnotationProperty, superAnnotationProperty));
	}

	@Override
	public ElkSubClassOfAxiom getSubClassOfAxiom(
			ElkClassExpression subClassExpression,
			ElkClassExpression superClassExpression) {
		return filter(factory_.getSubClassOfAxiom(subClassExpression,
				superClassExpression));
	}

	@Override
	public ElkSubDataPropertyOfAxiom getSubDataPropertyOfAxiom(
			ElkDataPropertyExpression subDataPropertyExpression,
			ElkDataPropertyExpression superDataPropertyExpression) {
		return filter(factory_.getSubDataPropertyOfAxiom(
				subDataPropertyExpression, superDataPropertyExpression));
	}

	@Override
	public ElkSubObjectPropertyOfAxiom getSubObjectPropertyOfAxiom(
			ElkSubObjectPropertyExpression subObjectPropertyExpression,
			ElkObjectPropertyExpression superObjectPropertyExpression) {
		return filter(factory_.getSubObjectPropertyOfAxiom(
				subObjectPropertyExpression, superObjectPropertyExpression));
	}

	@Override
	public ElkSymmetricObjectPropertyAxiom getSymmetricObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression) {
		return filter(factory_
				.getSymmetricObjectPropertyAxiom(objectPropertyExpression));
	}

	@Override
	public ElkTransitiveObjectPropertyAxiom getTransitiveObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression) {
		return filter(factory_
				.getTransitiveObjectPropertyAxiom(objectPropertyExpression));
	}

	@Override
	public ElkHasKeyAxiom getHasKeyAxiom(ElkClassExpression classExpr,
			List<? extends ElkObjectPropertyExpression> objectPEs,
			List<? extends ElkDataPropertyExpression> dataPEs) {
		return filter(factory_.getHasKeyAxiom(classExpr, objectPEs, dataPEs));
	}

	@Override
	public ElkDatatypeDefinitionAxiom getDatatypeDefinitionAxiom(
			ElkDatatype datatype, ElkDataRange dataRange) {
		return filter(factory_.getDatatypeDefinitionAxiom(datatype, dataRange));
	}

	@Override
	public ElkFacetRestriction getFacetRestriction(ElkIri iri,
			ElkLiteral literal) {
		return filter(factory_.getFacetRestriction(iri, literal));
	}

	@Override
	public ElkSWRLRule getSWRLRule() {
		return filter(factory_.getSWRLRule());
	}

}
