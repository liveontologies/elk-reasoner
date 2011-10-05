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
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import java.util.Arrays;

import org.semanticweb.elk.owl.ElkAxiomProcessor;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkAsymmetricObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyRangeAxiom;
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
import org.semanticweb.elk.owl.interfaces.ElkFunctionalDataPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkFunctionalObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkInverseObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkIrreflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkNegativeDataPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyRangeAxiom;
import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSameIndividualAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubDataPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSymmetricObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkTransitiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.managers.DummyObjectManager;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;

abstract class ElkAxiomIndexerVisitor implements ElkAxiomProcessor, ElkAxiomVisitor<Void> {
	
	protected abstract void indexSubClassOfAxiom(
			ElkClassExpression subClass,
			ElkClassExpression superClass);
	
	protected abstract void indexSubObjectPropertyOfAxiom(
			ElkSubObjectPropertyExpression subPropertyExpression,
			ElkObjectPropertyExpression superProperty);
	
	protected abstract void indexDeclarationAxiom(ElkEntity entity);
	
	/**
	 * Object factory that is used internally to replace some syntactic
	 * constructs with other logically equivalent constructs. ElkObjects created
	 * in this class are only used for this purpose (temporarily), hence we can
	 * use any factory implementation here.
	 */
	private final ElkObjectFactory objectFactory =
		new ElkObjectFactoryImpl(new DummyObjectManager());
	
	public void process(ElkAxiom elkAxiom) {
		elkAxiom.accept(this);
	}

	public Void visit(
			ElkDisjointDataPropertiesAxiom elkDisjointDataPropertiesAxiom) {
		// TODO Auto-generated method stub
		return null;
	}
	public Void visit(
			ElkEquivalentDataPropertiesAxiom elkEquivalentDataProperties) {
		// TODO Auto-generated method stub
		return null;
	}
	public Void visit(
			ElkFunctionalDataPropertyAxiom elkFunctionalDataPropertyAxiom) {
		// TODO Auto-generated method stub
		return null;
	}
	public Void visit(ElkDataPropertyDomainAxiom elkDataPropertyDomainAxiom) {
		// TODO Auto-generated method stub
		return null;
	}
	public Void visit(ElkDataPropertyRangeAxiom elkDataPropertyRangeAxiom) {
		// TODO Auto-generated method stub
		return null;
	}
	public Void visit(ElkSubDataPropertyOfAxiom elkSubDataPropertyOfAxiom) {
		// TODO Auto-generated method stub
		return null;
	}
	public Void visit(
			ElkAsymmetricObjectPropertyAxiom elkAsymmetricObjectPropertyAxiom) {
		// TODO Auto-generated method stub
		return null;
	}
	public Void visit(
			ElkDisjointObjectPropertiesAxiom elkDisjointObjectPropertiesAxiom) {
		// TODO Auto-generated method stub
		return null;
	}
	public Void visit(
			ElkEquivalentObjectPropertiesAxiom axiom) {
		ElkObjectPropertyExpression first = null;
		for (ElkObjectPropertyExpression p : axiom.getObjectPropertyExpressions()) {
			// implement EquivalentObjectPropertyExpressionAxiom as two SubObjectPropertyOfAxioms

			if (first == null)
				first = p;
			else {
				indexSubObjectPropertyOfAxiom(first, p);
				indexSubObjectPropertyOfAxiom(p, first);
			}
		}
		return null;
	}
	public Void visit(
			ElkFunctionalObjectPropertyAxiom elkFunctionalObjectPropertyAxiom) {
		// TODO Auto-generated method stub
		return null;
	}
	public Void visit(
			ElkInverseFunctionalObjectPropertyAxiom elkInverseFunctionalObjectPropertyAxiom) {
		// TODO Auto-generated method stub
		return null;
	}
	public Void visit(
			ElkInverseObjectPropertiesAxiom elkInverseObjectPropertiesAxiom) {
		// TODO Auto-generated method stub
		return null;
	}
	public Void visit(
			ElkIrreflexiveObjectPropertyAxiom elkIrreflexiveObjectPropertyAxiom) {
		// TODO Auto-generated method stub
		return null;
	}
	public Void visit(ElkObjectPropertyDomainAxiom axiom) {
		indexSubClassOfAxiom(
				objectFactory.getObjectSomeValuesFrom(
						axiom.getObjectPropertyExpression(),
						objectFactory.getOwlThing()),
				axiom.getClassExpression());
		return null;
	}
	public Void visit(ElkObjectPropertyRangeAxiom elkObjectPropertyRangeAxiom) {
		// TODO Auto-generated method stub
		return null;
	}
	public Void visit(
			ElkReflexiveObjectPropertyAxiom elkReflexiveObjectPropertyAxiom) {
		// TODO Auto-generated method stub
		return null;
	}
	public Void visit(ElkSubObjectPropertyOfAxiom axiom) {
		indexSubObjectPropertyOfAxiom(axiom.getSubObjectPropertyExpression(), axiom.getSuperObjectPropertyExpression());
		return null;
	}
	public Void visit(
			ElkSymmetricObjectPropertyAxiom elkSymmetricObjectPropertyAxiom) {
		// TODO Auto-generated method stub
		return null;
	}
	public Void visit(
			ElkTransitiveObjectPropertyAxiom axiom) {
		ElkObjectPropertyExpression ope = axiom.getObjectPropertyExpression();
		indexSubObjectPropertyOfAxiom(objectFactory.getObjectPropertyChain(
				Arrays.asList(ope, ope)), ope);
		return null;
	}
	public Void visit(ElkEquivalentClassesAxiom axiom) {
		ElkClassExpression first = null;
		for (ElkClassExpression c : axiom.getClassExpressions()) {
			// implement EquivalentClassesAxiom as two SubClassOfAxioms

			if (first == null)
				first = c;
			else {
				indexSubClassOfAxiom(first, c);
				indexSubClassOfAxiom(c, first);
			}
		}
		return null;
	}
	public Void visit(ElkSubClassOfAxiom axiom) {
		indexSubClassOfAxiom(axiom.getSubClassExpression(),
				axiom.getSuperClassExpression());
		return null;
	}
	public Void visit(ElkDisjointClassesAxiom elkDisjointClasses) {
		// TODO Auto-generated method stub
		return null;
	}
	public Void visit(ElkDisjointUnionAxiom elkDisjointUnionAxiom) {
		// TODO Auto-generated method stub
		return null;
	}
	public Void visit(ElkClassAssertionAxiom elkClassAssertionAxiom) {
		// TODO Auto-generated method stub
		return null;
	}
	public Void visit(ElkDifferentIndividualsAxiom elkDifferentIndividualsAxiom) {
		// TODO Auto-generated method stub
		return null;
	}
	public Void visit(
			ElkNegativeObjectPropertyAssertionAxiom elkNegativeObjectPropertyAssertion) {
		// TODO Auto-generated method stub
		return null;
	}
	public Void visit(
			ElkObjectPropertyAssertionAxiom elkObjectPropertyAssertionAxiom) {
		// TODO Auto-generated method stub
		return null;
	}
	public Void visit(ElkSameIndividualAxiom elkSameIndividualAxiom) {
		// TODO Auto-generated method stub
		return null;
	}
	public Void visit(
			ElkNegativeDataPropertyAssertionAxiom elkNegativeDataPropertyAssertion) {
		// TODO Auto-generated method stub
		return null;
	}
	public Void visit(ElkDataPropertyAssertionAxiom elkObjectDataAssertionAxiom) {
		// TODO Auto-generated method stub
		return null;
	}
	public Void visit(ElkDeclarationAxiom axiom) {
		indexDeclarationAxiom(axiom.getEntity());
		return null;
	}
}
