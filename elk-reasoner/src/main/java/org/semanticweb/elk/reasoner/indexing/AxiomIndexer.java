/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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
 * @author Yevgeny Kazakov, May 13, 2011
 */
package org.semanticweb.elk.reasoner.indexing;

import java.util.Arrays;

import org.semanticweb.elk.syntax.ElkAxiom;
import org.semanticweb.elk.syntax.ElkAxiomProcessor;
import org.semanticweb.elk.syntax.ElkAxiomVisitor;
import org.semanticweb.elk.syntax.ElkClassExpression;
import org.semanticweb.elk.syntax.ElkDeclarationAxiom;
import org.semanticweb.elk.syntax.ElkDisjointClassesAxiom;
import org.semanticweb.elk.syntax.ElkDisjointObjectPropertiesAxiom;
import org.semanticweb.elk.syntax.ElkDisjointUnionAxiom;
import org.semanticweb.elk.syntax.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.syntax.ElkEquivalentObjectPropertiesAxiom;
import org.semanticweb.elk.syntax.ElkFunctionalObjectPropertyAxiom;
import org.semanticweb.elk.syntax.ElkInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.elk.syntax.ElkInverseObjectPropertiesAxiom;
import org.semanticweb.elk.syntax.ElkObjectInverseOf;
import org.semanticweb.elk.syntax.ElkObjectProperty;
import org.semanticweb.elk.syntax.ElkObjectPropertyChain;
import org.semanticweb.elk.syntax.ElkObjectPropertyExpression;
import org.semanticweb.elk.syntax.ElkSubClassOfAxiom;
import org.semanticweb.elk.syntax.ElkSubObjectPropertyExpressionVisitor;
import org.semanticweb.elk.syntax.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.syntax.ElkTransitiveObjectPropertyAxiom;

/**
 * An ElkAxiomProcessor that updates an OntologyIndex for the given ElkAxioms.
 * 
 * @author Yevgeny Kazakov
 * @author Frantisek Simancik
 * @author Markus Kroetzsch
 * 
 */
class AxiomIndexer implements ElkAxiomProcessor, ElkAxiomVisitor<Void> {

	protected final OntologyIndexModifier ontologyIndex;
	protected final ClassExpressionIndexer classExpressionIndexer;
	protected final NegativeClassExpressionIndexer negativeClassExpressionIndexer;
	protected final PositiveClassExpressionIndexer positiveClassExpressionIndexer;
	protected final ObjectPropertyExpressionIndexer objectPropertyExpressionIndexer;

	protected final int multiplicity;

	/**
	 * Constructor.
	 * 
	 * @param ontologyIndex
	 *            to add indexed axioms to
	 */
	protected AxiomIndexer(OntologyIndexModifier ontologyIndex, int multiplicity) {
		assert (multiplicity == 1 || multiplicity == -1);

		this.ontologyIndex = ontologyIndex;
		this.multiplicity = multiplicity;

		classExpressionIndexer =
			new ClassExpressionIndexer(this);
		negativeClassExpressionIndexer = 
			new NegativeClassExpressionIndexer(this);
		positiveClassExpressionIndexer = 
			new PositiveClassExpressionIndexer(this);
		objectPropertyExpressionIndexer =
			new ObjectPropertyExpressionIndexer(this);
	}

	/**
	 * Index the given axiom.
	 */
	public void process(ElkAxiom elkAxiom) {
		elkAxiom.accept(this);
	}

	public Void visit(ElkSubClassOfAxiom axiom) {
		indexSubClassOfAxiom(axiom.getSubClassExpression(),
				axiom.getSuperClassExpression());
		return null;
	}

	public Void visit(ElkEquivalentClassesAxiom axiom) {
		ElkClassExpression first = null;
		for (ElkClassExpression c : axiom.getEquivalentClassExpressions()) {
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

	public Void visit(ElkFunctionalObjectPropertyAxiom axiom) {

		throw new UnsupportedOperationException("Not yet implemented");
	}

	public Void visit(ElkInverseFunctionalObjectPropertyAxiom axiom) {

		throw new UnsupportedOperationException("Not yet implemented");
	}

	public Void visit(ElkInverseObjectPropertiesAxiom axiom) {

		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	public Void visit(final ElkSubObjectPropertyOfAxiom axiom) {
		axiom.getSubObjectPropertyExpression().accept(
				new ElkSubObjectPropertyExpressionVisitor<Void>() {

					public Void visit(ElkObjectProperty subProperty) {
						indexSubObjectPropertyOfAxiom(subProperty, axiom.getSuperObjectPropertyExpression());
						return null;
					}

					public Void visit(ElkObjectInverseOf subProperty) {
						throw new UnsupportedOperationException("Not yet implemented");
					}

					public Void visit(ElkObjectPropertyChain subPropertyChain) {
						indexPropertyComposition(axiom);
						return null;
					}
				});

		return null;
	}

	public Void visit(ElkTransitiveObjectPropertyAxiom axiom) {
		ElkObjectPropertyExpression ope = axiom.getObjectPropertyExpression();
		indexPropertyComposition(ElkSubObjectPropertyOfAxiom.create(
				ElkObjectPropertyChain.create(Arrays.asList(ope, ope)), ope));
		return null;
	}

	public Void visit(ElkDeclarationAxiom axiom) {

		IndexedEntity ie = ontologyIndex.getIndexedEntity(axiom.getEntity());

		if (multiplicity == 1) {
			if (ie == null)
				ie = ontologyIndex.createIndexed(axiom.getEntity());
		}
		if (multiplicity == -1) {
			if (ie == null)
				return null;
		}

		ie.accept(new IndexedEntityVisitor<Void> () {

			public Void visit(IndexedClass indexedClass) {
				return indexedClass.accept(classExpressionIndexer);
			}

			public Void visit(IndexedObjectProperty indexedObjectProperty) {
				return indexedObjectProperty.accept(objectPropertyExpressionIndexer);
			}
		});
		
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

	public Void visit(
			ElkEquivalentObjectPropertiesAxiom elkEquivalentObjectProperties) {
		// TODO Auto-generated method stub
		return null;
	}

	public Void visit(
			ElkDisjointObjectPropertiesAxiom elkDisjointObjectPropertiesAxiom) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
	protected void indexSubClassOfAxiom(
			ElkClassExpression elkSubClass,
			ElkClassExpression elkSuperClass) {
		
		IndexedClassExpression subClass = null;
		IndexedClassExpression superClass = null;
		
		if (multiplicity == 1) {
			subClass = ontologyIndex.getIndexedClassExpression(elkSubClass);
			if (subClass == null)
				subClass = ontologyIndex.createIndexed(elkSubClass);
			
			superClass = ontologyIndex.getIndexedClassExpression(elkSuperClass);
			if (superClass == null)
				superClass = ontologyIndex.createIndexed(elkSuperClass);
			
			subClass.addToldSuperClassExpression(superClass);
		}

		if (multiplicity == -1) {
			subClass = ontologyIndex.getIndexedClassExpression(elkSubClass);
			superClass = ontologyIndex.getIndexedClassExpression(elkSuperClass);
			if (subClass == null || superClass == null)
				return;
			
			subClass.removeToldSuperClassExpression(superClass);
		}

		subClass.accept(classExpressionIndexer);
		superClass.accept(classExpressionIndexer);
		subClass.accept(negativeClassExpressionIndexer);
		superClass.accept(positiveClassExpressionIndexer);
	}
	
	protected void indexSubObjectPropertyOfAxiom(
			ElkObjectPropertyExpression elkSubProperty,
			ElkObjectPropertyExpression elkSuperProperty) {

		IndexedObjectProperty subProperty = null;
		IndexedObjectProperty superProperty = null;

		if (multiplicity == 1) {
			subProperty = ontologyIndex.getIndexedObjectProperty(elkSubProperty);
			if (subProperty == null)
				subProperty = ontologyIndex.createIndexed(elkSubProperty);
			
			superProperty = ontologyIndex.getIndexedObjectProperty(elkSuperProperty);
			if (superProperty == null)
				superProperty = ontologyIndex.createIndexed(elkSuperProperty);
			
			subProperty.addToldSuperObjectProperty(superProperty);
			superProperty.addToldSubObjectProperty(subProperty);
		}

		if (multiplicity == -1) {
			subProperty = ontologyIndex.getIndexedObjectProperty(elkSubProperty);
			superProperty = ontologyIndex.getIndexedObjectProperty(elkSuperProperty);
			if (subProperty == null || superProperty == null)
				return;
			
			subProperty.removeToldSuperObjectProperty(superProperty);
			superProperty.removeToldSubObjectProperty(subProperty);
		}

		subProperty.accept(objectPropertyExpressionIndexer);
		superProperty.accept(objectPropertyExpressionIndexer);
	}
	
	protected void indexPropertyComposition(
			ElkSubObjectPropertyOfAxiom axiom) {

		IndexedPropertyComposition ipc = null;

		if (multiplicity == 1) {
			ipc = ontologyIndex.getIndexedPropertyChain(axiom);
			if (ipc == null)
				ipc = ontologyIndex.createIndexed(axiom);
		}

		if (multiplicity == -1) {
			ipc = ontologyIndex.getIndexedPropertyChain(axiom);
			if (ipc == null)
				return;
		}

		ipc.accept(objectPropertyExpressionIndexer);
	}
	
}