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
import java.util.List;

import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationProperty;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDataProperty;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkDeclarationAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkTransitiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.owl.visitors.ElkEntityVisitor;
import org.semanticweb.elk.owl.visitors.NoOpElkAxiomVisitor;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkIndexingUnsupportedException;

/**
 * An abstract class for indexing axioms. Its purpose is to reduce many
 * syntactically different forms of OWL axioms to a small number of canonical
 * axiom forms. Concrete instances of this class then only need to implement
 * indexing of the canonical axioms.
 * 
 * @author Frantisek Simancik
 * 
 */
public abstract class AbstractElkAxiomIndexerVisitor extends
		NoOpElkAxiomVisitor<Void> implements ElkAxiomIndexer {

	/**
	 * Object factory that is used internally to replace some syntactic
	 * constructs with other logically equivalent constructs. ElkObjects created
	 * in this class are only used for this purpose (temporarily), hence we can
	 * use any factory implementation here.
	 */
	private final ElkObjectFactory objectFactory = new ElkObjectFactoryImpl();

	@Override
	protected Void defaultLogicalVisit(ElkAxiom axiom) {
		throw new ElkIndexingUnsupportedException(axiom);
	}

	/**
	 * Reduces equivalent object properties to subproperty axioms.
	 */
	@Override
	public Void visit(ElkEquivalentObjectPropertiesAxiom axiom) {
		ElkObjectPropertyExpression first = null;
		for (ElkObjectPropertyExpression p : axiom
				.getObjectPropertyExpressions()) {
			//

			if (first == null)
				first = p;
			else {
				indexSubObjectPropertyOfAxiom(first, p);
				indexSubObjectPropertyOfAxiom(p, first);
			}
		}

		return null;
	}

	@Override
	public Void visit(ElkObjectPropertyDomainAxiom axiom) {
		indexSubClassOfAxiom(objectFactory.getObjectSomeValuesFrom(
				axiom.getProperty(), PredefinedElkClass.OWL_THING),
				axiom.getDomain());
		return null;
	}

	@Override
	public Void visit(ElkReflexiveObjectPropertyAxiom axiom) {
		indexReflexiveObjectProperty(axiom.getProperty());
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.semanticweb.elk.owl.visitors.ElkObjectPropertyAxiomVisitor#visit(
	 * org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom)
	 * 
	 * subproperty axioms are supported directly
	 */
	@Override
	public Void visit(ElkSubObjectPropertyOfAxiom axiom) {
		indexSubObjectPropertyOfAxiom(axiom.getSubObjectPropertyExpression(),
				axiom.getSuperObjectPropertyExpression());
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.semanticweb.elk.owl.visitors.ElkObjectPropertyAxiomVisitor#visit(
	 * org.semanticweb.elk.owl.interfaces.ElkTransitiveObjectPropertyAxiom)
	 * 
	 * Reduces a transitivity axioms to a subproperty axiom with a role chain on
	 * left
	 */
	@Override
	public Void visit(ElkTransitiveObjectPropertyAxiom axiom) {
		ElkObjectPropertyExpression ope = axiom.getProperty();
		indexSubObjectPropertyOfAxiom(
				objectFactory.getObjectPropertyChain(Arrays.asList(ope, ope)),
				ope);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.semanticweb.elk.owl.visitors.ElkClassAxiomVisitor#visit(org.semanticweb
	 * .elk.owl.interfaces.ElkEquivalentClassesAxiom)
	 * 
	 * Reduces equivalent classes to subclass axioms.
	 */
	@Override
	public Void visit(ElkEquivalentClassesAxiom axiom) {
		ElkClassExpression first = null;
		for (ElkClassExpression c : axiom.getClassExpressions()) {
			if (first == null)
				first = c;
			else {
				indexEquivalentClasses(first, c);
				// indexSubClassOfAxiom(first, c);
				// indexSubClassOfAxiom(c, first);
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.semanticweb.elk.owl.visitors.ElkClassAxiomVisitor#visit(org.semanticweb
	 * .elk.owl.interfaces.ElkSubClassOfAxiom)
	 * 
	 * Subclass axioms are supported directly.
	 */
	@Override
	public Void visit(ElkSubClassOfAxiom axiom) {
		indexSubClassOfAxiom(axiom.getSubClassExpression(),
				axiom.getSuperClassExpression());
		return null;
	}

	private final static int DISJOINT_AXIOM_BINARIZATION_THRESHOLD = 2;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.semanticweb.elk.owl.AbstractElkAxiomVisitor#visit(org.semanticweb
	 * .elk.owl.interfaces.ElkDisjointClassesAxiom)
	 * 
	 * Binarize disjointness axioms that contain not many classes
	 */
	@Override
	public Void visit(ElkDisjointClassesAxiom axiom) {
		List<? extends ElkClassExpression> members = axiom
				.getClassExpressions();
		// if the axiom contains sufficiently many disjoint classes, convert it
		// natively
		if (axiom.getClassExpressions().size() > DISJOINT_AXIOM_BINARIZATION_THRESHOLD) {
			indexDisjointClassExpressions(axiom.getClassExpressions());
			return null;
		}
		// create a binary disjointness axiom for all pairs (member,
		// otherMember) where
		// otherMember occurs after member in members
		for (final ElkClassExpression member : members) {
			boolean selfFound = false; // true when otherMember = member
			for (ElkClassExpression otherMember : members) {
				if (!selfFound) {
					if (otherMember == member)
						selfFound = true;
				} else
					indexSubClassOfAxiom(objectFactory.getObjectIntersectionOf(
							member, otherMember), objectFactory.getOwlNothing());
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.semanticweb.elk.owl.visitors.ElkAssertionAxiomVisitor#visit(org.
	 * semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom)
	 * 
	 * Class assertions are supported directly.
	 */
	@Override
	public Void visit(ElkClassAssertionAxiom axiom) {
		indexClassAssertion(axiom.getIndividual(), axiom.getClassExpression());
		return null;
	}

	/**
	 * Reduces property assertions to class assertions with ObjectHasValue.
	 */
	@Override
	public Void visit(ElkObjectPropertyAssertionAxiom axiom) {
		indexClassAssertion(
				axiom.getSubject(),
				objectFactory.getObjectHasValue(axiom.getProperty(),
						axiom.getObject()));
		return null;
	}

	/**
	 * Declares the corresponding entity
	 */
	@Override
	public Void visit(ElkDeclarationAxiom axiom) {
		return axiom.getEntity().accept(entityDeclarator);
	}

	/**
	 * Entity visitor for calling the appropriate type of declarations.
	 */
	private final ElkEntityVisitor<Void> entityDeclarator = new ElkEntityVisitor<Void>() {

		@Override
		public Void visit(ElkClass elkClass) {
			indexClassDeclaration(elkClass);
			return null;
		}

		/**
		 * Nothing is done, datatypes are supported only syntactically. Warning
		 * is logged when indexing ElkDataHasValue.
		 */
		@Override
		public Void visit(ElkDatatype elkDatatype) {
			return null;
		}

		@Override
		public Void visit(ElkObjectProperty elkObjectProperty) {
			indexObjectPropertyDeclaration(elkObjectProperty);
			return null;
		}

		/**
		 * Nothing is done, datatypes are supported only syntactically. Warning
		 * is logged when indexing ElkDataHasValue.
		 */
		@Override
		public Void visit(ElkDataProperty elkDataProperty) {
			return null;
		}

		@Override
		public Void visit(ElkNamedIndividual elkNamedIndividual) {
			indexNamedIndividualDeclaration(elkNamedIndividual);
			return null;
		}

		/**
		 * Nothing is done, annotations are ignored during indexing.
		 */
		@Override
		public Void visit(ElkAnnotationProperty elkAnnotationProperty) {
			return null;
		}
	};
}