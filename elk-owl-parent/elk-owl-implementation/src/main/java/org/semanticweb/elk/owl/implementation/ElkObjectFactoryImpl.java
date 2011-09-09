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

import org.semanticweb.elk.owl.ElkObjectManager;
import org.semanticweb.elk.owl.WeakCanonicalSet;
import org.semanticweb.elk.owl.interfaces.ElkAnonymousIndividual;
import org.semanticweb.elk.owl.interfaces.ElkAsymmetricObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDeclarationAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDifferentIndividualsAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointUnionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkFunctionalObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkInverseObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkIrreflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectAllValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectComplementOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectExactCardinality;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasSelf;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasValue;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectInverseOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectMaxCardinality;
import org.semanticweb.elk.owl.interfaces.ElkObjectMinCardinality;
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
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSymmetricObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkTransitiveObjectPropertyAxiom;

/**
 * A factory for creating ElkObjects based on the implementations in the
 * org.semanticweb.elk.syntax.implementation package.
 * 
 * @author Markus Kroetzsch
 */
public class ElkObjectFactoryImpl implements ElkObjectFactory {

	protected static final ElkClass ELK_OWL_THING = new ElkClassImpl(
			"owl:Thing");

	protected static final ElkClass ELK_OWL_NOTHING = new ElkClassImpl(
			"owl:Nothing");

	protected static final ElkObjectProperty ELK_OWL_TOP_OBJECT_PROPERTY = new ElkObjectPropertyImpl(
			"owl:TobObjectProperty");

	protected static final ElkObjectProperty ELK_OWL_BOTTOM_OBJECT_PROPERTY = new ElkObjectPropertyImpl(
			"owl:BottomObjectProperty");

	protected final ElkObjectManager objectManager;

	public ElkObjectFactoryImpl() {
		this.objectManager = new WeakCanonicalSet();
	}

	public ElkClass getClass(String iri) {
		return (ElkClass) objectManager.getCanonicalElkObject(new ElkClassImpl(
				iri));
	}

	public ElkAnonymousIndividual getAnonymousIndividual(String nodeId) {
		return (ElkAnonymousIndividual) objectManager
				.getCanonicalElkObject(new ElkAnonymousIndividualImpl(nodeId));
	}

	public ElkDeclarationAxiom getDeclarationAxiom(ElkEntity entity) {
		return (ElkDeclarationAxiom) objectManager
				.getCanonicalElkObject(new ElkDeclarationAxiomImpl(entity));
	}

	public ElkDisjointClassesAxiom getDisjointClassesAxiom(
			List<? extends ElkClassExpression> disjointClassExpressions) {
		return (ElkDisjointClassesAxiom) objectManager
				.getCanonicalElkObject(new ElkDisjointClassesAxiomImpl(
						disjointClassExpressions));
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

	public ElkDisjointObjectPropertiesAxiom getDisjointObjectPropertiesAxiom(
			List<? extends ElkObjectPropertyExpression> disjointObjectPropertyExpressions) {
		return (ElkDisjointObjectPropertiesAxiom) objectManager
				.getCanonicalElkObject(new ElkDisjointObjectPropertiesAxiomImpl(
						disjointObjectPropertyExpressions));
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

	public ElkDisjointUnionAxiom getDisjointUnionAxiom(
			List<? extends ElkClassExpression> disjointClassExpressions) {
		return (ElkDisjointUnionAxiom) objectManager
				.getCanonicalElkObject(new ElkDisjointUnionAxiomImpl(
						disjointClassExpressions));
	}

	public ElkDisjointUnionAxiom getDisjointUnionAxiom(
			ElkClassExpression firstClassExpression,
			ElkClassExpression secondClassExpression,
			ElkClassExpression... otherClassExpressions) {
		return (ElkDisjointUnionAxiom) objectManager
				.getCanonicalElkObject(new ElkDisjointUnionAxiomImpl(
						ElkObjectListObject.varArgsToList(firstClassExpression,
								secondClassExpression, otherClassExpressions)));
	}

	public ElkEquivalentClassesAxiom getEquivalentClassesAxiom(
			List<? extends ElkClassExpression> equivalentClassExpressions) {
		return (ElkEquivalentClassesAxiom) objectManager
				.getCanonicalElkObject(new ElkEquivalentClassesAxiomImpl(
						equivalentClassExpressions));
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

	public ElkEquivalentObjectPropertiesAxiom getEquivalentObjectPropertiesAxiom(
			List<? extends ElkObjectPropertyExpression> equivalentObjectPropertyExpressions) {
		return (ElkEquivalentObjectPropertiesAxiom) objectManager
				.getCanonicalElkObject(new ElkEquivalentObjectPropertiesAxiomImpl(
						equivalentObjectPropertyExpressions));
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

	public ElkNamedIndividual getNamedIndividual(String iri) {
		return (ElkNamedIndividual) objectManager
				.getCanonicalElkObject(new ElkNamedIndividualImpl(iri));
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
			List<? extends ElkClassExpression> classExpressions) {
		return (ElkObjectIntersectionOf) objectManager
				.getCanonicalElkObject(new ElkObjectIntersectionOfImpl(
						classExpressions));
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

	public ElkObjectInverseOf getObjectInverseOf(
			ElkObjectProperty objectProperty) {
		return (ElkObjectInverseOf) objectManager
				.getCanonicalElkObject(new ElkObjectInverseOfImpl(
						objectProperty));
	}

	public ElkObjectOneOf getObjectOneOf(
			List<? extends ElkIndividual> individuals) {
		return (ElkObjectOneOf) objectManager
				.getCanonicalElkObject(new ElkObjectOneOfImpl(individuals));
	}

	public ElkObjectOneOf getObjectOneOf(ElkIndividual firstIndividual,
			ElkIndividual... otherIndividuals) {
		return (ElkObjectOneOf) objectManager
				.getCanonicalElkObject(new ElkObjectOneOfImpl(
						ElkObjectListObject.varArgsToList(firstIndividual,
								otherIndividuals)));
	}

	public ElkObjectPropertyChain getObjectPropertyChain(
			List<? extends ElkObjectPropertyExpression> objectPropertyExpressions) {
		return (ElkObjectPropertyChain) objectManager
				.getCanonicalElkObject(new ElkObjectPropertyChainImpl(
						objectPropertyExpressions));
	}

	public ElkObjectProperty getObjectProperty(String objectPropertyIri) {
		return (ElkObjectProperty) objectManager
				.getCanonicalElkObject(new ElkObjectPropertyImpl(
						objectPropertyIri));
	}

	public ElkObjectSomeValuesFrom getObjectSomeValuesFrom(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkClassExpression classExpression) {
		return (ElkObjectSomeValuesFrom) objectManager
				.getCanonicalElkObject(new ElkObjectSomeValuesFromImpl(
						objectPropertyExpression, classExpression));
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

	public ElkObjectProperty getOwlTopObjectProperty() {
		return ElkObjectFactoryImpl.ELK_OWL_TOP_OBJECT_PROPERTY;
	}

	public ElkReflexiveObjectPropertyAxiom getReflexiveObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression) {
		return (ElkReflexiveObjectPropertyAxiom) objectManager
				.getCanonicalElkObject(new ElkReflexiveObjectPropertyAxiomImpl(
						objectPropertyExpression));
	}

	public ElkSubClassOfAxiom getSubClassOfAxiom(
			ElkClassExpression subClassExpression,
			ElkClassExpression superClassExpression) {
		return (ElkSubClassOfAxiom) objectManager
				.getCanonicalElkObject(new ElkSubClassOfAxiomImpl(
						subClassExpression, superClassExpression));
	}

	public ElkSubObjectPropertyOfAxiom getSubObjectPropertyOfAxiom(
			ElkSubObjectPropertyExpression subObjectPropertyExpression,
			ElkObjectPropertyExpression superObjectPropertyExpression) {
		return (ElkSubObjectPropertyOfAxiom) objectManager
				.getCanonicalElkObject(new ElkSubObjectPropertyOfAxiomImpl(
						subObjectPropertyExpression,
						superObjectPropertyExpression));
	}

	public ElkTransitiveObjectPropertyAxiom getTransitiveObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression) {
		return (ElkTransitiveObjectPropertyAxiom) objectManager
				.getCanonicalElkObject(new ElkTransitiveObjectPropertyAxiomImpl(
						objectPropertyExpression));
	}

	public ElkAsymmetricObjectPropertyAxiom getAsymmetricObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression) {
		return (ElkAsymmetricObjectPropertyAxiom) objectManager
				.getCanonicalElkObject(new ElkAsymmetricObjectPropertyAxiomImpl(
						objectPropertyExpression));
	}

	public ElkClassAssertionAxiom getClassAssertionAxiom(
			ElkClassExpression classExpression, ElkIndividual individual) {
		return (ElkClassAssertionAxiom) objectManager
				.getCanonicalElkObject(new ElkClassAssertionAxiomImpl(
						classExpression, individual));
	}

	public ElkDifferentIndividualsAxiom getDifferentIndividualsAxiom(
			List<? extends ElkIndividual> individuals) {
		return (ElkDifferentIndividualsAxiom) objectManager
				.getCanonicalElkObject(new ElkDifferentIndividualsAxiomImpl(
						individuals));
	}

	public ElkDifferentIndividualsAxiom getDifferentIndividualsAxiom(
			ElkIndividual firstIndividual, ElkIndividual secondIndividual,
			ElkIndividual... otherIndividuals) {
		return (ElkDifferentIndividualsAxiom) objectManager
				.getCanonicalElkObject(new ElkDifferentIndividualsAxiomImpl(
						ElkObjectListObject.varArgsToList(firstIndividual,
								secondIndividual, otherIndividuals)));
	}

	public ElkIrreflexiveObjectPropertyAxiom getIrreflexiveObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression) {
		return (ElkIrreflexiveObjectPropertyAxiom) objectManager
				.getCanonicalElkObject(new ElkIrreflexiveObjectPropertyAxiomImpl(
						objectPropertyExpression));
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
			int cardinality, ElkClassExpression classExpression) {
		return (ElkObjectExactCardinality) objectManager
				.getCanonicalElkObject(new ElkObjectExactCardinalityImpl(
						objectPropertyExpression, cardinality, classExpression));
	}

	public ElkObjectMaxCardinality getObjectMaxCardinality(
			ElkObjectPropertyExpression objectPropertyExpression,
			int cardinality, ElkClassExpression classExpression) {
		return (ElkObjectMaxCardinality) objectManager
				.getCanonicalElkObject(new ElkObjectMaxCardinalityImpl(
						objectPropertyExpression, cardinality, classExpression));
	}

	public ElkObjectMinCardinality getObjectMinCardinality(
			ElkObjectPropertyExpression objectPropertyExpression,
			int cardinality, ElkClassExpression classExpression) {
		return (ElkObjectMinCardinality) objectManager
				.getCanonicalElkObject(new ElkObjectMinCardinalityImpl(
						objectPropertyExpression, cardinality, classExpression));
	}

	public ElkObjectPropertyAssertionAxiom getObjectPropertyAssertionAxiom(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkIndividual firstIndividual, ElkIndividual secondIndividual) {
		return (ElkObjectPropertyAssertionAxiom) objectManager
				.getCanonicalElkObject(new ElkObjectPropertyAssertionAxiomImpl(
						objectPropertyExpression, firstIndividual,
						secondIndividual));
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

	public ElkObjectUnionOf getObjectUnionOf(
			List<? extends ElkClassExpression> classExpressions) {
		return (ElkObjectUnionOf) objectManager
				.getCanonicalElkObject(new ElkObjectUnionOfImpl(
						classExpressions));
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

	public ElkSameIndividualAxiom getSameIndividualAxiom(
			List<? extends ElkIndividual> individuals) {
		return (ElkSameIndividualAxiom) objectManager
				.getCanonicalElkObject(new ElkSameIndividualAxiomImpl(
						individuals));
	}

	public ElkSameIndividualAxiom getSameIndividualAxiom(
			ElkIndividual firstIndividual, ElkIndividual secondIndividual,
			ElkIndividual... otherIndividuals) {
		return (ElkSameIndividualAxiom) objectManager
				.getCanonicalElkObject(new ElkSameIndividualAxiomImpl(
						ElkObjectListObject.varArgsToList(firstIndividual,
								secondIndividual, otherIndividuals)));
	}

	public ElkSymmetricObjectPropertyAxiom getSymmetricObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression) {
		return (ElkSymmetricObjectPropertyAxiom) objectManager
				.getCanonicalElkObject(new ElkSymmetricObjectPropertyAxiomImpl(
						objectPropertyExpression));
	}
}
