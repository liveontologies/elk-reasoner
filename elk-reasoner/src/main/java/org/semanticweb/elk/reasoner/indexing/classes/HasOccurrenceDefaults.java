package org.semanticweb.elk.reasoner.indexing.classes;

/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2021 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.RevertibleAction;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkUnexpectedIndexingException;
import org.semanticweb.elk.reasoner.indexing.model.HasOccurrence;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.model.OccurrenceIncrement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of the methods from {@link HasOccurrence} using an
 * occurrence counter
 * 
 * @author Yevgeny Kazakov
 *
 */
interface HasOccurrenceDefaults extends HasOccurrence {

	// logger for events
	static final Logger LOGGER_ = LoggerFactory
			.getLogger(HasOccurrenceDefaults.class);

	/**
	 * @return the total number of occurrences of this object in the ontology
	 */
	int getTotalOccurrenceNumber();

	/**
	 * Change the total number of occurrences of this object in the ontology by
	 * the given increment
	 * 
	 * @param increment
	 *            the value by which the number of occurrences should change
	 */
	void updateTotalOccurrenceNumber(int increment);

	@Override
	default boolean occurs() {
		return getTotalOccurrenceNumber() > 0;
	}

	@Override
	default String printOccurrenceNumbers() {
		return "all=" + getTotalOccurrenceNumber();
	}

	/**
	 * verifies that occurrence numbers are not negative
	 */
	default void checkOccurrenceNumbers() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace(
					toString() + " occurences: " + printOccurrenceNumbers());
		if (getTotalOccurrenceNumber() < 0)
			throw new ElkUnexpectedIndexingException(
					toString() + " has a negative total occurrence: "
							+ printOccurrenceNumbers());
	}

	default RevertibleAction getIndexingAction(ModifiableOntologyIndex index,
			OccurrenceIncrement increment) {
		return RevertibleAction.create(() -> {
			updateTotalOccurrenceNumber(increment.totalIncrement);
			checkOccurrenceNumbers();
			return true;
		}, () -> {
			updateTotalOccurrenceNumber(-increment.totalIncrement);
		});
	}

}
