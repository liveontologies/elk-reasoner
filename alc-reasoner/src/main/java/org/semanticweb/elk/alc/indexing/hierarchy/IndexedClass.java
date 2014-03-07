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
import org.semanticweb.elk.owl.interfaces.ElkClass;

/**
 * Represents all occurrences of an {@link ElkClass} in an ontology.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public class IndexedClass extends IndexedClassExpression {

	/**
	 * The indexed ElkClass
	 */
	private final ElkClass elkClass;

	/**
	 * This counts how many times this object occurred in the ontology. Because
	 * of declaration axioms, this number might differ from the sum of the
	 * negative and the positive occurrences counts
	 */
	protected int occurrenceNo = 0;

	/**
	 * Creates an object representing the given ElkClass.
	 */
	protected IndexedClass(ElkClass elkClass) {
		this.elkClass = elkClass;
	}

	/**
	 * @return The represented ElkClass.
	 */
	public ElkClass getElkClass() {
		return elkClass;
	}

	@Override
	protected void updateOccurrenceNumbers(OntologyIndex index, int increment,
			int positiveIncrement, int negativeIncrement) {

		if (occurrenceNo == 0 && increment > 0) {
			index.addClass(this);
		}

		occurrenceNo += increment;
		positiveOccurrenceNo += positiveIncrement;
		negativeOccurrenceNo += negativeIncrement;

		if (occurrenceNo == 0 && increment < 0) {
			index.removeClass(elkClass);
		}

	}

	@Override
	public boolean occurs() {
		return occurrenceNo > 0;
	}

	@Override
	public String toStringStructural() {
		return '<' + getElkClass().getIri().getFullIriAsString() + '>';
	}

	@Override
	public <O> O accept(IndexedClassExpressionVisitor<O> visitor) {
		return visitor.visit(this);
	}
}
