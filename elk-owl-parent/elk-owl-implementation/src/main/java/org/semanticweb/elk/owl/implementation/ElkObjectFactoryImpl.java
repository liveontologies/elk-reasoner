/*
 * #%L
 * ELK Reasoner
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
package org.semanticweb.elk.owl.implementation;

import java.util.List;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkAnnotationAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationProperty;
import org.semanticweb.elk.owl.interfaces.ElkAnonymousIndividual;
import org.semanticweb.elk.owl.interfaces.ElkAsymmetricObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDataAllValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkDataComplementOf;
import org.semanticweb.elk.owl.interfaces.ElkDataExactCardinality;
import org.semanticweb.elk.owl.interfaces.ElkDataExactCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkDataIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkDataMaxCardinality;
import org.semanticweb.elk.owl.interfaces.ElkDataMaxCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataMinCardinality;
import org.semanticweb.elk.owl.interfaces.ElkDataMinCardinalityQualified;
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
import org.semanticweb.elk.owl.interfaces.ElkObjectAllValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectComplementOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectExactCardinality;
import org.semanticweb.elk.owl.interfaces.ElkObjectExactCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasSelf;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasValue;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectInverseOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectMaxCardinality;
import org.semanticweb.elk.owl.interfaces.ElkObjectMaxCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectMinCardinality;
import org.semanticweb.elk.owl.interfaces.ElkObjectMinCardinalityQualified;
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
import org.semanticweb.elk.owl.interfaces.ElkSameIndividualAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubDataPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSymmetricObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkTransitiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.owl.managers.DummyElkObjectManager;
import org.semanticweb.elk.owl.managers.ElkObjectManager;
import org.semanticweb.elk.owl.predefined.PredefinedElkIri;

/**
 * A factory for creating ElkObjects based on the implementations in the
 * org.semanticweb.elk.syntax.implementation package.
 * 
 * @author Markus Kroetzsch
 */
public class ElkObjectFactoryImpl implements ElkObjectFactory {

	protected static final ElkClass ELK_OWL_THING = new ElkClassImpl(
			PredefinedElkIri.OWL_THING);

	protected static final ElkClass ELK_OWL_NOTHING = new ElkClassImpl(
			PredefinedElkIri.OWL_NOTHING);

	protected static final ElkObjectProperty ELK_OWL_TOP_OBJECT_PROPERTY = new ElkObjectPropertyImpl(
			PredefinedElkIri.OWL_TOP_OBJECT_PROPERTY);

	protected static final ElkObjectProperty ELK_OWL_BOTTOM_OBJECT_PROPERTY = new ElkObjectPropertyImpl(
			PredefinedElkIri.OWL_BOTTOM_OBJECT_PROPERTY);

	protected static final ElkDataProperty ELK_OWL_TOP_DATA_PROPERTY = new ElkDataPropertyImpl(
			PredefinedElkIri.OWL_TOP_DATA_PROPERTY);

	protected static final ElkDataProperty ELK_OWL_BOTTOM_DATA_PROPERTY = new ElkDataPropertyImpl(
			PredefinedElkIri.OWL_BOTTOM_DATA_PROPERTY);

	protected static final ElkDatatype ELK_RDF_PLAIN_LITERAL = new ElkDatatypeImpl(
			PredefinedElkIri.RDF_PLAIN_LITERAL);

	protected static final ElkAnnotationAxiom ELK_ANNOTATION_AXIOM = new ElkAnnotationAxiomImpl();

	protected final ElkObjectManager objectManager;

	/**
	 * Construct an ElkObjectFactoryImpl that uses the DummyElkObjectManager.
	 */
	public ElkObjectFactoryImpl() {
		this(new DummyElkObjectManager());
	}

	/**
	 * Construct an ElkObjectFactoryImpl that uses the given object manager for
	 * handling objects. The object manager can be used to manage global object
	 * references, especially to avoid duplicates. Multiple factories can share
	 * one manager if desired. An instance of ElkDummyObjectManager can be used
	 * to ignore duplicates.
	 * 
	 * @param objectManager
	 *            object manager to be used
	 */
	public ElkObjectFactoryImpl(ElkObjectManager objectManager) {
		this.objectManager = objectManager;
	}

	public ElkAnnotationAxiom getAnnotationAxiom() {
		return ELK_ANNOTATION_AXIOM;
	}

	public ElkAnnotationProperty getAnnotationProperty(ElkIri iri) {
		return (ElkAnnotationProperty) objectManager
				.getCanonicalElkObject(new ElkAnnotationPropertyImpl(iri));
	}

	public ElkAnonymousIndividual getAnonymousIndividual(String nodeId) {
		return (ElkAnonymousIndividual) objectManager
				.getCanonicalElkObject(new ElkAnonymousIndividualImpl(nodeId));
	}

	public ElkAsymmetricObjectPropertyAxiom getAsymmetricObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression) {
		return (ElkAsymmetricObjectPropertyAxiom) objectManager
				.getCanonicalElkObject(new ElkAsymmetricObjectPropertyAxiomImpl(
						objectPropertyExpression));
	}

	public ElkClass getClass(ElkIri iri) {
		return (ElkClass) objectManager.getCanonicalElkObject(new ElkClassImpl(
				iri));
	}

	public ElkClassAssertionAxiom getClassAssertionAxiom(
			ElkClassExpression classExpression, ElkIndividual individual) {
		return (ElkClassAssertionAxiom) objectManager
				.getCanonicalElkObject(new ElkClassAssertionAxiomImpl(
						classExpression, individual));
	}

	public ElkDataAllValuesFrom getDataAllValuesFrom(
			ElkDataPropertyExpression dataPropertyExpression,
			ElkDataRange dataRange) {
		return (ElkDataAllValuesFrom) objectManager
				.getCanonicalElkObject(new ElkDataAllValuesFromImpl(
						dataPropertyExpression, dataRange));
	}

	public ElkDataComplementOf getDataComplementOf(ElkDataRange dataRange) {
		return (ElkDataComplementOf) objectManager
				.getCanonicalElkObject(new ElkDataComplementOfImpl(dataRange));
	}

	public ElkDataExactCardinality getDataExactCardinality(
			ElkDataPropertyExpression dataPropertyExpression, int cardinality) {
		return (ElkDataExactCardinality) objectManager
				.getCanonicalElkObject(new ElkDataExactCardinalityImpl(
						dataPropertyExpression, cardinality));
	}

	public ElkDataExactCardinalityQualified getDataExactCardinalityQualified(
			ElkDataPropertyExpression dataPropertyExpression, int cardinality,
			ElkDataRange dataRange) {
		return (ElkDataExactCardinalityQualified) objectManager
				.getCanonicalElkObject(new ElkDataExactCardinalityQualifiedImpl(
						dataPropertyExpression, cardinality, dataRange));
	}

	public ElkDataHasValue getDataHasValue(
			ElkDataPropertyExpression dataPropertyExpression, ElkLiteral literal) {
		return (ElkDataHasValue) objectManager
				.getCanonicalElkObject(new ElkDataHasValueImpl(
						dataPropertyExpression, literal));
	}

	public ElkDataIntersectionOf getDataIntersectionOf(
			ElkDataRange firstDataRange, ElkDataRange secondDataRange,
			ElkDataRange... otherDataRanges) {
		return (ElkDataIntersectionOf) objectManager
				.getCanonicalElkObject(new ElkDataIntersectionOfImpl(
						ElkObjectListObject.varArgsToList(firstDataRange,
								secondDataRange, otherDataRanges)));
	}

	public ElkDataIntersectionOf getDataIntersectionOf(
			List<? extends ElkDataRange> dataRanges) {
		return (ElkDataIntersectionOf) objectManager
				.getCanonicalElkObject(new ElkDataIntersectionOfImpl(dataRanges));
	}

	public ElkDataMaxCardinality getDataMaxCardinality(
			ElkDataPropertyExpression dataPropertyExpression, int cardinality) {
		return (ElkDataMaxCardinality) objectManager
				.getCanonicalElkObject(new ElkDataMaxCardinalityImpl(
						dataPropertyExpression, cardinality));
	}

	public ElkDataMaxCardinalityQualified getDataMaxCardinalityQualified(
			ElkDataPropertyExpression dataPropertyExpression, int cardinality,
			ElkDataRange dataRange) {
		return (ElkDataMaxCardinalityQualified) objectManager
				.getCanonicalElkObject(new ElkDataMaxCardinalityQualifiedImpl(
						dataPropertyExpression, cardinality, dataRange));
	}

	public ElkDataMinCardinality getDataMinCardinality(
			ElkDataPropertyExpression dataPropertyExpression, int cardinality) {
		return (ElkDataMinCardinality) objectManager
				.getCanonicalElkObject(new ElkDataMinCardinalityImpl(
						dataPropertyExpression, cardinality));
	}

	public ElkDataMinCardinalityQualified getDataMinCardinalityQualified(
			ElkDataPropertyExpression dataPropertyExpression, int cardinality,
			ElkDataRange dataRange) {
		return (ElkDataMinCardinalityQualified) objectManager
				.getCanonicalElkObject(new ElkDataMinCardinalityQualifiedImpl(
						dataPropertyExpression, cardinality, dataRange));
	}

	public ElkDataOneOf getDataOneOf(ElkLiteral firstLiteral,
			ElkLiteral... otherLiterals) {
		return (ElkDataOneOf) objectManager
				.getCanonicalElkObject(new ElkDataOneOfImpl(ElkObjectListObject
						.varArgsToList(firstLiteral, otherLiterals)));
	}

	public ElkDataOneOf getDataOneOf(List<? extends ElkLiteral> literals) {
		return (ElkDataOneOf) objectManager
				.getCanonicalElkObject(new ElkDataOneOfImpl(literals));
	}

	public ElkDataProperty getDataProperty(ElkIri iri) {
		return (ElkDataProperty) objectManager
				.getCanonicalElkObject(new ElkDataPropertyImpl(iri));
	}

	public ElkDataPropertyAssertionAxiom getDataPropertyAssertionAxiom(
			ElkDataPropertyExpression dataPropertyExpression,
			ElkIndividual individual, ElkLiteral literal) {
		return (ElkDataPropertyAssertionAxiom) objectManager
				.getCanonicalElkObject(new ElkDataPropertyAssertionAxiomImpl(
						dataPropertyExpression, individual, literal));
	}

	public ElkDataPropertyDomainAxiom getDataPropertyDomainAxiom(
			ElkDataPropertyExpression dataPropertyExpression,
			ElkClassExpression classExpression) {
		return (ElkDataPropertyDomainAxiom) objectManager
				.getCanonicalElkObject(new ElkDataPropertyDomainAxiomImpl(
						dataPropertyExpression, classExpression));
	}

	public ElkDataPropertyRangeAxiom getDataPropertyRangeAxiom(
			ElkDataPropertyExpression dataPropertyExpression,
			ElkDataRange dataRange) {
		return (ElkDataPropertyRangeAxiom) objectManager
				.getCanonicalElkObject(new ElkDataPropertyRangeAxiomImpl(
						dataPropertyExpression, dataRange));
	}

	public ElkDataSomeValuesFrom getDataSomeValuesFrom(
			ElkDataPropertyExpression dataPropertyExpression,
			ElkDataRange dataRange) {
		return (ElkDataSomeValuesFrom) objectManager
				.getCanonicalElkObject(new ElkDataSomeValuesFromImpl(
						dataPropertyExpression, dataRange));
	}

	public ElkDatatype getDatatype(ElkIri iri) {
		return (ElkDatatype) objectManager
				.getCanonicalElkObject(new ElkDatatypeImpl(iri));
	}

	public ElkDatatype getDatatypeRdfPlainLiteral() {
		return ELK_RDF_PLAIN_LITERAL;
	}

	public ElkDatatypeRestriction getDatatypeRestriction(ElkDatatype datatype,
			List<ElkFacetRestriction> facetRestrictions) {
		return (ElkDatatypeRestriction) objectManager
				.getCanonicalElkObject(new ElkDatatypeRestrictionImpl(datatype,
						facetRestrictions));
	}

	public ElkDataUnionOf getDataUnionOf(ElkDataRange firstDataRange,
			ElkDataRange secondDataRange, ElkDataRange... otherDataRanges) {
		return (ElkDataUnionOf) objectManager
				.getCanonicalElkObject(new ElkDataUnionOfImpl(
						ElkObjectListObject.varArgsToList(firstDataRange,
								secondDataRange, otherDataRanges)));
	}

	public ElkDataUnionOf getDataUnionOf(List<? extends ElkDataRange> dataRanges) {
		return (ElkDataUnionOf) objectManager
				.getCanonicalElkObject(new ElkDataUnionOfImpl(dataRanges));
	}

	public ElkDeclarationAxiom getDeclarationAxiom(ElkEntity entity) {
		return (ElkDeclarationAxiom) objectManager
				.getCanonicalElkObject(new ElkDeclarationAxiomImpl(entity));
	}

	public ElkDifferentIndividualsAxiom getDifferentIndividualsAxiom(
			ElkIndividual firstIndividual, ElkIndividual secondIndividual,
			ElkIndividual... otherIndividuals) {
		return (ElkDifferentIndividualsAxiom) objectManager
				.getCanonicalElkObject(new ElkDifferentIndividualsAxiomImpl(
						ElkObjectListObject.varArgsToList(firstIndividual,
								secondIndividual, otherIndividuals)));
	}

	public ElkDifferentIndividualsAxiom getDifferentIndividualsAxiom(
			List<? extends ElkIndividual> individuals) {
		return (ElkDifferentIndividualsAxiom) objectManager
				.getCanonicalElkObject(new ElkDifferentIndividualsAxiomImpl(
						individuals));
	}

	public ElkDisjointClassesAxiom getDisjointClassesAxiom(
			ElkClassExpression firstClassExpression,
			ElkClassExpression secondClassExpression,
			ElkClassExpression... otherClassExpressions) {
		return (ElkDisjointClassesAxiom) objectManager
				.getCanonicalElkObject(new ElkDisjointClassesAxiomImpl(
						ElkClassExpressionListObject.varArgsToList(
								firstClassExpression, secondClassExpression,
								otherClassExpressions)));
	}

	public ElkDisjointClassesAxiom getDisjointClassesAxiom(
			List<? extends ElkClassExpression> disjointClassExpressions) {
		return (ElkDisjointClassesAxiom) objectManager
				.getCanonicalElkObject(new ElkDisjointClassesAxiomImpl(
						disjointClassExpressions));
	}

	public ElkDisjointDataPropertiesAxiom getDisjointDataPropertiesAxiom(
			ElkDataPropertyExpression firstDataPropertyExpression,
			ElkDataPropertyExpression secondDataPropertyExpression,
			ElkDataPropertyExpression... otherDataPropertyExpressions) {
		return (ElkDisjointDataPropertiesAxiom) objectManager
				.getCanonicalElkObject(new ElkDisjointDataPropertiesAxiomImpl(
						ElkObjectListObject.varArgsToList(
								firstDataPropertyExpression,
								secondDataPropertyExpression,
								otherDataPropertyExpressions)));
	}

	public ElkDisjointDataPropertiesAxiom getDisjointDataPropertiesAxiom(
			List<? extends ElkDataPropertyExpression> disjointDataPropertyExpressions) {
		return (ElkDisjointDataPropertiesAxiom) objectManager
				.getCanonicalElkObject(new ElkDisjointDataPropertiesAxiomImpl(
						disjointDataPropertyExpressions));
	}

	public ElkDisjointObjectPropertiesAxiom getDisjointObjectPropertiesAxiom(
			ElkObjectPropertyExpression firstObjectPropertyExpression,
			ElkObjectPropertyExpression secondObjectPropertyExpression,
			ElkObjectPropertyExpression... otherObjectPropertyExpressions) {
		return (ElkDisjointObjectPropertiesAxiom) objectManager
				.getCanonicalElkObject(new ElkDisjointObjectPropertiesAxiomImpl(
						ElkObjectListObject.varArgsToList(
								firstObjectPropertyExpression,
								secondObjectPropertyExpression,
								otherObjectPropertyExpressions)));
	}

	public ElkDisjointObjectPropertiesAxiom getDisjointObjectPropertiesAxiom(
			List<? extends ElkObjectPropertyExpression> disjointObjectPropertyExpressions) {
		return (ElkDisjointObjectPropertiesAxiom) objectManager
				.getCanonicalElkObject(new ElkDisjointObjectPropertiesAxiomImpl(
						disjointObjectPropertyExpressions));
	}

	public ElkDisjointUnionAxiom getDisjointUnionAxiom(ElkClass definedClass,
			ElkClassExpression firstClassExpression,
			ElkClassExpression secondClassExpression,
			ElkClassExpression... otherClassExpressions) {
		return (ElkDisjointUnionAxiom) objectManager
				.getCanonicalElkObject(new ElkDisjointUnionAxiomImpl(
						definedClass, ElkObjectListObject.varArgsToList(
								firstClassExpression, secondClassExpression,
								otherClassExpressions)));
	}

	public ElkDisjointUnionAxiom getDisjointUnionAxiom(ElkClass definedClass,
			List<? extends ElkClassExpression> disjointClassExpressions) {
		return (ElkDisjointUnionAxiom) objectManager
				.getCanonicalElkObject(new ElkDisjointUnionAxiomImpl(
						definedClass, disjointClassExpressions));
	}

	public ElkEquivalentClassesAxiom getEquivalentClassesAxiom(
			ElkClassExpression firstClassExpression,
			ElkClassExpression secondClassExpression,
			ElkClassExpression... otherClassExpressions) {
		return (ElkEquivalentClassesAxiom) objectManager
				.getCanonicalElkObject(new ElkEquivalentClassesAxiomImpl(
						ElkObjectListObject.varArgsToList(firstClassExpression,
								secondClassExpression, otherClassExpressions)));
	}

	public ElkEquivalentClassesAxiom getEquivalentClassesAxiom(
			List<? extends ElkClassExpression> equivalentClassExpressions) {
		return (ElkEquivalentClassesAxiom) objectManager
				.getCanonicalElkObject(new ElkEquivalentClassesAxiomImpl(
						equivalentClassExpressions));
	}

	public ElkEquivalentDataPropertiesAxiom getEquivalentDataPropertiesAxiom(
			ElkDataPropertyExpression firstDataPropertyExpression,
			ElkDataPropertyExpression secondDataPropertyExpression,
			ElkDataPropertyExpression... otherDataPropertyExpressions) {
		return (ElkEquivalentDataPropertiesAxiom) objectManager
				.getCanonicalElkObject(new ElkEquivalentDataPropertiesAxiomImpl(
						ElkObjectListObject.varArgsToList(
								firstDataPropertyExpression,
								secondDataPropertyExpression,
								otherDataPropertyExpressions)));
	}

	public ElkEquivalentDataPropertiesAxiom getEquivalentDataPropertiesAxiom(
			List<? extends ElkDataPropertyExpression> equivalentDataPropertyExpressions) {
		return (ElkEquivalentDataPropertiesAxiom) objectManager
				.getCanonicalElkObject(new ElkEquivalentDataPropertiesAxiomImpl(
						equivalentDataPropertyExpressions));
	}

	public ElkEquivalentObjectPropertiesAxiom getEquivalentObjectPropertiesAxiom(
			ElkObjectPropertyExpression firstObjectPropertyExpression,
			ElkObjectPropertyExpression secondObjectPropertyExpression,
			ElkObjectPropertyExpression... otherObjectPropertyExpressions) {
		return (ElkEquivalentObjectPropertiesAxiom) objectManager
				.getCanonicalElkObject(new ElkEquivalentObjectPropertiesAxiomImpl(
						ElkObjectListObject.varArgsToList(
								firstObjectPropertyExpression,
								secondObjectPropertyExpression,
								otherObjectPropertyExpressions)));
	}

	public ElkEquivalentObjectPropertiesAxiom getEquivalentObjectPropertiesAxiom(
			List<? extends ElkObjectPropertyExpression> equivalentObjectPropertyExpressions) {
		return (ElkEquivalentObjectPropertiesAxiom) objectManager
				.getCanonicalElkObject(new ElkEquivalentObjectPropertiesAxiomImpl(
						equivalentObjectPropertyExpressions));
	}

	public ElkFunctionalDataPropertyAxiom getFunctionalDataPropertyAxiom(
			ElkDataPropertyExpression dataPropertyExpression) {
		return (ElkFunctionalDataPropertyAxiom) objectManager
				.getCanonicalElkObject(new ElkFunctionalDataPropertyAxiomImpl(
						dataPropertyExpression));
	}

	public ElkFunctionalObjectPropertyAxiom getFunctionalObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression) {
		return (ElkFunctionalObjectPropertyAxiom) objectManager
				.getCanonicalElkObject(new ElkFunctionalObjectPropertyAxiomImpl(
						objectPropertyExpression));
	}

	public ElkInverseFunctionalObjectPropertyAxiom getInverseFunctionalObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression) {
		return (ElkInverseFunctionalObjectPropertyAxiom) objectManager
				.getCanonicalElkObject(new ElkInverseFunctionalObjectPropertyAxiomImpl(
						objectPropertyExpression));
	}

	public ElkInverseObjectPropertiesAxiom getInverseObjectPropertiesAxiom(
			ElkObjectPropertyExpression firstObjectPropertyExpression,
			ElkObjectPropertyExpression secondObjectPropertyExpression) {
		return (ElkInverseObjectPropertiesAxiom) objectManager
				.getCanonicalElkObject(new ElkInverseObjectPropertiesAxiomImpl(
						firstObjectPropertyExpression,
						secondObjectPropertyExpression));
	}

	public ElkIrreflexiveObjectPropertyAxiom getIrreflexiveObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression) {
		return (ElkIrreflexiveObjectPropertyAxiom) objectManager
				.getCanonicalElkObject(new ElkIrreflexiveObjectPropertyAxiomImpl(
						objectPropertyExpression));
	}

	public ElkLiteral getLiteral(String lexicalForm, ElkDatatype datatype) {
		return (ElkLiteral) objectManager
				.getCanonicalElkObject(new ElkLiteralImpl(lexicalForm, datatype));
	}

	public ElkNamedIndividual getNamedIndividual(ElkIri iri) {
		return (ElkNamedIndividual) objectManager
				.getCanonicalElkObject(new ElkNamedIndividualImpl(iri));
	}

	public ElkNegativeDataPropertyAssertionAxiom getNegativeDataPropertyAssertionAxiom(
			ElkDataPropertyExpression dataPropertyExpression,
			ElkIndividual individual, ElkLiteral literal) {
		return (ElkNegativeDataPropertyAssertionAxiom) objectManager
				.getCanonicalElkObject(new ElkNegativeDataPropertyAssertionAxiomImpl(
						dataPropertyExpression, individual, literal));
	}

	public ElkNegativeObjectPropertyAssertionAxiom getNegativeObjectPropertyAssertionAxiom(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkIndividual firstIndividual, ElkIndividual secondIndividual) {
		return (ElkNegativeObjectPropertyAssertionAxiom) objectManager
				.getCanonicalElkObject(new ElkNegativeObjectPropertyAssertionAxiomImpl(
						objectPropertyExpression, firstIndividual,
						secondIndividual));
	}

	public ElkObjectAllValuesFrom getObjectAllValuesFrom(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkClassExpression classExpression) {
		return (ElkObjectAllValuesFrom) objectManager
				.getCanonicalElkObject(new ElkObjectAllValuesFromImpl(
						objectPropertyExpression, classExpression));
	}

	public ElkObjectComplementOf getObjectComplementOf(
			ElkClassExpression classExpression) {
		return (ElkObjectComplementOf) objectManager
				.getCanonicalElkObject(new ElkObjectComplementOfImpl(
						classExpression));
	}

	public ElkObjectExactCardinality getObjectExactCardinality(
			ElkObjectPropertyExpression objectPropertyExpression,
			int cardinality) {
		return (ElkObjectExactCardinality) objectManager
				.getCanonicalElkObject(new ElkObjectExactCardinalityImpl(
						objectPropertyExpression, cardinality));
	}

	public ElkObjectExactCardinalityQualified getObjectExactCardinalityQualified(
			ElkObjectPropertyExpression objectPropertyExpression,
			int cardinality, ElkClassExpression classExpression) {
		return (ElkObjectExactCardinalityQualified) objectManager
				.getCanonicalElkObject(new ElkObjectExactCardinalityQualifiedImpl(
						objectPropertyExpression, cardinality, classExpression));
	}

	public ElkObjectHasSelf getObjectHasSelf(
			ElkObjectPropertyExpression objectPropertyExpression) {
		return (ElkObjectHasSelf) objectManager
				.getCanonicalElkObject(new ElkObjectHasSelfImpl(
						objectPropertyExpression));
	}

	public ElkObjectHasValue getObjectHasValue(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkIndividual individual) {
		return (ElkObjectHasValue) objectManager
				.getCanonicalElkObject(new ElkObjectHasValueImpl(
						objectPropertyExpression, individual));
	}

	public ElkObjectIntersectionOf getObjectIntersectionOf(
			ElkClassExpression firstClassExpression,
			ElkClassExpression secondClassExpression,
			ElkClassExpression... otherClassExpressions) {
		return (ElkObjectIntersectionOf) objectManager
				.getCanonicalElkObject(new ElkObjectIntersectionOfImpl(
						ElkObjectListObject.varArgsToList(firstClassExpression,
								secondClassExpression, otherClassExpressions)));
	}

	public ElkObjectIntersectionOf getObjectIntersectionOf(
			List<? extends ElkClassExpression> classExpressions) {
		return (ElkObjectIntersectionOf) objectManager
				.getCanonicalElkObject(new ElkObjectIntersectionOfImpl(
						classExpressions));
	}

	public ElkObjectInverseOf getObjectInverseOf(
			ElkObjectProperty objectProperty) {
		return (ElkObjectInverseOf) objectManager
				.getCanonicalElkObject(new ElkObjectInverseOfImpl(
						objectProperty));
	}

	/**
	 * Obtain access to the classes object manager. Can be used to share the
	 * same object manager among multiple factories.
	 * 
	 * @return
	 */
	public ElkObjectManager getObjectManager() {
		return objectManager;
	}

	public ElkObjectMaxCardinality getObjectMaxCardinality(
			ElkObjectPropertyExpression objectPropertyExpression,
			int cardinality) {
		return (ElkObjectMaxCardinality) objectManager
				.getCanonicalElkObject(new ElkObjectMaxCardinalityImpl(
						objectPropertyExpression, cardinality));
	}

	public ElkObjectMaxCardinalityQualified getObjectMaxCardinalityQualified(
			ElkObjectPropertyExpression objectPropertyExpression,
			int cardinality, ElkClassExpression classExpression) {
		return (ElkObjectMaxCardinalityQualified) objectManager
				.getCanonicalElkObject(new ElkObjectMaxCardinalityQualifiedImpl(
						objectPropertyExpression, cardinality, classExpression));
	}

	public ElkObjectMinCardinality getObjectMinCardinality(
			ElkObjectPropertyExpression objectPropertyExpression,
			int cardinality) {
		return (ElkObjectMinCardinality) objectManager
				.getCanonicalElkObject(new ElkObjectMinCardinalityImpl(
						objectPropertyExpression, cardinality));
	}

	public ElkObjectMinCardinalityQualified getObjectMinCardinalityQualified(
			ElkObjectPropertyExpression objectPropertyExpression,
			int cardinality, ElkClassExpression classExpression) {
		return (ElkObjectMinCardinalityQualified) objectManager
				.getCanonicalElkObject(new ElkObjectMinCardinalityQualifiedImpl(
						objectPropertyExpression, cardinality, classExpression));
	}

	public ElkObjectOneOf getObjectOneOf(ElkIndividual firstIndividual,
			ElkIndividual... otherIndividuals) {
		return (ElkObjectOneOf) objectManager
				.getCanonicalElkObject(new ElkObjectOneOfImpl(
						ElkObjectListObject.varArgsToList(firstIndividual,
								otherIndividuals)));
	}

	public ElkObjectOneOf getObjectOneOf(
			List<? extends ElkIndividual> individuals) {
		return (ElkObjectOneOf) objectManager
				.getCanonicalElkObject(new ElkObjectOneOfImpl(individuals));
	}

	public ElkObjectProperty getObjectProperty(ElkIri iri) {
		return (ElkObjectProperty) objectManager
				.getCanonicalElkObject(new ElkObjectPropertyImpl(iri));
	}

	public ElkObjectPropertyAssertionAxiom getObjectPropertyAssertionAxiom(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkIndividual firstIndividual, ElkIndividual secondIndividual) {
		return (ElkObjectPropertyAssertionAxiom) objectManager
				.getCanonicalElkObject(new ElkObjectPropertyAssertionAxiomImpl(
						objectPropertyExpression, firstIndividual,
						secondIndividual));
	}

	public ElkObjectPropertyChain getObjectPropertyChain(
			List<? extends ElkObjectPropertyExpression> objectPropertyExpressions) {
		return (ElkObjectPropertyChain) objectManager
				.getCanonicalElkObject(new ElkObjectPropertyChainImpl(
						objectPropertyExpressions));
	}

	public ElkObjectPropertyDomainAxiom getObjectPropertyDomainAxiom(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkClassExpression classExpression) {
		return (ElkObjectPropertyDomainAxiom) objectManager
				.getCanonicalElkObject(new ElkObjectPropertyDomainAxiomImpl(
						objectPropertyExpression, classExpression));
	}

	public ElkObjectPropertyRangeAxiom getObjectPropertyRangeAxiom(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkClassExpression classExpression) {
		return (ElkObjectPropertyRangeAxiom) objectManager
				.getCanonicalElkObject(new ElkObjectPropertyRangeAxiomImpl(
						objectPropertyExpression, classExpression));
	}

	public ElkObjectSomeValuesFrom getObjectSomeValuesFrom(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkClassExpression classExpression) {
		return (ElkObjectSomeValuesFrom) objectManager
				.getCanonicalElkObject(new ElkObjectSomeValuesFromImpl(
						objectPropertyExpression, classExpression));
	}

	public ElkObjectUnionOf getObjectUnionOf(
			ElkClassExpression firstClassExpression,
			ElkClassExpression secondClassExpression,
			ElkClassExpression... otherClassExpressions) {
		return (ElkObjectUnionOf) objectManager
				.getCanonicalElkObject(new ElkObjectUnionOfImpl(
						ElkObjectListObject.varArgsToList(firstClassExpression,
								secondClassExpression, otherClassExpressions)));
	}

	public ElkObjectUnionOf getObjectUnionOf(
			List<? extends ElkClassExpression> classExpressions) {
		return (ElkObjectUnionOf) objectManager
				.getCanonicalElkObject(new ElkObjectUnionOfImpl(
						classExpressions));
	}

	public ElkDataProperty getOwlBottomDataProperty() {
		return ElkObjectFactoryImpl.ELK_OWL_BOTTOM_DATA_PROPERTY;
	}

	public ElkObjectProperty getOwlBottomObjectProperty() {
		return ElkObjectFactoryImpl.ELK_OWL_BOTTOM_OBJECT_PROPERTY;
	}

	public ElkClass getOwlNothing() {
		return ElkObjectFactoryImpl.ELK_OWL_NOTHING;
	}

	public ElkClass getOwlThing() {
		return ElkObjectFactoryImpl.ELK_OWL_THING;
	}

	public ElkDataProperty getOwlTopDataProperty() {
		return ElkObjectFactoryImpl.ELK_OWL_TOP_DATA_PROPERTY;
	}

	public ElkObjectProperty getOwlTopObjectProperty() {
		return ElkObjectFactoryImpl.ELK_OWL_TOP_OBJECT_PROPERTY;
	}

	public ElkReflexiveObjectPropertyAxiom getReflexiveObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression) {
		return (ElkReflexiveObjectPropertyAxiom) objectManager
				.getCanonicalElkObject(new ElkReflexiveObjectPropertyAxiomImpl(
						objectPropertyExpression));
	}

	public ElkSameIndividualAxiom getSameIndividualAxiom(
			ElkIndividual firstIndividual, ElkIndividual secondIndividual,
			ElkIndividual... otherIndividuals) {
		return (ElkSameIndividualAxiom) objectManager
				.getCanonicalElkObject(new ElkSameIndividualAxiomImpl(
						ElkObjectListObject.varArgsToList(firstIndividual,
								secondIndividual, otherIndividuals)));
	}

	public ElkSameIndividualAxiom getSameIndividualAxiom(
			List<? extends ElkIndividual> individuals) {
		return (ElkSameIndividualAxiom) objectManager
				.getCanonicalElkObject(new ElkSameIndividualAxiomImpl(
						individuals));
	}

	public ElkSubClassOfAxiom getSubClassOfAxiom(
			ElkClassExpression subClassExpression,
			ElkClassExpression superClassExpression) {
		return (ElkSubClassOfAxiom) objectManager
				.getCanonicalElkObject(new ElkSubClassOfAxiomImpl(
						subClassExpression, superClassExpression));
	}

	public ElkSubDataPropertyOfAxiom getSubDataPropertyOfAxiom(
			ElkDataPropertyExpression subDataPropertyExpression,
			ElkDataPropertyExpression superDataPropertyExpression) {
		return (ElkSubDataPropertyOfAxiom) objectManager
				.getCanonicalElkObject(new ElkSubDataPropertyOfAxiomImpl(
						subDataPropertyExpression, superDataPropertyExpression));
	}

	public ElkSubObjectPropertyOfAxiom getSubObjectPropertyOfAxiom(
			ElkSubObjectPropertyExpression subObjectPropertyExpression,
			ElkObjectPropertyExpression superObjectPropertyExpression) {
		return (ElkSubObjectPropertyOfAxiom) objectManager
				.getCanonicalElkObject(new ElkSubObjectPropertyOfAxiomImpl(
						subObjectPropertyExpression,
						superObjectPropertyExpression));
	}

	public ElkSymmetricObjectPropertyAxiom getSymmetricObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression) {
		return (ElkSymmetricObjectPropertyAxiom) objectManager
				.getCanonicalElkObject(new ElkSymmetricObjectPropertyAxiomImpl(
						objectPropertyExpression));
	}

	public ElkTransitiveObjectPropertyAxiom getTransitiveObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression) {
		return (ElkTransitiveObjectPropertyAxiom) objectManager
				.getCanonicalElkObject(new ElkTransitiveObjectPropertyAxiomImpl(
						objectPropertyExpression));
	}

	@Override
	public ElkHasKeyAxiom getHasKeyAxiom(ElkClassExpression classExpr,
			Set<ElkObjectPropertyExpression> objectPEs,
			Set<ElkDataPropertyExpression> dataPEs) {

		return (ElkHasKeyAxiom)objectManager.getCanonicalElkObject(new ElkHasKeyAxiomImpl(classExpr, objectPEs, dataPEs));
	}
	
	@Override
	public ElkDatatypeDefinitionAxiom getDatatypeDefinitionAxiom( ElkDatatype datatype, ElkDataRange dataRange) {
		
		return 	(ElkDatatypeDefinitionAxiom)objectManager.getCanonicalElkObject(new ElkDatatypeDefinitionAxiomImpl(datatype, dataRange));
	}
}