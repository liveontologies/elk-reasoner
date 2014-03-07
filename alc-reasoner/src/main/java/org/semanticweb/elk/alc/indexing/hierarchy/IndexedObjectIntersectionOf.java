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
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.util.collections.ArrayHashMap;

/**
 * Represents all occurrences of an {@link ElkObjectIntersectionOf} in an
 * ontology.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public class IndexedObjectIntersectionOf extends IndexedClassExpression {

	/**
	 * The conjunction has only two conjuncts. To ensure uniqueness of a
	 * conjunction for the conjuncts, the conjuncts are sorted according to the
	 * comparator of {@link IndexedClassExpression}. This is required for
	 * correct construction of {@link ObjectIntersectionFromConjunctRule}
	 * because conjunctions (A & B) and (B & A) result in the same rules.
	 */
	private final IndexedClassExpression firstConjunct_, secondConjunct_;

	protected IndexedObjectIntersectionOf(IndexedClassExpression conjunctA,
			IndexedClassExpression conjunctB) {

		if (conjunctA.compareTo(conjunctB) < 0) {
			this.firstConjunct_ = conjunctA;
			this.secondConjunct_ = conjunctB;
		} else {
			this.firstConjunct_ = conjunctB;
			this.secondConjunct_ = conjunctA;
		}
	}

	public IndexedClassExpression getFirstConjunct() {
		return firstConjunct_;
	}

	public IndexedClassExpression getSecondConjunct() {
		return secondConjunct_;
	}

	@Override
	public <O> O accept(IndexedClassExpressionVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	protected void updateOccurrenceNumbers(OntologyIndex index, int increment,
			int positiveIncrement, int negativeIncrement) {

		if (negativeOccurrenceNo == 0 && negativeIncrement > 0) {
			addConjunctionByConjunct(firstConjunct_, secondConjunct_, this);
			addConjunctionByConjunct(secondConjunct_, firstConjunct_, this);
		}

		positiveOccurrenceNo += positiveIncrement;
		negativeOccurrenceNo += negativeIncrement;

		if (negativeOccurrenceNo == 0 && negativeIncrement < 0) {
			removeConjunctionByConjunct(firstConjunct_, secondConjunct_);
			removeConjunctionByConjunct(secondConjunct_, firstConjunct_);
		}
	}

	private void addConjunctionByConjunct(IndexedClassExpression mainConjunct,
			IndexedClassExpression sideConjunct,
			IndexedObjectIntersectionOf conjunction) {
		if (mainConjunct.conjunctionsByConjunct_ == null) {
			mainConjunct.conjunctionsByConjunct_ = new ArrayHashMap<IndexedClassExpression, IndexedObjectIntersectionOf>(
					16);
		}
		mainConjunct.conjunctionsByConjunct_.put(sideConjunct, conjunction);
	}

	private void removeConjunctionByConjunct(
			IndexedClassExpression mainConjunct,
			IndexedClassExpression sideConjunct) {
		mainConjunct.conjunctionsByConjunct_.remove(sideConjunct);
		if (mainConjunct.conjunctionsByConjunct_.isEmpty()) {
			mainConjunct.conjunctionsByConjunct_ = null;
		}
	}

	@Override
	public String toStringStructural() {
		return "ObjectIntersectionOf(" + this.firstConjunct_ + ' '
				+ this.secondConjunct_ + ')';
	}

}
