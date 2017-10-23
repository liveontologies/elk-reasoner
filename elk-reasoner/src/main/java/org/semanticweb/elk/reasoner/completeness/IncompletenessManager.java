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
import org.semanticweb.elk.util.logging.LogLevel;
import org.semanticweb.elk.util.logging.LoggerWrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class IncompletenessManager {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(IncompletenessManager.class);

	private static final Marker MARKER_ = MarkerFactory
			.getMarker("Incompleteness");

	private final IncompletenessMonitor incompletenessDueToStatedAxiomsMonitor_;

	/**
	 * @param occurrencesInStatedAxiomsStore
	 *            The store of occurrences in the loaded ontology.
	 */
	public IncompletenessManager(
			final OccurrenceStore occurrencesInStatedAxiomsStore) {
		this.incompletenessDueToStatedAxiomsMonitor_ = new DelegatingIncompletenessMonitor(
				new IncompletenessDueToNegativeOccurrenceOfObjectComplementOfMonitor(
						occurrencesInStatedAxiomsStore, LogLevel.INFO,
						IncompletenessDueToInStatedAxiomsMessageProvider.INSTANCE),
				new IncompletenessDueToNegativeOccurrenceOfTopObjectPropertyMonitor(
						occurrencesInStatedAxiomsStore, LogLevel.INFO,
						IncompletenessDueToInStatedAxiomsMessageProvider.INSTANCE),
				new IncompletenessDueToOccurrenceOfDataHasValueMonitor(
						occurrencesInStatedAxiomsStore, LogLevel.INFO,
						IncompletenessDueToInStatedAxiomsMessageProvider.INSTANCE),
				new IncompletenessDueToOccurrenceOfDisjointUnionMonitor(
						occurrencesInStatedAxiomsStore, LogLevel.INFO,
						IncompletenessDueToInStatedAxiomsMessageProvider.INSTANCE),
				new IncompletenessDueToOccurrenceOfNominalMonitor(
						occurrencesInStatedAxiomsStore, LogLevel.INFO,
						IncompletenessDueToInStatedAxiomsMessageProvider.INSTANCE),
				new IncompletenessDueToOccurrenceOfUnsupportedExpressionMonitor(
						occurrencesInStatedAxiomsStore, LogLevel.INFO,
						IncompletenessDueToInStatedAxiomsMessageProvider.INSTANCE),
				new IncompletenessDueToPositiveOccurrenceOfBottomObjectPropertyMonitor(
						occurrencesInStatedAxiomsStore, LogLevel.INFO,
						IncompletenessDueToInStatedAxiomsMessageProvider.INSTANCE),
				new IncompletenessDueToPositiveOccurrenceOfObjectUnionOfMonitor(
						occurrencesInStatedAxiomsStore, LogLevel.INFO,
						IncompletenessDueToInStatedAxiomsMessageProvider.INSTANCE),
				new IncompletenessDueToRangeAndPropertyAssertionOccurrencesInStatedAxiomsMonitor(
						occurrencesInStatedAxiomsStore, LogLevel.INFO));
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
				new IncompletenessDueToNegativeOccurrenceOfObjectComplementOfMonitor(
						occurrencesInQuery, LogLevel.INFO, messageProvider),
				new IncompletenessDueToNegativeOccurrenceOfTopObjectPropertyMonitor(
						occurrencesInQuery, LogLevel.INFO, messageProvider),
				new IncompletenessDueToOccurrenceOfDataHasValueMonitor(
						occurrencesInQuery, LogLevel.INFO, messageProvider),
				new IncompletenessDueToOccurrenceOfNominalMonitor(
						occurrencesInQuery, LogLevel.INFO, messageProvider),
				new IncompletenessDueToOccurrenceOfUnsupportedExpressionMonitor(
						occurrencesInQuery, LogLevel.INFO, messageProvider),
				new IncompletenessDueToPositiveOccurrenceOfBottomObjectPropertyMonitor(
						occurrencesInQuery, LogLevel.INFO, messageProvider),
				new IncompletenessDueToPositiveOccurrenceOfObjectUnionOfMonitor(
						occurrencesInQuery, LogLevel.INFO, messageProvider));
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
			public boolean logNewIncompletenessReasons(final Logger logger) {
				final boolean result = super.logNewIncompletenessReasons(
						logger);
				if (result) {
					LoggerWrap.log(logger, LogLevel.WARN, MARKER_,
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

	public void log(final IncompletenessMonitor monitor) {
		monitor.logNewIncompletenessReasons(LOGGER_);
	}

}
