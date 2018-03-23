package org.semanticweb.elk.reasoner.completeness;

/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2018 Department of Computer Science, University of Oxford
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

import org.slf4j.Logger;

/**
 * A monitor for checking incompleteness of general ontology reasoning tasks,
 * such as checking satisfiability, computing classification, realization.
 * 
 * @author Yevgeny Kazakov
 */
public class OntologyIncompletenessMonitor extends TopIncompletenessMonitor {

	private final OccurrencesInOntology occurrences_;

	public OntologyIncompletenessMonitor(OccurrencesInOntology occurrences) {
		super(occurrences);
		this.occurrences_ = occurrences;
	}

	public OntologyIncompletenessMonitor() {
		this(new OccurrencesInOntology());
	}

	public OccurrencesInOntology getOccurrencesInOntology() {
		return occurrences_;
	}

	/**
	 * Checks ontology reasoning completeness and, if necessary, prints messages
	 * about incompleteness using the provided logger.
	 * 
	 * @param logger
	 * @return {@code true} if the reasoning results are guaranteed to be
	 *         complete and {@code false} otherwise
	 */
	public boolean checkCompleteness(Logger logger) {
		if (hasNewExplanation()) {
			logger.warn(
					"Reasoning results with the current ontology may be incomplete! See INFO for more details.");
			explainIncompleteness(logger);
		}
		return !isIncompletenessDetected();
	}

}
