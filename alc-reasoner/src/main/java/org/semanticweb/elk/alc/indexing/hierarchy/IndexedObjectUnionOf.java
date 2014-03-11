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

		// no need to index anything
		positiveOccurrenceNo += positiveIncrement;
		negativeOccurrenceNo += negativeIncrement;

	}

	@Override
	public String toStringStructural() {
		return "ObjectUnionOf(" + this.firstDisjunct_ + ' '
				+ this.secondDisjunct_ + ')';
	}

}
