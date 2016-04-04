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
 * An {@link ElkObject.Factory} that delegates all calls to the provided
 * {@code ElkObject.Factory}. Useful as a prototype for factories that perform
 * additional actions in addition to creation of the {@link ElkObject}s.
 * 
 * @author Yevgeny Kazakov
 *
 */
public class ElkObjectDelegatingFactory implements ElkObject.Factory {

	private final ElkObject.Factory factory_;

	public ElkObjectDelegatingFactory(ElkObject.Factory factory) {
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
	public ElkAnnotationAssertionAxiom getAnnotationAssertionAxiom(
			ElkAnnotationProperty property, ElkAnnotationSubject subject,
			ElkAnnotationValue value) {
		return filter(
				factory_.getAnnotationAssertionAxiom(property, subject, value));
	}

	@Override
	public ElkAnnotationProperty getAnnotationProperty(ElkIri iri) {
		return filter(factory_.getAnnotationProperty(iri));
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
			ElkObjectPropertyExpression property) {
		return filter(factory_
				.getAsymmetricObjectPropertyAxiom(property));
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
	public ElkDataAllValuesFrom getDataAllValuesFrom(ElkDataRange range,
			ElkDataPropertyExpression first, ElkDataPropertyExpression... other) {
		return filter(factory_.getDataAllValuesFrom(range, first, other));
	}

	@Override
	public ElkDataAllValuesFrom getDataAllValuesFrom(List<? extends ElkDataPropertyExpression> properties,
			ElkDataRange range) {
		return filter(factory_.getDataAllValuesFrom(properties, range));
	}

	@Override
	public ElkDataComplementOf getDataComplementOf(ElkDataRange range) {
		return filter(factory_.getDataComplementOf(range));
	}

	@Override
	public ElkDataExactCardinalityQualified getDataExactCardinalityQualified(
			ElkDataPropertyExpression property, int cardinality,
			ElkDataRange range) {
		return filter(factory_.getDataExactCardinalityQualified(
				property, cardinality, range));
	}

	@Override
	public ElkDataExactCardinalityUnqualified getDataExactCardinalityUnqualified(
			ElkDataPropertyExpression property, int cardinality) {
		return filter(factory_.getDataExactCardinalityUnqualified(
				property, cardinality));
	}

	@Override
	public ElkDataHasValue getDataHasValue(
			ElkDataPropertyExpression property,
			ElkLiteral value) {
		return filter(
				factory_.getDataHasValue(property, value));
	}

	@Override
	public ElkDataIntersectionOf getDataIntersectionOf(
			ElkDataRange first, ElkDataRange second,
			ElkDataRange... other) {
		return filter(factory_.getDataIntersectionOf(first,
				second, other));
	}

	@Override
	public ElkDataIntersectionOf getDataIntersectionOf(
			List<? extends ElkDataRange> ranges) {
		return filter(factory_.getDataIntersectionOf(ranges));
	}

	@Override
	public ElkDataMaxCardinalityQualified getDataMaxCardinalityQualified(
			ElkDataPropertyExpression property, int cardinality,
			ElkDataRange range) {
		return filter(factory_.getDataMaxCardinalityQualified(
				property, cardinality, range));
	}

	@Override
	public ElkDataMaxCardinalityUnqualified getDataMaxCardinalityUnqualified(
			ElkDataPropertyExpression property, int cardinality) {
		return filter(factory_.getDataMaxCardinalityUnqualified(
				property, cardinality));
	}

	@Override
	public ElkDataMinCardinalityQualified getDataMinCardinalityQualified(
			ElkDataPropertyExpression property, int cardinality,
			ElkDataRange range) {
		return filter(factory_.getDataMinCardinalityQualified(
				property, cardinality, range));
	}

	@Override
	public ElkDataMinCardinalityUnqualified getDataMinCardinalityUnqualified(
			ElkDataPropertyExpression property, int cardinality) {
		return filter(factory_.getDataMinCardinalityUnqualified(
				property, cardinality));
	}

	@Override
	public ElkDataOneOf getDataOneOf(ElkLiteral first,
			ElkLiteral... other) {
		return filter(factory_.getDataOneOf(first, other));
	}

	@Override
	public ElkDataOneOf getDataOneOf(List<? extends ElkLiteral> members) {
		return filter(factory_.getDataOneOf(members));
	}

	@Override
	public ElkDataProperty getDataProperty(ElkIri iri) {
		return filter(factory_.getDataProperty(iri));
	}

	@Override
	public ElkDataPropertyAssertionAxiom getDataPropertyAssertionAxiom(
			ElkDataPropertyExpression property,
			ElkIndividual subject, ElkLiteral object) {
		return filter(factory_.getDataPropertyAssertionAxiom(
				property, subject, object));
	}

	@Override
	public ElkDataPropertyDomainAxiom getDataPropertyDomainAxiom(
			ElkDataPropertyExpression property,
			ElkClassExpression domain) {
		return filter(factory_.getDataPropertyDomainAxiom(
				property, domain));
	}

	@Override
	public ElkDataPropertyRangeAxiom getDataPropertyRangeAxiom(
			ElkDataPropertyExpression property,
			ElkDataRange range) {
		return filter(factory_.getDataPropertyRangeAxiom(property,
				range));
	}

	@Override
	public ElkDataSomeValuesFrom getDataSomeValuesFrom(ElkDataRange range,
			ElkDataPropertyExpression first,
			ElkDataPropertyExpression... other) {
		return filter(factory_.getDataSomeValuesFrom(range,
				first, other));
	}

	@Override
	public ElkDataSomeValuesFrom getDataSomeValuesFrom(List<? extends ElkDataPropertyExpression> properties,
			ElkDataRange range) {
		return filter(factory_.getDataSomeValuesFrom(properties, range));
	}

	@Override
	public ElkDatatype getDatatype(ElkIri iri) {
		return filter(factory_.getDatatype(iri));
	}

	@Override
	public ElkDatatypeDefinitionAxiom getDatatypeDefinitionAxiom(
			ElkDatatype datatype, ElkDataRange dataRange) {
		return filter(factory_.getDatatypeDefinitionAxiom(datatype, dataRange));
	}

	@Override
	public ElkDatatype getDatatypeRdfPlainLiteral() {
		return filter(factory_.getDatatypeRdfPlainLiteral());
	}

	@Override
	public ElkDatatypeRestriction getDatatypeRestriction(ElkDatatype datatype,
			List<ElkFacetRestriction> restrictions) {
		return filter(
				factory_.getDatatypeRestriction(datatype, restrictions));
	}

	@Override
	public ElkDataUnionOf getDataUnionOf(ElkDataRange first,
			ElkDataRange second, ElkDataRange... other) {
		return filter(factory_.getDataUnionOf(first, second,
				other));
	}

	@Override
	public ElkDataUnionOf getDataUnionOf(
			List<? extends ElkDataRange> ranges) {
		return filter(factory_.getDataUnionOf(ranges));
	}

	@Override
	public ElkDeclarationAxiom getDeclarationAxiom(ElkEntity entity) {
		return filter(factory_.getDeclarationAxiom(entity));
	}

	@Override
	public ElkDifferentIndividualsAxiom getDifferentIndividualsAxiom(
			ElkIndividual first, ElkIndividual second,
			ElkIndividual... other) {
		return filter(factory_.getDifferentIndividualsAxiom(first,
				second, other));
	}

	@Override
	public ElkDifferentIndividualsAxiom getDifferentIndividualsAxiom(
			List<? extends ElkIndividual> individuals) {
		return filter(factory_.getDifferentIndividualsAxiom(individuals));
	}

	@Override
	public ElkDisjointClassesAxiom getDisjointClassesAxiom(
			ElkClassExpression first,
			ElkClassExpression second,
			ElkClassExpression... other) {
		return filter(factory_.getDisjointClassesAxiom(first,
				second, other));
	}

	@Override
	public ElkDisjointClassesAxiom getDisjointClassesAxiom(
			List<? extends ElkClassExpression> disjointClassExpressions) {
		return filter(
				factory_.getDisjointClassesAxiom(disjointClassExpressions));
	}

	@Override
	public ElkDisjointDataPropertiesAxiom getDisjointDataPropertiesAxiom(
			ElkDataPropertyExpression first,
			ElkDataPropertyExpression second,
			ElkDataPropertyExpression... other) {
		return filter(factory_.getDisjointDataPropertiesAxiom(
				first, second,
				other));
	}

	@Override
	public ElkDisjointDataPropertiesAxiom getDisjointDataPropertiesAxiom(
			List<? extends ElkDataPropertyExpression> disjointDataPropertyExpressions) {
		return filter(factory_.getDisjointDataPropertiesAxiom(
				disjointDataPropertyExpressions));
	}

	@Override
	public ElkDisjointObjectPropertiesAxiom getDisjointObjectPropertiesAxiom(
			ElkObjectPropertyExpression first,
			ElkObjectPropertyExpression second,
			ElkObjectPropertyExpression... other) {
		return filter(factory_.getDisjointObjectPropertiesAxiom(
				first, second,
				other));
	}

	@Override
	public ElkDisjointObjectPropertiesAxiom getDisjointObjectPropertiesAxiom(
			List<? extends ElkObjectPropertyExpression> disjointObjectPropertyExpressions) {
		return filter(factory_.getDisjointObjectPropertiesAxiom(
				disjointObjectPropertyExpressions));
	}

	@Override
	public ElkDisjointUnionAxiom getDisjointUnionAxiom(ElkClass definedClass,
			ElkClassExpression first,
			ElkClassExpression second,
			ElkClassExpression... other) {
		return filter(factory_.getDisjointUnionAxiom(definedClass,
				first, second,
				other));
	}

	@Override
	public ElkDisjointUnionAxiom getDisjointUnionAxiom(ElkClass definedClass,
			List<? extends ElkClassExpression> disjointClassExpressions) {
		return filter(factory_.getDisjointUnionAxiom(definedClass,
				disjointClassExpressions));
	}

	@Override
	public ElkEquivalentClassesAxiom getEquivalentClassesAxiom(
			ElkClassExpression first,
			ElkClassExpression second,
			ElkClassExpression... other) {
		return filter(factory_.getEquivalentClassesAxiom(first,
				second, other));
	}

	@Override
	public ElkEquivalentClassesAxiom getEquivalentClassesAxiom(
			List<? extends ElkClassExpression> equivalentClassExpressions) {
		return filter(
				factory_.getEquivalentClassesAxiom(equivalentClassExpressions));
	}

	@Override
	public ElkEquivalentDataPropertiesAxiom getEquivalentDataPropertiesAxiom(
			ElkDataPropertyExpression first,
			ElkDataPropertyExpression second,
			ElkDataPropertyExpression... other) {
		return filter(factory_.getEquivalentDataPropertiesAxiom(
				first, second,
				other));
	}

	@Override
	public ElkEquivalentDataPropertiesAxiom getEquivalentDataPropertiesAxiom(
			List<? extends ElkDataPropertyExpression> equivalentDataPropertyExpressions) {
		return filter(factory_.getEquivalentDataPropertiesAxiom(
				equivalentDataPropertyExpressions));
	}

	@Override
	public ElkEquivalentObjectPropertiesAxiom getEquivalentObjectPropertiesAxiom(
			ElkObjectPropertyExpression first,
			ElkObjectPropertyExpression second,
			ElkObjectPropertyExpression... other) {
		return filter(factory_.getEquivalentObjectPropertiesAxiom(
				first, second,
				other));
	}

	@Override
	public ElkEquivalentObjectPropertiesAxiom getEquivalentObjectPropertiesAxiom(
			List<? extends ElkObjectPropertyExpression> equivalentObjectPropertyExpressions) {
		return filter(factory_.getEquivalentObjectPropertiesAxiom(
				equivalentObjectPropertyExpressions));
	}

	@Override
	public ElkFacetRestriction getFacetRestriction(ElkIri iri,
			ElkLiteral literal) {
		return filter(factory_.getFacetRestriction(iri, literal));
	}

	@Override
	public ElkFunctionalDataPropertyAxiom getFunctionalDataPropertyAxiom(
			ElkDataPropertyExpression property) {
		return filter(factory_
				.getFunctionalDataPropertyAxiom(property));
	}

	@Override
	public ElkFunctionalObjectPropertyAxiom getFunctionalObjectPropertyAxiom(
			ElkObjectPropertyExpression property) {
		return filter(factory_
				.getFunctionalObjectPropertyAxiom(property));
	}

	@Override
	public ElkHasKeyAxiom getHasKeyAxiom(ElkClassExpression object,
			List<? extends ElkObjectPropertyExpression> objectPropertyKeys,
			List<? extends ElkDataPropertyExpression> dataPropertyKeys) {
		return filter(factory_.getHasKeyAxiom(object, objectPropertyKeys, dataPropertyKeys));
	}

	@Override
	public ElkInverseFunctionalObjectPropertyAxiom getInverseFunctionalObjectPropertyAxiom(
			ElkObjectPropertyExpression property) {
		return filter(factory_.getInverseFunctionalObjectPropertyAxiom(
				property));
	}

	@Override
	public ElkInverseObjectPropertiesAxiom getInverseObjectPropertiesAxiom(
			ElkObjectPropertyExpression first,
			ElkObjectPropertyExpression second) {
		return filter(factory_.getInverseObjectPropertiesAxiom(
				first, second));
	}

	@Override
	public ElkIrreflexiveObjectPropertyAxiom getIrreflexiveObjectPropertyAxiom(
			ElkObjectPropertyExpression property) {
		return filter(factory_
				.getIrreflexiveObjectPropertyAxiom(property));
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
			ElkDataPropertyExpression property,
			ElkIndividual subject, ElkLiteral object) {
		return filter(factory_.getNegativeDataPropertyAssertionAxiom(
				property, subject, object));
	}

	@Override
	public ElkNegativeObjectPropertyAssertionAxiom getNegativeObjectPropertyAssertionAxiom(
			ElkObjectPropertyExpression property,
			ElkIndividual subject, ElkIndividual object) {
		return filter(factory_.getNegativeObjectPropertyAssertionAxiom(
				property, subject, object));
	}

	@Override
	public ElkObjectAllValuesFrom getObjectAllValuesFrom(
			ElkObjectPropertyExpression property,
			ElkClassExpression filler) {
		return filter(factory_.getObjectAllValuesFrom(property,
				filler));
	}

	@Override
	public ElkObjectComplementOf getObjectComplementOf(
			ElkClassExpression negated) {
		return filter(factory_.getObjectComplementOf(negated));
	}

	@Override
	public ElkObjectExactCardinalityQualified getObjectExactCardinalityQualified(
			ElkObjectPropertyExpression property,
			int cardinality, ElkClassExpression filler) {
		return filter(factory_.getObjectExactCardinalityQualified(
				property, cardinality, filler));
	}

	@Override
	public ElkObjectExactCardinalityUnqualified getObjectExactCardinalityUnqualified(
			ElkObjectPropertyExpression property,
			int cardinality) {
		return filter(factory_.getObjectExactCardinalityUnqualified(
				property, cardinality));
	}

	@Override
	public ElkObjectHasSelf getObjectHasSelf(
			ElkObjectPropertyExpression property) {
		return filter(factory_.getObjectHasSelf(property));
	}

	@Override
	public ElkObjectHasValue getObjectHasValue(
			ElkObjectPropertyExpression property,
			ElkIndividual value) {
		return filter(factory_.getObjectHasValue(property,
				value));
	}

	@Override
	public ElkObjectIntersectionOf getObjectIntersectionOf(
			ElkClassExpression first,
			ElkClassExpression second,
			ElkClassExpression... other) {
		return filter(factory_.getObjectIntersectionOf(first,
				second, other));
	}

	@Override
	public ElkObjectIntersectionOf getObjectIntersectionOf(
			List<? extends ElkClassExpression> members) {
		return filter(factory_.getObjectIntersectionOf(members));
	}

	@Override
	public ElkObjectInverseOf getObjectInverseOf(
			ElkObjectProperty property) {
		return filter(factory_.getObjectInverseOf(property));
	}

	@Override
	public ElkObjectMaxCardinalityQualified getObjectMaxCardinalityQualified(
			ElkObjectPropertyExpression property,
			int cardinality, ElkClassExpression filler) {
		return filter(factory_.getObjectMaxCardinalityQualified(
				property, cardinality, filler));
	}

	@Override
	public ElkObjectMaxCardinalityUnqualified getObjectMaxCardinalityUnqualified(
			ElkObjectPropertyExpression property,
			int cardinality) {
		return filter(factory_.getObjectMaxCardinalityUnqualified(
				property, cardinality));
	}

	@Override
	public ElkObjectMinCardinalityQualified getObjectMinCardinalityQualified(
			ElkObjectPropertyExpression property,
			int cardinality, ElkClassExpression filler) {
		return filter(factory_.getObjectMinCardinalityQualified(
				property, cardinality, filler));
	}

	@Override
	public ElkObjectMinCardinalityUnqualified getObjectMinCardinalityUnqualified(
			ElkObjectPropertyExpression property,
			int cardinality) {
		return filter(factory_.getObjectMinCardinalityUnqualified(
				property, cardinality));
	}

	@Override
	public ElkObjectOneOf getObjectOneOf(ElkIndividual first,
			ElkIndividual... other) {
		return filter(
				factory_.getObjectOneOf(first, other));
	}

	@Override
	public ElkObjectOneOf getObjectOneOf(
			List<? extends ElkIndividual> members) {
		return filter(factory_.getObjectOneOf(members));
	}

	@Override
	public ElkObjectProperty getObjectProperty(ElkIri iri) {
		return filter(factory_.getObjectProperty(iri));
	}

	@Override
	public ElkObjectPropertyAssertionAxiom getObjectPropertyAssertionAxiom(
			ElkObjectPropertyExpression property,
			ElkIndividual subject, ElkIndividual object) {
		return filter(factory_.getObjectPropertyAssertionAxiom(
				property, subject, object));
	}

	@Override
	public ElkObjectPropertyChain getObjectPropertyChain(
			List<? extends ElkObjectPropertyExpression> properties) {
		return filter(
				factory_.getObjectPropertyChain(properties));
	}

	@Override
	public ElkObjectPropertyDomainAxiom getObjectPropertyDomainAxiom(
			ElkObjectPropertyExpression property,
			ElkClassExpression domain) {
		return filter(factory_.getObjectPropertyDomainAxiom(
				property, domain));
	}

	@Override
	public ElkObjectPropertyRangeAxiom getObjectPropertyRangeAxiom(
			ElkObjectPropertyExpression property,
			ElkClassExpression range) {
		return filter(factory_.getObjectPropertyRangeAxiom(
				property, range));
	}

	@Override
	public ElkObjectSomeValuesFrom getObjectSomeValuesFrom(
			ElkObjectPropertyExpression property,
			ElkClassExpression filler) {
		return filter(factory_.getObjectSomeValuesFrom(property,
				filler));
	}

	@Override
	public ElkObjectUnionOf getObjectUnionOf(
			ElkClassExpression first,
			ElkClassExpression second,
			ElkClassExpression... other) {
		return filter(factory_.getObjectUnionOf(first,
				second, other));
	}

	@Override
	public ElkObjectUnionOf getObjectUnionOf(
			List<? extends ElkClassExpression> members) {
		return filter(factory_.getObjectUnionOf(members));
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
	public ElkDatatype getOwlRational() {
		return filter(factory_.getOwlRational());
	}

	@Override
	public ElkDatatype getOwlReal() {
		return filter(factory_.getOwlReal());
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
	public ElkDatatype getRdfsLiteral() {
		return filter(factory_.getRdfsLiteral());
	}

	@Override
	public ElkDatatype getRdfXMLLiteral() {
		return filter(factory_.getRdfXMLLiteral());
	}

	@Override
	public ElkReflexiveObjectPropertyAxiom getReflexiveObjectPropertyAxiom(
			ElkObjectPropertyExpression property) {
		return filter(factory_
				.getReflexiveObjectPropertyAxiom(property));
	}

	@Override
	public ElkSameIndividualAxiom getSameIndividualAxiom(
			ElkIndividual first, ElkIndividual second,
			ElkIndividual... other) {
		return filter(factory_.getSameIndividualAxiom(first,
				second, other));
	}

	@Override
	public ElkSameIndividualAxiom getSameIndividualAxiom(
			List<? extends ElkIndividual> individuals) {
		return filter(factory_.getSameIndividualAxiom(individuals));
	}

	@Override
	public ElkSubAnnotationPropertyOfAxiom getSubAnnotationPropertyOfAxiom(
			ElkAnnotationProperty subProperty,
			ElkAnnotationProperty superProperty) {
		return filter(factory_.getSubAnnotationPropertyOfAxiom(subProperty,
				superProperty));
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
			ElkDataPropertyExpression subProperty,
			ElkDataPropertyExpression superProperty) {
		return filter(factory_.getSubDataPropertyOfAxiom(
				subProperty, superProperty));
	}

	@Override
	public ElkSubObjectPropertyOfAxiom getSubObjectPropertyOfAxiom(
			ElkSubObjectPropertyExpression subProperty,
			ElkObjectPropertyExpression superProperty) {
		return filter(factory_.getSubObjectPropertyOfAxiom(
				subProperty, superProperty));
	}

	@Override
	public ElkSWRLRule getSWRLRule() {
		return filter(factory_.getSWRLRule());
	}

	@Override
	public ElkSymmetricObjectPropertyAxiom getSymmetricObjectPropertyAxiom(
			ElkObjectPropertyExpression property) {
		return filter(factory_
				.getSymmetricObjectPropertyAxiom(property));
	}

	@Override
	public ElkTransitiveObjectPropertyAxiom getTransitiveObjectPropertyAxiom(
			ElkObjectPropertyExpression property) {
		return filter(factory_
				.getTransitiveObjectPropertyAxiom(property));
	}

	@Override
	public ElkDatatype getXsdAnyUri() {
		return filter(factory_.getXsdAnyUri());
	}

	@Override
	public ElkDatatype getXsdBase64Binary() {
		return filter(factory_.getXsdBase64Binary());
	}

	@Override
	public ElkDatatype getXsdByte() {
		return filter(factory_.getXsdByte());
	}

	@Override
	public ElkDatatype getXsdDateTime() {
		return filter(factory_.getXsdDateTime());
	}

	@Override
	public ElkDatatype getXsdDateTimeStamp() {
		return filter(factory_.getXsdDateTimeStamp());
	}

	@Override
	public ElkDatatype getXsdDecimal() {
		return filter(factory_.getXsdDecimal());
	}

	@Override
	public ElkDatatype getXsdDouble() {
		return filter(factory_.getXsdDouble());
	}

	@Override
	public ElkDatatype getXsdFloat() {
		return filter(factory_.getXsdFloat());
	}

	@Override
	public ElkDatatype getXsdHexBinary() {
		return filter(factory_.getXsdHexBinary());
	}

	@Override
	public ElkDatatype getXsdInt() {
		return filter(factory_.getXsdInt());
	}

	@Override
	public ElkDatatype getXsdInteger() {
		return filter(factory_.getXsdInteger());
	}

	@Override
	public ElkDatatype getXsdLanguage() {
		return filter(factory_.getXsdLanguage());
	}

	@Override
	public ElkDatatype getXsdLong() {
		return filter(factory_.getXsdLong());
	}

	@Override
	public ElkDatatype getXsdName() {
		return filter(factory_.getXsdName());
	}

	@Override
	public ElkDatatype getXsdNCName() {
		return filter(factory_.getXsdNCName());
	}

	@Override
	public ElkDatatype getXsdNegativeInteger() {
		return filter(factory_.getXsdNegativeInteger());
	}

	@Override
	public ElkDatatype getXsdNMTOKEN() {
		return filter(factory_.getXsdNMTOKEN());
	}

	@Override
	public ElkDatatype getXsdNonNegativeInteger() {
		return filter(factory_.getXsdNonNegativeInteger());
	}

	@Override
	public ElkDatatype getXsdNonPositiveInteger() {
		return filter(factory_.getXsdNonPositiveInteger());
	}

	@Override
	public ElkDatatype getXsdNormalizedString() {
		return filter(factory_.getXsdNormalizedString());
	}

	@Override
	public ElkDatatype getXsdPositiveInteger() {
		return filter(factory_.getXsdPositiveInteger());
	}

	@Override
	public ElkDatatype getXsdShort() {
		return filter(factory_.getXsdShort());
	}

	@Override
	public ElkDatatype getXsdString() {
		return filter(factory_.getXsdString());
	}

	@Override
	public ElkDatatype getXsdToken() {
		return filter(factory_.getXsdToken());
	}

	@Override
	public ElkDatatype getXsdUnsignedByte() {
		return filter(factory_.getXsdUnsignedByte());
	}

	@Override
	public ElkDatatype getXsdUnsignedInt() {
		return filter(factory_.getXsdUnsignedInt());
	}

	@Override
	public ElkDatatype getXsdUnsignedLong() {
		return filter(factory_.getXsdUnsignedLong());
	}

	@Override
	public ElkDatatype getXsdUnsignedShort() {
		return filter(factory_.getXsdUnsignedShort());
	}

}
