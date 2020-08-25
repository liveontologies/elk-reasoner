package org.semanticweb.elk.reasoner.completeness;

/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2020 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.reasoner.query.ElkQueryException;
import org.semanticweb.elk.reasoner.query.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Incompleteness {

	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(Incompleteness.class);

	public static IncompletenessMonitor combine(
			IncompletenessMonitor... monitors) {
		return new CombinedIncompletenessMonitor(monitors);
	}

	static public <R> R getValue(ReasoningResult<R> result) {
		R value = result.getValue();
		IncompletenessMonitor monitor = result.geIncompletenessMonitor();
		if (monitor.hasNewExplanation()) {
			monitor.explainIncompleteness(LOGGER_);
		}
		return value;
	}

	static public boolean getValue(QueryResult result)
			throws ElkQueryException {
		if (result.entailmentProved()) {
			return true;
		}
		// else check for incompleteness
		IncompletenessMonitor queryMonitor = result.getIncompletenessMonitor();
		if (queryMonitor.hasNewExplanation()) {
			LOGGER_.warn(
					"Cannot determine entailment of the query {}! See INFO for more details.",
					result.getQuery());
			queryMonitor.explainIncompleteness(LOGGER_);
		}
		return false;
	}

	private static boolean checkQueryReasoningCompleteness(ElkObject query,
			OntologySatisfiabilityIncompletenessMonitor ontologyMonitor,
			OccurrenceManager occurrencesInQuery, Logger logger) {
		if (!ontologyMonitor.checkCompleteness(logger)) {
			// general ontology reasoning could be already incomplete
			return false;
		}
		// else
		IncompletenessMonitor queryMonitor = new QueryIncompletenessMonitor(
				ontologyMonitor, occurrencesInQuery);
		if (queryMonitor.hasNewExplanation()) {
			logger.warn(
					"Reasoning results for the query {} may be incomplete! See INFO for more details.",
					query);
			queryMonitor.explainIncompleteness(logger);
		}
		return !queryMonitor.isIncompletenessDetected();
	}

	/**
	 * Checks reasoning completeness for the given query and, if necessary,
	 * prints messages about incompleteness using the provided logger.
	 * 
	 * @param query
	 *            the query for which to check incompleteness
	 * @param ontologyMonitor
	 *            the main {@link OntologySatisfiabilityIncompletenessMonitor}
	 * @param occurrencesInQuery
	 *            occurrences of {@link Feature}s in the query
	 * @param logger
	 * @return {@code true} if the reasoning results are guaranteed to be
	 *         complete and {@code false} otherwise
	 */
	public static boolean checkQueryReasoningCompleteness(
			ElkClassExpression query,
			OntologySatisfiabilityIncompletenessMonitor ontologyMonitor,
			OccurrenceCounter occurrencesInQuery, Logger logger) {
		return checkQueryReasoningCompleteness((ElkObject) query,
				ontologyMonitor, new OccurrencesInClassExpressionQuery(query,
						occurrencesInQuery),
				logger);
	}

}
