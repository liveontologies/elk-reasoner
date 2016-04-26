package org.semanticweb.elk.owl.comparison;

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

import org.semanticweb.elk.owl.interfaces.ElkAnnotation;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationProperty;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationPropertyRangeAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAnonymousIndividual;
import org.semanticweb.elk.owl.interfaces.ElkAsymmetricObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
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
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyRangeAxiom;
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
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentDataPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkFacetRestriction;
import org.semanticweb.elk.owl.interfaces.ElkFunctionalDataPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkFunctionalObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkHasKeyAxiom;
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
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * A visitor for computing hash codes for {@link ElkObject}s modulo
 * syntactic equality. Two {@link ElkObject}s are syntactically equal if all
 * their corresponding values are syntactically equal. Syntactically equal
 * objects should return the same hash values.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @see ElkObjectEquality
 *
 */
public class ElkObjectHash implements ElkObjectVisitor<Integer> {

	private static ElkObjectHash INSTANCE_ = new ElkObjectHash();

	private ElkObjectHash() {

	}
	
	public static ElkObjectVisitor<Integer> getInstance() {
		return INSTANCE_;
	}

	private static int combinedHashCode(int... hashes) {
		return HashGenerator.combineListHash(hashes);
	}

	private static int hashCode(Object object) {
		return object.hashCode();
	}
	
	private static int hashCode(int i) {
		return i;
	}

	@Override
	public Integer visit(ElkAnnotationAssertionAxiom axiom) {
		return combinedHashCode(hashCode(ElkAnnotationAssertionAxiom.class),
				hashCode(axiom.getSubject()), hashCode(axiom.getProperty()),
				hashCode(axiom.getValue()));
	}

	@Override
	public Integer visit(ElkAnnotationPropertyDomainAxiom axiom) {
		return combinedHashCode(
				hashCode(ElkAnnotationPropertyDomainAxiom.class),
				hashCode(axiom.getProperty()), hashCode(axiom.getDomain()));
	}

	@Override
	public Integer visit(ElkAnnotationPropertyRangeAxiom axiom) {
		return combinedHashCode(hashCode(ElkAnnotationPropertyRangeAxiom.class),
				hashCode(axiom.getProperty()), hashCode(axiom.getRange()));
	}

	@Override
	public Integer visit(ElkSubAnnotationPropertyOfAxiom axiom) {
		return combinedHashCode(hashCode(ElkSubAnnotationPropertyOfAxiom.class),
				hashCode(axiom.getSubAnnotationProperty()),
				hashCode(axiom.getSuperAnnotationProperty()));
	}

	@Override
	public Integer visit(ElkClassAssertionAxiom axiom) {
		return combinedHashCode(hashCode(ElkClassAssertionAxiom.class),
				hashCode(axiom.getClassExpression()),
				hashCode(axiom.getIndividual()));
	}

	@Override
	public Integer visit(ElkDifferentIndividualsAxiom axiom) {
		return combinedHashCode(hashCode(ElkDifferentIndividualsAxiom.class),
				hashCode(axiom.getIndividuals()));
	}

	@Override
	public Integer visit(ElkDataPropertyAssertionAxiom axiom) {
		return combinedHashCode(hashCode(ElkDataPropertyAssertionAxiom.class),
				hashCode(axiom.getProperty()), hashCode(axiom.getObject()),
				hashCode(axiom.getSubject()));
	}

	@Override
	public Integer visit(ElkNegativeDataPropertyAssertionAxiom axiom) {
		return combinedHashCode(
				hashCode(ElkNegativeDataPropertyAssertionAxiom.class),
				hashCode(axiom.getProperty()), hashCode(axiom.getObject()),
				hashCode(axiom.getSubject()));
	}

	@Override
	public Integer visit(ElkNegativeObjectPropertyAssertionAxiom axiom) {
		return combinedHashCode(
				hashCode(ElkNegativeObjectPropertyAssertionAxiom.class),
				hashCode(axiom.getProperty()), hashCode(axiom.getObject()),
				hashCode(axiom.getSubject()));
	}

	@Override
	public Integer visit(ElkObjectPropertyAssertionAxiom axiom) {
		return combinedHashCode(hashCode(ElkObjectPropertyAssertionAxiom.class),
				hashCode(axiom.getProperty()), hashCode(axiom.getObject()),
				hashCode(axiom.getSubject()));
	}

	@Override
	public Integer visit(ElkSameIndividualAxiom axiom) {
		return combinedHashCode(hashCode(ElkSameIndividualAxiom.class),
				hashCode(axiom.getIndividuals()));
	}

	@Override
	public Integer visit(ElkDisjointClassesAxiom axiom) {
		return combinedHashCode(hashCode(ElkDisjointClassesAxiom.class),
				hashCode(axiom.getClassExpressions()));
	}

	@Override
	public Integer visit(ElkDisjointUnionAxiom axiom) {
		return combinedHashCode(hashCode(ElkDisjointUnionAxiom.class),
				hashCode(axiom.getDefinedClass()),
				hashCode(axiom.getClassExpressions()));
	}

	@Override
	public Integer visit(ElkEquivalentClassesAxiom axiom) {
		return combinedHashCode(hashCode(ElkEquivalentClassesAxiom.class),
				hashCode(axiom.getClassExpressions()));
	}

	@Override
	public Integer visit(ElkSubClassOfAxiom axiom) {
		return combinedHashCode(hashCode(ElkSubClassOfAxiom.class),
				hashCode(axiom.getSubClassExpression()),
				hashCode(axiom.getSuperClassExpression()));
	}

	@Override
	public Integer visit(ElkDataPropertyDomainAxiom axiom) {
		return combinedHashCode(hashCode(ElkDataPropertyDomainAxiom.class),
				hashCode(axiom.getProperty()), hashCode(axiom.getDomain()));
	}

	@Override
	public Integer visit(ElkDataPropertyRangeAxiom axiom) {
		return combinedHashCode(hashCode(ElkDataPropertyRangeAxiom.class),
				hashCode(axiom.getProperty()), hashCode(axiom.getRange()));
	}

	@Override
	public Integer visit(ElkDisjointDataPropertiesAxiom axiom) {
		return combinedHashCode(hashCode(ElkDisjointDataPropertiesAxiom.class),
				hashCode(axiom.getDataPropertyExpressions()));
	}

	@Override
	public Integer visit(ElkEquivalentDataPropertiesAxiom axiom) {
		return combinedHashCode(
				hashCode(ElkEquivalentDataPropertiesAxiom.class),
				hashCode(axiom.getDataPropertyExpressions()));
	}

	@Override
	public Integer visit(ElkFunctionalDataPropertyAxiom axiom) {
		return combinedHashCode(hashCode(ElkFunctionalDataPropertyAxiom.class),
				hashCode(axiom.getProperty()));
	}

	@Override
	public Integer visit(ElkSubDataPropertyOfAxiom axiom) {
		return combinedHashCode(hashCode(ElkSubDataPropertyOfAxiom.class),
				hashCode(axiom.getSubDataPropertyExpression()),
				hashCode(axiom.getSuperDataPropertyExpression()));
	}

	@Override
	public Integer visit(ElkDatatypeDefinitionAxiom axiom) {
		return combinedHashCode(hashCode(ElkDatatypeDefinitionAxiom.class),
				hashCode(axiom.getDatatype()), hashCode(axiom.getDataRange()));
	}

	@Override
	public Integer visit(ElkDeclarationAxiom axiom) {
		return combinedHashCode(hashCode(ElkDeclarationAxiom.class),
				hashCode(axiom.getEntity()));
	}

	@Override
	public Integer visit(ElkHasKeyAxiom axiom) {
		return combinedHashCode(hashCode(ElkHasKeyAxiom.class),
				hashCode(axiom.getClassExpression()),
				hashCode(axiom.getObjectPropertyExpressions()),
				hashCode(axiom.getDataPropertyExpressions()));
	}

	@Override
	public Integer visit(ElkAsymmetricObjectPropertyAxiom axiom) {
		return combinedHashCode(
				hashCode(ElkAsymmetricObjectPropertyAxiom.class),
				hashCode(axiom.getProperty()));
	}

	@Override
	public Integer visit(ElkDisjointObjectPropertiesAxiom axiom) {
		return combinedHashCode(
				hashCode(ElkDisjointObjectPropertiesAxiom.class),
				hashCode(axiom.getObjectPropertyExpressions()));
	}

	@Override
	public Integer visit(ElkEquivalentObjectPropertiesAxiom axiom) {
		return combinedHashCode(
				hashCode(ElkEquivalentObjectPropertiesAxiom.class),
				hashCode(axiom.getObjectPropertyExpressions()));
	}

	@Override
	public Integer visit(ElkFunctionalObjectPropertyAxiom axiom) {
		return combinedHashCode(
				hashCode(ElkFunctionalObjectPropertyAxiom.class),
				hashCode(axiom.getProperty()));
	}

	@Override
	public Integer visit(ElkInverseFunctionalObjectPropertyAxiom axiom) {
		return combinedHashCode(
				hashCode(ElkInverseFunctionalObjectPropertyAxiom.class),
				hashCode(axiom.getProperty()));
	}

	@Override
	public Integer visit(ElkInverseObjectPropertiesAxiom axiom) {
		return combinedHashCode(hashCode(ElkInverseObjectPropertiesAxiom.class),
				hashCode(axiom.getFirstObjectPropertyExpression()),
				hashCode(axiom.getSecondObjectPropertyExpression()));
	}

	@Override
	public Integer visit(ElkIrreflexiveObjectPropertyAxiom axiom) {
		return combinedHashCode(
				hashCode(ElkIrreflexiveObjectPropertyAxiom.class),
				hashCode(axiom.getProperty()));
	}

	@Override
	public Integer visit(ElkObjectPropertyDomainAxiom axiom) {
		return combinedHashCode(hashCode(ElkObjectPropertyDomainAxiom.class),
				hashCode(axiom.getProperty()), hashCode(axiom.getDomain()));
	}

	@Override
	public Integer visit(ElkObjectPropertyRangeAxiom axiom) {
		return combinedHashCode(hashCode(ElkObjectPropertyRangeAxiom.class),
				hashCode(axiom.getProperty()), hashCode(axiom.getRange()));
	}

	@Override
	public Integer visit(ElkReflexiveObjectPropertyAxiom axiom) {
		return combinedHashCode(hashCode(ElkReflexiveObjectPropertyAxiom.class),
				hashCode(axiom.getProperty()));
	}

	@Override
	public Integer visit(ElkSubObjectPropertyOfAxiom axiom) {
		return combinedHashCode(hashCode(ElkSubObjectPropertyOfAxiom.class),
				hashCode(axiom.getSubObjectPropertyExpression()),
				hashCode(axiom.getSuperObjectPropertyExpression()));
	}

	@Override
	public Integer visit(ElkSymmetricObjectPropertyAxiom axiom) {
		return combinedHashCode(hashCode(ElkSymmetricObjectPropertyAxiom.class),
				hashCode(axiom.getProperty()));
	}

	@Override
	public Integer visit(ElkTransitiveObjectPropertyAxiom axiom) {
		return combinedHashCode(
				hashCode(ElkTransitiveObjectPropertyAxiom.class),
				hashCode(axiom.getProperty()));
	}

	@Override
	public Integer visit(ElkSWRLRule axiom) {
		return combinedHashCode(hashCode(ElkSWRLRule.class));
	}

	@Override
	public Integer visit(ElkClass expression) {
		return combinedHashCode(hashCode(ElkClass.class),
				hashCode(expression.getIri()));
	}

	@Override
	public Integer visit(ElkDataAllValuesFrom expression) {
		return combinedHashCode(hashCode(ElkDataAllValuesFrom.class),
				hashCode(expression.getDataPropertyExpressions()),
				hashCode(expression.getDataRange()));
	}

	@Override
	public Integer visit(ElkDataSomeValuesFrom expression) {
		return combinedHashCode(hashCode(ElkDataSomeValuesFrom.class),
				hashCode(expression.getDataPropertyExpressions()),
				hashCode(expression.getDataRange()));
	}

	@Override
	public Integer visit(ElkObjectComplementOf expression) {
		return combinedHashCode(hashCode(ElkObjectComplementOf.class),
				hashCode(expression.getClassExpression()));
	}

	@Override
	public Integer visit(ElkObjectIntersectionOf expression) {
		return combinedHashCode(hashCode(ElkObjectSomeValuesFrom.class),
				hashCode(expression.getClassExpressions()));
	}

	@Override
	public Integer visit(ElkObjectOneOf expression) {
		return combinedHashCode(hashCode(ElkObjectOneOf.class),
				hashCode(expression.getIndividuals()));
	}

	@Override
	public Integer visit(ElkObjectUnionOf expression) {
		return combinedHashCode(hashCode(ElkObjectUnionOf.class),
				hashCode(expression.getClassExpressions()));
	}

	@Override
	public Integer visit(ElkDataExactCardinalityQualified expression) {
		return combinedHashCode(
				hashCode(ElkDataExactCardinalityQualified.class),
				hashCode(expression.getProperty()),
				hashCode(expression.getCardinality()),
				hashCode(expression.getFiller()));
	}

	@Override
	public Integer visit(ElkDataMaxCardinalityQualified expression) {
		return combinedHashCode(hashCode(ElkDataMaxCardinalityQualified.class),
				hashCode(expression.getProperty()),
				hashCode(expression.getCardinality()),
				hashCode(expression.getFiller()));
	}

	@Override
	public Integer visit(ElkDataMinCardinalityQualified expression) {
		return combinedHashCode(hashCode(ElkDataMinCardinalityQualified.class),
				hashCode(expression.getProperty()),
				hashCode(expression.getCardinality()),
				hashCode(expression.getFiller()));
	}

	@Override
	public Integer visit(ElkObjectExactCardinalityQualified expression) {
		return combinedHashCode(
				hashCode(ElkObjectExactCardinalityQualified.class),
				hashCode(expression.getProperty()),
				hashCode(expression.getCardinality()),
				hashCode(expression.getFiller()));
	}

	@Override
	public Integer visit(ElkObjectMaxCardinalityQualified expression) {
		return combinedHashCode(
				hashCode(ElkObjectMaxCardinalityQualified.class),
				hashCode(expression.getProperty()),
				hashCode(expression.getCardinality()),
				hashCode(expression.getFiller()));
	}

	@Override
	public Integer visit(ElkObjectMinCardinalityQualified expression) {
		return combinedHashCode(
				hashCode(ElkObjectMinCardinalityQualified.class),
				hashCode(expression.getProperty()),
				hashCode(expression.getCardinality()),
				hashCode(expression.getFiller()));
	}

	@Override
	public Integer visit(ElkDataExactCardinalityUnqualified expression) {
		return combinedHashCode(
				hashCode(ElkDataExactCardinalityUnqualified.class),
				hashCode(expression.getProperty()),
				hashCode(expression.getCardinality()));
	}

	@Override
	public Integer visit(ElkDataMaxCardinalityUnqualified expression) {
		return combinedHashCode(
				hashCode(ElkDataMaxCardinalityUnqualified.class),
				hashCode(expression.getProperty()),
				hashCode(expression.getCardinality()));
	}

	@Override
	public Integer visit(ElkDataMinCardinalityUnqualified expression) {
		return combinedHashCode(
				hashCode(ElkDataMinCardinalityUnqualified.class),
				hashCode(expression.getProperty()),
				hashCode(expression.getCardinality()));
	}

	@Override
	public Integer visit(ElkObjectExactCardinalityUnqualified expression) {
		return combinedHashCode(
				hashCode(ElkObjectExactCardinalityUnqualified.class),
				hashCode(expression.getProperty()),
				hashCode(expression.getCardinality()));
	}

	@Override
	public Integer visit(ElkObjectMaxCardinalityUnqualified expression) {
		return combinedHashCode(
				hashCode(ElkObjectMaxCardinalityUnqualified.class),
				hashCode(expression.getProperty()),
				hashCode(expression.getCardinality()));
	}

	@Override
	public Integer visit(ElkObjectMinCardinalityUnqualified expression) {
		return combinedHashCode(
				hashCode(ElkObjectMinCardinalityUnqualified.class),
				hashCode(expression.getProperty()),
				hashCode(expression.getCardinality()));
	}

	@Override
	public Integer visit(ElkObjectHasSelf expression) {
		return combinedHashCode(hashCode(ElkObjectHasSelf.class),
				hashCode(expression.getProperty()));
	}

	@Override
	public Integer visit(ElkDataHasValue expression) {
		return combinedHashCode(hashCode(ElkDataHasValue.class),
				hashCode(expression.getProperty()),
				hashCode(expression.getFiller()));
	}

	@Override
	public Integer visit(ElkObjectAllValuesFrom expression) {
		return combinedHashCode(hashCode(ElkObjectAllValuesFrom.class),
				hashCode(expression.getProperty()),
				hashCode(expression.getFiller()));
	}

	@Override
	public Integer visit(ElkObjectHasValue expression) {
		return combinedHashCode(hashCode(ElkObjectHasValue.class),
				hashCode(expression.getProperty()),
				hashCode(expression.getFiller()));
	}

	@Override
	public Integer visit(ElkObjectSomeValuesFrom expression) {
		return combinedHashCode(hashCode(ElkObjectSomeValuesFrom.class),
				hashCode(expression.getProperty()),
				hashCode(expression.getFiller()));
	}

	@Override
	public Integer visit(ElkObjectPropertyChain expression) {
		return combinedHashCode(hashCode(ElkObjectPropertyChain.class),
				hashCode(expression.getObjectPropertyExpressions()));
	}

	@Override
	public Integer visit(ElkObjectInverseOf expression) {
		return combinedHashCode(hashCode(ElkObjectInverseOf.class),
				hashCode(expression.getObjectProperty()));
	}

	@Override
	public Integer visit(ElkObjectProperty expression) {
		return combinedHashCode(hashCode(ElkObjectProperty.class),
				hashCode(expression.getIri()));
	}

	@Override
	public Integer visit(ElkDataProperty expression) {
		return combinedHashCode(hashCode(ElkDataProperty.class),
				hashCode(expression.getIri()));
	}

	@Override
	public Integer visit(ElkAnonymousIndividual expression) {
		return combinedHashCode(hashCode(ElkAnonymousIndividual.class),
				hashCode(expression.getNodeId()));
	}

	@Override
	public Integer visit(ElkNamedIndividual expression) {
		return combinedHashCode(hashCode(ElkNamedIndividual.class),
				hashCode(expression.getIri()));
	}

	@Override
	public Integer visit(ElkLiteral expression) {
		return combinedHashCode(hashCode(ElkLiteral.class),
				hashCode(expression.getLexicalForm()),
				hashCode(expression.getDatatype()));
	}

	@Override
	public Integer visit(ElkAnnotationProperty expression) {
		return combinedHashCode(hashCode(ElkAnnotationProperty.class),
				hashCode(expression.getIri()));
	}

	@Override
	public Integer visit(ElkDatatype expression) {
		return combinedHashCode(hashCode(ElkDatatype.class),
				hashCode(expression.getIri()));
	}

	@Override
	public Integer visit(ElkDataComplementOf expression) {
		return combinedHashCode(hashCode(ElkDataComplementOf.class),
				hashCode(expression.getDataRange()));
	}

	@Override
	public Integer visit(ElkDataIntersectionOf expression) {
		return combinedHashCode(hashCode(ElkDataIntersectionOf.class),
				hashCode(expression.getDataRanges()));
	}

	@Override
	public Integer visit(ElkDataOneOf expression) {
		return combinedHashCode(hashCode(ElkDataOneOf.class),
				hashCode(expression.getLiterals()));
	}

	@Override
	public Integer visit(ElkDatatypeRestriction expression) {
		return combinedHashCode(hashCode(ElkDatatypeRestriction.class),
				hashCode(expression.getDatatype()),
				hashCode(expression.getFacetRestrictions()));
	}

	@Override
	public Integer visit(ElkDataUnionOf expression) {
		return combinedHashCode(hashCode(ElkDataUnionOf.class),
				hashCode(expression.getDataRanges()));
	}

	@Override
	public Integer visit(ElkFacetRestriction expression) {
		return combinedHashCode(hashCode(ElkFacetRestriction.class),
				hashCode(expression.getConstrainingFacet()),
				hashCode(expression.getRestrictionValue()));
	}

	@Override
	public Integer visit(ElkAnnotation expression) {
		return combinedHashCode(hashCode(ElkAnnotation.class),
				hashCode(expression.getProperty()),
				hashCode(expression.getValue()));
	}

	@Override
	public Integer visit(ElkFullIri expression) {
		return combinedHashCode(hashCode(ElkIri.class),
				hashCode(expression.getFullIriAsString()));
	}

	@Override
	public Integer visit(ElkAbbreviatedIri expression) {
		return combinedHashCode(hashCode(ElkIri.class),
				hashCode(expression.getFullIriAsString()));
	}
}
