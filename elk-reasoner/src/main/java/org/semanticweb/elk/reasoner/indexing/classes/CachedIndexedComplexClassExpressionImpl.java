package org.semanticweb.elk.reasoner.indexing.classes;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.conversion.ElkUnexpectedIndexingException;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedComplexClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.model.OccurrenceIncrement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements {@link CachedIndexedComplexClassExpression}.
 * 
 * @author "Yevgeny Kazakov"
 *
 * @param <T>
 *            the type of objects this object can be structurally equal to
 */
abstract class CachedIndexedComplexClassExpressionImpl<T extends CachedIndexedComplexClassExpression<T>>
		extends
		CachedIndexedClassExpressionImpl<T, CachedIndexedComplexClassExpression<?>>
		implements CachedIndexedComplexClassExpression<T> {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(CachedIndexedComplexClassExpressionImpl.class);

	/**
	 * This counts how often this object occurred positively. Some indexing
	 * operations are only needed when encountering objects positively for the
	 * first time.
	 */
	int positiveOccurrenceNo = 0;

	/**
	 * This counts how often this object occurred negatively. Some indexing
	 * operations are only needed when encountering objects negatively for the
	 * first time.
	 */
	int negativeOccurrenceNo = 0;

	CachedIndexedComplexClassExpressionImpl(int structuralHash) {
		super(structuralHash);
	}

	/**
	 * This method should always return true apart from intermediate steps
	 * during the indexing.
	 * 
	 * @return true if the represented class expression occurs in the ontology
	 */
	@Override
	public final boolean occurs() {
		return positiveOccurrenceNo > 0 || negativeOccurrenceNo > 0;
	}

	/**
	 * @return the string representation for the occurrence numbers of this
	 *         {@link IndexedClassExpression}
	 */
	@Override
	public final String printOccurrenceNumbers() {
		return "[pos=" + positiveOccurrenceNo + "; neg="
				+ +negativeOccurrenceNo + "]";
	}

	/**
	 * verifies that occurrence numbers are not negative
	 */
	public final void checkOccurrenceNumbers() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace(toString() + " occurences: "
					+ printOccurrenceNumbers());
		if (positiveOccurrenceNo < 0 || negativeOccurrenceNo < 0)
			throw new ElkUnexpectedIndexingException(toString()
					+ " has a negative occurrence: " + printOccurrenceNumbers());
	}

	final boolean updateAndCheckOccurrenceNumbers(
			ModifiableOntologyIndex index, OccurrenceIncrement increment) {
		if (!updateOccurrenceNumbers(index, increment)) {
			LOGGER_.trace("{}: cannot index!", this);
			return false;
		}
		checkOccurrenceNumbers();
		return true;
	}

}
