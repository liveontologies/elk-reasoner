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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.semanticweb.elk.reasoner.indexing.model.OccurrenceStore;
import org.semanticweb.elk.reasoner.stages.AbstractReasonerState;
import org.semanticweb.elk.util.logging.LogLevel;
import org.semanticweb.elk.util.logging.LoggerWrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IncompletenessManager {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(AbstractReasonerState.class);

	private final IncompletenessMonitor incompletenessDueToStatedAxiomsMonitor_;

	/**
	 * @param occurrencesInStatedAxiomsStore
	 *            The store of occurrences in the loaded ontology.
	 */
	public IncompletenessManager(
			final OccurrenceStore occurrencesInStatedAxiomsStore) {
		this.incompletenessDueToStatedAxiomsMonitor_ = new DelegatingIncompletenessMonitor(
				new LoggingIncompletenessDueToNegativeOccurrenceOfObjectComplementOfMonitor(
						occurrencesInStatedAxiomsStore, LOGGER_, LogLevel.INFO,
						IncompletenessDueToInStatedAxiomsMessageProvider.INSTANCE),
				new LoggingIncompletenessDueToNegativeOccurrenceOfTopObjectPropertyMonitor(
						occurrencesInStatedAxiomsStore, LOGGER_, LogLevel.INFO,
						IncompletenessDueToInStatedAxiomsMessageProvider.INSTANCE),
				new LoggingIncompletenessDueToOccurrenceOfDataHasValueMonitor(
						occurrencesInStatedAxiomsStore, LOGGER_, LogLevel.INFO,
						IncompletenessDueToInStatedAxiomsMessageProvider.INSTANCE),
				new LoggingIncompletenessDueToOccurrenceOfDisjointUnionMonitor(
						occurrencesInStatedAxiomsStore, LOGGER_, LogLevel.INFO,
						IncompletenessDueToInStatedAxiomsMessageProvider.INSTANCE),
				new LoggingIncompletenessDueToOccurrenceOfNominalMonitor(
						occurrencesInStatedAxiomsStore, LOGGER_, LogLevel.INFO,
						IncompletenessDueToInStatedAxiomsMessageProvider.INSTANCE),
				new LoggingIncompletenessDueToOccurrenceOfUnsupportedExpressionMonitor(
						occurrencesInStatedAxiomsStore, LOGGER_, LogLevel.INFO,
						IncompletenessDueToInStatedAxiomsMessageProvider.INSTANCE),
				new LoggingIncompletenessDueToPositiveOccurrenceOfBottomObjectPropertyMonitor(
						occurrencesInStatedAxiomsStore, LOGGER_, LogLevel.INFO,
						IncompletenessDueToInStatedAxiomsMessageProvider.INSTANCE),
				new LoggingIncompletenessDueToPositiveOccurrenceOfObjectUnionOfMonitor(
						occurrencesInStatedAxiomsStore, LOGGER_, LogLevel.INFO,
						IncompletenessDueToInStatedAxiomsMessageProvider.INSTANCE),
				new LoggingIncompletenessDueToRangeAndPropertyAssertionOccurrencesInStatedAxiomsMonitor(
						occurrencesInStatedAxiomsStore, LOGGER_,
						LogLevel.INFO));
	}

	/**
	 * @return The {@link IncompletenessMonitor} that monitors incompleteness
	 *         that follows from the loaded ontology.
	 */
	public IncompletenessMonitor getIncompletenessDueToStatedAxiomsMonitor() {
		return incompletenessDueToStatedAxiomsMonitor_;
	}

	/**
	 * @param occurrencesInQuery
	 *            Occurrences in a query that is being checked for completeness.
	 * @param messageProvider
	 * @return The {@link IncompletenessMonitor} that monitors incompleteness
	 *         that follows from the provided occurrences in a class expression
	 *         or an entailment query.
	 */
	private IncompletenessMonitor getIncompletenessDueToOccurrencesInQueryMonitor(
			final OccurrenceStore occurrencesInQuery,
			final IncompletenessMessageProvider messageProvider) {
		return new DelegatingIncompletenessMonitor(
				new LoggingIncompletenessDueToNegativeOccurrenceOfObjectComplementOfMonitor(
						occurrencesInQuery, LOGGER_, LogLevel.INFO,
						messageProvider),
				new LoggingIncompletenessDueToNegativeOccurrenceOfTopObjectPropertyMonitor(
						occurrencesInQuery, LOGGER_, LogLevel.INFO,
						messageProvider),
				new LoggingIncompletenessDueToOccurrenceOfDataHasValueMonitor(
						occurrencesInQuery, LOGGER_, LogLevel.INFO,
						messageProvider),
				new LoggingIncompletenessDueToOccurrenceOfNominalMonitor(
						occurrencesInQuery, LOGGER_, LogLevel.INFO,
						messageProvider),
				new LoggingIncompletenessDueToOccurrenceOfUnsupportedExpressionMonitor(
						occurrencesInQuery, LOGGER_, LogLevel.INFO,
						messageProvider),
				new LoggingIncompletenessDueToPositiveOccurrenceOfBottomObjectPropertyMonitor(
						occurrencesInQuery, LOGGER_, LogLevel.INFO,
						messageProvider),
				new LoggingIncompletenessDueToPositiveOccurrenceOfObjectUnionOfMonitor(
						occurrencesInQuery, LOGGER_, LogLevel.INFO,
						messageProvider));
	}

	/**
	 * Combines the provided partial incompleteness monitors into the top-level
	 * monitor for reasoning tasks.
	 * 
	 * @param additionalMonitors
	 *            The partial monitors.
	 * @return The top-level monitor for reasoning tasks.
	 */
	public IncompletenessMonitor getReasonerIncompletenessMonitor(
			final IncompletenessMonitor... additionalMonitors) {
		final List<IncompletenessMonitor> monitors = new ArrayList<IncompletenessMonitor>(
				additionalMonitors.length + 1);
		monitors.add(getIncompletenessDueToStatedAxiomsMonitor());
		monitors.addAll(Arrays.asList(additionalMonitors));
		return new DelegatingIncompletenessMonitor(monitors) {
			@Override
			public boolean isIncomplete() {
				final boolean result = super.isIncomplete();
				if (result) {
					LoggerWrap.log(LOGGER_, LogLevel.WARN, "Incompleteness",
							"Reasoning may be incomplete! See log level INFO for more details.");
				}
				return result;
			}
		};
	}

	public IncompletenessMonitor getIncompletenessMonitorForClassification() {
		return getReasonerIncompletenessMonitor();
	}

	/**
	 * @param occurrencesInQuery
	 *            Occurrences in a class expression query that is being checked
	 *            for completeness.
	 * @return
	 */
	public IncompletenessMonitor getIncompletenessMonitorForClassExpressionQuery(
			final OccurrenceStore occurrencesInQuery) {
		return getReasonerIncompletenessMonitor(
				getIncompletenessDueToOccurrencesInQueryMonitor(
						occurrencesInQuery,
						IncompletenessDueToOccurrencesInClassExpressionQueryMessageProvider.INSTANCE));
	}

	/**
	 * @param occurrencesInQuery
	 *            Occurrences in a entailment query that is being checked for
	 *            completeness.
	 * @return
	 */
	public IncompletenessMonitor getIncompletenessMonitorForEntailmentQuery(
			final OccurrenceStore occurrencesInQuery) {
		return getReasonerIncompletenessMonitor(
				getIncompletenessDueToOccurrencesInQueryMonitor(
						occurrencesInQuery,
						IncompletenessDueToOccurrencesInEntailmentQueryMessageProvider.INSTANCE));
	}

}
