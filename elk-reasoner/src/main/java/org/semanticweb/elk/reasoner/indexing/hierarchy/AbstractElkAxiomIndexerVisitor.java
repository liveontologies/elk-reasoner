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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.ElkAxiomProcessor;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationProperty;
import org.semanticweb.elk.owl.interfaces.ElkAsymmetricObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDataProperty;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyRangeAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkDatatypeDefinitionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDeclarationAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDifferentIndividualsAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointDataPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointUnionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentDataPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkFunctionalDataPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkFunctionalObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkHasKeyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkInverseObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkIrreflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkNegativeDataPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
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
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkEntityVisitor;

/**
 * An abstract class for indexing axioms. Its purpose is to reduce many
 * syntactically different forms of OWL axioms to a small number of
 * canonical axiom forms. Concrete instances of this class then only need
 * to implement indexing of the canonical axioms.
 * 
 * @author Frantisek Simancik
 * 
 */
public abstract class AbstractElkAxiomIndexerVisitor implements
		ElkAxiomProcessor, ElkAxiomVisitor<Void> {

	// logger for events
	private static final Logger LOGGER_ = Logger
			.getLogger(AbstractElkAxiomIndexerVisitor.class);

	public abstract void indexSubClassOfAxiom(ElkClassExpression subClass,
			ElkClassExpression superClass);

	public abstract void indexSubObjectPropertyOfAxiom(
			ElkSubObjectPropertyExpression subProperty,
			ElkObjectPropertyExpression superProperty);
	
	public abstract void indexDisjointClassExpressions(List<? extends ElkClassExpression> list);

	public abstract void indexClassDeclaration(ElkClass ec);

	public abstract void indexObjectPropertyDeclaration(ElkObjectProperty eop);

	public abstract void indexNamedIndividualDeclaration(ElkNamedIndividual eni);

	/**
	 * Object factory that is used internally to replace some syntactic
	 * constructs with other logically equivalent constructs. ElkObjects created
	 * in this class are only used for this purpose (temporarily), hence we can
	 * use any factory implementation here.
	 */
	private final ElkObjectFactory objectFactory = new ElkObjectFactoryImpl();

	@Override
	public void process(ElkAxiom elkAxiom) {
		try {
			elkAxiom.accept(this);
		} catch (RuntimeException e) {
			if (LOGGER_.isEnabledFor(Level.WARN))
				LOGGER_.warn("Axiom ignored: "
						+ OwlFunctionalStylePrinter.toString(elkAxiom) + " : "
						+ e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.elk.owl.visitors.ElkEntityVisitor#visit(org.semanticweb.elk.owl.interfaces.ElkAnnotationProperty)
	 * 
	 * Nothing is done, annotations are ignored during indexing.
	 */
	@SuppressWarnings("static-method")
	public Void visit(ElkAnnotationAxiom elkAnnotationAxiom) {
		return null;
	}

	@Override
	public Void visit(
			ElkDisjointDataPropertiesAxiom elkDisjointDataPropertiesAxiom) {
		throw new IndexingException(
				ElkDisjointDataPropertiesAxiom.class.getSimpleName()
						+ " not supported");
	}

	@Override
	public Void visit(
			ElkEquivalentDataPropertiesAxiom elkEquivalentDataProperties) {
		throw new IndexingException(
				ElkEquivalentDataPropertiesAxiom.class.getSimpleName()
						+ " not supported");
	}

	@Override
	public Void visit(
			ElkFunctionalDataPropertyAxiom elkFunctionalDataPropertyAxiom) {
		throw new IndexingException(
				ElkFunctionalDataPropertyAxiom.class.getSimpleName()
						+ " not supported");
	}

	@Override
	public Void visit(ElkDataPropertyDomainAxiom elkDataPropertyDomainAxiom) {
		throw new IndexingException(
				ElkDataPropertyDomainAxiom.class.getSimpleName()
						+ " not supported");
	}

	@Override
	public Void visit(ElkDataPropertyRangeAxiom elkDataPropertyRangeAxiom) {
		throw new IndexingException(
				ElkDataPropertyRangeAxiom.class.getSimpleName()
						+ " not supported");
	}

	@Override
	public Void visit(ElkSubDataPropertyOfAxiom elkSubDataPropertyOfAxiom) {
		throw new IndexingException(
				ElkSubDataPropertyOfAxiom.class.getSimpleName()
						+ " not supported");
	}

	@Override
	public Void visit(
			ElkAsymmetricObjectPropertyAxiom elkAsymmetricObjectPropertyAxiom) {
		throw new IndexingException(
				ElkAsymmetricObjectPropertyAxiom.class.getSimpleName()
						+ " not supported");
	}

	@Override
	public Void visit(
			ElkDisjointObjectPropertiesAxiom elkDisjointObjectPropertiesAxiom) {
		throw new IndexingException(
				ElkDisjointObjectPropertiesAxiom.class.getSimpleName()
						+ " not supported");
	}
	
	@Override
	public Void visit(ElkAnnotationAssertionAxiom annAssertionAxiom) {
		throw new IndexingException(ElkAnnotationAssertionAxiom.class.getSimpleName() + " not supported");
	}	

	/* (non-Javadoc)
	 * @see org.semanticweb.elk.owl.visitors.ElkObjectPropertyAxiomVisitor#visit(org.semanticweb.elk.owl.interfaces.ElkEquivalentObjectPropertiesAxiom)
	 * 
	 * Reduces equivalent properties to subproperty axioms 
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
	public Void visit(
			ElkFunctionalObjectPropertyAxiom elkFunctionalObjectPropertyAxiom) {
		throw new IndexingException(
				ElkFunctionalObjectPropertyAxiom.class.getSimpleName()
						+ " not supported");
	}

	@Override
	public Void visit(
			ElkInverseFunctionalObjectPropertyAxiom elkInverseFunctionalObjectPropertyAxiom) {
		throw new IndexingException(
				ElkInverseFunctionalObjectPropertyAxiom.class.getSimpleName()
						+ " not supported");
	}

	@Override
	public Void visit(
			ElkInverseObjectPropertiesAxiom elkInverseObjectPropertiesAxiom) {
		throw new IndexingException(
				ElkInverseObjectPropertiesAxiom.class.getSimpleName()
						+ " not supported");
	}

	@Override
	public Void visit(
			ElkIrreflexiveObjectPropertyAxiom elkIrreflexiveObjectPropertyAxiom) {
		throw new IndexingException(
				ElkIrreflexiveObjectPropertyAxiom.class.getSimpleName()
						+ " not supported");
	}

	@Override
	public Void visit(ElkObjectPropertyDomainAxiom axiom) {
		indexSubClassOfAxiom(objectFactory.getObjectSomeValuesFrom(
				axiom.getProperty(), PredefinedElkClass.OWL_THING),
				axiom.getDomain());
		return null;
	}

	@Override
	public Void visit(ElkObjectPropertyRangeAxiom elkObjectPropertyRangeAxiom) {
		throw new IndexingException(
				ElkObjectPropertyRangeAxiom.class.getSimpleName()
						+ " not supported");
	}

	@Override
	public Void visit(
			ElkReflexiveObjectPropertyAxiom elkReflexiveObjectPropertyAxiom) {
		throw new IndexingException(
				ElkReflexiveObjectPropertyAxiom.class.getSimpleName()
						+ " not supported");
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.elk.owl.visitors.ElkObjectPropertyAxiomVisitor#visit(org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom)
	 * 
	 * subproperty axioms are supported directly
	 */
	@Override
	public Void visit(ElkSubObjectPropertyOfAxiom axiom) {
		indexSubObjectPropertyOfAxiom(axiom.getSubObjectPropertyExpression(),
				axiom.getSuperObjectPropertyExpression());
		return null;
	}

	@Override
	public Void visit(
			ElkSymmetricObjectPropertyAxiom elkSymmetricObjectPropertyAxiom) {
		throw new IndexingException(
				ElkSymmetricObjectPropertyAxiom.class.getSimpleName()
						+ " not supported");
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.elk.owl.visitors.ElkObjectPropertyAxiomVisitor#visit(org.semanticweb.elk.owl.interfaces.ElkTransitiveObjectPropertyAxiom)
	 * 
	 * Reduces a transitivity axioms to a subproperty axiom with a role chain on left
	 */
	@Override
	public Void visit(ElkTransitiveObjectPropertyAxiom axiom) {
		ElkObjectPropertyExpression ope = axiom.getProperty();
		indexSubObjectPropertyOfAxiom(
				objectFactory.getObjectPropertyChain(Arrays.asList(ope, ope)),
				ope);
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.elk.owl.visitors.ElkClassAxiomVisitor#visit(org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom)
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
				indexSubClassOfAxiom(first, c);
				indexSubClassOfAxiom(c, first);
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.elk.owl.visitors.ElkClassAxiomVisitor#visit(org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom)
	 * 
	 * Subclass axioms are supported directly.
	 */
	@Override
	public Void visit(ElkSubClassOfAxiom axiom) {
		indexSubClassOfAxiom(axiom.getSubClassExpression(),
				axiom.getSuperClassExpression());
		return null;
	}

	@Override
	public Void visit(ElkDisjointClassesAxiom axiom) {
		indexDisjointClassExpressions(axiom.getClassExpressions());
		return null;
	}

	@Override
	public Void visit(ElkDisjointUnionAxiom elkDisjointUnionAxiom) {
		throw new IndexingException(ElkDisjointUnionAxiom.class.getSimpleName()
				+ " not supported");
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.elk.owl.visitors.ElkAssertionAxiomVisitor#visit(org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom)
	 * 
	 * Reduces a class assertion to a subclass axiom with a nominal on left.
	 */
	@Override
	public Void visit(ElkClassAssertionAxiom axiom) {
		indexSubClassOfAxiom(
				objectFactory.getObjectOneOf(axiom.getIndividual()),
				axiom.getClassExpression());
		return null;
	}

	@Override
	public Void visit(ElkDifferentIndividualsAxiom elkDifferentIndividualsAxiom) {
		throw new IndexingException(
				ElkDifferentIndividualsAxiom.class.getSimpleName()
						+ " not supported");
	}

	@Override
	public Void visit(ElkNegativeObjectPropertyAssertionAxiom axiom) {
		throw new IndexingException(
				ElkNegativeObjectPropertyAssertionAxiom.class.getSimpleName()
						+ " not supported");
	}
	
	@Override
	public Void visit(ElkHasKeyAxiom elkHasKey) {
		throw new IndexingException(
				ElkHasKeyAxiom.class.getSimpleName()
						+ " not supported");
	}
	
	@Override
	public Void visit(ElkDatatypeDefinitionAxiom elkDatatypeDefn) {
		throw new IndexingException(
				ElkHasKeyAxiom.class.getSimpleName()
						+ " not supported");
	}	

	/* (non-Javadoc)
	 * @see org.semanticweb.elk.owl.visitors.ElkAssertionAxiomVisitor#visit(org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAssertionAxiom)
	 * 
	 * Reduces property assertions to subclass axioms with nominals.
	 */
	@Override
	public Void visit(ElkObjectPropertyAssertionAxiom axiom) {
		indexSubClassOfAxiom(objectFactory.getObjectOneOf(axiom.getSubject()),
				objectFactory.getObjectSomeValuesFrom(axiom.getProperty(),
						objectFactory.getObjectOneOf(axiom.getObject())));
		return null;
	}

	@Override
	public Void visit(ElkSameIndividualAxiom elkSameIndividualAxiom) {
		throw new IndexingException(
				ElkSameIndividualAxiom.class.getSimpleName() + " not supported");
	}

	@Override
	public Void visit(
			ElkNegativeDataPropertyAssertionAxiom elkNegativeDataPropertyAssertion) {
		throw new IndexingException(
				ElkNegativeDataPropertyAssertionAxiom.class.getSimpleName()
						+ " not supported");
	}

	@Override
	public Void visit(ElkDataPropertyAssertionAxiom elkObjectDataAssertionAxiom) {
		throw new IndexingException(
				ElkDataPropertyAssertionAxiom.class.getSimpleName()
						+ " not supported");
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.elk.owl.visitors.ElkAxiomVisitor#visit(org.semanticweb.elk.owl.interfaces.ElkDeclarationAxiom)
	 * 
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

		@Override
		public Void visit(ElkDatatype elkDatatype) {
			LOGGER_.warn(ElkDatatype.class.getSimpleName()
					+ " is supported only partially.");
			return null;
		}

		@Override
		public Void visit(ElkObjectProperty elkObjectProperty) {
			indexObjectPropertyDeclaration(elkObjectProperty);
			return null;
		}

		@Override
		public Void visit(ElkDataProperty elkDataProperty) {
			LOGGER_.warn(ElkDataProperty.class.getSimpleName()
					+ " is supported only partially.");
			return null;
		}

		@Override
		public Void visit(ElkNamedIndividual elkNamedIndividual) {
			indexNamedIndividualDeclaration(elkNamedIndividual);
			return null;
		}

		
		/* (non-Javadoc)
		 * @see org.semanticweb.elk.owl.visitors.ElkEntityVisitor#visit(org.semanticweb.elk.owl.interfaces.ElkAnnotationProperty)
		 * 
		 * Nothing is done, annotations are ignored during indexing.
		 */
		@Override
		public Void visit(ElkAnnotationProperty elkAnnotationProperty) {
			return null;
		}
	};
}
