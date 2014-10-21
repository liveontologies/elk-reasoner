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
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassEntityVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.OwlThingContextInitRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ContradictionFromOwlNothingRule;

/**
 * Represents all occurrences of an {@link ElkClass} in an ontology.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public class IndexedClass extends IndexedClassEntity {

	/**
	 * The indexed ElkClass
	 */
	protected final ElkClass elkClass;

	/**
	 * This counts how many times this object occurred in the ontology. Because
	 * of declaration axioms, this number might differ from the sum of the
	 * negative and the positive occurrences counts
	 */
	protected int occurrenceNo = 0;

	/**
	 * Creates an object representing the given ElkClass.
	 */
	protected IndexedClass(ElkClass clazz) {
		elkClass = clazz;
	}

	/**
	 * @return The represented ElkClass.
	 */
	public ElkClass getElkClass() {
		return elkClass;
	}

	public <O> O accept(IndexedClassVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(IndexedClassEntityVisitor<O> visitor) {
		return visitor.visit(this);
	}

	boolean updateOccurrenceNo(final ModifiableOntologyIndex index,
			int increment) {

		if (occurrenceNo == 0 && increment > 0) {
			if (!index.addClass(elkClass))
				return false;
			if (elkClass == PredefinedElkClass.OWL_NOTHING
					&& !ContradictionFromOwlNothingRule.addRuleFor(this, index)) {
				// revert the changes
				index.removeClass(elkClass);
				return false;
			}
		}
		occurrenceNo += increment;
		if (occurrenceNo == 0 && increment < 0) {
			if (!index.removeClass(elkClass)) {
				occurrenceNo -= increment;
				return false;
			}
			if (elkClass == PredefinedElkClass.OWL_NOTHING
					&& !ContradictionFromOwlNothingRule.removeRuleFor(this,
							index)) {
				// revert the changes
				index.addClass(elkClass);
				occurrenceNo -= increment;
				return false;
			}
		}
		return true;
	}

	boolean updateNegativeOccurrenceNo(final ModifiableOntologyIndex index,
			int negativeIncrement) {
		if (negativeOccurrenceNo == 0 && negativeIncrement > 0
				&& elkClass == PredefinedElkClass.OWL_THING) {
			if (!OwlThingContextInitRule.addRuleFor(this, index)) {
				return false;
			}
		}
		negativeOccurrenceNo += negativeIncrement;
		if (negativeOccurrenceNo == 0 && negativeIncrement < 0
				&& elkClass == PredefinedElkClass.OWL_THING) {
			if (!OwlThingContextInitRule.removeRuleFor(this, index)) {
				// revert the changes
				negativeOccurrenceNo -= negativeIncrement;
				return false;
			}
		}
		return true;
	}

	@Override
	protected boolean updateOccurrenceNumbers(
			final ModifiableOntologyIndex index, int increment,
			int positiveIncrement, int negativeIncrement) {

		if (!updateOccurrenceNo(index, increment)) {
			return false;
		}
		if (!updateNegativeOccurrenceNo(index, negativeIncrement)) {
			// revert the changes
			updateOccurrenceNo(index, -increment);
			return false;
		}
		positiveOccurrenceNo += positiveIncrement;
		return true;
	}

	@Override
	public String printOccurrenceNumbers() {
		return "[all=" + occurrenceNo + "; pos=" + positiveOccurrenceNo
				+ "; neg=" + +negativeOccurrenceNo + "]";
	}

	@Override
	public void checkOccurrenceNumbers() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace(toString() + " occurences: "
					+ printOccurrenceNumbers());
		if (occurrenceNo < 0 || positiveOccurrenceNo < 0
				|| negativeOccurrenceNo < 0)
			throw new ElkUnexpectedIndexingException(toString()
					+ " has a negative occurrence: " + printOccurrenceNumbers());
	}

	@Override
	public boolean occurs() {
		return occurrenceNo > 0;
	}

	@Override
	public String toStringStructural() {
		return '<' + getElkClass().getIri().getFullIriAsString() + '>';
	}
}
