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

import org.semanticweb.elk.syntax.ElkAxiomVisitor;
import org.semanticweb.elk.syntax.ElkClassExpression;
import org.semanticweb.elk.syntax.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.syntax.ElkFunctionalObjectPropertyAxiom;
import org.semanticweb.elk.syntax.ElkInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.elk.syntax.ElkInverseObjectPropertiesAxiom;
import org.semanticweb.elk.syntax.ElkSubClassOfAxiom;
import org.semanticweb.elk.syntax.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.syntax.ElkTransitiveObjectPropertyAxiom;

/**
 * @author Yevgeny Kazakov
 * 
 */
public class AxiomIndexer implements ElkAxiomVisitor<Void> {

	final OntologyIndexComputation ontologyIndex;
	final NegativeClassExpressionIndexer negativeClassExpressionIndexer;
	final PositiveClassExpressionIndexer positiveClassExpressionIndexer;
	final ObjectPropertyExpressionIndexer objectPropertyExpressionIndexer;

	AxiomIndexer(OntologyIndexComputation ontologyIndex) {
		this.ontologyIndex = ontologyIndex;
		negativeClassExpressionIndexer = new NegativeClassExpressionIndexer(this);
		positiveClassExpressionIndexer = new PositiveClassExpressionIndexer(this);
		objectPropertyExpressionIndexer = new ObjectPropertyExpressionIndexer(this);
	}

	public Void visit(ElkEquivalentClassesAxiom axiom) {
		IndexedClassExpression first = null;
		for (ElkClassExpression c : axiom.getEquivalentClassExpressions()) {
//			implement EquivalentClassesAxiom as two SubClassOfAxioms			

			IndexedClassExpression ice = c.accept(negativeClassExpressionIndexer);
			c.accept(positiveClassExpressionIndexer);
			if (first == null)
				first = ice;
			else {
				ice.addSuperClassExpression(first);
				first.addSuperClassExpression(ice);
			}
		}
		return null;
	}

	public Void visit(ElkSubClassOfAxiom axiom) {

		IndexedClassExpression subClass = axiom.getSubClassExpression().accept(negativeClassExpressionIndexer);
		IndexedClassExpression superClass = axiom.getSuperClassExpression().accept(positiveClassExpressionIndexer);
		subClass.addSuperClassExpression(superClass);

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

		IndexedObjectProperty subProperty = axiom.getSubObjectPropertyExpression().accept(
				objectPropertyExpressionIndexer);
		IndexedObjectProperty superProperty = axiom.getSuperObjectPropertyExpression().accept(
				objectPropertyExpressionIndexer);
		
		subProperty.addSuperObjectProperty(superProperty);
		superProperty.addSubObjectProperty(subProperty);

		return null;
	}

	public Void visit(ElkTransitiveObjectPropertyAxiom axiom) {
		axiom.getObjectPropertyExpression().accept(objectPropertyExpressionIndexer).isTransitive = true;
		
		return null;
	}
}
