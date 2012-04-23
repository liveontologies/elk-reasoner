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
/**
 * @author Markus Kroetzsch, Aug 8, 2011
 */
package org.semanticweb.elk.owl.interfaces;

import java.util.List;
import java.util.Set;

import org.semanticweb.elk.owl.iris.ElkIri;

/**
 * Interface that provides methods for creating instances of ElkObjects.
 * 
 * @author Markus Kroetzsch
 */
public interface ElkObjectFactory {

	/**
	 * Create an {@link ElkAnnotation}}
	 * 
	 * @return
	 */
	public ElkAnnotation getAnnotation(ElkAnnotationProperty property, ElkAnnotationValue value);
	
	/**
	 * Create an {@link ElkAnnotationProperty}.
	 * 
	 * @param iri
	 * @return
	 */
	public  ElkAnnotationProperty getAnnotationProperty(ElkIri iri);
	
	/**
	 * Create an {@link ElkAnnotationAssertionAxiom}}
	 * 
	 * @return
	 */
	public ElkAnnotationAssertionAxiom getAnnotationAssertionAxiom(ElkAnnotationProperty property, ElkAnnotationSubject subject, ElkAnnotationValue value);
	
	
	/**
	 * Create an {@link ElkAnonymousIndividual}.
	 * 
	 * @param nodeId
	 * @return
	 */
	public  ElkAnonymousIndividual getAnonymousIndividual(String nodeId);

	/**
	 * Create an {@link ElkAsymmetricObjectPropertyAxiom}.
	 * 
	 * @param objectPropertyExpression
	 * @return
	 */
	public  ElkAsymmetricObjectPropertyAxiom getAsymmetricObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression);

	/**
	 * Create an {@link ElkClass}.
	 * 
	 * @param iri
	 * @return
	 */
	public  ElkClass getClass(ElkIri iri);

	/**
	 * Create an {@link ElkClassAssertionAxiom}.
	 * 
	 * @param classExpression
	 * @param individual
	 * @return
	 */
	public  ElkClassAssertionAxiom getClassAssertionAxiom(
			ElkClassExpression classExpression, ElkIndividual individual);

	/**
	 * Create an {@link ElkDataAllValuesFrom}.
	 * 
	 * @param dataRange
	 * @param dpe1
	 * @param dpe
	 * @return
	 */
	public  ElkDataAllValuesFrom getDataAllValuesFrom(
			ElkDataRange dataRange,
			ElkDataPropertyExpression dpe1,
			ElkDataPropertyExpression... dpe);
	
	public  ElkDataAllValuesFrom getDataAllValuesFrom(
			ElkDataRange dataRange,
			List<? extends ElkDataPropertyExpression> dpList);	

	/**
	 * Create an {@link ElkDataComplementOf}.
	 * 
	 * @param dataRange
	 * @return
	 */
	public  ElkDataComplementOf getDataComplementOf(
			ElkDataRange dataRange);

	/**
	 * Create an {@link ElkDataExactCardinality}.
	 * 
	 * @param dataPropertyExpression
	 * @param cardinality
	 * @return
	 */
	public  ElkDataExactCardinality getDataExactCardinality(
			ElkDataPropertyExpression dataPropertyExpression, int cardinality);

	/**
	 * Create an {@link ElkDataExactCardinalityQualified}.
	 * 
	 * @param dataPropertyExpression
	 * @param cardinality
	 * @param dataRange
	 * @return
	 */
	public  ElkDataExactCardinalityQualified getDataExactCardinalityQualified(
			ElkDataPropertyExpression dataPropertyExpression, int cardinality,
			ElkDataRange dataRange);

	/**
	 * Create an {@link ElkDataHasValue}.
	 * 
	 * @param dataPropertyExpression
	 * @param literal
	 * @return
	 */
	public  ElkDataHasValue getDataHasValue(
			ElkDataPropertyExpression dataPropertyExpression, ElkLiteral literal);

	/**
	 * Create an {@link ElkDataIntersectionOf}.
	 * 
	 * @param firstDataRange
	 * @param secondDataRange
	 * @param otherDataRanges
	 * @return
	 */
	public  ElkDataIntersectionOf getDataIntersectionOf(
			ElkDataRange firstDataRange, ElkDataRange secondDataRange,
			ElkDataRange... otherDataRanges);

	/**
	 * Create an {@link ElkDataIntersectionOf}.
	 * 
	 * @param dataRanges
	 * @return
	 */
	public  ElkDataIntersectionOf getDataIntersectionOf(
			List<? extends ElkDataRange> dataRanges);

	/**
	 * Create an {@link ElkDataMaxCardinality}.
	 * 
	 * @param dataPropertyExpression
	 * @param cardinality
	 * @return
	 */
	public  ElkDataMaxCardinality getDataMaxCardinality(
			ElkDataPropertyExpression dataPropertyExpression, int cardinality);

	/**
	 * Create an {@link ElkDataMaxCardinalityQualified}.
	 * 
	 * @param dataPropertyExpression
	 * @param cardinality
	 * @param dataRange
	 * @return
	 */
	public  ElkDataMaxCardinalityQualified getDataMaxCardinalityQualified(
			ElkDataPropertyExpression dataPropertyExpression, int cardinality,
			ElkDataRange dataRange);

	/**
	 * Create an {@link ElkDataMinCardinality}.
	 * 
	 * @param dataPropertyExpression
	 * @param cardinality
	 * @return
	 */
	public  ElkDataMinCardinality getDataMinCardinality(
			ElkDataPropertyExpression dataPropertyExpression, int cardinality);

	/**
	 * Create an {@link ElkDataMinCardinalityQualified}.
	 * 
	 * @param dataPropertyExpression
	 * @param cardinality
	 * @param dataRange
	 * @return
	 */
	public  ElkDataMinCardinalityQualified getDataMinCardinalityQualified(
			ElkDataPropertyExpression dataPropertyExpression, int cardinality,
			ElkDataRange dataRange);

	/**
	 * Create an {@link ElkDataOneOf}.
	 * 
	 * @param firstIndividual
	 * @param otherIndividuals
	 * @return
	 */
	public  ElkDataOneOf getDataOneOf(ElkLiteral firstLiteral,
			ElkLiteral... otherLiterals);

	/**
	 * Create an {@link ElkDataOneOf}.
	 * 
	 * @param literals
	 * @return
	 */
	public  ElkDataOneOf getDataOneOf(
			List<? extends ElkLiteral> literals);

	/**
	 * Create an {@link ElkDataProperty}.
	 * 
	 * @param dataPropertyIri
	 * @return
	 */
	public  ElkDataProperty getDataProperty(ElkIri iri);

	/**
	 * Create an {@link ElkDataPropertyAssertionAxiom}.
	 * 
	 * @param dataPropertyExpression
	 * @param firstIndividual
	 * @param secondIndividual
	 * @return
	 */
	public  ElkDataPropertyAssertionAxiom getDataPropertyAssertionAxiom(
			ElkDataPropertyExpression dataPropertyExpression,
			ElkIndividual individual, ElkLiteral literal);

	/**
	 * Create an {@link ElkDataPropertyDomainAxiom}.
	 * 
	 * @param dataPropertyExpression
	 * @param classExpression
	 * @return
	 */
	public  ElkDataPropertyDomainAxiom getDataPropertyDomainAxiom(
			ElkDataPropertyExpression dataPropertyExpression,
			ElkClassExpression classExpression);

	/**
	 * Create an {@link ElkDataPropertyRangeAxiom}.
	 * 
	 * @param dataPropertyExpression
	 * @param dataRange
	 * @return
	 */
	public  ElkDataPropertyRangeAxiom getDataPropertyRangeAxiom(
			ElkDataPropertyExpression dataPropertyExpression,
			ElkDataRange dataRange);

	/**
	 * Create an {@link ElkDataSomeValuesFrom}
	 * 
	 * @param dataRange
	 * @param dpe1
	 * @param dpe
	 * @return
	 */
	public  ElkDataSomeValuesFrom getDataSomeValuesFrom(
			ElkDataRange dataRange,
			ElkDataPropertyExpression dpe1,
			ElkDataPropertyExpression... dpe);
	
	public  ElkDataSomeValuesFrom getDataSomeValuesFrom(
			ElkDataRange dataRange,
			List<? extends ElkDataPropertyExpression> dpList);

	/**
	 * Create an {@link ElkDatatype}.
	 * 
	 * @param iri
	 * @return
	 */
	public  ElkDatatype getDatatype(ElkIri iri);

	/**
	 * Create the ElkDatatype for rdf:PlainLiteral}.
	 * 
	 * @return
	 */
	public  ElkDatatype getDatatypeRdfPlainLiteral();

	/**
	 * Create an {@link ElkDatatypeRestriction}.
	 * 
	 * @param datatype
	 * @param facetRestrictions
	 * @return
	 */
	public  ElkDatatypeRestriction getDatatypeRestriction(
			ElkDatatype datatype, List<ElkFacetRestriction> facetRestrictions);

	/**
	 * Create an {@link ElkDataUnionOf}.
	 * 
	 * @param firstDataRange
	 * @param secondDataRange
	 * @param otherDataRanges
	 * @return
	 */
	public  ElkDataUnionOf getDataUnionOf(ElkDataRange firstDataRange,
			ElkDataRange secondDataRange, ElkDataRange... otherDataRanges);

	/**
	 * Create an {@link ElkDataUnionOf}.
	 * 
	 * @param dataRanges
	 * @return
	 */
	public  ElkDataUnionOf getDataUnionOf(
			List<? extends ElkDataRange> dataRanges);

	/**
	 * Create an {@link ElkDeclarationAxiom}.
	 * 
	 * @param entity
	 * @return
	 */
	public  ElkDeclarationAxiom getDeclarationAxiom(ElkEntity entity);

	/**
	 * Create an {@link ElkDifferentIndividualsAxiom}.
	 * 
	 * @param firstIndividual
	 * @param secondIndividual
	 * @param otherIndividuals
	 * @return
	 */
	public  ElkDifferentIndividualsAxiom getDifferentIndividualsAxiom(
			ElkIndividual firstIndividual, ElkIndividual secondIndividual,
			ElkIndividual... otherIndividuals);

	/**
	 * Create an {@link ElkDifferentIndividualsAxiom}.
	 * 
	 * @param individuals
	 * @return
	 */
	public  ElkDifferentIndividualsAxiom getDifferentIndividualsAxiom(
			List<? extends ElkIndividual> individuals);

	/**
	 * Create an {@link ElkDisjointClassesAxiom}.
	 * 
	 * @param firstClassExpression
	 * @param secondClassExpression
	 * @param otherClassExpressions
	 * @return
	 */
	public  ElkDisjointClassesAxiom getDisjointClassesAxiom(
			ElkClassExpression firstClassExpression,
			ElkClassExpression secondClassExpression,
			ElkClassExpression... otherClassExpressions);

	/**
	 * Create an {@link ElkDisjointClassesAxiom}.
	 * 
	 * @param disjointClassExpressions
	 * @return
	 */
	public  ElkDisjointClassesAxiom getDisjointClassesAxiom(
			List<? extends ElkClassExpression> disjointClassExpressions);

	/**
	 * Create an {@link ElkDisjointDataPropertiesAxiom}.
	 * 
	 * @param firstDataPropertyExpression
	 * @param secondDataPropertyExpression
	 * @param otherDataPropertyExpressions
	 * @return
	 */
	public  ElkDisjointDataPropertiesAxiom getDisjointDataPropertiesAxiom(
			ElkDataPropertyExpression firstDataPropertyExpression,
			ElkDataPropertyExpression secondDataPropertyExpression,
			ElkDataPropertyExpression... otherDataPropertyExpressions);

	/**
	 * Create an {@link ElkDisjointDataPropertiesAxiom}.
	 * 
	 * @param disjointDataPropertyExpressions
	 * @return
	 */
	public  ElkDisjointDataPropertiesAxiom getDisjointDataPropertiesAxiom(
			List<? extends ElkDataPropertyExpression> disjointDataPropertyExpressions);

	/**
	 * Create an {@link ElkDisjointObjectPropertiesAxiom}.
	 * 
	 * @param firstObjectPropertyExpression
	 * @param secondObjectPropertyExpression
	 * @param otherObjectPropertyExpressions
	 * @return
	 */
	public  ElkDisjointObjectPropertiesAxiom getDisjointObjectPropertiesAxiom(
			ElkObjectPropertyExpression firstObjectPropertyExpression,
			ElkObjectPropertyExpression secondObjectPropertyExpression,
			ElkObjectPropertyExpression... otherObjectPropertyExpressions);

	/**
	 * Create an {@link ElkDisjointObjectPropertiesAxiom}.
	 * 
	 * @param disjointObjectPropertyExpressions
	 * @return
	 */
	public  ElkDisjointObjectPropertiesAxiom getDisjointObjectPropertiesAxiom(
			List<? extends ElkObjectPropertyExpression> disjointObjectPropertyExpressions);

	/**
	 * Create an {@link ElkDisjointUnionAxiom}.
	 * 
	 * @param firstClassExpression
	 * @param secondClassExpression
	 * @param otherClassExpressions
	 * @return
	 */
	public  ElkDisjointUnionAxiom getDisjointUnionAxiom(
			ElkClass definedClass, ElkClassExpression firstClassExpression,
			ElkClassExpression secondClassExpression,
			ElkClassExpression... otherClassExpressions);

	/**
	 * Create an {@link ElkDisjointUnionAxiom}.
	 * 
	 * @param disjointClassExpressions
	 * @return
	 */
	public  ElkDisjointUnionAxiom getDisjointUnionAxiom(
			ElkClass definedClass,
			List<? extends ElkClassExpression> disjointClassExpressions);

	/**
	 * Create an {@link ElkEquivalentClassesAxiom}.
	 * 
	 * @param firstClassExpression
	 * @param secondClassExpression
	 * @param otherClassExpressions
	 * @return
	 */
	public  ElkEquivalentClassesAxiom getEquivalentClassesAxiom(
			ElkClassExpression firstClassExpression,
			ElkClassExpression secondClassExpression,
			ElkClassExpression... otherClassExpressions);

	/**
	 * Create an {@link ElkEquivalentClassesAxiom}.
	 * 
	 * @param equivalentClassExpressions
	 * @return
	 */
	public  ElkEquivalentClassesAxiom getEquivalentClassesAxiom(
			List<? extends ElkClassExpression> equivalentClassExpressions);

	/**
	 * Create an {@link ElkEquivalentDataPropertiesAxiom}.
	 * 
	 * @param firstDataPropertyExpression
	 * @param secondDataPropertyExpression
	 * @param otherDataPropertyExpressions
	 * @return
	 */
	public  ElkEquivalentDataPropertiesAxiom getEquivalentDataPropertiesAxiom(
			ElkDataPropertyExpression firstDataPropertyExpression,
			ElkDataPropertyExpression secondDataPropertyExpression,
			ElkDataPropertyExpression... otherDataPropertyExpressions);

	/**
	 * Create an {@link ElkEquivalentDataPropertiesAxiom}.
	 * 
	 * @param equivalentDataPropertyExpressions
	 * @return
	 */
	public  ElkEquivalentDataPropertiesAxiom getEquivalentDataPropertiesAxiom(
			List<? extends ElkDataPropertyExpression> equivalentDataPropertyExpressions);

	/**
	 * Create an {@link ElkEquivalentObjectPropertiesAxiom}.
	 * 
	 * @param firstObjectPropertyExpression
	 * @param secondObjectPropertyExpression
	 * @param otherObjectPropertyExpressions
	 * @return
	 */
	public  ElkEquivalentObjectPropertiesAxiom getEquivalentObjectPropertiesAxiom(
			ElkObjectPropertyExpression firstObjectPropertyExpression,
			ElkObjectPropertyExpression secondObjectPropertyExpression,
			ElkObjectPropertyExpression... otherObjectPropertyExpressions);

	/**
	 * Create an {@link ElkEquivalentObjectPropertiesAxiom}.
	 * 
	 * @param equivalentObjectPropertyExpressions
	 * @return
	 */
	public  ElkEquivalentObjectPropertiesAxiom getEquivalentObjectPropertiesAxiom(
			List<? extends ElkObjectPropertyExpression> equivalentObjectPropertyExpressions);

	/**
	 * Create an {@link ElkFunctionalDataPropertyAxiom}.
	 * 
	 * @param dataPropertyExpression
	 * @return
	 */
	public  ElkFunctionalDataPropertyAxiom getFunctionalDataPropertyAxiom(
			ElkDataPropertyExpression dataPropertyExpression);

	/**
	 * Create an {@link ElkFunctionalObjectPropertyAxiom}.
	 * 
	 * @param objectPropertyExpression
	 * @return
	 */
	public  ElkFunctionalObjectPropertyAxiom getFunctionalObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression);

	/**
	 * Create an {@link ElkInverseFunctionalObjectPropertyAxiom}.
	 * 
	 * @param objectPropertyExpression
	 * @return
	 */
	public  ElkInverseFunctionalObjectPropertyAxiom getInverseFunctionalObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression);

	/**
	 * Create an {@link ElkInverseObjectPropertiesAxiom}.
	 * 
	 * @param firstObjectPropertyExpression
	 * @param secondObjectPropertyExpression
	 * @return
	 */
	public  ElkInverseObjectPropertiesAxiom getInverseObjectPropertiesAxiom(
			ElkObjectPropertyExpression firstObjectPropertyExpression,
			ElkObjectPropertyExpression secondObjectPropertyExpression);

	/**
	 * Create an {@link ElkIrreflexiveObjectPropertyAxiom}.
	 * 
	 * @param objectPropertyExpression
	 * @return
	 */
	public  ElkIrreflexiveObjectPropertyAxiom getIrreflexiveObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression);

	/**
	 * Create an {@link ElkLiteral}.
	 * 
	 * @param lexicalForm
	 * @param datatype
	 * @return
	 */
	public  ElkLiteral getLiteral(String lexicalForm,
			ElkDatatype datatype);

	/**
	 * Create an {@link ElkNamedIndividual}.
	 * 
	 * @param iri
	 * @return
	 */
	public  ElkNamedIndividual getNamedIndividual(ElkIri iri);

	/**
	 * Create an {@link ElkNegativeDataPropertyAssertionAxiom}.
	 * 
	 * @param dataPropertyExpression
	 * @param firstIndividual
	 * @param secondIndividual
	 * @return
	 */
	public  ElkNegativeDataPropertyAssertionAxiom getNegativeDataPropertyAssertionAxiom(
			ElkDataPropertyExpression dataPropertyExpression,
			ElkIndividual individual, ElkLiteral literal);

	/**
	 * Create an {@link ElkNegativeObjectPropertyAssertionAxiom}.
	 * 
	 * @param objectPropertyExpression
	 * @param firstIndividual
	 * @param secondIndividual
	 * @return
	 */
	public  ElkNegativeObjectPropertyAssertionAxiom getNegativeObjectPropertyAssertionAxiom(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkIndividual firstIndividual, ElkIndividual secondIndividual);

	/**
	 * Create an {@link ElkObjectAllValuesFrom}.
	 * 
	 * @param objectPropertyExpression
	 * @param classExpression
	 * @return
	 */
	public  ElkObjectAllValuesFrom getObjectAllValuesFrom(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkClassExpression classExpression);

	/**
	 * Create an {@link ElkObjectComplementOf}.
	 * 
	 * @param classExpression
	 * @return
	 */
	public  ElkObjectComplementOf getObjectComplementOf(
			ElkClassExpression classExpression);

	/**
	 * Create an {@link ElkObjectExactCardinality}.
	 * 
	 * @param objectPropertyExpression
	 * @param cardinality
	 * @return
	 */
	public  ElkObjectExactCardinality getObjectExactCardinality(
			ElkObjectPropertyExpression objectPropertyExpression,
			int cardinality);

	/**
	 * Create an {@link ElkObjectExactCardinalityQualified}.
	 * 
	 * @param objectPropertyExpression
	 * @param cardinality
	 * @param classExpression
	 * @return
	 */
	public  ElkObjectExactCardinalityQualified getObjectExactCardinalityQualified(
			ElkObjectPropertyExpression objectPropertyExpression,
			int cardinality, ElkClassExpression classExpression);

	/**
	 * Create an {@link ElkObjectHasSelf}.
	 * 
	 * @param objectPropertyExpression
	 * @return
	 */
	public  ElkObjectHasSelf getObjectHasSelf(
			ElkObjectPropertyExpression objectPropertyExpression);

	/**
	 * Create an {@link ElkObjectHasValue}.
	 * 
	 * @param objectPropertyExpression
	 * @param individual
	 * @return
	 */
	public  ElkObjectHasValue getObjectHasValue(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkIndividual individual);

	/**
	 * Create an {@link ElkObjectIntersectionOf}.
	 * 
	 * @param firstClassExpression
	 * @param secondClassExpression
	 * @param otherClassExpressions
	 * @return
	 */
	public  ElkObjectIntersectionOf getObjectIntersectionOf(
			ElkClassExpression firstClassExpression,
			ElkClassExpression secondClassExpression,
			ElkClassExpression... otherClassExpressions);

	/**
	 * Create an {@link ElkObjectIntersectionOf}.
	 * 
	 * @param classExpressions
	 * @return
	 */
	public  ElkObjectIntersectionOf getObjectIntersectionOf(
			List<? extends ElkClassExpression> classExpressions);

	/**
	 * Create an {@link ElkObjectInverseOf}.
	 * 
	 * @param objectProperty
	 * @return
	 */
	public  ElkObjectInverseOf getObjectInverseOf(
			ElkObjectProperty objectProperty);

	/**
	 * Create an {@link ElkObjectMaxCardinality}.
	 * 
	 * @param objectPropertyExpression
	 * @param cardinality
	 * @return
	 */
	public  ElkObjectMaxCardinality getObjectMaxCardinality(
			ElkObjectPropertyExpression objectPropertyExpression,
			int cardinality);

	/**
	 * Create an {@link ElkObjectMaxCardinalityQualified}.
	 * 
	 * @param objectPropertyExpression
	 * @param cardinality
	 * @param classExpression
	 * @return
	 */
	public  ElkObjectMaxCardinalityQualified getObjectMaxCardinalityQualified(
			ElkObjectPropertyExpression objectPropertyExpression,
			int cardinality, ElkClassExpression classExpression);

	/**
	 * Create an {@link ElkObjectMinCardinality}.
	 * 
	 * @param objectPropertyExpression
	 * @param cardinality
	 * @return
	 */
	public  ElkObjectMinCardinality getObjectMinCardinality(
			ElkObjectPropertyExpression objectPropertyExpression,
			int cardinality);

	/**
	 * Create an {@link ElkObjectMinCardinalityQualified}.
	 * 
	 * @param objectPropertyExpression
	 * @param cardinality
	 * @param classExpression
	 * @return
	 */
	public  ElkObjectMinCardinalityQualified getObjectMinCardinalityQualified(
			ElkObjectPropertyExpression objectPropertyExpression,
			int cardinality, ElkClassExpression classExpression);

	/**
	 * Create an {@link ElkObjectOneOf}.
	 * 
	 * @param firstIndividual
	 * @param otherIndividuals
	 * @return
	 */
	public  ElkObjectOneOf getObjectOneOf(
			ElkIndividual firstIndividual, ElkIndividual... otherIndividuals);

	/**
	 * Create an {@link ElkObjectOneOf}.
	 * 
	 * @param individuals
	 * @return
	 */
	public  ElkObjectOneOf getObjectOneOf(
			List<? extends ElkIndividual> individuals);

	/**
	 * Create an {@link ElkObjectProperty}.
	 * 
	 * @param objectPropertyIri
	 * @return
	 */
	public  ElkObjectProperty getObjectProperty(ElkIri iri);

	/**
	 * Create an {@link ElkObjectPropertyAssertionAxiom}.
	 * 
	 * @param objectPropertyExpression
	 * @param firstIndividual
	 * @param secondIndividual
	 * @return
	 */
	public  ElkObjectPropertyAssertionAxiom getObjectPropertyAssertionAxiom(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkIndividual firstIndividual, ElkIndividual secondIndividual);

	/**
	 * Create an {@link ElkObjectPropertyChain}.
	 * 
	 * @param objectPropertyExpressions
	 * @return
	 */
	public  ElkObjectPropertyChain getObjectPropertyChain(
			List<? extends ElkObjectPropertyExpression> objectPropertyExpressions);

	/**
	 * Create an {@link ElkObjectPropertyDomainAxiom}.
	 * 
	 * @param objectPropertyExpression
	 * @param classExpression
	 * @return
	 */
	public  ElkObjectPropertyDomainAxiom getObjectPropertyDomainAxiom(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkClassExpression classExpression);

	/**
	 * Create an {@link ElkObjectPropertyRangeAxiom}.
	 * 
	 * @param objectPropertyExpression
	 * @param classExpression
	 * @return
	 */
	public  ElkObjectPropertyRangeAxiom getObjectPropertyRangeAxiom(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkClassExpression classExpression);

	/**
	 * Create an {@link ElkObjectSomeValuesFrom}.
	 * 
	 * @param objectPropertyExpression
	 * @param classExpression
	 * @return
	 */
	public  ElkObjectSomeValuesFrom getObjectSomeValuesFrom(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkClassExpression classExpression);

	/**
	 * Create an {@link ElkObjectUnionOf}.
	 * 
	 * @param firstClassExpression
	 * @param secondClassExpression
	 * @param otherClassExpressions
	 * @return
	 */
	public  ElkObjectUnionOf getObjectUnionOf(
			ElkClassExpression firstClassExpression,
			ElkClassExpression secondClassExpression,
			ElkClassExpression... otherClassExpressions);

	/**
	 * Create an {@link ElkObjectUnionOf}.
	 * 
	 * @param classExpressions
	 * @return
	 */
	public  ElkObjectUnionOf getObjectUnionOf(
			List<? extends ElkClassExpression> classExpressions);

	/**
	 * Create the ElkDataProperty representing owl:BottomDataProperty}.
	 * 
	 * @return
	 */
	public  ElkDataProperty getOwlBottomDataProperty();

	/**
	 * Create the ElkObjectProperty representing owl:BottomObjectProperty}.
	 * 
	 * @return
	 */
	public  ElkObjectProperty getOwlBottomObjectProperty();

	/**
	 * Create the ElkClass representing owl:Nothing}.
	 * 
	 * @return
	 */
	public  ElkClass getOwlNothing();

	/**
	 * Create the ElkClass representing owl:Thing}.
	 * 
	 * @return
	 */
	public  ElkClass getOwlThing();

	/**
	 * Create the ElkDataProperty representing owl:TopDataProperty}.
	 * 
	 * @return
	 */
	public  ElkDataProperty getOwlTopDataProperty();

	/**
	 * Create the ElkObjectProperty representing owl:TopObjectProperty}.
	 * 
	 * @return
	 */
	public  ElkObjectProperty getOwlTopObjectProperty();

	/**
	 * Create an {@link ElkReflexiveObjectPropertyAxiom}.
	 * 
	 * @param objectPropertyExpression
	 * @return
	 */
	public  ElkReflexiveObjectPropertyAxiom getReflexiveObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression);

	/**
	 * Create an {@link ElkSameIndividualAxiom}.
	 * 
	 * @param firstIndividual
	 * @param secondIndividual
	 * @param otherIndividuals
	 * @return
	 */
	public  ElkSameIndividualAxiom getSameIndividualAxiom(
			ElkIndividual firstIndividual, ElkIndividual secondIndividual,
			ElkIndividual... otherIndividuals);

	/**
	 * Create an {@link ElkSameIndividualAxiom}.
	 * 
	 * @param individuals
	 * @return
	 */
	public  ElkSameIndividualAxiom getSameIndividualAxiom(
			List<? extends ElkIndividual> individuals);

	/**
	 * Create an {@link ElkSubClassOfAxiom}.
	 * 
	 * @param subClassExpression
	 * @param superClassExpression
	 * @return
	 */
	public  ElkSubClassOfAxiom getSubClassOfAxiom(
			ElkClassExpression subClassExpression,
			ElkClassExpression superClassExpression);

	/**
	 * Create an {@link ElkSubDataPropertyOfAxiom}.
	 * 
	 * @param subDataPropertyExpression
	 * @param superDataPropertyExpression
	 * @return
	 */
	public  ElkSubDataPropertyOfAxiom getSubDataPropertyOfAxiom(
			ElkDataPropertyExpression subDataPropertyExpression,
			ElkDataPropertyExpression superDataPropertyExpression);

	/**
	 * Create an {@link ElkSubObjectPropertyOfAxiom}.
	 * 
	 * @param subObjectPropertyExpression
	 * @param superObjectPropertyExpression
	 * @return
	 */
	public  ElkSubObjectPropertyOfAxiom getSubObjectPropertyOfAxiom(
			ElkSubObjectPropertyExpression subObjectPropertyExpression,
			ElkObjectPropertyExpression superObjectPropertyExpression);

	/**
	 * Create an {@link ElkSymmetricObjectPropertyAxiom}.
	 * 
	 * @param objectPropertyExpression
	 * @return
	 */
	public  ElkSymmetricObjectPropertyAxiom getSymmetricObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression);

	/**
	 * Create an {@link ElkTransitiveObjectPropertyAxiom}.
	 * 
	 * @param objectPropertyExpression
	 * @return
	 */
	public  ElkTransitiveObjectPropertyAxiom getTransitiveObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression);
	
	/**
	 * Create an {@link ElkHasKeyAxiom}
	 * 
	 * @param classExpr
	 * @param objectPEs
	 * @param dataPEs
	 * @return
	 */
	public ElkHasKeyAxiom getHasKeyAxiom(	ElkClassExpression classExpr,
											Set<ElkObjectPropertyExpression> objectPEs,
											Set<ElkDataPropertyExpression> dataPEs);
	
	/**
	 * Create an {@link ElkDatatypeDefinitionAxiom}
	 * 
	 * @param datatype
	 * @param dataRange
	 * @return
	 */
	public ElkDatatypeDefinitionAxiom getDatatypeDefinitionAxiom( ElkDatatype datatype, ElkDataRange dataRange);
	
	public ElkFacetRestriction getFacetRestriction( ElkIri iri, ElkLiteral literal);
}