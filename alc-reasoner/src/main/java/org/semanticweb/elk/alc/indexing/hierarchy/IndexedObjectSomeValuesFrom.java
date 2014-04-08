/*
 * #%L
 * elk-reasoner
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
package org.semanticweb.elk.alc.indexing.hierarchy;

import org.semanticweb.elk.alc.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.util.collections.ArrayHashSet;

/**
 * Represents all occurrences of an {@link ElkObjectSomeValuesFrom} in an
 * ontology.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public class IndexedObjectSomeValuesFrom extends IndexedClassExpression {

	private final IndexedObjectProperty property_;

	private final IndexedClassExpression filler_;

	IndexedObjectSomeValuesFrom(IndexedObjectProperty indexedObjectProperty,
			IndexedClassExpression filler) {
		this.property_ = indexedObjectProperty;
		this.filler_ = filler;
	}

	/**
	 * @return The indexed object property comprising this ObjectSomeValuesFrom.
	 */
	public IndexedObjectProperty getRelation() {
		return property_;
	}

	/**
	 * @return The indexed class expression comprising this
	 *         ObjectSomeValuesFrom.
	 */
	public IndexedClassExpression getFiller() {
		return filler_;
	}

	@Override
	public <O> O accept(IndexedClassExpressionVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	protected void updateOccurrenceNumbers(OntologyIndex index, int increment,
			int positiveIncrement, int negativeIncrement) {

		if (negativeOccurrenceNo == 0 && negativeIncrement > 0) {
			// first negative occurrence of this expression
			if (filler_.negativeExistentials_ == null) {
				filler_.negativeExistentials_ = new ArrayHashSet<IndexedObjectSomeValuesFrom>(
						16);
			}
			filler_.negativeExistentials_.add(this);
		}

		positiveOccurrenceNo += positiveIncrement;
		negativeOccurrenceNo += negativeIncrement;

		if (negativeOccurrenceNo == 0 && negativeIncrement < 0) {
			// no negative occurrences of this expression left
			filler_.negativeExistentials_.remove(this);
			if (filler_.negativeExistentials_.isEmpty()) {
				filler_.negativeExistentials_ = null;
			}
		}

	}

	@Override
	public String toStringStructural() {
		return "ObjectSomeValuesFrom(" + this.property_ + ' ' + this.filler_
				+ ')';
	}

}
