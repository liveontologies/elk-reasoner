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

import org.semanticweb.elk.syntax.ElkAxiom;
import org.semanticweb.elk.syntax.ElkAxiomProcessor;
import org.semanticweb.elk.syntax.ElkAxiomVisitor;
import org.semanticweb.elk.syntax.ElkClassExpression;
import org.semanticweb.elk.syntax.ElkDeclarationAxiom;
import org.semanticweb.elk.syntax.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.syntax.ElkFunctionalObjectPropertyAxiom;
import org.semanticweb.elk.syntax.ElkInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.elk.syntax.ElkInverseObjectPropertiesAxiom;
import org.semanticweb.elk.syntax.ElkSubClassOfAxiom;
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

		classExpressionIndexer = new ClassExpressionIndexer(this);

		negativeClassExpressionIndexer = new NegativeClassExpressionIndexer(
				this);
		positiveClassExpressionIndexer = new PositiveClassExpressionIndexer(
				this);
		objectPropertyExpressionIndexer = new ObjectPropertyExpressionIndexer(
				this);
	}

	/**
	 * Index the given axiom.
	 */
	public void process(ElkAxiom elkAxiom) {
		elkAxiom.accept(this);
	}

	protected void indexSubClassOfAxiom(ElkClassExpression elkSubClass,
			ElkClassExpression elkSuperClass) {
		IndexedClassExpression subClass = ontologyIndex
				.getIndexedClassExpression(elkSubClass);
		IndexedClassExpression superClass = ontologyIndex
				.getIndexedClassExpression(elkSuperClass);

		if (multiplicity == 1) {
			if (subClass == null)
				subClass = ontologyIndex.createIndexed(elkSubClass);
			if (superClass == null)
				superClass = ontologyIndex.createIndexed(elkSuperClass);
			subClass.addToldSuperClassExpression(superClass);
		}

		if (multiplicity == -1) {
			if (subClass == null || superClass == null
					|| !subClass.removeToldSuperClassExpression(superClass))
				return;
		}

		subClass.accept(classExpressionIndexer);
		superClass.accept(classExpressionIndexer);
		subClass.accept(negativeClassExpressionIndexer);
		superClass.accept(positiveClassExpressionIndexer);
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

	public Void visit(ElkSubObjectPropertyOfAxiom axiom) {

		IndexedObjectProperty subProperty = ontologyIndex
				.getIndexedObjectPropertyExpression(axiom
						.getSubObjectPropertyExpression());
		IndexedObjectProperty superProperty = ontologyIndex
				.getIndexedObjectPropertyExpression(axiom
						.getSuperObjectPropertyExpression());

		if (multiplicity == 1) {
			if (subProperty == null)
				subProperty = ontologyIndex.createIndexed(axiom
						.getSubObjectPropertyExpression());
			if (superProperty == null)
				superProperty = ontologyIndex.createIndexed(axiom
						.getSuperObjectPropertyExpression());
			subProperty.addToldSuperObjectProperty(superProperty);
			superProperty.addToldSubObjectProperty(subProperty);
		}

		if (multiplicity == -1) {
			if (subProperty == null
					|| superProperty == null
					|| !subProperty
							.removeToldSuperObjectProperty(superProperty))
				return null;
			superProperty.removeToldSubObjectProperty(subProperty);
		}

		objectPropertyExpressionIndexer.visit(subProperty);
		objectPropertyExpressionIndexer.visit(superProperty);

		return null;
	}

	public Void visit(ElkTransitiveObjectPropertyAxiom axiom) {
		IndexedObjectProperty iop = ontologyIndex
				.getIndexedObjectPropertyExpression(axiom
						.getObjectPropertyExpression());

		if (multiplicity == 1) {
			if (iop == null)
				iop = ontologyIndex.createIndexed(axiom
						.getObjectPropertyExpression());
			iop.addTransitive();
		}
		if (multiplicity == -1) {
			if (iop == null || !iop.removeTransitive())
				return null;
		}

		objectPropertyExpressionIndexer.visit(iop);
		return null;
	}

	private class EntityIndexer implements IndexedEntityVisitor<Void> {

		public Void visit(IndexedClass indexedClass) {
			return indexedClass.accept(classExpressionIndexer);
		}

		public Void visit(IndexedObjectProperty indexedObjectProperty) {
			objectPropertyExpressionIndexer.visit(indexedObjectProperty);
			return null;
		}
	}

	private EntityIndexer entityIndexer = new EntityIndexer();

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

		ie.accept(entityIndexer);
		return null;
	}

}