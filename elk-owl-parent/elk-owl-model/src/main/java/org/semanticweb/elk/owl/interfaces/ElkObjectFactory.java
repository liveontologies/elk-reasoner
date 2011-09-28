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

/**
 * Interface that provides methods for creating instances of ElkObjects.
 * 
 * @author Markus Kroetzsch
 */
public interface ElkObjectFactory {

	/**
	 * Create an ElkAnonymousIndividual.
	 * 
	 * @param nodeId
	 * @return
	 */
	public abstract ElkAnonymousIndividual getAnonymousIndividual(String nodeId);

	/**
	 * Create an ElkAsymmetricObjectPropertyAxiom.
	 * 
	 * @param objectPropertyExpression
	 * @return
	 */
	public abstract ElkAsymmetricObjectPropertyAxiom getAsymmetricObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression);

	/**
	 * Create an ElkClass.
	 * 
	 * @param iri
	 * @return
	 */
	public abstract ElkClass getClass(String iri);

	/**
	 * Create an ElkClassAssertionAxiom.
	 * 
	 * @param classExpression
	 * @param individual
	 * @return
	 */
	public abstract ElkClassAssertionAxiom getClassAssertionAxiom(
			ElkClassExpression classExpression, ElkIndividual individual);

	/**
	 * Create an ElkDeclarationAxiom.
	 * 
	 * @param entity
	 * @return
	 */
	public abstract ElkDeclarationAxiom getDeclarationAxiom(ElkEntity entity);

	/**
	 * Create an ElkDifferentIndividualsAxiom.
	 * 
	 * @param individuals
	 * @return
	 */
	public abstract ElkDifferentIndividualsAxiom getDifferentIndividualsAxiom(
			List<? extends ElkIndividual> individuals);

	/**
	 * Create an ElkDifferentIndividualsAxiom.
	 * 
	 * @param firstIndividual
	 * @param secondIndividual
	 * @param otherIndividuals
	 * @return
	 */
	public abstract ElkDifferentIndividualsAxiom getDifferentIndividualsAxiom(
			ElkIndividual firstIndividual, ElkIndividual secondIndividual,
			ElkIndividual... otherIndividuals);

	/**
	 * Create an ElkDisjointClassesAxiom.
	 * 
	 * @param disjointClassExpressions
	 * @return
	 */
	public abstract ElkDisjointClassesAxiom getDisjointClassesAxiom(
			List<? extends ElkClassExpression> disjointClassExpressions);

	/**
	 * Create an ElkDisjointClassesAxiom.
	 * 
	 * @param firstClassExpression
	 * @param secondClassExpression
	 * @param otherClassExpressions
	 * @return
	 */
	public abstract ElkDisjointClassesAxiom getDisjointClassesAxiom(
			ElkClassExpression firstClassExpression,
			ElkClassExpression secondClassExpression,
			ElkClassExpression... otherClassExpressions);

	/**
	 * Create an ElkDisjointObjectPropertiesAxiom.
	 * 
	 * @param disjointObjectPropertyExpressions
	 * @return
	 */
	public abstract ElkDisjointObjectPropertiesAxiom getDisjointObjectPropertiesAxiom(
			List<? extends ElkObjectPropertyExpression> disjointObjectPropertyExpressions);

	/**
	 * Create an ElkDisjointObjectPropertiesAxiom.
	 * 
	 * @param firstObjectPropertyExpression
	 * @param secondObjectPropertyExpression
	 * @param otherObjectPropertyExpressions
	 * @return
	 */
	public abstract ElkDisjointObjectPropertiesAxiom getDisjointObjectPropertiesAxiom(
			ElkObjectPropertyExpression firstObjectPropertyExpression,
			ElkObjectPropertyExpression secondObjectPropertyExpression,
			ElkObjectPropertyExpression... otherObjectPropertyExpressions);

	/**
	 * Create an ElkDisjointUnionAxiom.
	 * 
	 * @param disjointClassExpressions
	 * @return
	 */
	public abstract ElkDisjointUnionAxiom getDisjointUnionAxiom(
			ElkClass definedClass,
			List<? extends ElkClassExpression> disjointClassExpressions);

	/**
	 * Create an ElkDisjointUnionAxiom.
	 * 
	 * @param firstClassExpression
	 * @param secondClassExpression
	 * @param otherClassExpressions
	 * @return
	 */
	public abstract ElkDisjointUnionAxiom getDisjointUnionAxiom(
			ElkClass definedClass, ElkClassExpression firstClassExpression,
			ElkClassExpression secondClassExpression,
			ElkClassExpression... otherClassExpressions);

	/**
	 * Create an ElkEquivalentClassesAxiom.
	 * 
	 * @param equivalentClassExpressions
	 * @return
	 */
	public abstract ElkEquivalentClassesAxiom getEquivalentClassesAxiom(
			List<? extends ElkClassExpression> equivalentClassExpressions);

	/**
	 * Create an ElkEquivalentClassesAxiom.
	 * 
	 * @param firstClassExpression
	 * @param secondClassExpression
	 * @param otherClassExpressions
	 * @return
	 */
	public abstract ElkEquivalentClassesAxiom getEquivalentClassesAxiom(
			ElkClassExpression firstClassExpression,
			ElkClassExpression secondClassExpression,
			ElkClassExpression... otherClassExpressions);

	/**
	 * Create an ElkEquivalentObjectPropertiesAxiom.
	 * 
	 * @param equivalentObjectPropertyExpressions
	 * @return
	 */
	public abstract ElkEquivalentObjectPropertiesAxiom getEquivalentObjectPropertiesAxiom(
			List<? extends ElkObjectPropertyExpression> equivalentObjectPropertyExpressions);

	/**
	 * Create an ElkEquivalentObjectPropertiesAxiom.
	 * 
	 * @param firstObjectPropertyExpression
	 * @param secondObjectPropertyExpression
	 * @param otherObjectPropertyExpressions
	 * @return
	 */
	public abstract ElkEquivalentObjectPropertiesAxiom getEquivalentObjectPropertiesAxiom(
			ElkObjectPropertyExpression firstObjectPropertyExpression,
			ElkObjectPropertyExpression secondObjectPropertyExpression,
			ElkObjectPropertyExpression... otherObjectPropertyExpressions);

	/**
	 * Create an ElkFunctionalObjectPropertyAxiom.
	 * 
	 * @param objectPropertyExpression
	 * @return
	 */
	public abstract ElkFunctionalObjectPropertyAxiom getFunctionalObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression);

	/**
	 * Create an ElkInverseFunctionalObjectPropertyAxiom.
	 * 
	 * @param objectPropertyExpression
	 * @return
	 */
	public abstract ElkInverseFunctionalObjectPropertyAxiom getInverseFunctionalObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression);

	/**
	 * Create an ElkInverseObjectPropertiesAxiom.
	 * 
	 * @param firstObjectPropertyExpression
	 * @param secondObjectPropertyExpression
	 * @return
	 */
	public abstract ElkInverseObjectPropertiesAxiom getInverseObjectPropertiesAxiom(
			ElkObjectPropertyExpression firstObjectPropertyExpression,
			ElkObjectPropertyExpression secondObjectPropertyExpression);

	/**
	 * Create an ElkIrreflexiveObjectPropertyAxiom.
	 * 
	 * @param objectPropertyExpression
	 * @return
	 */
	public abstract ElkIrreflexiveObjectPropertyAxiom getIrreflexiveObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression);

	/**
	 * Create an ElkNamedIndividual.
	 * 
	 * @param iri
	 * @return
	 */
	public abstract ElkNamedIndividual getNamedIndividual(String iri);

	/**
	 * Create an ElkNegativeObjectPropertyAssertionAxiom.
	 * 
	 * @param objectPropertyExpression
	 * @param firstIndividual
	 * @param secondIndividual
	 * @return
	 */
	public abstract ElkNegativeObjectPropertyAssertionAxiom getNegativeObjectPropertyAssertionAxiom(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkIndividual firstIndividual, ElkIndividual secondIndividual);

	/**
	 * Create an ElkObjectAllValuesFrom.
	 * 
	 * @param objectPropertyExpression
	 * @param classExpression
	 * @return
	 */
	public abstract ElkObjectAllValuesFrom getObjectAllValuesFrom(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkClassExpression classExpression);

	/**
	 * Create an ElkObjectComplementOf.
	 * 
	 * @param classExpression
	 * @return
	 */
	public abstract ElkObjectComplementOf getObjectComplementOf(
			ElkClassExpression classExpression);

	/**
	 * Create an ElkObjectExactCardinality.
	 * 
	 * @param objectPropertyExpression
	 * @param cardinality
	 * @param classExpression
	 *            can be null for unqualified cardinality restrictions
	 * @return
	 */
	public abstract ElkObjectExactCardinality getObjectExactCardinality(
			ElkObjectPropertyExpression objectPropertyExpression,
			int cardinality, ElkClassExpression classExpression);

	/**
	 * Create an ElkObjectHasSelf.
	 * 
	 * @param objectPropertyExpression
	 * @return
	 */
	public abstract ElkObjectHasSelf getObjectHasSelf(
			ElkObjectPropertyExpression objectPropertyExpression);

	/**
	 * Create an ElkObjectHasValue.
	 * 
	 * @param objectPropertyExpression
	 * @param individual
	 * @return
	 */
	public abstract ElkObjectHasValue getObjectHasValue(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkIndividual individual);

	/**
	 * Create an ElkObjectIntersectionOf.
	 * 
	 * @param classExpressions
	 * @return
	 */
	public abstract ElkObjectIntersectionOf getObjectIntersectionOf(
			List<? extends ElkClassExpression> classExpressions);

	/**
	 * Create an ElkObjectIntersectionOf.
	 * 
	 * @param firstClassExpression
	 * @param secondClassExpression
	 * @param otherClassExpressions
	 * @return
	 */
	public abstract ElkObjectIntersectionOf getObjectIntersectionOf(
			ElkClassExpression firstClassExpression,
			ElkClassExpression secondClassExpression,
			ElkClassExpression... otherClassExpressions);

	/**
	 * Create an ElkObjectInverseOf.
	 * 
	 * @param objectProperty
	 * @return
	 */
	public abstract ElkObjectInverseOf getObjectInverseOf(
			ElkObjectProperty objectProperty);

	/**
	 * Create an ElkObjectOneOf.
	 * 
	 * @param individuals
	 * @return
	 */
	public abstract ElkObjectOneOf getObjectOneOf(
			List<? extends ElkIndividual> individuals);

	/**
	 * Create an ElkObjectOneOf.
	 * 
	 * @param firstIndividual
	 * @param otherIndividuals
	 * @return
	 */
	public abstract ElkObjectOneOf getObjectOneOf(
			ElkIndividual firstIndividual, ElkIndividual... otherIndividuals);

	/**
	 * Create an ElkObjectMaxCardinality.
	 * 
	 * @param objectPropertyExpression
	 * @param cardinality
	 * @param classExpression
	 *            can be null for unqualified cardinality restrictions
	 * @return
	 */
	public abstract ElkObjectMaxCardinality getObjectMaxCardinality(
			ElkObjectPropertyExpression objectPropertyExpression,
			int cardinality, ElkClassExpression classExpression);

	/**
	 * Create an ElkObjectMinCardinality.
	 * 
	 * @param objectPropertyExpression
	 * @param cardinality
	 * @param classExpression
	 *            can be null for unqualified cardinality restrictions
	 * @return
	 */
	public abstract ElkObjectMinCardinality getObjectMinCardinality(
			ElkObjectPropertyExpression objectPropertyExpression,
			int cardinality, ElkClassExpression classExpression);

	/**
	 * Create an ElkObjectProperty.
	 * 
	 * @param objectPropertyIri
	 * @return
	 */
	public abstract ElkObjectProperty getObjectProperty(String objectPropertyIri);

	/**
	 * Create an ElkObjectPropertyAssertionAxiom.
	 * 
	 * @param objectPropertyExpression
	 * @param firstIndividual
	 * @param secondIndividual
	 * @return
	 */
	public abstract ElkObjectPropertyAssertionAxiom getObjectPropertyAssertionAxiom(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkIndividual firstIndividual, ElkIndividual secondIndividual);

	/**
	 * Create an ElkObjectPropertyChain.
	 * 
	 * @param objectPropertyExpressions
	 * @return
	 */
	public abstract ElkObjectPropertyChain getObjectPropertyChain(
			List<? extends ElkObjectPropertyExpression> objectPropertyExpressions);

	/**
	 * Create an ElkObjectPropertyDomainAxiom.
	 * 
	 * @param objectPropertyExpression
	 * @param classExpression
	 * @return
	 */
	public abstract ElkObjectPropertyDomainAxiom getObjectPropertyDomainAxiom(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkClassExpression classExpression);

	/**
	 * Create an ElkObjectPropertyRangeAxiom.
	 * 
	 * @param objectPropertyExpression
	 * @param classExpression
	 * @return
	 */
	public abstract ElkObjectPropertyRangeAxiom getObjectPropertyRangeAxiom(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkClassExpression classExpression);

	/**
	 * Create an ElkObjectSomeValuesFrom.
	 * 
	 * @param objectPropertyExpression
	 * @param classExpression
	 * @return
	 */
	public abstract ElkObjectSomeValuesFrom getObjectSomeValuesFrom(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkClassExpression classExpression);

	/**
	 * Create an ElkObjectUnionOf.
	 * 
	 * @param classExpressions
	 * @return
	 */
	public abstract ElkObjectUnionOf getObjectUnionOf(
			List<? extends ElkClassExpression> classExpressions);

	/**
	 * Create an ElkObjectUnionOf.
	 * 
	 * @param firstClassExpression
	 * @param secondClassExpression
	 * @param otherClassExpressions
	 * @return
	 */
	public abstract ElkObjectUnionOf getObjectUnionOf(
			ElkClassExpression firstClassExpression,
			ElkClassExpression secondClassExpression,
			ElkClassExpression... otherClassExpressions);

	/**
	 * Create the ElkObjectProperty representing owl:BottomObjectProperty.
	 * 
	 * @return
	 */
	public abstract ElkObjectProperty getOwlBottomObjectProperty();

	/**
	 * Create the ElkClass representing owl:Nothing.
	 * 
	 * @return
	 */
	public abstract ElkClass getOwlNothing();

	/**
	 * Create the ElkClass representing owl:Thing.
	 * 
	 * @return
	 */
	public abstract ElkClass getOwlThing();

	/**
	 * Create the ElkObjectProperty representing owl:TopObjectProperty.
	 * 
	 * @return
	 */
	public abstract ElkObjectProperty getOwlTopObjectProperty();

	/**
	 * Create an ElkReflexiveObjectPropertyAxiom.
	 * 
	 * @param objectPropertyExpression
	 * @return
	 */
	public abstract ElkReflexiveObjectPropertyAxiom getReflexiveObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression);

	/**
	 * Create an ElkSameIndividualAxiom.
	 * 
	 * @param individuals
	 * @return
	 */
	public abstract ElkSameIndividualAxiom getSameIndividualAxiom(
			List<? extends ElkIndividual> individuals);

	/**
	 * Create an ElkSameIndividualAxiom.
	 * 
	 * @param firstIndividual
	 * @param secondIndividual
	 * @param otherIndividuals
	 * @return
	 */
	public abstract ElkSameIndividualAxiom getSameIndividualAxiom(
			ElkIndividual firstIndividual, ElkIndividual secondIndividual,
			ElkIndividual... otherIndividuals);

	/**
	 * Create an ElkSubClassOfAxiom.
	 * 
	 * @param subClassExpression
	 * @param superClassExpression
	 * @return
	 */
	public abstract ElkSubClassOfAxiom getSubClassOfAxiom(
			ElkClassExpression subClassExpression,
			ElkClassExpression superClassExpression);

	/**
	 * Create an ElkSubObjectPropertyOfAxiom.
	 * 
	 * @param subObjectPropertyExpression
	 * @param superObjectPropertyExpression
	 * @return
	 */
	public abstract ElkSubObjectPropertyOfAxiom getSubObjectPropertyOfAxiom(
			ElkSubObjectPropertyExpression subObjectPropertyExpression,
			ElkObjectPropertyExpression superObjectPropertyExpression);

	/**
	 * Create an ElkSymmetricObjectPropertyAxiom.
	 * 
	 * @param objectPropertyExpression
	 * @return
	 */
	public abstract ElkSymmetricObjectPropertyAxiom getSymmetricObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression);

	/**
	 * Create an ElkTransitiveObjectPropertyAxiom.
	 * 
	 * @param objectPropertyExpression
	 * @return
	 */
	public abstract ElkTransitiveObjectPropertyAxiom getTransitiveObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression);

	/**
	 * Create an ElkDatatype.
	 * 
	 * @param iri
	 * @return
	 */
	public abstract ElkDatatype getDatatype(String iri);

	/**
	 * Create an ElkDisjointDataPropertiesAxiom.
	 * 
	 * @param disjointDataPropertyExpressions
	 * @return
	 */
	public abstract ElkDisjointDataPropertiesAxiom getDisjointDataPropertiesAxiom(
			List<? extends ElkDataPropertyExpression> disjointDataPropertyExpressions);

	/**
	 * Create an ElkDisjointDataPropertiesAxiom.
	 * 
	 * @param firstDataPropertyExpression
	 * @param secondDataPropertyExpression
	 * @param otherDataPropertyExpressions
	 * @return
	 */
	public abstract ElkDisjointDataPropertiesAxiom getDisjointDataPropertiesAxiom(
			ElkDataPropertyExpression firstDataPropertyExpression,
			ElkDataPropertyExpression secondDataPropertyExpression,
			ElkDataPropertyExpression... otherDataPropertyExpressions);

	/**
	 * Create an ElkEquivalentDataPropertiesAxiom.
	 * 
	 * @param equivalentDataPropertyExpressions
	 * @return
	 */
	public abstract ElkEquivalentDataPropertiesAxiom getEquivalentDataPropertiesAxiom(
			List<? extends ElkDataPropertyExpression> equivalentDataPropertyExpressions);

	/**
	 * Create an ElkEquivalentDataPropertiesAxiom.
	 * 
	 * @param firstDataPropertyExpression
	 * @param secondDataPropertyExpression
	 * @param otherDataPropertyExpressions
	 * @return
	 */
	public abstract ElkEquivalentDataPropertiesAxiom getEquivalentDataPropertiesAxiom(
			ElkDataPropertyExpression firstDataPropertyExpression,
			ElkDataPropertyExpression secondDataPropertyExpression,
			ElkDataPropertyExpression... otherDataPropertyExpressions);

	/**
	 * Create an ElkFunctionalDataPropertyAxiom.
	 * 
	 * @param dataPropertyExpression
	 * @return
	 */
	public abstract ElkFunctionalDataPropertyAxiom getFunctionalDataPropertyAxiom(
			ElkDataPropertyExpression dataPropertyExpression);

	/**
	 * Create an ElkNegativeDataPropertyAssertionAxiom.
	 * 
	 * @param dataPropertyExpression
	 * @param firstIndividual
	 * @param secondIndividual
	 * @return
	 */
	public abstract ElkNegativeDataPropertyAssertionAxiom getNegativeDataPropertyAssertionAxiom(
			ElkDataPropertyExpression dataPropertyExpression,
			ElkIndividual individual, ElkLiteral literal);

	/**
	 * Create an ElkDataAllValuesFrom.
	 * 
	 * @param dataPropertyExpression
	 * @param dataRange
	 * @return
	 */
	public abstract ElkDataAllValuesFrom getDataAllValuesFrom(
			ElkDataPropertyExpression dataPropertyExpression,
			ElkDataRange dataRange);

	/**
	 * Create an ElkDataComplementOf.
	 * 
	 * @param dataRange
	 * @return
	 */
	public abstract ElkDataComplementOf getDataComplementOf(
			ElkDataRange dataRange);

	/**
	 * Create an ElkDataExactCardinality.
	 * 
	 * @param dataPropertyExpression
	 * @param cardinality
	 * @param dataRange
	 *            can be null for unqualified cardinality restrictions
	 * @return
	 */
	public abstract ElkDataExactCardinality getDataExactCardinality(
			ElkDataPropertyExpression dataPropertyExpression, int cardinality,
			ElkDataRange dataRange);

	/**
	 * Create an ElkDataHasValue.
	 * 
	 * @param dataPropertyExpression
	 * @param literal
	 * @return
	 */
	public abstract ElkDataHasValue getDataHasValue(
			ElkDataPropertyExpression dataPropertyExpression, ElkLiteral literal);

	/**
	 * Create an ElkDataIntersectionOf.
	 * 
	 * @param dataRanges
	 * @return
	 */
	public abstract ElkDataIntersectionOf getDataIntersectionOf(
			List<? extends ElkDataRange> dataRanges);

	/**
	 * Create an ElkDataIntersectionOf.
	 * 
	 * @param firstDataRange
	 * @param secondDataRange
	 * @param otherDataRanges
	 * @return
	 */
	public abstract ElkDataIntersectionOf getDataIntersectionOf(
			ElkDataRange firstDataRange, ElkDataRange secondDataRange,
			ElkDataRange... otherDataRanges);

	/**
	 * Create an ElkDataOneOf.
	 * 
	 * @param literals
	 * @return
	 */
	public abstract ElkDataOneOf getDataOneOf(
			List<? extends ElkLiteral> literals);

	/**
	 * Create an ElkDataOneOf.
	 * 
	 * @param firstIndividual
	 * @param otherIndividuals
	 * @return
	 */
	public abstract ElkDataOneOf getDataOneOf(ElkLiteral firstLiteral,
			ElkLiteral... otherLiterals);

	/**
	 * Create an ElkDataMaxCardinality.
	 * 
	 * @param dataPropertyExpression
	 * @param cardinality
	 * @param dataRange
	 *            can be null for unqualified cardinality restrictions
	 * @return
	 */
	public abstract ElkDataMaxCardinality getDataMaxCardinality(
			ElkDataPropertyExpression dataPropertyExpression, int cardinality,
			ElkDataRange dataRange);

	/**
	 * Create an ElkDataMinCardinality.
	 * 
	 * @param dataPropertyExpression
	 * @param cardinality
	 * @param dataRange
	 *            can be null for unqualified cardinality restrictions
	 * @return
	 */
	public abstract ElkDataMinCardinality getDataMinCardinality(
			ElkDataPropertyExpression dataPropertyExpression, int cardinality,
			ElkDataRange dataRange);

	/**
	 * Create an ElkDataProperty.
	 * 
	 * @param dataPropertyIri
	 * @return
	 */
	public abstract ElkDataProperty getDataProperty(String dataPropertyIri);

	/**
	 * Create an ElkDataPropertyAssertionAxiom.
	 * 
	 * @param dataPropertyExpression
	 * @param firstIndividual
	 * @param secondIndividual
	 * @return
	 */
	public abstract ElkDataPropertyAssertionAxiom getDataPropertyAssertionAxiom(
			ElkDataPropertyExpression dataPropertyExpression,
			ElkIndividual individual, ElkLiteral literal);

	/**
	 * Create an ElkDataPropertyDomainAxiom.
	 * 
	 * @param dataPropertyExpression
	 * @param classExpression
	 * @return
	 */
	public abstract ElkDataPropertyDomainAxiom getDataPropertyDomainAxiom(
			ElkDataPropertyExpression dataPropertyExpression,
			ElkClassExpression classExpression);

	/**
	 * Create an ElkDataPropertyRangeAxiom.
	 * 
	 * @param dataPropertyExpression
	 * @param dataRange
	 * @return
	 */
	public abstract ElkDataPropertyRangeAxiom getDataPropertyRangeAxiom(
			ElkDataPropertyExpression dataPropertyExpression,
			ElkDataRange dataRange);

	/**
	 * Create an ElkDataSomeValuesFrom.
	 * 
	 * @param dataPropertyExpression
	 * @param dataRange
	 * @return
	 */
	public abstract ElkDataSomeValuesFrom getDataSomeValuesFrom(
			ElkDataPropertyExpression dataPropertyExpression,
			ElkDataRange dataRange);

	/**
	 * Create an ElkDataUnionOf.
	 * 
	 * @param dataRanges
	 * @return
	 */
	public abstract ElkDataUnionOf getDataUnionOf(
			List<? extends ElkDataRange> dataRanges);

	/**
	 * Create an ElkDataUnionOf.
	 * 
	 * @param firstDataRange
	 * @param secondDataRange
	 * @param otherDataRanges
	 * @return
	 */
	public abstract ElkDataUnionOf getDataUnionOf(ElkDataRange firstDataRange,
			ElkDataRange secondDataRange, ElkDataRange... otherDataRanges);

	/**
	 * Create the ElkDataProperty representing owl:BottomDataProperty.
	 * 
	 * @return
	 */
	public abstract ElkDataProperty getOwlBottomDataProperty();

	/**
	 * Create the ElkDataProperty representing owl:TopDataProperty.
	 * 
	 * @return
	 */
	public abstract ElkDataProperty getOwlTopDataProperty();

	/**
	 * Create an ElkSubDataPropertyOfAxiom.
	 * 
	 * @param subDataPropertyExpression
	 * @param superDataPropertyExpression
	 * @return
	 */
	public abstract ElkSubDataPropertyOfAxiom getSubDataPropertyOfAxiom(
			ElkDataPropertyExpression subDataPropertyExpression,
			ElkDataPropertyExpression superDataPropertyExpression);

}