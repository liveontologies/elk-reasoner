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
package org.semanticweb.elk.syntax.interfaces;

import java.util.List;

/**
 * Interface that provides methods for creating instances of ElkObjects.
 * 
 * @author Markus Kroetzsch
 */
public interface ElkObjectFactory {

	/**
	 * Create an ElkClass.
	 * 
	 * @param iri
	 * @return
	 */
	public abstract ElkClass getClass(String iri);

	/**
	 * Create an ElkAnonymousIndividual.
	 * 
	 * @param nodeId
	 * @return
	 */
	public abstract ElkAnonymousIndividual getAnonymousIndividual(String nodeId);

	/**
	 * Create an ElkDeclarationAxiom.
	 * 
	 * @param entity
	 * @return
	 */
	public abstract ElkDeclarationAxiom getDeclarationAxiom(ElkEntity entity);

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
			ElkClassExpression firstClassExpression,
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
	 * Create an ElkNamedIndividual.
	 * 
	 * @param iri
	 * @return
	 */
	public abstract ElkNamedIndividual getNamedIndividual(String iri);

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
	 * Create an ElkObjectPropertyChain.
	 * 
	 * @param objectPropertyExpressions
	 * @return
	 */
	public abstract ElkObjectPropertyChain getObjectPropertyChain(
			List<? extends ElkObjectPropertyExpression> objectPropertyExpressions);

	/**
	 * Create an ElkObjectProperty.
	 * 
	 * @param objectPropertyIri
	 * @return
	 */
	public abstract ElkObjectProperty getObjectProperty(String objectPropertyIri);

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
	 * Create an ElkTransitiveObjectPropertyAxiom.
	 * 
	 * @param objectPropertyExpression
	 * @return
	 */
	public abstract ElkTransitiveObjectPropertyAxiom getTransitiveObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression);

}