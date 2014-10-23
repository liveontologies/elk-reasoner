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
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
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
import org.semanticweb.elk.owl.managers.DummyElkObjectRecycler;
import org.semanticweb.elk.owl.managers.ElkObjectRecycler;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.owl.predefined.PredefinedElkIri;

/**
 * A factory for creating {@link ElkObject}s based on the implementations in the
 * {@link org.semanticweb.elk.owl.implementation} package.
 * 
 * @author Markus Kroetzsch
 */
public class ElkObjectFactoryImpl implements ElkObjectFactory {

	protected static final ElkObjectProperty ELK_OWL_TOP_OBJECT_PROPERTY = new ElkObjectPropertyImpl(
			PredefinedElkIri.OWL_TOP_OBJECT_PROPERTY.get());

	protected static final ElkObjectProperty ELK_OWL_BOTTOM_OBJECT_PROPERTY = new ElkObjectPropertyImpl(
			PredefinedElkIri.OWL_BOTTOM_OBJECT_PROPERTY.get());

	protected static final ElkDataProperty ELK_OWL_TOP_DATA_PROPERTY = new ElkDataPropertyImpl(
			PredefinedElkIri.OWL_TOP_DATA_PROPERTY.get());

	protected static final ElkDataProperty ELK_OWL_BOTTOM_DATA_PROPERTY = new ElkDataPropertyImpl(
			PredefinedElkIri.OWL_BOTTOM_DATA_PROPERTY.get());

	protected static final ElkDatatype ELK_RDF_PLAIN_LITERAL = new ElkDatatypeImpl(
			PredefinedElkIri.RDF_PLAIN_LITERAL.get());

	protected final ElkObjectRecycler objectRecycler_;

	/**
	 * Construct an {@link ElkObjectFactoryImpl} that uses the
	 * {@link DummyElkObjectRecycler} for recycling {@link ElkObject}s.
	 */
	public ElkObjectFactoryImpl() {
		this(new DummyElkObjectRecycler());
	}

	/**
	 * Construct an {@link ElkObjectFactoryImpl} that uses the given
	 * {@link ElkObjectRecycler} for recycling {@link ElkObject}s.
	 * 
	 * @param objectRecycler
	 *            object manager to be used
	 */
	public ElkObjectFactoryImpl(ElkObjectRecycler objectRecycler) {
		this.objectRecycler_ = objectRecycler;
	}

	@Override
	public ElkAnnotationProperty getAnnotationProperty(ElkIri iri) {
		return (ElkAnnotationProperty) objectRecycler_
				.recycle(new ElkAnnotationPropertyImpl(iri));
	}

	@Override
	public ElkAnonymousIndividual getAnonymousIndividual(String nodeId) {
		return (ElkAnonymousIndividual) objectRecycler_
				.recycle(new ElkAnonymousIndividualImpl(nodeId));
	}

	@Override
	public ElkAsymmetricObjectPropertyAxiom getAsymmetricObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression) {
		return (ElkAsymmetricObjectPropertyAxiom) objectRecycler_
				.recycle(new ElkAsymmetricObjectPropertyAxiomImpl(
						objectPropertyExpression));
	}

	@Override
	public ElkClass getClass(ElkIri iri) {
		return (ElkClass) objectRecycler_.recycle(new ElkClassImpl(iri));
	}

	@Override
	public ElkClassAssertionAxiom getClassAssertionAxiom(
			ElkClassExpression classExpression, ElkIndividual individual) {
		return (ElkClassAssertionAxiom) objectRecycler_
				.recycle(new ElkClassAssertionAxiomImpl(classExpression,
						individual));
	}

	@Override
	public ElkDataComplementOf getDataComplementOf(ElkDataRange dataRange) {
		return (ElkDataComplementOf) objectRecycler_
				.recycle(new ElkDataComplementOfImpl(dataRange));
	}

	@Override
	public ElkDataExactCardinalityUnqualified getDataExactCardinalityUnqualified(
			ElkDataPropertyExpression dataPropertyExpression, int cardinality) {
		return (ElkDataExactCardinalityUnqualified) objectRecycler_
				.recycle(new ElkDataExactCardinalityUnqualifiedImpl(
						dataPropertyExpression, cardinality));
	}

	@Override
	public ElkDataExactCardinalityQualified getDataExactCardinalityQualified(
			ElkDataPropertyExpression dataPropertyExpression, int cardinality,
			ElkDataRange dataRange) {
		return (ElkDataExactCardinalityQualified) objectRecycler_
				.recycle(new ElkDataExactCardinalityQualifiedImpl(
						dataPropertyExpression, cardinality, dataRange));
	}

	@Override
	public ElkDataHasValue getDataHasValue(
			ElkDataPropertyExpression dataPropertyExpression, ElkLiteral literal) {
		return (ElkDataHasValue) objectRecycler_
				.recycle(new ElkDataHasValueImpl(dataPropertyExpression,
						literal));
	}

	@Override
	public ElkDataIntersectionOf getDataIntersectionOf(
			ElkDataRange firstDataRange, ElkDataRange secondDataRange,
			ElkDataRange... otherDataRanges) {
		return (ElkDataIntersectionOf) objectRecycler_
				.recycle(new ElkDataIntersectionOfImpl(ElkObjectListObject
						.varArgsToList(firstDataRange, secondDataRange,
								otherDataRanges)));
	}

	@Override
	public ElkDataIntersectionOf getDataIntersectionOf(
			List<? extends ElkDataRange> dataRanges) {
		return (ElkDataIntersectionOf) objectRecycler_
				.recycle(new ElkDataIntersectionOfImpl(dataRanges));
	}

	@Override
	public ElkDataMaxCardinalityUnqualified getDataMaxCardinalityUnqualified(
			ElkDataPropertyExpression dataPropertyExpression, int cardinality) {
		return (ElkDataMaxCardinalityUnqualified) objectRecycler_
				.recycle(new ElkDataMaxCardinalityUnqualifiedImpl(
						dataPropertyExpression, cardinality));
	}

	@Override
	public ElkDataMaxCardinalityQualified getDataMaxCardinalityQualified(
			ElkDataPropertyExpression dataPropertyExpression, int cardinality,
			ElkDataRange dataRange) {
		return (ElkDataMaxCardinalityQualified) objectRecycler_
				.recycle(new ElkDataMaxCardinalityQualifiedImpl(
						dataPropertyExpression, cardinality, dataRange));
	}

	@Override
	public ElkDataMinCardinalityUnqualified getDataMinCardinalityUnqualified(
			ElkDataPropertyExpression dataPropertyExpression, int cardinality) {
		return (ElkDataMinCardinalityUnqualified) objectRecycler_
				.recycle(new ElkDataMinCardinalityUnqualifiedImpl(
						dataPropertyExpression, cardinality));
	}

	@Override
	public ElkDataMinCardinalityQualified getDataMinCardinalityQualified(
			ElkDataPropertyExpression dataPropertyExpression, int cardinality,
			ElkDataRange dataRange) {
		return (ElkDataMinCardinalityQualified) objectRecycler_
				.recycle(new ElkDataMinCardinalityQualifiedImpl(
						dataPropertyExpression, cardinality, dataRange));
	}

	@Override
	public ElkDataOneOf getDataOneOf(ElkLiteral firstLiteral,
			ElkLiteral... otherLiterals) {
		return (ElkDataOneOf) objectRecycler_
				.recycle(new ElkDataOneOfImpl(ElkObjectListObject
						.varArgsToList(firstLiteral, otherLiterals)));
	}

	@Override
	public ElkDataOneOf getDataOneOf(List<? extends ElkLiteral> literals) {
		return (ElkDataOneOf) objectRecycler_.recycle(new ElkDataOneOfImpl(
				literals));
	}

	@Override
	public ElkDataProperty getDataProperty(ElkIri iri) {
		return (ElkDataProperty) objectRecycler_
				.recycle(new ElkDataPropertyImpl(iri));
	}

	@Override
	public ElkDataPropertyAssertionAxiom getDataPropertyAssertionAxiom(
			ElkDataPropertyExpression dataPropertyExpression,
			ElkIndividual individual, ElkLiteral literal) {
		return (ElkDataPropertyAssertionAxiom) objectRecycler_
				.recycle(new ElkDataPropertyAssertionAxiomImpl(
						dataPropertyExpression, individual, literal));
	}

	@Override
	public ElkDataPropertyDomainAxiom getDataPropertyDomainAxiom(
			ElkDataPropertyExpression dataPropertyExpression,
			ElkClassExpression classExpression) {
		return (ElkDataPropertyDomainAxiom) objectRecycler_
				.recycle(new ElkDataPropertyDomainAxiomImpl(
						dataPropertyExpression, classExpression));
	}

	@Override
	public ElkDataPropertyRangeAxiom getDataPropertyRangeAxiom(
			ElkDataPropertyExpression dataPropertyExpression,
			ElkDataRange dataRange) {
		return (ElkDataPropertyRangeAxiom) objectRecycler_
				.recycle(new ElkDataPropertyRangeAxiomImpl(
						dataPropertyExpression, dataRange));
	}

	@Override
	public ElkDatatype getDatatype(ElkIri iri) {
		return (ElkDatatype) objectRecycler_.recycle(new ElkDatatypeImpl(iri));
	}

	@Override
	public ElkDatatype getDatatypeRdfPlainLiteral() {
		return ELK_RDF_PLAIN_LITERAL;
	}

	@Override
	public ElkDatatypeRestriction getDatatypeRestriction(ElkDatatype datatype,
			List<ElkFacetRestriction> facetRestrictions) {
		return (ElkDatatypeRestriction) objectRecycler_
				.recycle(new ElkDatatypeRestrictionImpl(datatype,
						facetRestrictions));
	}

	@Override
	public ElkDataUnionOf getDataUnionOf(ElkDataRange firstDataRange,
			ElkDataRange secondDataRange, ElkDataRange... otherDataRanges) {
		return (ElkDataUnionOf) objectRecycler_.recycle(new ElkDataUnionOfImpl(
				ElkObjectListObject.varArgsToList(firstDataRange,
						secondDataRange, otherDataRanges)));
	}

	@Override
	public ElkDataUnionOf getDataUnionOf(List<? extends ElkDataRange> dataRanges) {
		return (ElkDataUnionOf) objectRecycler_.recycle(new ElkDataUnionOfImpl(
				dataRanges));
	}

	@Override
	public ElkDeclarationAxiom getDeclarationAxiom(ElkEntity entity) {
		return (ElkDeclarationAxiom) objectRecycler_
				.recycle(new ElkDeclarationAxiomImpl(entity));
	}

	@Override
	public ElkDifferentIndividualsAxiom getDifferentIndividualsAxiom(
			ElkIndividual firstIndividual, ElkIndividual secondIndividual,
			ElkIndividual... otherIndividuals) {
		return (ElkDifferentIndividualsAxiom) objectRecycler_
				.recycle(new ElkDifferentIndividualsAxiomImpl(
						ElkObjectListObject.varArgsToList(firstIndividual,
								secondIndividual, otherIndividuals)));
	}

	@Override
	public ElkDifferentIndividualsAxiom getDifferentIndividualsAxiom(
			List<? extends ElkIndividual> individuals) {
		return (ElkDifferentIndividualsAxiom) objectRecycler_
				.recycle(new ElkDifferentIndividualsAxiomImpl(individuals));
	}

	@Override
	public ElkDisjointClassesAxiom getDisjointClassesAxiom(
			ElkClassExpression firstClassExpression,
			ElkClassExpression secondClassExpression,
			ElkClassExpression... otherClassExpressions) {
		return (ElkDisjointClassesAxiom) objectRecycler_
				.recycle(new ElkDisjointClassesAxiomImpl(
						ElkClassExpressionListObject.varArgsToList(
								firstClassExpression, secondClassExpression,
								otherClassExpressions)));
	}

	@Override
	public ElkDisjointClassesAxiom getDisjointClassesAxiom(
			List<? extends ElkClassExpression> disjointClassExpressions) {
		return (ElkDisjointClassesAxiom) objectRecycler_
				.recycle(new ElkDisjointClassesAxiomImpl(
						disjointClassExpressions));
	}

	@Override
	public ElkDisjointDataPropertiesAxiom getDisjointDataPropertiesAxiom(
			ElkDataPropertyExpression firstDataPropertyExpression,
			ElkDataPropertyExpression secondDataPropertyExpression,
			ElkDataPropertyExpression... otherDataPropertyExpressions) {
		return (ElkDisjointDataPropertiesAxiom) objectRecycler_
				.recycle(new ElkDisjointDataPropertiesAxiomImpl(
						ElkObjectListObject.varArgsToList(
								firstDataPropertyExpression,
								secondDataPropertyExpression,
								otherDataPropertyExpressions)));
	}

	@Override
	public ElkDisjointDataPropertiesAxiom getDisjointDataPropertiesAxiom(
			List<? extends ElkDataPropertyExpression> disjointDataPropertyExpressions) {
		return (ElkDisjointDataPropertiesAxiom) objectRecycler_
				.recycle(new ElkDisjointDataPropertiesAxiomImpl(
						disjointDataPropertyExpressions));
	}

	@Override
	public ElkDisjointObjectPropertiesAxiom getDisjointObjectPropertiesAxiom(
			ElkObjectPropertyExpression firstObjectPropertyExpression,
			ElkObjectPropertyExpression secondObjectPropertyExpression,
			ElkObjectPropertyExpression... otherObjectPropertyExpressions) {
		return (ElkDisjointObjectPropertiesAxiom) objectRecycler_
				.recycle(new ElkDisjointObjectPropertiesAxiomImpl(
						ElkObjectListObject.varArgsToList(
								firstObjectPropertyExpression,
								secondObjectPropertyExpression,
								otherObjectPropertyExpressions)));
	}

	@Override
	public ElkDisjointObjectPropertiesAxiom getDisjointObjectPropertiesAxiom(
			List<? extends ElkObjectPropertyExpression> disjointObjectPropertyExpressions) {
		return (ElkDisjointObjectPropertiesAxiom) objectRecycler_
				.recycle(new ElkDisjointObjectPropertiesAxiomImpl(
						disjointObjectPropertyExpressions));
	}

	@Override
	public ElkDisjointUnionAxiom getDisjointUnionAxiom(ElkClass definedClass,
			ElkClassExpression firstClassExpression,
			ElkClassExpression secondClassExpression,
			ElkClassExpression... otherClassExpressions) {
		return (ElkDisjointUnionAxiom) objectRecycler_
				.recycle(new ElkDisjointUnionAxiomImpl(definedClass,
						ElkObjectListObject.varArgsToList(firstClassExpression,
								secondClassExpression, otherClassExpressions)));
	}

	@Override
	public ElkDisjointUnionAxiom getDisjointUnionAxiom(ElkClass definedClass,
			List<? extends ElkClassExpression> disjointClassExpressions) {
		return (ElkDisjointUnionAxiom) objectRecycler_
				.recycle(new ElkDisjointUnionAxiomImpl(definedClass,
						disjointClassExpressions));
	}

	@Override
	public ElkEquivalentClassesAxiom getEquivalentClassesAxiom(
			ElkClassExpression firstClassExpression,
			ElkClassExpression secondClassExpression,
			ElkClassExpression... otherClassExpressions) {
		return (ElkEquivalentClassesAxiom) objectRecycler_
				.recycle(new ElkEquivalentClassesAxiomImpl(ElkObjectListObject
						.varArgsToList(firstClassExpression,
								secondClassExpression, otherClassExpressions)));
	}

	@Override
	public ElkEquivalentClassesAxiom getEquivalentClassesAxiom(
			List<? extends ElkClassExpression> equivalentClassExpressions) {
		return (ElkEquivalentClassesAxiom) objectRecycler_
				.recycle(new ElkEquivalentClassesAxiomImpl(
						equivalentClassExpressions));
	}

	@Override
	public ElkEquivalentDataPropertiesAxiom getEquivalentDataPropertiesAxiom(
			ElkDataPropertyExpression firstDataPropertyExpression,
			ElkDataPropertyExpression secondDataPropertyExpression,
			ElkDataPropertyExpression... otherDataPropertyExpressions) {
		return (ElkEquivalentDataPropertiesAxiom) objectRecycler_
				.recycle(new ElkEquivalentDataPropertiesAxiomImpl(
						ElkObjectListObject.varArgsToList(
								firstDataPropertyExpression,
								secondDataPropertyExpression,
								otherDataPropertyExpressions)));
	}

	@Override
	public ElkEquivalentDataPropertiesAxiom getEquivalentDataPropertiesAxiom(
			List<? extends ElkDataPropertyExpression> equivalentDataPropertyExpressions) {
		return (ElkEquivalentDataPropertiesAxiom) objectRecycler_
				.recycle(new ElkEquivalentDataPropertiesAxiomImpl(
						equivalentDataPropertyExpressions));
	}

	@Override
	public ElkEquivalentObjectPropertiesAxiom getEquivalentObjectPropertiesAxiom(
			ElkObjectPropertyExpression firstObjectPropertyExpression,
			ElkObjectPropertyExpression secondObjectPropertyExpression,
			ElkObjectPropertyExpression... otherObjectPropertyExpressions) {
		return (ElkEquivalentObjectPropertiesAxiom) objectRecycler_
				.recycle(new ElkEquivalentObjectPropertiesAxiomImpl(
						ElkObjectListObject.varArgsToList(
								firstObjectPropertyExpression,
								secondObjectPropertyExpression,
								otherObjectPropertyExpressions)));
	}

	@Override
	public ElkEquivalentObjectPropertiesAxiom getEquivalentObjectPropertiesAxiom(
			List<? extends ElkObjectPropertyExpression> equivalentObjectPropertyExpressions) {
		return (ElkEquivalentObjectPropertiesAxiom) objectRecycler_
				.recycle(new ElkEquivalentObjectPropertiesAxiomImpl(
						equivalentObjectPropertyExpressions));
	}

	@Override
	public ElkFunctionalDataPropertyAxiom getFunctionalDataPropertyAxiom(
			ElkDataPropertyExpression dataPropertyExpression) {
		return (ElkFunctionalDataPropertyAxiom) objectRecycler_
				.recycle(new ElkFunctionalDataPropertyAxiomImpl(
						dataPropertyExpression));
	}

	@Override
	public ElkFunctionalObjectPropertyAxiom getFunctionalObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression) {
		return (ElkFunctionalObjectPropertyAxiom) objectRecycler_
				.recycle(new ElkFunctionalObjectPropertyAxiomImpl(
						objectPropertyExpression));
	}

	@Override
	public ElkInverseFunctionalObjectPropertyAxiom getInverseFunctionalObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression) {
		return (ElkInverseFunctionalObjectPropertyAxiom) objectRecycler_
				.recycle(new ElkInverseFunctionalObjectPropertyAxiomImpl(
						objectPropertyExpression));
	}

	@Override
	public ElkInverseObjectPropertiesAxiom getInverseObjectPropertiesAxiom(
			ElkObjectPropertyExpression firstObjectPropertyExpression,
			ElkObjectPropertyExpression secondObjectPropertyExpression) {
		return (ElkInverseObjectPropertiesAxiom) objectRecycler_
				.recycle(new ElkInverseObjectPropertiesAxiomImpl(
						firstObjectPropertyExpression,
						secondObjectPropertyExpression));
	}

	@Override
	public ElkIrreflexiveObjectPropertyAxiom getIrreflexiveObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression) {
		return (ElkIrreflexiveObjectPropertyAxiom) objectRecycler_
				.recycle(new ElkIrreflexiveObjectPropertyAxiomImpl(
						objectPropertyExpression));
	}

	@Override
	public ElkLiteral getLiteral(String lexicalForm, ElkDatatype datatype) {
		return (ElkLiteral) objectRecycler_.recycle(new ElkLiteralImpl(
				lexicalForm, datatype));
	}

	@Override
	public ElkNamedIndividual getNamedIndividual(ElkIri iri) {
		return (ElkNamedIndividual) objectRecycler_
				.recycle(new ElkNamedIndividualImpl(iri));
	}

	@Override
	public ElkNegativeDataPropertyAssertionAxiom getNegativeDataPropertyAssertionAxiom(
			ElkDataPropertyExpression dataPropertyExpression,
			ElkIndividual individual, ElkLiteral literal) {
		return (ElkNegativeDataPropertyAssertionAxiom) objectRecycler_
				.recycle(new ElkNegativeDataPropertyAssertionAxiomImpl(
						dataPropertyExpression, individual, literal));
	}

	@Override
	public ElkNegativeObjectPropertyAssertionAxiom getNegativeObjectPropertyAssertionAxiom(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkIndividual firstIndividual, ElkIndividual secondIndividual) {
		return (ElkNegativeObjectPropertyAssertionAxiom) objectRecycler_
				.recycle(new ElkNegativeObjectPropertyAssertionAxiomImpl(
						objectPropertyExpression, firstIndividual,
						secondIndividual));
	}

	@Override
	public ElkObjectAllValuesFrom getObjectAllValuesFrom(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkClassExpression classExpression) {
		return (ElkObjectAllValuesFrom) objectRecycler_
				.recycle(new ElkObjectAllValuesFromImpl(
						objectPropertyExpression, classExpression));
	}

	@Override
	public ElkObjectComplementOf getObjectComplementOf(
			ElkClassExpression classExpression) {
		return (ElkObjectComplementOf) objectRecycler_
				.recycle(new ElkObjectComplementOfImpl(classExpression));
	}

	@Override
	public ElkObjectExactCardinalityUnqualified getObjectExactCardinalityUnqualified(
			ElkObjectPropertyExpression objectPropertyExpression,
			int cardinality) {
		return (ElkObjectExactCardinalityUnqualified) objectRecycler_
				.recycle(new ElkObjectExactCardinalityUnqualifiedImpl(
						objectPropertyExpression, cardinality));
	}

	@Override
	public ElkObjectExactCardinalityQualified getObjectExactCardinalityQualified(
			ElkObjectPropertyExpression objectPropertyExpression,
			int cardinality, ElkClassExpression classExpression) {
		return (ElkObjectExactCardinalityQualified) objectRecycler_
				.recycle(new ElkObjectExactCardinalityQualifiedImpl(
						objectPropertyExpression, cardinality, classExpression));
	}

	@Override
	public ElkObjectHasSelf getObjectHasSelf(
			ElkObjectPropertyExpression objectPropertyExpression) {
		return (ElkObjectHasSelf) objectRecycler_
				.recycle(new ElkObjectHasSelfImpl(objectPropertyExpression));
	}

	@Override
	public ElkObjectHasValue getObjectHasValue(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkIndividual individual) {
		return (ElkObjectHasValue) objectRecycler_
				.recycle(new ElkObjectHasValueImpl(objectPropertyExpression,
						individual));
	}

	@Override
	public ElkObjectIntersectionOf getObjectIntersectionOf(
			ElkClassExpression firstClassExpression,
			ElkClassExpression secondClassExpression,
			ElkClassExpression... otherClassExpressions) {
		return (ElkObjectIntersectionOf) objectRecycler_
				.recycle(new ElkObjectIntersectionOfImpl(ElkObjectListObject
						.varArgsToList(firstClassExpression,
								secondClassExpression, otherClassExpressions)));
	}

	@Override
	public ElkObjectIntersectionOf getObjectIntersectionOf(
			List<? extends ElkClassExpression> classExpressions) {
		return (ElkObjectIntersectionOf) objectRecycler_
				.recycle(new ElkObjectIntersectionOfImpl(classExpressions));
	}

	@Override
	public ElkObjectInverseOf getObjectInverseOf(
			ElkObjectProperty objectProperty) {
		return (ElkObjectInverseOf) objectRecycler_
				.recycle(new ElkObjectInverseOfImpl(objectProperty));
	}

	@Override
	public ElkObjectMaxCardinalityUnqualified getObjectMaxCardinalityUnqualified(
			ElkObjectPropertyExpression objectPropertyExpression,
			int cardinality) {
		return (ElkObjectMaxCardinalityUnqualified) objectRecycler_
				.recycle(new ElkObjectMaxCardinalityUnqualifiedImpl(
						objectPropertyExpression, cardinality));
	}

	@Override
	public ElkObjectMaxCardinalityQualified getObjectMaxCardinalityQualified(
			ElkObjectPropertyExpression objectPropertyExpression,
			int cardinality, ElkClassExpression classExpression) {
		return (ElkObjectMaxCardinalityQualified) objectRecycler_
				.recycle(new ElkObjectMaxCardinalityQualifiedImpl(
						objectPropertyExpression, cardinality, classExpression));
	}

	@Override
	public ElkObjectMinCardinalityUnqualified getObjectMinCardinalityUnqualified(
			ElkObjectPropertyExpression objectPropertyExpression,
			int cardinality) {
		return (ElkObjectMinCardinalityUnqualified) objectRecycler_
				.recycle(new ElkObjectMinCardinalityUnqualifiedImpl(
						objectPropertyExpression, cardinality));
	}

	@Override
	public ElkObjectMinCardinalityQualified getObjectMinCardinalityQualified(
			ElkObjectPropertyExpression objectPropertyExpression,
			int cardinality, ElkClassExpression classExpression) {
		return (ElkObjectMinCardinalityQualified) objectRecycler_
				.recycle(new ElkObjectMinCardinalityQualifiedImpl(
						objectPropertyExpression, cardinality, classExpression));
	}

	@Override
	public ElkObjectOneOf getObjectOneOf(ElkIndividual firstIndividual,
			ElkIndividual... otherIndividuals) {
		return (ElkObjectOneOf) objectRecycler_.recycle(new ElkObjectOneOfImpl(
				ElkObjectListObject.varArgsToList(firstIndividual,
						otherIndividuals)));
	}

	@Override
	public ElkObjectOneOf getObjectOneOf(
			List<? extends ElkIndividual> individuals) {
		return (ElkObjectOneOf) objectRecycler_.recycle(new ElkObjectOneOfImpl(
				individuals));
	}

	@Override
	public ElkObjectProperty getObjectProperty(ElkIri iri) {
		return (ElkObjectProperty) objectRecycler_
				.recycle(new ElkObjectPropertyImpl(iri));
	}

	@Override
	public ElkObjectPropertyAssertionAxiom getObjectPropertyAssertionAxiom(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkIndividual firstIndividual, ElkIndividual secondIndividual) {
		return (ElkObjectPropertyAssertionAxiom) objectRecycler_
				.recycle(new ElkObjectPropertyAssertionAxiomImpl(
						objectPropertyExpression, firstIndividual,
						secondIndividual));
	}

	@Override
	public ElkObjectPropertyChain getObjectPropertyChain(
			List<? extends ElkObjectPropertyExpression> objectPropertyExpressions) {
		return (ElkObjectPropertyChain) objectRecycler_
				.recycle(new ElkObjectPropertyChainImpl(
						objectPropertyExpressions));
	}

	@Override
	public ElkObjectPropertyDomainAxiom getObjectPropertyDomainAxiom(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkClassExpression classExpression) {
		return (ElkObjectPropertyDomainAxiom) objectRecycler_
				.recycle(new ElkObjectPropertyDomainAxiomImpl(
						objectPropertyExpression, classExpression));
	}

	@Override
	public ElkObjectPropertyRangeAxiom getObjectPropertyRangeAxiom(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkClassExpression classExpression) {
		return (ElkObjectPropertyRangeAxiom) objectRecycler_
				.recycle(new ElkObjectPropertyRangeAxiomImpl(
						objectPropertyExpression, classExpression));
	}

	@Override
	public ElkObjectSomeValuesFrom getObjectSomeValuesFrom(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkClassExpression classExpression) {
		return (ElkObjectSomeValuesFrom) objectRecycler_
				.recycle(new ElkObjectSomeValuesFromImpl(
						objectPropertyExpression, classExpression));
	}

	@Override
	public ElkObjectUnionOf getObjectUnionOf(
			ElkClassExpression firstClassExpression,
			ElkClassExpression secondClassExpression,
			ElkClassExpression... otherClassExpressions) {
		return (ElkObjectUnionOf) objectRecycler_
				.recycle(new ElkObjectUnionOfImpl(ElkObjectListObject
						.varArgsToList(firstClassExpression,
								secondClassExpression, otherClassExpressions)));
	}

	@Override
	public ElkObjectUnionOf getObjectUnionOf(
			List<? extends ElkClassExpression> classExpressions) {
		return (ElkObjectUnionOf) objectRecycler_
				.recycle(new ElkObjectUnionOfImpl(classExpressions));
	}

	@Override
	public ElkDataProperty getOwlBottomDataProperty() {
		return ElkObjectFactoryImpl.ELK_OWL_BOTTOM_DATA_PROPERTY;
	}

	@Override
	public ElkObjectProperty getOwlBottomObjectProperty() {
		return ElkObjectFactoryImpl.ELK_OWL_BOTTOM_OBJECT_PROPERTY;
	}

	@Override
	public ElkClass getOwlNothing() {
		return PredefinedElkClass.OWL_NOTHING;
	}

	@Override
	public ElkClass getOwlThing() {
		return PredefinedElkClass.OWL_THING;
	}

	@Override
	public ElkDataProperty getOwlTopDataProperty() {
		return ElkObjectFactoryImpl.ELK_OWL_TOP_DATA_PROPERTY;
	}

	@Override
	public ElkObjectProperty getOwlTopObjectProperty() {
		return ElkObjectFactoryImpl.ELK_OWL_TOP_OBJECT_PROPERTY;
	}

	@Override
	public ElkReflexiveObjectPropertyAxiom getReflexiveObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression) {
		return (ElkReflexiveObjectPropertyAxiom) objectRecycler_
				.recycle(new ElkReflexiveObjectPropertyAxiomImpl(
						objectPropertyExpression));
	}

	@Override
	public ElkSameIndividualAxiom getSameIndividualAxiom(
			ElkIndividual firstIndividual, ElkIndividual secondIndividual,
			ElkIndividual... otherIndividuals) {
		return (ElkSameIndividualAxiom) objectRecycler_
				.recycle(new ElkSameIndividualAxiomImpl(ElkObjectListObject
						.varArgsToList(firstIndividual, secondIndividual,
								otherIndividuals)));
	}

	@Override
	public ElkSameIndividualAxiom getSameIndividualAxiom(
			List<? extends ElkIndividual> individuals) {
		return (ElkSameIndividualAxiom) objectRecycler_
				.recycle(new ElkSameIndividualAxiomImpl(individuals));
	}

	@Override
	public ElkSubClassOfAxiom getSubClassOfAxiom(
			ElkClassExpression subClassExpression,
			ElkClassExpression superClassExpression) {
		return (ElkSubClassOfAxiom) objectRecycler_
				.recycle(new ElkSubClassOfAxiomImpl(subClassExpression,
						superClassExpression));
	}

	@Override
	public ElkSubDataPropertyOfAxiom getSubDataPropertyOfAxiom(
			ElkDataPropertyExpression subDataPropertyExpression,
			ElkDataPropertyExpression superDataPropertyExpression) {
		return (ElkSubDataPropertyOfAxiom) objectRecycler_
				.recycle(new ElkSubDataPropertyOfAxiomImpl(
						subDataPropertyExpression, superDataPropertyExpression));
	}

	@Override
	public ElkSubObjectPropertyOfAxiom getSubObjectPropertyOfAxiom(
			ElkSubObjectPropertyExpression subObjectPropertyExpression,
			ElkObjectPropertyExpression superObjectPropertyExpression) {
		return (ElkSubObjectPropertyOfAxiom) objectRecycler_
				.recycle(new ElkSubObjectPropertyOfAxiomImpl(
						subObjectPropertyExpression,
						superObjectPropertyExpression));
	}

	@Override
	public ElkSymmetricObjectPropertyAxiom getSymmetricObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression) {
		return (ElkSymmetricObjectPropertyAxiom) objectRecycler_
				.recycle(new ElkSymmetricObjectPropertyAxiomImpl(
						objectPropertyExpression));
	}

	@Override
	public ElkTransitiveObjectPropertyAxiom getTransitiveObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression) {
		return (ElkTransitiveObjectPropertyAxiom) objectRecycler_
				.recycle(new ElkTransitiveObjectPropertyAxiomImpl(
						objectPropertyExpression));
	}

	@Override
	public ElkHasKeyAxiom getHasKeyAxiom(ElkClassExpression classExpr,
			Set<ElkObjectPropertyExpression> objectPEs,
			Set<ElkDataPropertyExpression> dataPEs) {

		return (ElkHasKeyAxiom) objectRecycler_.recycle(new ElkHasKeyAxiomImpl(
				classExpr, objectPEs, dataPEs));
	}

	@Override
	public ElkDatatypeDefinitionAxiom getDatatypeDefinitionAxiom(
			ElkDatatype datatype, ElkDataRange dataRange) {

		return (ElkDatatypeDefinitionAxiom) objectRecycler_
				.recycle(new ElkDatatypeDefinitionAxiomImpl(datatype, dataRange));
	}

	@Override
	public ElkDataAllValuesFrom getDataAllValuesFrom(ElkDataRange dataRange,
			ElkDataPropertyExpression dpe1, ElkDataPropertyExpression... dpe) {
		return (ElkDataAllValuesFrom) objectRecycler_
				.recycle(new ElkDataAllValuesFromImpl(ElkObjectListObject
						.varArgsToList(dpe1, dpe), dataRange));
	}

	@Override
	public ElkDataAllValuesFrom getDataAllValuesFrom(ElkDataRange dataRange,
			List<? extends ElkDataPropertyExpression> dpList) {
		return (ElkDataAllValuesFrom) objectRecycler_
				.recycle(new ElkDataAllValuesFromImpl(dpList, dataRange));
	}

	@Override
	public ElkDataSomeValuesFrom getDataSomeValuesFrom(ElkDataRange dataRange,
			ElkDataPropertyExpression dpe1, ElkDataPropertyExpression... dpe) {
		return (ElkDataSomeValuesFrom) objectRecycler_
				.recycle(new ElkDataSomeValuesFromImpl(ElkObjectListObject
						.varArgsToList(dpe1, dpe), dataRange));
	}

	@Override
	public ElkDataSomeValuesFrom getDataSomeValuesFrom(ElkDataRange dataRange,
			List<? extends ElkDataPropertyExpression> dpList) {
		return (ElkDataSomeValuesFrom) objectRecycler_
				.recycle(new ElkDataSomeValuesFromImpl(dpList, dataRange));
	}

	@Override
	public ElkFacetRestriction getFacetRestriction(ElkIri iri,
			ElkLiteral literal) {
		return (ElkFacetRestriction) objectRecycler_
				.recycle(new ElkFacetRestrictionImpl(iri, literal));
	}

	@Override
	public ElkAnnotation getAnnotation(ElkAnnotationProperty property,
			ElkAnnotationValue value) {
		return (ElkAnnotation) objectRecycler_.recycle(new ElkAnnotationImpl(
				property, value));
	}

	@Override
	public ElkAnnotationAssertionAxiom getAnnotationAssertionAxiom(
			ElkAnnotationProperty property, ElkAnnotationSubject subject,
			ElkAnnotationValue value) {

		return (ElkAnnotationAssertionAxiom) objectRecycler_
				.recycle(new ElkAnnotationAssertionAxiomImpl(property, subject,
						value));
	}

	@Override
	public ElkAnnotationPropertyDomainAxiom getAnnotationPropertyDomainAxiom(
			ElkAnnotationProperty property, ElkIri domain) {

		return (ElkAnnotationPropertyDomainAxiom) objectRecycler_
				.recycle(new ElkAnnotationPropertyDomainAxiomImpl(property,
						domain));
	}

	@Override
	public ElkAnnotationPropertyRangeAxiom getAnnotationPropertyRangeAxiom(
			ElkAnnotationProperty property, ElkIri range) {
		return (ElkAnnotationPropertyRangeAxiom) objectRecycler_
				.recycle(new ElkAnnotationPropertyRangeAxiomImpl(property,
						range));
	}

	@Override
	public ElkSubAnnotationPropertyOfAxiom getSubAnnotationPropertyOfAxiom(
			ElkAnnotationProperty subAnnotationProperty,
			ElkAnnotationProperty superAnnotationProperty) {
		return (ElkSubAnnotationPropertyOfAxiom) objectRecycler_
				.recycle(new ElkSubAnnotationPropertyOfAxiomImpl(
						subAnnotationProperty, superAnnotationProperty));
	}

	@Override
	public ElkSWRLRule getSWRLRule() {
		return new ElkSWRLRuleImpl();
	}
}