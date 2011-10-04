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

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyCompositionVisitable;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyCompositionVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedSubPropertyExpressionVisitor;

/**
 * Represents a complex ElkSubObjectPropertyOfAxiom. The chain consists of
 * two components: an IndexedObjectProperty on the left and an
 * IndexedPropertyExpression on the right. This reflects the fact that property
 * inclusions are binarized during index constructions. The auxiliary 
 * inclusions may not represent any ElkObject in the ontology. 
 * 
 * @author Frantisek Simancik
 *
 */
/**
 * @author Frantisek Simancik
 *
 */
/**
 * @author Frantisek Simancik
 *
 */
/**
 * @author Frantisek Simancik
 * 
 */
public class IndexedPropertyComposition extends IndexedSubPropertyExpression
		implements Iterable<IndexedPropertyComposition>,
		IndexedPropertyCompositionVisitable {
	/**
	 * The SubObjectPropertyOfAxiom that is represented by this object. Null if
	 * this object corresponds to an auxiliary inclusion created during
	 * binarization.
	 */
	protected final ElkSubObjectPropertyOfAxiom representative;

	protected final IndexedObjectProperty leftProperty;
	protected final IndexedSubPropertyExpression rightProperty;
	protected final IndexedSubPropertyExpression superProperty;

	/**
	 * Safety for left-linear application.
	 */
	protected boolean isSafe;

	/**
	 * Used for creating auxiliary inclusions during binarization.
	 * 
	 * @param leftProperty
	 * @param rightProperty
	 */
	protected IndexedPropertyComposition(IndexedObjectProperty leftProperty,
			IndexedSubPropertyExpression rightProperty) {

		this.representative = null;
		this.leftProperty = leftProperty;
		this.rightProperty = rightProperty;
		this.superProperty = this;
	}

	/**
	 * Used for creating a property inclusion that represents a complex
	 * ElkSubObjectPropertyOfAxiom.
	 * 
	 * @param leftProperty
	 * @param rightProperty
	 * @param superProperty
	 * @param axiom
	 */
	protected IndexedPropertyComposition(IndexedObjectProperty leftProperty,
			IndexedSubPropertyExpression rightProperty,
			IndexedObjectProperty superProperty,
			ElkSubObjectPropertyOfAxiom axiom) {
		this.representative = axiom;
		this.leftProperty = leftProperty;
		this.rightProperty = rightProperty;
		this.superProperty = superProperty;
	}

	/**
	 * @return The left component of this (binary) complex property inclusion
	 *         axiom.
	 */
	public IndexedObjectProperty getLeftProperty() {
		return leftProperty;
	}

	/**
	 * @return The right component of this (binary) complex property inclusion
	 *         axiom.
	 */
	public IndexedSubPropertyExpression getRightProperty() {
		return rightProperty;
	}

	public boolean isAuxiliary() {
		return representative == null;
	}

	/**
	 * @return The result of the composition.
	 */
	public IndexedSubPropertyExpression getSuperProperty() {
		return superProperty;
	}

	public boolean isSafe() {
		return isSafe;
	}

	public void setSafe(boolean isSafe) {
		this.isSafe = isSafe;
	}

	@Override
	public <O> O accept(IndexedSubPropertyExpressionVisitor<O> visitor) {
		return visitor.visit(this);
	}

	/**
	 * Iterator through all auxiliary intermediate property compositions of this
	 * object.
	 */
	public Iterator<IndexedPropertyComposition> iterator() {
		return new Iterator<IndexedPropertyComposition>() {

			private IndexedSubPropertyExpression i = IndexedPropertyComposition.this;

			public boolean hasNext() {
				return (i instanceof IndexedPropertyComposition);
			}

			public IndexedPropertyComposition next() {
				if (i instanceof IndexedPropertyComposition) {
					IndexedPropertyComposition result = (IndexedPropertyComposition) i;
					i = result.rightProperty;
					return result;
				}
				throw new NoSuchElementException();

			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public <O> O accept(IndexedPropertyCompositionVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
