package org.semanticweb.elk.owl.implementation;

/*
 * #%L
 * ELK OWL Model Implementation
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
import org.semanticweb.elk.owl.predefined.PredefinedElkIris;

public class ElkObjectBaseFactory implements ElkObject.Factory {

	@Override
	public ElkAnnotation getAnnotation(ElkAnnotationProperty property,
			ElkAnnotationValue value) {
		return new ElkAnnotationImpl(property, value);
	}

	@Override
	public ElkAnnotationAssertionAxiom getAnnotationAssertionAxiom(
			ElkAnnotationProperty property, ElkAnnotationSubject subject,
			ElkAnnotationValue value) {
		return new ElkAnnotationAssertionAxiomImpl(property, subject, value);
	}

	@Override
	public ElkAnnotationProperty getAnnotationProperty(ElkIri iri) {
		return new ElkAnnotationPropertyImpl(iri);
	}

	@Override
	public ElkAnnotationPropertyDomainAxiom getAnnotationPropertyDomainAxiom(
			ElkAnnotationProperty property, ElkIri domain) {
		return new ElkAnnotationPropertyDomainAxiomImpl(property, domain);
	}

	@Override
	public ElkAnnotationPropertyRangeAxiom getAnnotationPropertyRangeAxiom(
			ElkAnnotationProperty property, ElkIri range) {
		return new ElkAnnotationPropertyRangeAxiomImpl(property, range);
	}

	@Override
	public ElkAnonymousIndividual getAnonymousIndividual(String nodeId) {
		return new ElkAnonymousIndividualImpl(nodeId);
	}

	@Override
	public ElkAsymmetricObjectPropertyAxiom getAsymmetricObjectPropertyAxiom(
			ElkObjectPropertyExpression property) {
		return new ElkAsymmetricObjectPropertyAxiomImpl(property);
	}

	@Override
	public ElkClass getClass(ElkIri iri) {
		return new ElkClassImpl(iri);
	}

	@Override
	public ElkClassAssertionAxiom getClassAssertionAxiom(
			ElkClassExpression classExpression, ElkIndividual individual) {
		return new ElkClassAssertionAxiomImpl(classExpression, individual);
	}

	@Override
	public ElkDataAllValuesFrom getDataAllValuesFrom(ElkDataRange range,
			ElkDataPropertyExpression first,
			ElkDataPropertyExpression... other) {
		return new ElkDataAllValuesFromImpl(
				ElkObjectListObject.varArgsToList(first, other), range);
	}

	@Override
	public ElkDataAllValuesFrom getDataAllValuesFrom(
			List<? extends ElkDataPropertyExpression> properties,
			ElkDataRange range) {
		return new ElkDataAllValuesFromImpl(properties, range);
	}

	@Override
	public ElkDataComplementOf getDataComplementOf(ElkDataRange range) {
		return new ElkDataComplementOfImpl(range);
	}

	@Override
	public ElkDataExactCardinalityQualified getDataExactCardinalityQualified(
			ElkDataPropertyExpression property, int cardinality,
			ElkDataRange range) {
		return new ElkDataExactCardinalityQualifiedImpl(property, cardinality,
				range);
	}

	@Override
	public ElkDataExactCardinalityUnqualified getDataExactCardinalityUnqualified(
			ElkDataPropertyExpression property, int cardinality) {
		return new ElkDataExactCardinalityUnqualifiedImpl(property,
				cardinality);
	}

	@Override
	public ElkDataHasValue getDataHasValue(ElkDataPropertyExpression property,
			ElkLiteral value) {
		return new ElkDataHasValueImpl(property, value);
	}

	@Override
	public ElkDataIntersectionOf getDataIntersectionOf(ElkDataRange first,
			ElkDataRange second, ElkDataRange... other) {
		return new ElkDataIntersectionOfImpl(
				ElkObjectListObject.varArgsToList(first, second, other));
	}

	@Override
	public ElkDataIntersectionOf getDataIntersectionOf(
			List<? extends ElkDataRange> ranges) {
		return new ElkDataIntersectionOfImpl(ranges);
	}

	@Override
	public ElkDataMaxCardinalityQualified getDataMaxCardinalityQualified(
			ElkDataPropertyExpression property, int cardinality,
			ElkDataRange range) {
		return new ElkDataMaxCardinalityQualifiedImpl(property, cardinality,
				range);
	}

	@Override
	public ElkDataMaxCardinalityUnqualified getDataMaxCardinalityUnqualified(
			ElkDataPropertyExpression property, int cardinality) {
		return new ElkDataMaxCardinalityUnqualifiedImpl(property, cardinality);
	}

	@Override
	public ElkDataMinCardinalityQualified getDataMinCardinalityQualified(
			ElkDataPropertyExpression property, int cardinality,
			ElkDataRange range) {
		return new ElkDataMinCardinalityQualifiedImpl(property, cardinality,
				range);
	}

	@Override
	public ElkDataMinCardinalityUnqualified getDataMinCardinalityUnqualified(
			ElkDataPropertyExpression property, int cardinality) {
		return new ElkDataMinCardinalityUnqualifiedImpl(property, cardinality);
	}

	@Override
	public ElkDataOneOf getDataOneOf(ElkLiteral first, ElkLiteral... other) {
		return new ElkDataOneOfImpl(
				ElkObjectListObject.varArgsToList(first, other));
	}

	@Override
	public ElkDataOneOf getDataOneOf(List<? extends ElkLiteral> members) {
		return new ElkDataOneOfImpl(members);
	}

	@Override
	public ElkDataProperty getDataProperty(ElkIri iri) {
		return new ElkDataPropertyImpl(iri);
	}

	@Override
	public ElkDataPropertyAssertionAxiom getDataPropertyAssertionAxiom(
			ElkDataPropertyExpression property, ElkIndividual subject,
			ElkLiteral object) {
		return new ElkDataPropertyAssertionAxiomImpl(property, subject, object);
	}

	@Override
	public ElkDataPropertyDomainAxiom getDataPropertyDomainAxiom(
			ElkDataPropertyExpression property, ElkClassExpression domain) {
		return new ElkDataPropertyDomainAxiomImpl(property, domain);
	}

	@Override
	public ElkDataPropertyRangeAxiom getDataPropertyRangeAxiom(
			ElkDataPropertyExpression property, ElkDataRange range) {
		return new ElkDataPropertyRangeAxiomImpl(property, range);
	}

	@Override
	public ElkDataSomeValuesFrom getDataSomeValuesFrom(ElkDataRange dataRange,
			ElkDataPropertyExpression first,
			ElkDataPropertyExpression... other) {
		return new ElkDataSomeValuesFromImpl(
				ElkObjectListObject.varArgsToList(first, other), dataRange);
	}

	@Override
	public ElkDataSomeValuesFrom getDataSomeValuesFrom(
			List<? extends ElkDataPropertyExpression> properties,
			ElkDataRange range) {
		return new ElkDataSomeValuesFromImpl(properties, range);
	}

	@Override
	public ElkDatatype getDatatype(ElkIri iri) {
		return new ElkDatatypeImpl(iri);
	}

	@Override
	public ElkDatatypeDefinitionAxiom getDatatypeDefinitionAxiom(
			ElkDatatype datatype, ElkDataRange dataRange) {
		return new ElkDatatypeDefinitionAxiomImpl(datatype, dataRange);
	}

	@Override
	public ElkDatatype getDatatypeRdfPlainLiteral() {
		return getDatatype(PredefinedElkIris.RDF_PLAIN_LITERAL);
	}

	@Override
	public ElkDatatypeRestriction getDatatypeRestriction(ElkDatatype datatype,
			List<ElkFacetRestriction> restrictions) {
		return new ElkDatatypeRestrictionImpl(datatype, restrictions);
	}

	@Override
	public ElkDataUnionOf getDataUnionOf(ElkDataRange first,
			ElkDataRange second, ElkDataRange... other) {
		return new ElkDataUnionOfImpl(
				ElkObjectListObject.varArgsToList(first, second, other));
	}

	@Override
	public ElkDataUnionOf getDataUnionOf(List<? extends ElkDataRange> ranges) {
		return new ElkDataUnionOfImpl(ranges);
	}

	@Override
	public ElkDeclarationAxiom getDeclarationAxiom(ElkEntity entity) {
		return new ElkDeclarationAxiomImpl(entity);
	}

	@Override
	public ElkDifferentIndividualsAxiom getDifferentIndividualsAxiom(
			ElkIndividual first, ElkIndividual second, ElkIndividual... other) {
		return new ElkDifferentIndividualsAxiomImpl(
				ElkObjectListObject.varArgsToList(first, second, other));
	}

	@Override
	public ElkDifferentIndividualsAxiom getDifferentIndividualsAxiom(
			List<? extends ElkIndividual> individuals) {
		return new ElkDifferentIndividualsAxiomImpl(individuals);
	}

	@Override
	public ElkDisjointClassesAxiom getDisjointClassesAxiom(
			ElkClassExpression first, ElkClassExpression second,
			ElkClassExpression... other) {
		return new ElkDisjointClassesAxiomImpl(
				ElkObjectListObject.varArgsToList(first, second, other));
	}

	@Override
	public ElkDisjointClassesAxiom getDisjointClassesAxiom(
			List<? extends ElkClassExpression> disjointClassExpressions) {
		return new ElkDisjointClassesAxiomImpl(disjointClassExpressions);
	}

	@Override
	public ElkDisjointDataPropertiesAxiom getDisjointDataPropertiesAxiom(
			ElkDataPropertyExpression first, ElkDataPropertyExpression second,
			ElkDataPropertyExpression... other) {
		return new ElkDisjointDataPropertiesAxiomImpl(
				ElkObjectListObject.varArgsToList(first, second, other));
	}

	@Override
	public ElkDisjointDataPropertiesAxiom getDisjointDataPropertiesAxiom(
			List<? extends ElkDataPropertyExpression> disjointDataPropertyExpressions) {
		return new ElkDisjointDataPropertiesAxiomImpl(
				disjointDataPropertyExpressions);
	}

	@Override
	public ElkDisjointObjectPropertiesAxiom getDisjointObjectPropertiesAxiom(
			ElkObjectPropertyExpression first,
			ElkObjectPropertyExpression second,
			ElkObjectPropertyExpression... other) {
		return new ElkDisjointObjectPropertiesAxiomImpl(
				ElkObjectListObject.varArgsToList(first, second, other));
	}

	@Override
	public ElkDisjointObjectPropertiesAxiom getDisjointObjectPropertiesAxiom(
			List<? extends ElkObjectPropertyExpression> disjointObjectPropertyExpressions) {
		return new ElkDisjointObjectPropertiesAxiomImpl(
				disjointObjectPropertyExpressions);
	}

	@Override
	public ElkDisjointUnionAxiom getDisjointUnionAxiom(ElkClass definedClass,
			ElkClassExpression first, ElkClassExpression second,
			ElkClassExpression... other) {
		return new ElkDisjointUnionAxiomImpl(definedClass,
				ElkObjectListObject.varArgsToList(first, second, other));
	}

	@Override
	public ElkDisjointUnionAxiom getDisjointUnionAxiom(ElkClass definedClass,
			List<? extends ElkClassExpression> disjointClassExpressions) {
		return new ElkDisjointUnionAxiomImpl(definedClass,
				disjointClassExpressions);
	}

	@Override
	public ElkEquivalentClassesAxiom getEquivalentClassesAxiom(
			ElkClassExpression first, ElkClassExpression second,
			ElkClassExpression... other) {
		return new ElkEquivalentClassesAxiomImpl(
				ElkObjectListObject.varArgsToList(first, second, other));
	}

	@Override
	public ElkEquivalentClassesAxiom getEquivalentClassesAxiom(
			List<? extends ElkClassExpression> equivalentClassExpressions) {
		return new ElkEquivalentClassesAxiomImpl(equivalentClassExpressions);
	}

	@Override
	public ElkEquivalentDataPropertiesAxiom getEquivalentDataPropertiesAxiom(
			ElkDataPropertyExpression first, ElkDataPropertyExpression second,
			ElkDataPropertyExpression... other) {
		return new ElkEquivalentDataPropertiesAxiomImpl(
				ElkObjectListObject.varArgsToList(first, second, other));
	}

	@Override
	public ElkEquivalentDataPropertiesAxiom getEquivalentDataPropertiesAxiom(
			List<? extends ElkDataPropertyExpression> equivalentDataPropertyExpressions) {
		return new ElkEquivalentDataPropertiesAxiomImpl(
				equivalentDataPropertyExpressions);
	}

	@Override
	public ElkEquivalentObjectPropertiesAxiom getEquivalentObjectPropertiesAxiom(
			ElkObjectPropertyExpression first,
			ElkObjectPropertyExpression second,
			ElkObjectPropertyExpression... other) {
		return new ElkEquivalentObjectPropertiesAxiomImpl(
				ElkObjectListObject.varArgsToList(first, second, other));
	}

	@Override
	public ElkEquivalentObjectPropertiesAxiom getEquivalentObjectPropertiesAxiom(
			List<? extends ElkObjectPropertyExpression> equivalentObjectPropertyExpressions) {
		return new ElkEquivalentObjectPropertiesAxiomImpl(
				equivalentObjectPropertyExpressions);
	}

	@Override
	public ElkFacetRestriction getFacetRestriction(ElkIri iri,
			ElkLiteral literal) {
		return new ElkFacetRestrictionImpl(iri, literal);
	}

	@Override
	public ElkFunctionalDataPropertyAxiom getFunctionalDataPropertyAxiom(
			ElkDataPropertyExpression property) {
		return new ElkFunctionalDataPropertyAxiomImpl(property);
	}

	@Override
	public ElkFunctionalObjectPropertyAxiom getFunctionalObjectPropertyAxiom(
			ElkObjectPropertyExpression property) {
		return new ElkFunctionalObjectPropertyAxiomImpl(property);
	}

	@Override
	public ElkHasKeyAxiom getHasKeyAxiom(ElkClassExpression object,
			List<? extends ElkObjectPropertyExpression> objectPropertyKeys,
			List<? extends ElkDataPropertyExpression> dataPropertyKeys) {
		return new ElkHasKeyAxiomImpl(object, objectPropertyKeys,
				dataPropertyKeys);
	}

	@Override
	public ElkInverseFunctionalObjectPropertyAxiom getInverseFunctionalObjectPropertyAxiom(
			ElkObjectPropertyExpression property) {
		return new ElkInverseFunctionalObjectPropertyAxiomImpl(property);
	}

	@Override
	public ElkInverseObjectPropertiesAxiom getInverseObjectPropertiesAxiom(
			ElkObjectPropertyExpression first,
			ElkObjectPropertyExpression second) {
		return new ElkInverseObjectPropertiesAxiomImpl(first, second);
	}

	@Override
	public ElkIrreflexiveObjectPropertyAxiom getIrreflexiveObjectPropertyAxiom(
			ElkObjectPropertyExpression property) {
		return new ElkIrreflexiveObjectPropertyAxiomImpl(property);
	}

	@Override
	public ElkLiteral getLiteral(String lexicalForm, ElkDatatype datatype) {
		return new ElkLiteralImpl(lexicalForm, datatype);
	}

	@Override
	public ElkNamedIndividual getNamedIndividual(ElkIri iri) {
		return new ElkNamedIndividualImpl(iri);
	}

	@Override
	public ElkNegativeDataPropertyAssertionAxiom getNegativeDataPropertyAssertionAxiom(
			ElkDataPropertyExpression property, ElkIndividual subject,
			ElkLiteral object) {
		return new ElkNegativeDataPropertyAssertionAxiomImpl(property, subject,
				object);
	}

	@Override
	public ElkNegativeObjectPropertyAssertionAxiom getNegativeObjectPropertyAssertionAxiom(
			ElkObjectPropertyExpression property, ElkIndividual subject,
			ElkIndividual object) {
		return new ElkNegativeObjectPropertyAssertionAxiomImpl(property,
				subject, object);
	}

	@Override
	public ElkObjectAllValuesFrom getObjectAllValuesFrom(
			ElkObjectPropertyExpression property, ElkClassExpression filler) {
		return new ElkObjectAllValuesFromImpl(property, filler);
	}

	@Override
	public ElkObjectComplementOf getObjectComplementOf(
			ElkClassExpression negated) {
		return new ElkObjectComplementOfImpl(negated);
	}

	@Override
	public ElkObjectExactCardinalityQualified getObjectExactCardinalityQualified(
			ElkObjectPropertyExpression property, int cardinality,
			ElkClassExpression filler) {
		return new ElkObjectExactCardinalityQualifiedImpl(property, cardinality,
				filler);
	}

	@Override
	public ElkObjectExactCardinalityUnqualified getObjectExactCardinalityUnqualified(
			ElkObjectPropertyExpression property, int cardinality) {
		return new ElkObjectExactCardinalityUnqualifiedImpl(property,
				cardinality);
	}

	@Override
	public ElkObjectHasSelf getObjectHasSelf(
			ElkObjectPropertyExpression property) {
		return new ElkObjectHasSelfImpl(property);
	}

	@Override
	public ElkObjectHasValue getObjectHasValue(
			ElkObjectPropertyExpression property, ElkIndividual value) {
		return new ElkObjectHasValueImpl(property, value);
	}

	@Override
	public ElkObjectIntersectionOf getObjectIntersectionOf(
			ElkClassExpression first, ElkClassExpression second,
			ElkClassExpression... other) {
		return new ElkObjectIntersectionOfImpl(
				ElkObjectListObject.varArgsToList(first, second, other));
	}

	@Override
	public ElkObjectIntersectionOf getObjectIntersectionOf(
			List<? extends ElkClassExpression> members) {
		return new ElkObjectIntersectionOfImpl(members);
	}

	@Override
	public ElkObjectInverseOf getObjectInverseOf(ElkObjectProperty property) {
		return new ElkObjectInverseOfImpl(property);
	}

	@Override
	public ElkObjectMaxCardinalityQualified getObjectMaxCardinalityQualified(
			ElkObjectPropertyExpression property, int cardinality,
			ElkClassExpression filler) {
		return new ElkObjectMaxCardinalityQualifiedImpl(property, cardinality,
				filler);
	}

	@Override
	public ElkObjectMaxCardinalityUnqualified getObjectMaxCardinalityUnqualified(
			ElkObjectPropertyExpression property, int cardinality) {
		return new ElkObjectMaxCardinalityUnqualifiedImpl(property,
				cardinality);
	}

	@Override
	public ElkObjectMinCardinalityQualified getObjectMinCardinalityQualified(
			ElkObjectPropertyExpression property, int cardinality,
			ElkClassExpression filler) {
		return new ElkObjectMinCardinalityQualifiedImpl(property, cardinality,
				filler);
	}

	@Override
	public ElkObjectMinCardinalityUnqualified getObjectMinCardinalityUnqualified(
			ElkObjectPropertyExpression property, int cardinality) {
		return new ElkObjectMinCardinalityUnqualifiedImpl(property,
				cardinality);
	}

	@Override
	public ElkObjectOneOf getObjectOneOf(ElkIndividual first,
			ElkIndividual... other) {
		return new ElkObjectOneOfImpl(
				ElkObjectListObject.varArgsToList(first, other));
	}

	@Override
	public ElkObjectOneOf getObjectOneOf(
			List<? extends ElkIndividual> members) {
		return new ElkObjectOneOfImpl(members);
	}

	@Override
	public ElkObjectProperty getObjectProperty(ElkIri iri) {
		return new ElkObjectPropertyImpl(iri);
	}

	@Override
	public ElkObjectPropertyAssertionAxiom getObjectPropertyAssertionAxiom(
			ElkObjectPropertyExpression property, ElkIndividual subject,
			ElkIndividual object) {
		return new ElkObjectPropertyAssertionAxiomImpl(property, subject,
				object);
	}

	@Override
	public ElkObjectPropertyChain getObjectPropertyChain(
			List<? extends ElkObjectPropertyExpression> properties) {
		return new ElkObjectPropertyChainImpl(properties);
	}

	@Override
	public ElkObjectPropertyDomainAxiom getObjectPropertyDomainAxiom(
			ElkObjectPropertyExpression property, ElkClassExpression domain) {
		return new ElkObjectPropertyDomainAxiomImpl(property, domain);
	}

	@Override
	public ElkObjectPropertyRangeAxiom getObjectPropertyRangeAxiom(
			ElkObjectPropertyExpression property, ElkClassExpression range) {
		return new ElkObjectPropertyRangeAxiomImpl(property, range);
	}

	@Override
	public ElkObjectSomeValuesFrom getObjectSomeValuesFrom(
			ElkObjectPropertyExpression property, ElkClassExpression filler) {
		return new ElkObjectSomeValuesFromImpl(property, filler);
	}

	@Override
	public ElkObjectUnionOf getObjectUnionOf(ElkClassExpression first,
			ElkClassExpression second, ElkClassExpression... other) {
		return new ElkObjectUnionOfImpl(
				ElkObjectListObject.varArgsToList(first, second, other));
	}

	@Override
	public ElkObjectUnionOf getObjectUnionOf(
			List<? extends ElkClassExpression> members) {
		return new ElkObjectUnionOfImpl(members);
	}

	@Override
	public ElkDataProperty getOwlBottomDataProperty() {
		return getDataProperty(PredefinedElkIris.OWL_BOTTOM_DATA_PROPERTY);
	}

	@Override
	public ElkObjectProperty getOwlBottomObjectProperty() {
		return getObjectProperty(PredefinedElkIris.OWL_BOTTOM_OBJECT_PROPERTY);
	}

	@Override
	public ElkClass getOwlNothing() {
		return getClass(PredefinedElkIris.OWL_NOTHING);
	}

	@Override
	public ElkDatatype getOwlRational() {
		return getDatatype(PredefinedElkIris.OWL_RATIONAL);
	}

	@Override
	public ElkDatatype getOwlReal() {
		return getDatatype(PredefinedElkIris.OWL_REAL);
	}

	@Override
	public ElkClass getOwlThing() {
		return getClass(PredefinedElkIris.OWL_THING);
	}

	@Override
	public ElkDataProperty getOwlTopDataProperty() {
		return getDataProperty(PredefinedElkIris.OWL_TOP_DATA_PROPERTY);
	}

	@Override
	public ElkObjectProperty getOwlTopObjectProperty() {
		return getObjectProperty(PredefinedElkIris.OWL_TOP_OBJECT_PROPERTY);
	}

	@Override
	public ElkDatatype getRdfsLiteral() {
		return getDatatype(PredefinedElkIris.RDFS_LITERAL);
	}

	@Override
	public ElkDatatype getRdfXMLLiteral() {
		return getDatatype(PredefinedElkIris.RDF_XML_LITERAL);
	}

	@Override
	public ElkReflexiveObjectPropertyAxiom getReflexiveObjectPropertyAxiom(
			ElkObjectPropertyExpression property) {
		return new ElkReflexiveObjectPropertyAxiomImpl(property);
	}

	@Override
	public ElkSameIndividualAxiom getSameIndividualAxiom(ElkIndividual first,
			ElkIndividual second, ElkIndividual... other) {
		return new ElkSameIndividualAxiomImpl(
				ElkObjectListObject.varArgsToList(first, second, other));
	}

	@Override
	public ElkSameIndividualAxiom getSameIndividualAxiom(
			List<? extends ElkIndividual> individuals) {
		return new ElkSameIndividualAxiomImpl(individuals);
	}

	@Override
	public ElkSubAnnotationPropertyOfAxiom getSubAnnotationPropertyOfAxiom(
			ElkAnnotationProperty subProperty,
			ElkAnnotationProperty superProperty) {
		return new ElkSubAnnotationPropertyOfAxiomImpl(subProperty,
				superProperty);
	}

	@Override
	public ElkSubClassOfAxiom getSubClassOfAxiom(
			ElkClassExpression subClassExpression,
			ElkClassExpression superClassExpression) {
		return new ElkSubClassOfAxiomImpl(subClassExpression,
				superClassExpression);
	}

	@Override
	public ElkSubDataPropertyOfAxiom getSubDataPropertyOfAxiom(
			ElkDataPropertyExpression subProperty,
			ElkDataPropertyExpression superProperty) {
		return new ElkSubDataPropertyOfAxiomImpl(subProperty, superProperty);
	}

	@Override
	public ElkSubObjectPropertyOfAxiom getSubObjectPropertyOfAxiom(
			ElkSubObjectPropertyExpression subProperty,
			ElkObjectPropertyExpression superProperty) {
		return new ElkSubObjectPropertyOfAxiomImpl(subProperty, superProperty);
	}

	@Override
	public ElkSWRLRule getSWRLRule() {
		return new ElkSWRLRuleImpl();
	}

	@Override
	public ElkSymmetricObjectPropertyAxiom getSymmetricObjectPropertyAxiom(
			ElkObjectPropertyExpression property) {
		return new ElkSymmetricObjectPropertyAxiomImpl(property);
	}

	@Override
	public ElkTransitiveObjectPropertyAxiom getTransitiveObjectPropertyAxiom(
			ElkObjectPropertyExpression property) {
		return new ElkTransitiveObjectPropertyAxiomImpl(property);
	}

	@Override
	public ElkDatatype getXsdAnyUri() {
		return getDatatype(PredefinedElkIris.XSD_ANY_URI);
	}

	@Override
	public ElkDatatype getXsdBase64Binary() {
		return getDatatype(PredefinedElkIris.XSD_BASE_64_BINARY);
	}

	@Override
	public ElkDatatype getXsdByte() {
		return getDatatype(PredefinedElkIris.XSD_BYTE);
	}

	@Override
	public ElkDatatype getXsdDateTime() {
		return getDatatype(PredefinedElkIris.XSD_DATE_TIME);
	}

	@Override
	public ElkDatatype getXsdDateTimeStamp() {
		return getDatatype(PredefinedElkIris.XSD_DATE_TIME_STAMP);
	}

	@Override
	public ElkDatatype getXsdDecimal() {
		return getDatatype(PredefinedElkIris.XSD_DECIMAL);
	}

	@Override
	public ElkDatatype getXsdDouble() {
		return getDatatype(PredefinedElkIris.XSD_DOUBLE);
	}

	@Override
	public ElkDatatype getXsdFloat() {
		return getDatatype(PredefinedElkIris.XSD_FLOAT);
	}

	@Override
	public ElkDatatype getXsdHexBinary() {
		return getDatatype(PredefinedElkIris.XSD_HEX_BINARY);
	}

	@Override
	public ElkDatatype getXsdInt() {
		return getDatatype(PredefinedElkIris.XSD_INT);
	}

	@Override
	public ElkDatatype getXsdInteger() {
		return getDatatype(PredefinedElkIris.XSD_INTEGER);
	}

	@Override
	public ElkDatatype getXsdLanguage() {
		return getDatatype(PredefinedElkIris.XSD_LANGUAGE);
	}

	@Override
	public ElkDatatype getXsdLong() {
		return getDatatype(PredefinedElkIris.XSD_LONG);
	}

	@Override
	public ElkDatatype getXsdName() {
		return getDatatype(PredefinedElkIris.XSD_NAME);
	}

	@Override
	public ElkDatatype getXsdNCName() {
		return getDatatype(PredefinedElkIris.XSD_NC_NAME);
	}

	@Override
	public ElkDatatype getXsdNegativeInteger() {
		return getDatatype(PredefinedElkIris.XSD_NEGATIVE_INTEGER);
	}

	@Override
	public ElkDatatype getXsdNMTOKEN() {
		return getDatatype(PredefinedElkIris.XSD_NM_TOKEN);
	}

	@Override
	public ElkDatatype getXsdNonNegativeInteger() {
		return getDatatype(PredefinedElkIris.XSD_NON_NEGATIVE_INTEGER);
	}

	@Override
	public ElkDatatype getXsdNonPositiveInteger() {
		return getDatatype(PredefinedElkIris.XSD_NON_POSITIVE_INTEGER);
	}

	@Override
	public ElkDatatype getXsdNormalizedString() {
		return getDatatype(PredefinedElkIris.XSD_NORMALIZED_STRING);
	}

	@Override
	public ElkDatatype getXsdPositiveInteger() {
		return getDatatype(PredefinedElkIris.XSD_POSITIVE_INTEGER);
	}

	@Override
	public ElkDatatype getXsdShort() {
		return getDatatype(PredefinedElkIris.XSD_SHORT);
	}

	@Override
	public ElkDatatype getXsdString() {
		return getDatatype(PredefinedElkIris.XSD_STRING);
	}

	@Override
	public ElkDatatype getXsdToken() {
		return getDatatype(PredefinedElkIris.XSD_TOKEN);
	}

	@Override
	public ElkDatatype getXsdUnsignedByte() {
		return getDatatype(PredefinedElkIris.XSD_UNSIGNED_BYTE);
	}

	@Override
	public ElkDatatype getXsdUnsignedInt() {
		return getDatatype(PredefinedElkIris.XSD_UNSIGNED_INT);
	}

	@Override
	public ElkDatatype getXsdUnsignedLong() {
		return getDatatype(PredefinedElkIris.XSD_UNSIGNED_LONG);
	}

	@Override
	public ElkDatatype getXsdUnsignedShort() {
		return getDatatype(PredefinedElkIris.XSD_UNSIGNED_SHORT);
	}

}
