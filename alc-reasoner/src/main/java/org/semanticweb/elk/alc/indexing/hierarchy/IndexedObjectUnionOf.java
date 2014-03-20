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
import org.semanticweb.elk.owl.interfaces.ElkObjectUnionOf;
import org.semanticweb.elk.util.collections.ArrayHashSet;

/**
 * Represents all occurrences of an {@link ElkObjectUnionOf} in an ontology.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public class IndexedObjectUnionOf extends IndexedClassExpression {
	/**
	 * The disjunction is binary.
	 */
	private final IndexedClassExpression firstDisjunct_, secondDisjunct_;

	protected IndexedObjectUnionOf(IndexedClassExpression firstDisjunct,
			IndexedClassExpression secondDisjunct) {
		this.firstDisjunct_ = firstDisjunct;
		this.secondDisjunct_ = secondDisjunct;
	}

	public IndexedClassExpression getFirstDisjunct() {
		return firstDisjunct_;
	}

	public IndexedClassExpression getSecondDisjunct() {
		return secondDisjunct_;
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
			addDisjunction(firstDisjunct_, this);
			addDisjunction(secondDisjunct_, this);
		}

		positiveOccurrenceNo += positiveIncrement;
		negativeOccurrenceNo += negativeIncrement;

		if (negativeOccurrenceNo == 0 && negativeIncrement < 0) {
			// no negative occurrences of this expression left
			removeDisjunction(firstDisjunct_, this);
			removeDisjunction(secondDisjunct_, this);
		}

	}

	private void addDisjunction(IndexedClassExpression disjunct,
			IndexedObjectUnionOf disjunction) {
		if (disjunct.negativeDisjunctions_ == null) {
			disjunct.negativeDisjunctions_ = new ArrayHashSet<IndexedObjectUnionOf>(
					4);
		}
		disjunct.negativeDisjunctions_.add(disjunction);
	}

	private void removeDisjunction(IndexedClassExpression disjunct,
			IndexedObjectUnionOf disjunction) {
		disjunct.negativeDisjunctions_.remove(disjunction);
		if (disjunct.negativeDisjunctions_.isEmpty()) {
			disjunct.negativeDisjunctions_ = null;
		}

	}

	@Override
	public String toStringStructural() {
		return "ObjectUnionOf(" + this.firstDisjunct_ + ' '
				+ this.secondDisjunct_ + ')';
	}

}
