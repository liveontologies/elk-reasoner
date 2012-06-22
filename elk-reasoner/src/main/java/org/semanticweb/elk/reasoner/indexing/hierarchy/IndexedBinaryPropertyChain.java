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

import java.util.List;

import org.semanticweb.elk.reasoner.indexing.visitors.IndexedBinaryPropertyChainVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyChainVisitor;

/**
 * Represents a complex ElkSubObjectPropertyOfAxiom. The chain consists of two
 * components: an IndexedObjectProperty on the left and an
 * IndexedPropertyExpression on the right. This reflects the fact that property
 * inclusions are binarized during index constructions. The auxiliary
 * IndexedBinaryPropertyChains may not represent any ElkObject in the ontology.
 * 
 * @author Frantisek Simancik
 * 
 */

public class IndexedBinaryPropertyChain extends IndexedPropertyChain {

	protected final IndexedObjectProperty leftProperty;
	protected final IndexedPropertyChain rightProperty;

	/**
	 * Used for creating auxiliary inclusions during binarization.
	 * 
	 * @param leftProperty
	 * @param rightProperty
	 */
	protected IndexedBinaryPropertyChain(IndexedObjectProperty leftProperty,
			IndexedPropertyChain rightProperty) {

		this.leftProperty = leftProperty;
		this.rightProperty = rightProperty;
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
	public IndexedPropertyChain getRightProperty() {
		return rightProperty;
	}

	@Override
	protected void updateOccurrenceNumber(int increment) {

		if (occurrenceNo == 0 && increment > 0) {
			// first occurrence of this expression
			rightProperty.addRightChain(this);
		}

		occurrenceNo += increment;

		if (occurrenceNo == 0 && increment < 0) {
			// no occurrences of this conjunction left
			rightProperty.removeRightChain(this);
		}

	}

	@Override
	public <O> O accept(IndexedPropertyChainVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public <O> O accept(IndexedBinaryPropertyChainVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public List<IndexedPropertyChain> getToldSubProperties() {
		return null;
	}

	@Override
	public String toString() {
		return "ObjectPropertyChain(" + this.leftProperty + ' '
				+ this.rightProperty + ')';
	}
}
