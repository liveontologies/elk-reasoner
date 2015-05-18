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

import org.semanticweb.elk.owl.iris.ElkIri;

/**
 * Interface that provides methods for creating instances of ElkObjects.
 * 
 * @author Markus Kroetzsch
 * @author "Yevgeny Kazakov"
 */
public interface ElkObjectFactory {

	/**
	 * Create an {@link ElkAnnotation}
	 * 
	 * @param property
	 *            the {@link ElkAnnotationProperty} for which the object should
	 *            be created
	 * @param value
	 *            the {@link ElkAnnotationValue} for which the object should be
	 *            created
	 * @return an {@link ElkAnnotation} corresponding to the input
	 */
	public ElkAnnotation getAnnotation(ElkAnnotationProperty property,
			ElkAnnotationValue value);

	/**
	 * Create an {@link ElkAnnotationProperty}.
	 * 
	 * @param iri
	 *            the {@link ElkIri} for which the object should be created
	 * @return an {@link ElkAnnotation} corresponding to the input
	 */
	public ElkAnnotationProperty getAnnotationProperty(ElkIri iri);

	/**
	 * Create an {@link ElkAnnotationAssertionAxiom}
	 * 
	 * @param property
	 *            the {@link ElkAnnotationProperty} for which the axiom should
	 *            be created
	 * @param subject
	 *            the {@link ElkAnnotationSubject} for which the axiom should be
	 *            created
	 * @param value
	 *            the {@link ElkAnnotationValue} for which the axiom should be
	 *            created
	 * @return an {@link ElkAnnotationAssertionAxiom} corresponding to the input
	 */
	public ElkAnnotationAssertionAxiom getAnnotationAssertionAxiom(
			ElkAnnotationProperty property, ElkAnnotationSubject subject,
			ElkAnnotationValue value);

	/**
	 * Create an {@link ElkAnnotationPropertyDomainAxiom}
	 * 
	 * @param property
	 *            the {@link ElkAnnotationProperty} for which the axiom should
	 *            be created
	 * @param domain
	 *            the {@link ElkIri} for which the axiom should be created
	 * @return an {@link ElkAnnotationPropertyDomainAxiom} corresponding to the
	 *         input
	 */
	public ElkAnnotationPropertyDomainAxiom getAnnotationPropertyDomainAxiom(
			ElkAnnotationProperty property, ElkIri domain);

	/**
	 * Create an {@link ElkAnnotationPropertyRangeAxiom}
	 * 
	 * @param property
	 *            the {@link ElkAnnotationProperty} for which the axiom should
	 *            be created
	 * @param range
	 *            the {@link ElkIri} for which the object axiom be created
	 * @return an {@link ElkAnnotationPropertyRangeAxiom} corresponding to the
	 *         input
	 */
	public ElkAnnotationPropertyRangeAxiom getAnnotationPropertyRangeAxiom(
			ElkAnnotationProperty property, ElkIri range);

	/**
	 * Create an {@link ElkAnonymousIndividual}.
	 * 
	 * @param nodeId
	 *            the {@link String} for which the object should be created
	 * @return an {@link ElkAnonymousIndividual} corresponding to the input
	 */
	public ElkAnonymousIndividual getAnonymousIndividual(String nodeId);

	/**
	 * Create an {@link ElkAsymmetricObjectPropertyAxiom}.
	 * 
	 * @param objectPropertyExpression
	 *            the {@link ElkObjectPropertyExpression} for which the axiom
	 *            should be created
	 * @return an {@link ElkAsymmetricObjectPropertyAxiom} corresponding to the
	 *         input
	 */
	public ElkAsymmetricObjectPropertyAxiom getAsymmetricObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression);

	/**
	 * Create an {@link ElkClass}.
	 * 
	 * @param iri
	 *            the {@link ElkIri} for which the object should be created
	 * @return an {@link ElkClass} corresponding to the input
	 */
	public ElkClass getClass(ElkIri iri);

	/**
	 * Create an {@link ElkClassAssertionAxiom}.
	 * 
	 * @param classExpression
	 *            the {@link ElkClassExpression} for which the axiom should be
	 *            created
	 * @param individual
	 *            the {@link ElkIndividual} for which the axiom should be
	 *            created
	 * @return an {@link ElkClassAssertionAxiom} corresponding to the input
	 */
	public ElkClassAssertionAxiom getClassAssertionAxiom(
			ElkClassExpression classExpression, ElkIndividual individual);

	/**
	 * Create an {@link ElkDataAllValuesFrom}.
	 * 
	 * @param dataRange
	 *            the {@link ElkDataRange} for which the object should be
	 *            created
	 * @param dpe1
	 *            the {@link ElkDataPropertyExpression} for which the object
	 *            should be created
	 * @param dpe
	 *            the {@link ElkDataPropertyExpression}s for which the object
	 *            should be created
	 * @return an {@link ElkDataAllValuesFrom} corresponding to the input
	 */
	public ElkDataAllValuesFrom getDataAllValuesFrom(ElkDataRange dataRange,
			ElkDataPropertyExpression dpe1, ElkDataPropertyExpression... dpe);

	/**
	 * Create an {@link ElkDataAllValuesFrom}.
	 * 
	 * @param dataRange
	 *            the {@link ElkDataRange} for which the object should be
	 *            created
	 * @param dpList
	 *            the {@link ElkDataPropertyExpression}s for which the object
	 *            should be created
	 * @return an {@link ElkDataAllValuesFrom} corresponding to the input
	 */
	public ElkDataAllValuesFrom getDataAllValuesFrom(ElkDataRange dataRange,
			List<? extends ElkDataPropertyExpression> dpList);

	/**
	 * Create an {@link ElkDataComplementOf}.
	 * 
	 * @param dataRange
	 *            the {@link ElkDataRange} for which the object should be
	 *            created
	 * @return an {@link ElkDataComplementOf} corresponding to the input
	 */
	public ElkDataComplementOf getDataComplementOf(ElkDataRange dataRange);

	/**
	 * Create an {@link ElkDataExactCardinalityUnqualified}.
	 * 
	 * @param dataPropertyExpression
	 *            the {@link ElkDataPropertyExpression} for which the object
	 *            should be created
	 * @param cardinality
	 *            the cardinality for which the object should be created
	 * @return an {@link ElkDataExactCardinalityUnqualified} corresponding to
	 *         the input
	 */
	public ElkDataExactCardinalityUnqualified getDataExactCardinalityUnqualified(
			ElkDataPropertyExpression dataPropertyExpression, int cardinality);

	/**
	 * Create an {@link ElkDataExactCardinalityQualified}.
	 * 
	 * @param dataPropertyExpression
	 *            the {@link ElkDataPropertyExpression} for which the object
	 *            should be created
	 * @param cardinality
	 *            the cardinality for which the object should be created
	 * @param dataRange
	 *            the {@link ElkDataRange} for which the object should be
	 *            created
	 * @return an {@link ElkDataExactCardinalityQualified} corresponding to the
	 *         input
	 */
	public ElkDataExactCardinalityQualified getDataExactCardinalityQualified(
			ElkDataPropertyExpression dataPropertyExpression, int cardinality,
			ElkDataRange dataRange);

	/**
	 * Create an {@link ElkDataHasValue}.
	 * 
	 * @param dataPropertyExpression
	 *            the {@link ElkDataPropertyExpression} for which the object
	 *            should be created
	 * @param literal
	 *            the {@link ElkLiteral} for which the object should be created
	 * @return an {@link ElkDataHasValue} corresponding to the input
	 */
	public ElkDataHasValue getDataHasValue(
			ElkDataPropertyExpression dataPropertyExpression, ElkLiteral literal);

	/**
	 * Create an {@link ElkDataIntersectionOf}.
	 * 
	 * @param firstDataRange
	 *            the first {@link ElkDataRange} for which the object should be
	 *            created
	 * @param secondDataRange
	 *            the second {@link ElkDataRange} for which the object should be
	 *            created
	 * @param otherDataRanges
	 *            the {@link ElkDataRange} for which the object should be
	 *            created
	 * @return an {@link ElkDataIntersectionOf} corresponding to the input
	 */
	public ElkDataIntersectionOf getDataIntersectionOf(
			ElkDataRange firstDataRange, ElkDataRange secondDataRange,
			ElkDataRange... otherDataRanges);

	/**
	 * Create an {@link ElkDataIntersectionOf}.
	 * 
	 * @param dataRanges
	 *            the {@link ElkDataRange}s for which the object should be
	 *            created
	 * @return an {@link ElkDataIntersectionOf} corresponding to the input
	 */
	public ElkDataIntersectionOf getDataIntersectionOf(
			List<? extends ElkDataRange> dataRanges);

	/**
	 * Create an {@link ElkDataMaxCardinalityUnqualified}.
	 * 
	 * @param dataPropertyExpression
	 *            the {@link ElkDataPropertyExpression} for which the object
	 *            should be created
	 * @param cardinality
	 *            the cardinality for which the object should be created
	 * @return an {@link ElkDataMaxCardinalityUnqualified} corresponding to the
	 *         input
	 */
	public ElkDataMaxCardinalityUnqualified getDataMaxCardinalityUnqualified(
			ElkDataPropertyExpression dataPropertyExpression, int cardinality);

	/**
	 * Create an {@link ElkDataMaxCardinalityQualified}.
	 * 
	 * @param dataPropertyExpression
	 *            the {@link ElkDataPropertyExpression} for which the object
	 *            should be created
	 * @param cardinality
	 *            the cardinality for which the object should be created
	 * @param dataRange
	 *            the {@link ElkDataRange} for which the object should be
	 *            created
	 * @return an {@link ElkDataMaxCardinalityQualified} corresponding to the
	 *         input
	 */
	public ElkDataMaxCardinalityQualified getDataMaxCardinalityQualified(
			ElkDataPropertyExpression dataPropertyExpression, int cardinality,
			ElkDataRange dataRange);

	/**
	 * Create an {@link ElkDataMinCardinalityUnqualified}.
	 * 
	 * @param dataPropertyExpression
	 *            the {@link ElkDataPropertyExpression} for which the object
	 *            should be created
	 * @param cardinality
	 *            the cardinality for which the object should be created
	 * @return an {@link ElkDataMinCardinalityUnqualified} corresponding to the
	 *         input
	 */
	public ElkDataMinCardinalityUnqualified getDataMinCardinalityUnqualified(
			ElkDataPropertyExpression dataPropertyExpression, int cardinality);

	/**
	 * Create an {@link ElkDataMinCardinalityQualified}.
	 * 
	 * @param dataPropertyExpression
	 *            the {@link ElkDataPropertyExpression} for which the object
	 *            should be created
	 * @param cardinality
	 *            the cardinality for which the object should be created
	 * @param dataRange
	 *            the {@link ElkDataRange} for which the object should be
	 *            created
	 * @return an {@link ElkDataMinCardinalityQualified} corresponding to the
	 *         input
	 */
	public ElkDataMinCardinalityQualified getDataMinCardinalityQualified(
			ElkDataPropertyExpression dataPropertyExpression, int cardinality,
			ElkDataRange dataRange);

	/**
	 * Create an {@link ElkDataOneOf}.
	 * 
	 * @param firstLiteral
	 *            the {@link ElkLiteral} for which the object should be created
	 * @param otherLiterals
	 *            other {@link ElkLiteral}s for which the object should be
	 *            created
	 * @return an {@link ElkDataOneOf} corresponding to the input
	 */
	public ElkDataOneOf getDataOneOf(ElkLiteral firstLiteral,
			ElkLiteral... otherLiterals);

	/**
	 * Create an {@link ElkDataOneOf}.
	 * 
	 * @param literals
	 *            the {@link ElkLiteral}s for which the object should be created
	 * @return an {@link ElkDataOneOf} corresponding to the input
	 */
	public ElkDataOneOf getDataOneOf(List<? extends ElkLiteral> literals);

	/**
	 * Create an {@link ElkDataProperty}.
	 * 
	 * @param iri
	 *            the {@link ElkIri} for which the object should be created
	 * @return an {@link ElkDataProperty} corresponding to the input
	 */
	public ElkDataProperty getDataProperty(ElkIri iri);

	/**
	 * Create an {@link ElkDataPropertyAssertionAxiom}.
	 * 
	 * @param dataPropertyExpression
	 *            the {@link ElkDataPropertyExpression} for which the object
	 *            should be created
	 * @param individual
	 *            the {@link ElkIndividual} for which the object should be
	 *            created
	 * @param literal
	 *            the {@link ElkLiteral} for which the object should be created
	 * @return an {@link ElkDataPropertyAssertionAxiom} corresponding to the
	 *         input
	 */
	public ElkDataPropertyAssertionAxiom getDataPropertyAssertionAxiom(
			ElkDataPropertyExpression dataPropertyExpression,
			ElkIndividual individual, ElkLiteral literal);

	/**
	 * Create an {@link ElkDataPropertyDomainAxiom}.
	 * 
	 * @param dataPropertyExpression
	 *            the {@link ElkDataPropertyExpression} for which the object
	 *            should be created
	 * @param classExpression
	 *            the {@link ElkClassExpression} for which the object should be
	 *            created
	 * @return an {@link ElkDataPropertyDomainAxiom} corresponding to the input
	 */
	public ElkDataPropertyDomainAxiom getDataPropertyDomainAxiom(
			ElkDataPropertyExpression dataPropertyExpression,
			ElkClassExpression classExpression);

	/**
	 * Create an {@link ElkDataPropertyRangeAxiom}.
	 * 
	 * @param dataPropertyExpression
	 *            the {@link ElkDataPropertyExpression} for which the object
	 *            should be created
	 * @param dataRange
	 *            the {@link ElkDataRange} for which the object should be
	 *            created
	 * @return an {@link ElkDataPropertyRangeAxiom} corresponding to the input
	 */
	public ElkDataPropertyRangeAxiom getDataPropertyRangeAxiom(
			ElkDataPropertyExpression dataPropertyExpression,
			ElkDataRange dataRange);

	/**
	 * Create an {@link ElkDataSomeValuesFrom}
	 * 
	 * @param dataRange
	 *            the {@link ElkDataRange} for which the object should be
	 *            created
	 * @param firstDataPropertyExpression
	 *            the first {@link ElkDataPropertyExpression} for which the
	 *            object should be created
	 * @param otherDataPropertyExpressions
	 *            other {@link ElkDataPropertyExpression}s for which the object
	 *            should be created
	 * @return an {@link ElkDataSomeValuesFrom} corresponding to the input
	 */
	public ElkDataSomeValuesFrom getDataSomeValuesFrom(ElkDataRange dataRange,
			ElkDataPropertyExpression firstDataPropertyExpression,
			ElkDataPropertyExpression... otherDataPropertyExpressions);

	/**
	 * Create an {@link ElkDataSomeValuesFrom}
	 * 
	 * @param dataRange
	 *            the {@link ElkDataRange} for which the object should be
	 *            created
	 * @param dpList
	 *            the {@link ElkDataPropertyExpression}s for which the object
	 *            should be created
	 * @return an {@link ElkDataSomeValuesFrom} corresponding to the input
	 */
	public ElkDataSomeValuesFrom getDataSomeValuesFrom(ElkDataRange dataRange,
			List<? extends ElkDataPropertyExpression> dpList);

	/**
	 * Create an {@link ElkDatatype}.
	 * 
	 * @param iri
	 *            the {@link ElkIri} for which the object should be created
	 * @return an {@link ElkDatatype} corresponding to the input
	 */
	public ElkDatatype getDatatype(ElkIri iri);

	/**
	 * Create the ElkDatatype for rdf:PlainLiteral}.
	 * 
	 * @return an {@link ElkDatatype} corresponding to the input
	 */
	public ElkDatatype getDatatypeRdfPlainLiteral();

	/**
	 * Create an {@link ElkDatatypeRestriction}.
	 * 
	 * @param datatype
	 *            the {@link ElkDatatype} for which the object should be created
	 * @param facetRestrictions
	 *            the {@link ElkFacetRestriction}s for which the object should
	 *            be created
	 * @return an {@link ElkDatatypeRestriction} corresponding to the input
	 */
	public ElkDatatypeRestriction getDatatypeRestriction(ElkDatatype datatype,
			List<ElkFacetRestriction> facetRestrictions);

	/**
	 * Create an {@link ElkDataUnionOf}.
	 * 
	 * @param firstDataRange
	 *            the first {@link ElkDataRange} for which the object should be
	 *            created
	 * @param secondDataRange
	 *            the second {@link ElkDataRange} for which the object should be
	 *            created
	 * @param otherDataRanges
	 *            other {@link ElkDataRange}s for which the object should be
	 *            created
	 * @return an {@link ElkDataUnionOf} corresponding to the input
	 */
	public ElkDataUnionOf getDataUnionOf(ElkDataRange firstDataRange,
			ElkDataRange secondDataRange, ElkDataRange... otherDataRanges);

	/**
	 * Create an {@link ElkDataUnionOf}.
	 * 
	 * @param dataRanges
	 *            the {@link ElkDataRange}s for which the object should be
	 *            created
	 * @return an {@link ElkDataUnionOf} corresponding to the input
	 */
	public ElkDataUnionOf getDataUnionOf(List<? extends ElkDataRange> dataRanges);

	/**
	 * Create an {@link ElkDeclarationAxiom}.
	 * 
	 * @param entity
	 *            the {@link ElkEntity} for which the axiom should be created
	 * @return an {@link ElkDeclarationAxiom} corresponding to the input
	 */
	public ElkDeclarationAxiom getDeclarationAxiom(ElkEntity entity);

	/**
	 * Create an {@link ElkDifferentIndividualsAxiom}.
	 * 
	 * @param firstIndividual
	 *            the first {@link ElkIndividual} for which the axiom should be
	 *            created
	 * @param secondIndividual
	 *            the second {@link ElkIndividual} for which the axiom should be
	 *            created
	 * @param otherIndividuals
	 *            other {@link ElkIndividual} for which the axiom should be
	 *            created
	 * @return an {@link ElkAnnotation} corresponding to the input
	 */
	public ElkDifferentIndividualsAxiom getDifferentIndividualsAxiom(
			ElkIndividual firstIndividual, ElkIndividual secondIndividual,
			ElkIndividual... otherIndividuals);

	/**
	 * Create an {@link ElkDifferentIndividualsAxiom}.
	 * 
	 * @param individuals
	 *            the {@link ElkIndividual}s for which the axiom should be
	 *            created
	 * @return an {@link ElkDifferentIndividualsAxiom} corresponding to the
	 *         input
	 */
	public ElkDifferentIndividualsAxiom getDifferentIndividualsAxiom(
			List<? extends ElkIndividual> individuals);

	/**
	 * Create an {@link ElkDisjointClassesAxiom}.
	 * 
	 * @param firstClassExpression
	 *            the first {@link ElkClassExpression} for which the axiom
	 *            should be created
	 * @param secondClassExpression
	 *            the second {@link ElkClassExpression} for which the axiom
	 *            should be created
	 * @param otherClassExpressions
	 *            other {@link ElkClassExpression} for which the axiom should be
	 *            created
	 * @return an {@link ElkDisjointClassesAxiom} corresponding to the input
	 */
	public ElkDisjointClassesAxiom getDisjointClassesAxiom(
			ElkClassExpression firstClassExpression,
			ElkClassExpression secondClassExpression,
			ElkClassExpression... otherClassExpressions);

	/**
	 * Create an {@link ElkDisjointClassesAxiom}.
	 * 
	 * @param disjointClassExpressions
	 *            the {@link ElkClassExpression}s for which the axiom should be
	 *            created
	 * @return an {@link ElkDisjointClassesAxiom} corresponding to the input
	 */
	public ElkDisjointClassesAxiom getDisjointClassesAxiom(
			List<? extends ElkClassExpression> disjointClassExpressions);

	/**
	 * Create an {@link ElkDisjointDataPropertiesAxiom}.
	 * 
	 * @param firstDataPropertyExpression
	 *            the {@link ElkDataPropertyExpression} for which the axiom
	 *            should be created
	 * @param secondDataPropertyExpression
	 *            the {@link ElkDataPropertyExpression} for which the axiom
	 *            should be created
	 * @param otherDataPropertyExpressions
	 *            the {@link ElkDataPropertyExpression} for which the axiom
	 *            should be created
	 * @return an {@link ElkDisjointDataPropertiesAxiom} corresponding to the
	 *         input
	 */
	public ElkDisjointDataPropertiesAxiom getDisjointDataPropertiesAxiom(
			ElkDataPropertyExpression firstDataPropertyExpression,
			ElkDataPropertyExpression secondDataPropertyExpression,
			ElkDataPropertyExpression... otherDataPropertyExpressions);

	/**
	 * Create an {@link ElkDisjointDataPropertiesAxiom}.
	 * 
	 * @param disjointDataPropertyExpressions
	 *            the {@link ElkDataPropertyExpression}s for which the axiom
	 *            should be created
	 * @return an {@link ElkDisjointDataPropertiesAxiom} corresponding to the
	 *         input
	 */
	public ElkDisjointDataPropertiesAxiom getDisjointDataPropertiesAxiom(
			List<? extends ElkDataPropertyExpression> disjointDataPropertyExpressions);

	/**
	 * Create an {@link ElkDisjointObjectPropertiesAxiom}.
	 * 
	 * @param firstObjectPropertyExpression
	 *            the first {@link ElkObjectPropertyExpression} for which the
	 *            axiom should be created
	 * @param secondObjectPropertyExpression
	 *            the second {@link ElkObjectPropertyExpression} for which the
	 *            axiom should be created
	 * @param otherObjectPropertyExpressions
	 *            other {@link ElkObjectPropertyExpression}s for which the axiom
	 *            should be created
	 * @return an {@link ElkDisjointObjectPropertiesAxiom} corresponding to the
	 *         input
	 */
	public ElkDisjointObjectPropertiesAxiom getDisjointObjectPropertiesAxiom(
			ElkObjectPropertyExpression firstObjectPropertyExpression,
			ElkObjectPropertyExpression secondObjectPropertyExpression,
			ElkObjectPropertyExpression... otherObjectPropertyExpressions);

	/**
	 * Create an {@link ElkDisjointObjectPropertiesAxiom}.
	 * 
	 * @param disjointObjectPropertyExpressions
	 *            the {@link ElkObjectPropertyExpression}s for which the axiom
	 *            should be created
	 * @return an {@link ElkDisjointObjectPropertiesAxiom} corresponding to the
	 *         input
	 */
	public ElkDisjointObjectPropertiesAxiom getDisjointObjectPropertiesAxiom(
			List<? extends ElkObjectPropertyExpression> disjointObjectPropertyExpressions);

	/**
	 * Create an {@link ElkDisjointUnionAxiom}.
	 * 
	 * @param definedClass
	 *            the defined {@link ElkClassExpression} for which the axiom
	 *            should be created
	 * @param firstClassExpression
	 *            the first disjoint {@link ElkClassExpression} for which the
	 *            axiom should be created
	 * @param secondClassExpression
	 *            the second disjoint {@link ElkClassExpression} for which the
	 *            axiom should be created
	 * @param otherClassExpressions
	 *            other disjoint {@link ElkClassExpression}s for which the axiom
	 *            should be created
	 * @return an {@link ElkDisjointUnionAxiom} corresponding to the input
	 */
	public ElkDisjointUnionAxiom getDisjointUnionAxiom(ElkClass definedClass,
			ElkClassExpression firstClassExpression,
			ElkClassExpression secondClassExpression,
			ElkClassExpression... otherClassExpressions);

	/**
	 * Create an {@link ElkDisjointUnionAxiom}.
	 * 
	 * @param definedClass
	 *            the defined {@link ElkClassExpression} for which the axiom
	 *            should be created
	 * @param disjointClassExpressions
	 *            the disjoint {@link ElkClassExpression}s for which the axiom
	 *            should be created
	 * @return an {@link ElkDisjointUnionAxiom} corresponding to the input
	 */
	public ElkDisjointUnionAxiom getDisjointUnionAxiom(ElkClass definedClass,
			List<? extends ElkClassExpression> disjointClassExpressions);

	/**
	 * Create an {@link ElkEquivalentClassesAxiom}.
	 * 
	 * @param firstClassExpression
	 *            the first equivalent {@link ElkClassExpression} for which the
	 *            axiom should be created
	 * @param secondClassExpression
	 *            the second equivalent {@link ElkClassExpression} for which the
	 *            axiom should be created
	 * @param otherClassExpressions
	 *            other equivalent {@link ElkClassExpression}s for which the
	 *            axiom should be created
	 * @return an {@link ElkEquivalentClassesAxiom} corresponding to the input
	 */
	public ElkEquivalentClassesAxiom getEquivalentClassesAxiom(
			ElkClassExpression firstClassExpression,
			ElkClassExpression secondClassExpression,
			ElkClassExpression... otherClassExpressions);

	/**
	 * Create an {@link ElkEquivalentClassesAxiom}.
	 * 
	 * @param equivalentClassExpressions
	 *            the equivalent {@link ElkClassExpression}s for which the axiom
	 *            should be created
	 * @return an {@link ElkEquivalentClassesAxiom} corresponding to the input
	 */
	public ElkEquivalentClassesAxiom getEquivalentClassesAxiom(
			List<? extends ElkClassExpression> equivalentClassExpressions);

	/**
	 * Create an {@link ElkEquivalentDataPropertiesAxiom}.
	 * 
	 * @param firstDataPropertyExpression
	 *            the fist equivalent {@link ElkDataPropertyExpression} for
	 *            which the axiom should be created
	 * @param secondDataPropertyExpression
	 *            the second equivalent {@link ElkDataPropertyExpression} for
	 *            which the axiom should be created
	 * @param otherDataPropertyExpressions
	 *            other equivalent {@link ElkDataPropertyExpression}s for which
	 *            the axiom should be created
	 * @return an {@link ElkEquivalentDataPropertiesAxiom} corresponding to the
	 *         input
	 */
	public ElkEquivalentDataPropertiesAxiom getEquivalentDataPropertiesAxiom(
			ElkDataPropertyExpression firstDataPropertyExpression,
			ElkDataPropertyExpression secondDataPropertyExpression,
			ElkDataPropertyExpression... otherDataPropertyExpressions);

	/**
	 * Create an {@link ElkEquivalentDataPropertiesAxiom}.
	 * 
	 * @param equivalentDataPropertyExpressions
	 *            the equivalent {@link ElkDataPropertyExpression}s for which
	 *            the axiom should be created
	 * @return an {@link ElkEquivalentDataPropertiesAxiom} corresponding to the
	 *         input
	 */
	public ElkEquivalentDataPropertiesAxiom getEquivalentDataPropertiesAxiom(
			List<? extends ElkDataPropertyExpression> equivalentDataPropertyExpressions);

	/**
	 * Create an {@link ElkEquivalentObjectPropertiesAxiom}.
	 * 
	 * @param firstObjectPropertyExpression
	 *            the first equivalent {@link ElkObjectPropertyExpression} for
	 *            which the axiom should be created
	 * @param secondObjectPropertyExpression
	 *            the second equivalent {@link ElkObjectPropertyExpression} for
	 *            which the axiom should be created
	 * @param otherObjectPropertyExpressions
	 *            other equivalent {@link ElkObjectPropertyExpression}s for
	 *            which the axiom should be created
	 * @return an {@link ElkEquivalentObjectPropertiesAxiom} corresponding to
	 *         the input
	 */
	public ElkEquivalentObjectPropertiesAxiom getEquivalentObjectPropertiesAxiom(
			ElkObjectPropertyExpression firstObjectPropertyExpression,
			ElkObjectPropertyExpression secondObjectPropertyExpression,
			ElkObjectPropertyExpression... otherObjectPropertyExpressions);

	/**
	 * Create an {@link ElkEquivalentObjectPropertiesAxiom}.
	 * 
	 * @param equivalentObjectPropertyExpressions
	 *            the equivalent {@link ElkObjectPropertyExpression}s for which
	 *            the axiom should be created
	 * @return an {@link ElkEquivalentObjectPropertiesAxiom} corresponding to
	 *         the input
	 */
	public ElkEquivalentObjectPropertiesAxiom getEquivalentObjectPropertiesAxiom(
			List<? extends ElkObjectPropertyExpression> equivalentObjectPropertyExpressions);

	/**
	 * Create an {@link ElkFunctionalDataPropertyAxiom}.
	 * 
	 * @param dataPropertyExpression
	 *            the functional {@link ElkDataPropertyExpression} for which the
	 *            axiom should be created
	 * @return an {@link ElkFunctionalDataPropertyAxiom} corresponding to the
	 *         input
	 */
	public ElkFunctionalDataPropertyAxiom getFunctionalDataPropertyAxiom(
			ElkDataPropertyExpression dataPropertyExpression);

	/**
	 * Create an {@link ElkFunctionalObjectPropertyAxiom}.
	 * 
	 * @param objectPropertyExpression
	 *            the functional {@link ElkObjectPropertyExpression} for which
	 *            the axiom should be created
	 * @return an {@link ElkFunctionalObjectPropertyAxiom} corresponding to the
	 *         input
	 */
	public ElkFunctionalObjectPropertyAxiom getFunctionalObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression);

	/**
	 * Create an {@link ElkInverseFunctionalObjectPropertyAxiom}.
	 * 
	 * @param objectPropertyExpression
	 *            the inverse functional {@link ElkObjectPropertyExpression} for
	 *            which the axiom should be created
	 * @return an {@link ElkInverseFunctionalObjectPropertyAxiom} corresponding
	 *         to the input
	 */
	public ElkInverseFunctionalObjectPropertyAxiom getInverseFunctionalObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression);

	/**
	 * Create an {@link ElkInverseObjectPropertiesAxiom}.
	 * 
	 * @param firstObjectPropertyExpression
	 *            the first {@link ElkObjectPropertyExpression} that should be
	 *            the inverse of the second one for which the axiom should be
	 *            created
	 * @param secondObjectPropertyExpression
	 *            the second {@link ElkObjectPropertyExpression} that should be
	 *            the inverse of the first one for which the axiom should be
	 *            created
	 * @return an {@link ElkInverseObjectPropertiesAxiom} corresponding to the
	 *         input
	 */
	public ElkInverseObjectPropertiesAxiom getInverseObjectPropertiesAxiom(
			ElkObjectPropertyExpression firstObjectPropertyExpression,
			ElkObjectPropertyExpression secondObjectPropertyExpression);

	/**
	 * Create an {@link ElkIrreflexiveObjectPropertyAxiom}.
	 * 
	 * @param objectPropertyExpression
	 *            the irreflexive {@link ElkObjectPropertyExpression} for which
	 *            the axiom should be created
	 * @return an {@link ElkIrreflexiveObjectPropertyAxiom} corresponding to the
	 *         input
	 */
	public ElkIrreflexiveObjectPropertyAxiom getIrreflexiveObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression);

	/**
	 * Create an {@link ElkLiteral}.
	 * 
	 * @param lexicalForm
	 *            the {@link String} for which the object should be created
	 * @param datatype
	 *            the {@link ElkDatatype} for which the object should be created
	 * @return an {@link ElkLiteral} corresponding to the input
	 */
	public ElkLiteral getLiteral(String lexicalForm, ElkDatatype datatype);

	/**
	 * Create an {@link ElkNamedIndividual}.
	 * 
	 * @param iri
	 *            the {@link ElkIri} for which the object should be created
	 * @return an {@link ElkNamedIndividual} corresponding to the input
	 */
	public ElkNamedIndividual getNamedIndividual(ElkIri iri);

	/**
	 * Create an {@link ElkNegativeDataPropertyAssertionAxiom}.
	 * 
	 * @param dataPropertyExpression
	 *            the {@link ElkDataPropertyExpression} for which the axiom
	 *            should be created
	 * @param individual
	 *            the {@link ElkIndividual} for which the axiom should be
	 *            created
	 * @param literal
	 *            the {@link ElkLiteral} for which the axiom should be created
	 * @return an {@link ElkNegativeDataPropertyAssertionAxiom} corresponding to
	 *         the input
	 */
	public ElkNegativeDataPropertyAssertionAxiom getNegativeDataPropertyAssertionAxiom(
			ElkDataPropertyExpression dataPropertyExpression,
			ElkIndividual individual, ElkLiteral literal);

	/**
	 * Create an {@link ElkNegativeObjectPropertyAssertionAxiom}.
	 * 
	 * @param objectPropertyExpression
	 *            the {@link ElkObjectPropertyExpression} for which the axiom
	 *            should be created
	 * @param firstIndividual
	 *            the {@link ElkIndividual} for which the axiom should be
	 *            created
	 * @param secondIndividual
	 *            the {@link ElkIndividual} for which the axiom should be
	 *            created
	 * @return an {@link ElkNegativeObjectPropertyAssertionAxiom} corresponding
	 *         to the input
	 */
	public ElkNegativeObjectPropertyAssertionAxiom getNegativeObjectPropertyAssertionAxiom(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkIndividual firstIndividual, ElkIndividual secondIndividual);

	/**
	 * Create an {@link ElkObjectAllValuesFrom}.
	 * 
	 * @param objectPropertyExpression
	 *            the {@link ElkObjectPropertyExpression} for which the axiom
	 *            should be created
	 * @param classExpression
	 *            the {@link ElkClassExpression} for which the axiom should be
	 *            created
	 * @return an {@link ElkObjectAllValuesFrom} corresponding to the input
	 */
	public ElkObjectAllValuesFrom getObjectAllValuesFrom(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkClassExpression classExpression);

	/**
	 * Create an {@link ElkObjectComplementOf}.
	 * 
	 * @param classExpression
	 * @return an {@link ElkObjectComplementOf} corresponding to the input
	 */
	public ElkObjectComplementOf getObjectComplementOf(
			ElkClassExpression classExpression);

	/**
	 * Create an {@link ElkObjectExactCardinalityUnqualified}.
	 * 
	 * @param objectPropertyExpression
	 *            the {@link ElkObjectPropertyExpression} for which the object
	 *            should be created
	 * @param cardinality
	 *            the cardinality for which the object should be created
	 * @return an {@link ElkObjectExactCardinalityUnqualified} corresponding to
	 *         the input
	 */
	public ElkObjectExactCardinalityUnqualified getObjectExactCardinalityUnqualified(
			ElkObjectPropertyExpression objectPropertyExpression,
			int cardinality);

	/**
	 * Create an {@link ElkObjectExactCardinalityQualified}.
	 * 
	 * @param objectPropertyExpression
	 *            the {@link ElkObjectPropertyExpression} for which the object
	 *            should be created
	 * @param cardinality
	 *            the cardinality for which the object should be created
	 * @param classExpression
	 *            the {@link ElkClassExpression} for which the object should be
	 *            created
	 * @return an {@link ElkObjectExactCardinalityQualified} corresponding to
	 *         the input
	 */
	public ElkObjectExactCardinalityQualified getObjectExactCardinalityQualified(
			ElkObjectPropertyExpression objectPropertyExpression,
			int cardinality, ElkClassExpression classExpression);

	/**
	 * Create an {@link ElkObjectHasSelf}.
	 * 
	 * @param objectPropertyExpression
	 *            the {@link ElkObjectPropertyExpression} for which the object
	 *            should be created
	 * @return an {@link ElkObjectHasSelf} corresponding to the input
	 */
	public ElkObjectHasSelf getObjectHasSelf(
			ElkObjectPropertyExpression objectPropertyExpression);

	/**
	 * Create an {@link ElkObjectHasValue}.
	 * 
	 * @param objectPropertyExpression
	 *            the {@link ElkObjectPropertyExpression} for which the object
	 *            should be created
	 * @param individual
	 *            the {@link ElkIndividual} for which the object should be
	 *            created
	 * @return an {@link ElkObjectHasValue} corresponding to the input
	 */
	public ElkObjectHasValue getObjectHasValue(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkIndividual individual);

	/**
	 * Create an {@link ElkObjectIntersectionOf}.
	 * 
	 * @param firstClassExpression
	 *            the first {@link ElkClassExpression} for which the object
	 *            should be created
	 * @param secondClassExpression
	 *            the second {@link ElkClassExpression} for which the object
	 *            should be created
	 * @param otherClassExpressions
	 *            other {@link ElkClassExpression}s for which the object should
	 *            be created
	 * @return an {@link ElkObjectIntersectionOf} corresponding to the input
	 */
	public ElkObjectIntersectionOf getObjectIntersectionOf(
			ElkClassExpression firstClassExpression,
			ElkClassExpression secondClassExpression,
			ElkClassExpression... otherClassExpressions);

	/**
	 * Create an {@link ElkObjectIntersectionOf}.
	 * 
	 * @param classExpressions
	 *            the {@link ElkClassExpression}s for which the object should be
	 *            created
	 * @return an {@link ElkObjectIntersectionOf} corresponding to the input
	 */
	public ElkObjectIntersectionOf getObjectIntersectionOf(
			List<? extends ElkClassExpression> classExpressions);

	/**
	 * Create an {@link ElkObjectInverseOf}.
	 * 
	 * @param objectProperty
	 *            the {@link ElkObjectProperty} for which the object should be
	 *            created
	 * @return an {@link ElkObjectInverseOf} corresponding to the input
	 */
	public ElkObjectInverseOf getObjectInverseOf(
			ElkObjectProperty objectProperty);

	/**
	 * Create an {@link ElkObjectMaxCardinalityUnqualified}.
	 * 
	 * @param objectPropertyExpression
	 *            the {@link ElkObjectPropertyExpression} for which the object
	 *            should be created
	 * @param cardinality
	 *            the cardinality for which the object should be created
	 * @return an {@link ElkObjectMaxCardinalityUnqualified} corresponding to
	 *         the input
	 */
	public ElkObjectMaxCardinalityUnqualified getObjectMaxCardinalityUnqualified(
			ElkObjectPropertyExpression objectPropertyExpression,
			int cardinality);

	/**
	 * Create an {@link ElkObjectMaxCardinalityQualified}.
	 * 
	 * @param objectPropertyExpression
	 *            the {@link ElkObjectPropertyExpression} for which the object
	 *            should be created
	 * @param cardinality
	 *            the cardinality for which the object should be created
	 * @param classExpression
	 *            the {@link ElkClassExpression} for which the object should be
	 *            created
	 * @return an {@link ElkObjectMaxCardinalityQualified} corresponding to the
	 *         input
	 */
	public ElkObjectMaxCardinalityQualified getObjectMaxCardinalityQualified(
			ElkObjectPropertyExpression objectPropertyExpression,
			int cardinality, ElkClassExpression classExpression);

	/**
	 * Create an {@link ElkObjectMinCardinalityUnqualified}.
	 * 
	 * @param objectPropertyExpression
	 *            the {@link ElkObjectPropertyExpression} for which the object
	 *            should be created
	 * @param cardinality
	 *            the cardinality for which the object should be created
	 * @return an {@link ElkObjectMinCardinalityUnqualified} corresponding to
	 *         the input
	 */
	public ElkObjectMinCardinalityUnqualified getObjectMinCardinalityUnqualified(
			ElkObjectPropertyExpression objectPropertyExpression,
			int cardinality);

	/**
	 * Create an {@link ElkObjectMinCardinalityQualified}.
	 * 
	 * @param objectPropertyExpression
	 *            the {@link ElkObjectPropertyExpression} for which the object
	 *            should be created
	 * @param cardinality
	 *            the cardinality for which the object should be created
	 * @param classExpression
	 *            the {@link ElkClassExpression} for which the object should be
	 *            created
	 * @return an {@link ElkObjectMinCardinalityQualified} corresponding to the
	 *         input
	 */
	public ElkObjectMinCardinalityQualified getObjectMinCardinalityQualified(
			ElkObjectPropertyExpression objectPropertyExpression,
			int cardinality, ElkClassExpression classExpression);

	/**
	 * Create an {@link ElkObjectOneOf}.
	 * 
	 * @param firstIndividual
	 *            the first {@link ElkIndividual} for which the object should be
	 *            created
	 * @param otherIndividuals
	 *            other {@link ElkIndividual}s for which the object should be
	 *            created
	 * @return an {@link ElkObjectOneOf} corresponding to the input
	 */
	public ElkObjectOneOf getObjectOneOf(ElkIndividual firstIndividual,
			ElkIndividual... otherIndividuals);

	/**
	 * Create an {@link ElkObjectOneOf}.
	 * 
	 * @param individuals
	 *            the {@link ElkIndividual}s for which the object should be
	 *            created
	 * @return an {@link ElkObjectOneOf} corresponding to the input
	 */
	public ElkObjectOneOf getObjectOneOf(
			List<? extends ElkIndividual> individuals);

	/**
	 * Create an {@link ElkObjectProperty}.
	 * 
	 * @param iri
	 *            the {@link ElkIri} for which the object should be created
	 * @return an {@link ElkObjectProperty} corresponding to the input
	 */
	public ElkObjectProperty getObjectProperty(ElkIri iri);

	/**
	 * Create an {@link ElkObjectPropertyAssertionAxiom}.
	 * 
	 * @param objectPropertyExpression
	 *            the {@link ElkObjectPropertyExpression} for which the axiom
	 *            should be created
	 * @param firstIndividual
	 *            the first {@link ElkIndividual} for which the axiom should be
	 *            created
	 * @param secondIndividual
	 *            the second {@link ElkIndividual} for which the axiom should be
	 *            created
	 * @return an {@link ElkObjectPropertyAssertionAxiom} corresponding to the
	 *         input
	 */
	public ElkObjectPropertyAssertionAxiom getObjectPropertyAssertionAxiom(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkIndividual firstIndividual, ElkIndividual secondIndividual);

	/**
	 * Create an {@link ElkObjectPropertyChain}.
	 * 
	 * @param objectPropertyExpressions
	 *            the {@link ElkObjectPropertyExpression}s for which the object
	 *            should be created
	 * @return an {@link ElkObjectPropertyChain} corresponding to the input
	 */
	public ElkObjectPropertyChain getObjectPropertyChain(
			List<? extends ElkObjectPropertyExpression> objectPropertyExpressions);

	/**
	 * Create an {@link ElkObjectPropertyDomainAxiom}.
	 * 
	 * @param objectPropertyExpression
	 *            the {@link ElkObjectPropertyExpression} for which the axiom
	 *            should be created
	 * @param classExpression
	 *            the {@link ElkClassExpression} for which the axiom should be
	 *            created
	 * @return an {@link ElkObjectPropertyDomainAxiom} corresponding to the
	 *         input
	 */
	public ElkObjectPropertyDomainAxiom getObjectPropertyDomainAxiom(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkClassExpression classExpression);

	/**
	 * Create an {@link ElkObjectPropertyRangeAxiom}.
	 * 
	 * @param objectPropertyExpression
	 *            the {@link ElkObjectPropertyExpression} for which the axiom
	 *            should be created
	 * @param classExpression
	 *            the {@link ElkClassExpression} for which the axiom should be
	 *            created
	 * @return an {@link ElkObjectPropertyRangeAxiom} corresponding to the input
	 */
	public ElkObjectPropertyRangeAxiom getObjectPropertyRangeAxiom(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkClassExpression classExpression);

	/**
	 * Create an {@link ElkObjectSomeValuesFrom}.
	 * 
	 * @param objectPropertyExpression
	 *            the {@link ElkObjectPropertyExpression} for which the object
	 *            should be created
	 * @param classExpression
	 *            the {@link ElkClassExpression} for which the object should be
	 *            created
	 * @return an {@link ElkObjectSomeValuesFrom} corresponding to the input
	 */
	public ElkObjectSomeValuesFrom getObjectSomeValuesFrom(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkClassExpression classExpression);

	/**
	 * Create an {@link ElkObjectUnionOf}.
	 * 
	 * @param firstClassExpression
	 *            the first {@link ElkClassExpression} of the union for which
	 *            the object should be created
	 * @param secondClassExpression
	 *            the second {@link ElkClassExpression} of the union for which
	 *            the object should be created
	 * @param otherClassExpressions
	 *            other {@link ElkClassExpression}s of the union for which the
	 *            object should be created
	 * @return an {@link ElkObjectUnionOf} corresponding to the input
	 */
	public ElkObjectUnionOf getObjectUnionOf(
			ElkClassExpression firstClassExpression,
			ElkClassExpression secondClassExpression,
			ElkClassExpression... otherClassExpressions);

	/**
	 * Create an {@link ElkObjectUnionOf}.
	 * 
	 * @param classExpressions
	 *            the {@link ElkClassExpression}s for which the object should be
	 *            created
	 * @return an {@link ElkObjectUnionOf} corresponding to the input
	 */
	public ElkObjectUnionOf getObjectUnionOf(
			List<? extends ElkClassExpression> classExpressions);

	/**
	 * Create the {@link ElkDataProperty} representing owl:BottomDataProperty}.
	 * 
	 * @return an {@link ElkDataProperty} corresponding to the input
	 */
	public ElkDataProperty getOwlBottomDataProperty();

	/**
	 * Create the {@link ElkObjectProperty} representing
	 * owl:BottomObjectProperty}.
	 * 
	 * @return an {@link ElkObjectProperty} corresponding to the input
	 */
	public ElkObjectProperty getOwlBottomObjectProperty();

	/**
	 * Create the {@link ElkClass} representing {@code owl:Nothing}.
	 * 
	 * @return an {@link ElkClass} corresponding to the input
	 */
	public ElkClass getOwlNothing();

	/**
	 * Create the {@link ElkClass} representing {@code owl:Thing}.
	 * 
	 * @return an {@link ElkClass} corresponding to the input
	 */
	public ElkClass getOwlThing();

	/**
	 * Create the {@link ElkDataProperty} representing
	 * {@code owl:TopDataProperty}.
	 * 
	 * @return an {@link ElkDataProperty} corresponding to the input
	 */
	public ElkDataProperty getOwlTopDataProperty();

	/**
	 * Create the {@link ElkObjectProperty} representing
	 * {@code owl:TopObjectProperty}.
	 * 
	 * @return an {@link ElkObjectProperty} corresponding to the input
	 */
	public ElkObjectProperty getOwlTopObjectProperty();

	/**
	 * Create an {@link ElkReflexiveObjectPropertyAxiom}.
	 * 
	 * @param objectPropertyExpression
	 *            the {@link ElkIri} for which the axiom should be created
	 * @return an {@link ElkReflexiveObjectPropertyAxiom} corresponding to the
	 *         input
	 */
	public ElkReflexiveObjectPropertyAxiom getReflexiveObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression);

	/**
	 * Create an {@link ElkSameIndividualAxiom}.
	 * 
	 * @param firstIndividual
	 *            the first equivalent {@link ElkIndividual} for which the axiom
	 *            should be created
	 * @param secondIndividual
	 *            the second equivalent {@link ElkIndividual} for which the
	 *            axiom should be created
	 * @param otherIndividuals
	 *            other equivalent {@link ElkIndividual} for which the axiom
	 *            should be created
	 * @return an {@link ElkSameIndividualAxiom} corresponding to the input
	 */
	public ElkSameIndividualAxiom getSameIndividualAxiom(
			ElkIndividual firstIndividual, ElkIndividual secondIndividual,
			ElkIndividual... otherIndividuals);

	/**
	 * Create an {@link ElkSameIndividualAxiom}.
	 * 
	 * @param individuals
	 *            the equivalent {@link ElkIndividual} for which the axiom
	 *            should be created
	 * @return an {@link ElkSameIndividualAxiom} corresponding to the input
	 */
	public ElkSameIndividualAxiom getSameIndividualAxiom(
			List<? extends ElkIndividual> individuals);

	/**
	 * Create an {@link ElkSubAnnotationPropertyOfAxiom}
	 * 
	 * @param subAnnotationProperty
	 *            the sub-{@link ElkAnnotationProperty} for which the object
	 *            should be created
	 * @param superAnnotationProperty
	 *            the super-{@link ElkAnnotationProperty} for which the object
	 *            should be created
	 * @return an {@link ElkSubAnnotationPropertyOfAxiom} corresponding to the
	 *         input
	 */
	public ElkSubAnnotationPropertyOfAxiom getSubAnnotationPropertyOfAxiom(
			ElkAnnotationProperty subAnnotationProperty,
			ElkAnnotationProperty superAnnotationProperty);

	/**
	 * Create an {@link ElkSubClassOfAxiom}.
	 * 
	 * @param subClassExpression
	 *            the {@link ElkClassExpression} for which the axiom should be
	 *            created
	 * @param superClassExpression
	 *            the {@link ElkClassExpression} for which the axiom should be
	 *            created
	 * @return an {@link ElkSubClassOfAxiom} corresponding to the input
	 */
	public ElkSubClassOfAxiom getSubClassOfAxiom(
			ElkClassExpression subClassExpression,
			ElkClassExpression superClassExpression);

	/**
	 * Create an {@link ElkSubDataPropertyOfAxiom}.
	 * 
	 * @param subDataPropertyExpression
	 *            the sub-{@link ElkDataPropertyExpression} for which the axiom
	 *            should be created
	 * @param superDataPropertyExpression
	 *            the super-{@link ElkDataPropertyExpression} for which the
	 *            axiom should be created
	 * @return an {@link ElkSubDataPropertyOfAxiom} corresponding to the input
	 */
	public ElkSubDataPropertyOfAxiom getSubDataPropertyOfAxiom(
			ElkDataPropertyExpression subDataPropertyExpression,
			ElkDataPropertyExpression superDataPropertyExpression);

	/**
	 * Create an {@link ElkSubObjectPropertyOfAxiom}.
	 * 
	 * @param subObjectPropertyExpression
	 *            the {@link ElkSubObjectPropertyExpression} for which the axiom
	 *            should be created
	 * @param superObjectPropertyExpression
	 *            the super-{@link ElkObjectPropertyExpression} for which the
	 *            axiom should be created
	 * @return an {@link ElkSubObjectPropertyOfAxiom} corresponding to the input
	 */
	public ElkSubObjectPropertyOfAxiom getSubObjectPropertyOfAxiom(
			ElkSubObjectPropertyExpression subObjectPropertyExpression,
			ElkObjectPropertyExpression superObjectPropertyExpression);

	/**
	 * Create an {@link ElkSymmetricObjectPropertyAxiom}.
	 * 
	 * @param objectPropertyExpression
	 *            the {@link ElkIri} for which the axiom should be created
	 * @return an {@link ElkAnnotation} corresponding to the input
	 */
	public ElkSymmetricObjectPropertyAxiom getSymmetricObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression);

	/**
	 * Create an {@link ElkTransitiveObjectPropertyAxiom}.
	 * 
	 * @param objectPropertyExpression
	 *            the {@link ElkObjectPropertyExpression} for which the axiom
	 *            should be created
	 * @return an {@link ElkTransitiveObjectPropertyAxiom} corresponding to the
	 *         input
	 */
	public ElkTransitiveObjectPropertyAxiom getTransitiveObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression);

	/**
	 * Create an {@link ElkHasKeyAxiom}
	 * 
	 * @param classExpr
	 *            the {@link ElkClassExpression} for which the axiom should be
	 *            created
	 * @param objectPEs
	 *            the {@link ElkObjectPropertyExpression}s for which the axiom
	 *            should be created
	 * @param dataPEs
	 *            the {@link ElkDataPropertyExpression}s for which the axiom
	 *            should be created
	 * @return an {@link ElkHasKeyAxiom} corresponding to the input
	 */
	public ElkHasKeyAxiom getHasKeyAxiom(ElkClassExpression classExpr,
			List<? extends ElkObjectPropertyExpression> objectPEs,
			List<? extends ElkDataPropertyExpression> dataPEs);

	/**
	 * Create an {@link ElkDatatypeDefinitionAxiom}
	 * 
	 * @param datatype
	 *            the {@link ElkDatatype} for which the axiom should be created
	 * @param dataRange
	 *            the {@link ElkDataRange} for which the axiom should be created
	 * @return an {@link ElkDatatypeDefinitionAxiom} corresponding to the input
	 */
	public ElkDatatypeDefinitionAxiom getDatatypeDefinitionAxiom(
			ElkDatatype datatype, ElkDataRange dataRange);

	/**
	 * Create an {@link ElkFacetRestriction}
	 * 
	 * @param iri
	 *            the {@link ElkIri} for which the object should be created
	 * @param literal
	 *            the {@link ElkLiteral} for which the object should be created
	 * @return an {@link ElkFacetRestriction} corresponding to the input
	 */
	public ElkFacetRestriction getFacetRestriction(ElkIri iri,
			ElkLiteral literal);

	/**
	 * No arguments since we don't have a full representation of SWRL rules
	 * 
	 * @return a dummy object
	 */
	public ElkSWRLRule getSWRLRule();
}