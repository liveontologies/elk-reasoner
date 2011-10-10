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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.ElkAxiomProcessor;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationAxiom;
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
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;

abstract class ElkAxiomIndexerVisitor implements ElkAxiomProcessor,
		ElkAxiomVisitor<Void> {

	// logger for events
	private static final Logger LOGGER_ = Logger
			.getLogger(ElkAxiomIndexerVisitor.class);

	protected abstract void indexSubClassOfAxiom(ElkClassExpression subClass,
			ElkClassExpression superClass);

	protected abstract void indexSubObjectPropertyOfAxiom(
			ElkSubObjectPropertyExpression subProperty,
			ElkObjectPropertyExpression superProperty);

	protected abstract void indexDeclarationAxiom(ElkEntity entity);

	/**
	 * Object factory that is used internally to replace some syntactic
	 * constructs with other logically equivalent constructs. ElkObjects created
	 * in this class are only used for this purpose (temporarily), hence we can
	 * use any factory implementation here.
	 */
	private final ElkObjectFactory objectFactory = new ElkObjectFactoryImpl(
			new DummyObjectManager());

	public void process(ElkAxiom elkAxiom) {
		try {
			elkAxiom.accept(this);
		} catch (RuntimeException e) {
			if (LOGGER_.isEnabledFor(Level.WARN))
				LOGGER_.warn("Axiom ignored: " + e.getMessage());
		}
	}

	public Void visit(ElkAnnotationAxiom elkAnnotationAxiom) {
		// annotations are ignored during indexing
		return null;
	}

	public Void visit(
			ElkDisjointDataPropertiesAxiom elkDisjointDataPropertiesAxiom) {
		throw new IndexingException(
				ElkDisjointDataPropertiesAxiom.class.getSimpleName()
						+ " not supported");
	}

	public Void visit(
			ElkEquivalentDataPropertiesAxiom elkEquivalentDataProperties) {
		throw new IndexingException(
				ElkEquivalentDataPropertiesAxiom.class.getSimpleName()
						+ " not supported");
	}

	public Void visit(
			ElkFunctionalDataPropertyAxiom elkFunctionalDataPropertyAxiom) {
		throw new IndexingException(
				ElkFunctionalDataPropertyAxiom.class.getSimpleName()
						+ " not supported");
	}

	public Void visit(ElkDataPropertyDomainAxiom elkDataPropertyDomainAxiom) {
		throw new IndexingException(
				ElkDataPropertyDomainAxiom.class.getSimpleName()
						+ " not supported");
	}

	public Void visit(ElkDataPropertyRangeAxiom elkDataPropertyRangeAxiom) {
		throw new IndexingException(
				ElkDataPropertyRangeAxiom.class.getSimpleName()
						+ " not supported");
	}

	public Void visit(ElkSubDataPropertyOfAxiom elkSubDataPropertyOfAxiom) {
		throw new IndexingException(
				ElkSubDataPropertyOfAxiom.class.getSimpleName()
						+ " not supported");
	}

	public Void visit(
			ElkAsymmetricObjectPropertyAxiom elkAsymmetricObjectPropertyAxiom) {
		throw new IndexingException(
				ElkAsymmetricObjectPropertyAxiom.class.getSimpleName()
						+ " not supported");
	}

	public Void visit(
			ElkDisjointObjectPropertiesAxiom elkDisjointObjectPropertiesAxiom) {
		throw new IndexingException(
				ElkDisjointObjectPropertiesAxiom.class.getSimpleName()
						+ " not supported");
	}

	public Void visit(ElkEquivalentObjectPropertiesAxiom axiom) {
		ElkObjectPropertyExpression first = null;
		for (ElkObjectPropertyExpression p : axiom
				.getObjectPropertyExpressions()) {
			// implement EquivalentObjectPropertyExpressionAxiom as two
			// SubObjectPropertyOfAxioms

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
		throw new IndexingException(
				ElkFunctionalObjectPropertyAxiom.class.getSimpleName()
						+ " not supported");
	}

	public Void visit(
			ElkInverseFunctionalObjectPropertyAxiom elkInverseFunctionalObjectPropertyAxiom) {
		throw new IndexingException(
				ElkInverseFunctionalObjectPropertyAxiom.class.getSimpleName()
						+ " not supported");
	}

	public Void visit(
			ElkInverseObjectPropertiesAxiom elkInverseObjectPropertiesAxiom) {
		throw new IndexingException(
				ElkInverseObjectPropertiesAxiom.class.getSimpleName()
						+ " not supported");
	}

	public Void visit(
			ElkIrreflexiveObjectPropertyAxiom elkIrreflexiveObjectPropertyAxiom) {
		throw new IndexingException(
				ElkIrreflexiveObjectPropertyAxiom.class.getSimpleName()
						+ " not supported");
	}

	public Void visit(ElkObjectPropertyDomainAxiom axiom) {
		indexSubClassOfAxiom(objectFactory.getObjectSomeValuesFrom(
				axiom.getObjectPropertyExpression(),
				PredefinedElkClass.OWL_THING), axiom.getClassExpression());
		return null;
	}

	public Void visit(ElkObjectPropertyRangeAxiom elkObjectPropertyRangeAxiom) {
		throw new IndexingException(
				ElkObjectPropertyRangeAxiom.class.getSimpleName()
						+ " not supported");
	}

	public Void visit(
			ElkReflexiveObjectPropertyAxiom elkReflexiveObjectPropertyAxiom) {
		throw new IndexingException(
				ElkReflexiveObjectPropertyAxiom.class.getSimpleName()
						+ " not supported");
	}

	public Void visit(ElkSubObjectPropertyOfAxiom axiom) {
		indexSubObjectPropertyOfAxiom(axiom.getSubObjectPropertyExpression(),
				axiom.getSuperObjectPropertyExpression());
		return null;
	}

	public Void visit(
			ElkSymmetricObjectPropertyAxiom elkSymmetricObjectPropertyAxiom) {
		throw new IndexingException(
				ElkSymmetricObjectPropertyAxiom.class.getSimpleName()
						+ " not supported");
	}

	public Void visit(ElkTransitiveObjectPropertyAxiom axiom) {
		ElkObjectPropertyExpression ope = axiom.getObjectPropertyExpression();
		indexSubObjectPropertyOfAxiom(
				objectFactory.getObjectPropertyChain(Arrays.asList(ope, ope)),
				ope);
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
		throw new IndexingException(
				ElkDisjointClassesAxiom.class.getSimpleName()
						+ " not supported");
	}

	public Void visit(ElkDisjointUnionAxiom elkDisjointUnionAxiom) {
		throw new IndexingException(ElkDisjointUnionAxiom.class.getSimpleName()
				+ " not supported");
	}

	public Void visit(ElkClassAssertionAxiom elkClassAssertionAxiom) {
		throw new IndexingException(
				ElkClassAssertionAxiom.class.getSimpleName() + " not supported");
	}

	public Void visit(ElkDifferentIndividualsAxiom elkDifferentIndividualsAxiom) {
		throw new IndexingException(
				ElkDifferentIndividualsAxiom.class.getSimpleName()
						+ " not supported");
	}

	public Void visit(
			ElkNegativeObjectPropertyAssertionAxiom elkNegativeObjectPropertyAssertion) {
		throw new IndexingException(
				ElkNegativeObjectPropertyAssertionAxiom.class.getSimpleName()
						+ " not supported");
	}

	public Void visit(
			ElkObjectPropertyAssertionAxiom elkObjectPropertyAssertionAxiom) {
		throw new IndexingException(
				ElkObjectPropertyAssertionAxiom.class.getSimpleName()
						+ " not supported");
	}

	public Void visit(ElkSameIndividualAxiom elkSameIndividualAxiom) {
		throw new IndexingException(
				ElkSameIndividualAxiom.class.getSimpleName() + " not supported");
	}

	public Void visit(
			ElkNegativeDataPropertyAssertionAxiom elkNegativeDataPropertyAssertion) {
		throw new IndexingException(
				ElkNegativeDataPropertyAssertionAxiom.class.getSimpleName()
						+ " not supported");
	}

	public Void visit(ElkDataPropertyAssertionAxiom elkObjectDataAssertionAxiom) {
		throw new IndexingException(
				ElkDataPropertyAssertionAxiom.class.getSimpleName()
						+ " not supported");
	}

	public Void visit(ElkDeclarationAxiom axiom) {
		indexDeclarationAxiom(axiom.getEntity());
		return null;
	}
}
