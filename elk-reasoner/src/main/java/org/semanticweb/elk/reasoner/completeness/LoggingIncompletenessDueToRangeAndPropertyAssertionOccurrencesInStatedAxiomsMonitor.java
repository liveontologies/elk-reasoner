/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2017 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.completeness;

import java.util.Collection;

import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.reasoner.indexing.model.Occurrence;
import org.semanticweb.elk.reasoner.indexing.model.OccurrenceStore;
import org.semanticweb.elk.util.logging.LogLevel;
import org.semanticweb.elk.util.logging.LoggerWrap;
import org.slf4j.Logger;

/**
 * Notifies about incompleteness caused by ObjectPropertyAssertion-s and
 * ObjectPropertyRange-s occurring in the ontology together.
 * 
 * @author Peter Skocovsky
 */
class LoggingIncompletenessDueToRangeAndPropertyAssertionOccurrencesInStatedAxiomsMonitor
		extends IncompletenessDueToOccurrencesMonitor {

	public static final int AT_MOST_N_OCCURRENCES_IN_MESSAGE = 3;

	private final Logger logger_;
	private final LogLevel logLevel_;

	public LoggingIncompletenessDueToRangeAndPropertyAssertionOccurrencesInStatedAxiomsMonitor(
			final OccurrenceStore occurrencesInStatedAxioms,
			final Logger logger, final LogLevel logLevel) {
		super(occurrencesInStatedAxioms);
		this.logger_ = logger;
		this.logLevel_ = logLevel;
	}

	@Override
	public boolean isIncomplete() {
		final Collection<? extends ElkObject> rangeOccursIn = occurrences
				.occursIn(Occurrence.OCCURRENCE_OF_OBJECT_PROPERTY_RANGE);
		final Collection<? extends ElkObject> assertionOccurrsIn = occurrences
				.occursIn(Occurrence.OCCURRENCE_OF_OBJECT_PROPERTY_ASSERTION);
		if (rangeOccursIn != null && !rangeOccursIn.isEmpty()
				&& assertionOccurrsIn != null
				&& !assertionOccurrsIn.isEmpty()) {

			if (LoggerWrap.isEnabledFor(logger_, logLevel_)) {
				final StringBuilder message = new StringBuilder(
						"ELK supports ObjectPropertyAssertion and ObjectPropertyRange in the same ontology only partially.");
				message.append("\n");
				message.append("occurrences of ObjectPropertyAssertion:");
				printOccurrences(assertionOccurrsIn, message);
				message.append("\n");
				message.append("occurrences of ObjectPropertyRange:");
				printOccurrences(rangeOccursIn, message);

				LoggerWrap.log(logger_, logLevel_,
						"ObjectPropertyAssertionWithObjectPropertyRange",
						message.toString());
			}

			return true;
		}
		// else
		return false;
	}

	private StringBuilder printOccurrences(
			final Collection<? extends ElkObject> occursIn,
			final StringBuilder message) {
		int i = 0;
		for (final ElkObject elkObject : occursIn) {
			if (i >= AT_MOST_N_OCCURRENCES_IN_MESSAGE) {
				message.append("\n\t...");
				break;
			}
			message.append("\n\t")
					.append(OwlFunctionalStylePrinter.toString(elkObject));
			i++;
		}
		return message;
	}

}
