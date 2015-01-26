package org.semanticweb.elk.reasoner.indexing.modifiable;

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

import org.semanticweb.elk.owl.predefined.ElkPolarity;

/**
 * The elementary change in the occurrences of a {@link ModifiableIndexedObject}
 * . Consists of three parts: {@link #totalIncrement},
 * {@link #positiveIncrement}, {@link #negativeIncrement} that describe
 * respectively how many occurrences, positive occurrences, or negative
 * occurrences of an object have been added / removed. If the value is positive,
 * the respective number of occurrences have been added, otherwise removed.
 *
 * @see ElkPolarity
 * @see ModifiableIndexedObject#updateOccurrenceNumbers
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public class OccurrenceIncrement {

	public final int totalIncrement, positiveIncrement, negativeIncrement;

	public OccurrenceIncrement(int increment, int positiveIncrement,
			int negativeIncrement) {
		this.totalIncrement = increment;
		this.positiveIncrement = positiveIncrement;
		this.negativeIncrement = negativeIncrement;
	}

	/**
	 * @param increment
	 *            the number of neutral occurrences changed
	 * @return the {@link OccurrenceIncrement} that changes only the number of
	 *         total occurrences, but not the number of positive or negative
	 *         occurrences
	 */
	public static OccurrenceIncrement getNeutralIncrement(int increment) {
		return new OccurrenceIncrement(increment, 0, 0);
	}

	/**
	 * @param increment
	 *            the number of positive occurrences changed
	 * @return the {@link OccurrenceIncrement} for changing only the number of
	 *         positive occurrences (and, consequently, total occurrences)
	 */
	public static OccurrenceIncrement getPositiveIncrement(int increment) {
		return new OccurrenceIncrement(increment, increment, 0);
	}

	/**
	 * @param increment
	 *            the number of negative occurrences changed
	 * @return the {@link OccurrenceIncrement} for changing only the number of
	 *         negative occurrences (and, consequently, total occurrences)
	 */
	public static OccurrenceIncrement getNegativeIncrement(int increment) {
		return new OccurrenceIncrement(increment, 0, increment);
	}

	/**
	 * @param increment
	 *            the number of dual occurrences changed
	 * @return the {@link OccurrenceIncrement} for changing all occurrence
	 *         counts (total, negative, and positive)
	 */
	public static OccurrenceIncrement getDualIncrement(int increment) {
		return new OccurrenceIncrement(increment, increment, increment);
	}

}
