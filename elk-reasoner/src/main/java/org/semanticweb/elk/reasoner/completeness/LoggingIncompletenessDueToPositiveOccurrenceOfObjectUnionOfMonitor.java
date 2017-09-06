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

import org.semanticweb.elk.reasoner.indexing.model.Occurrence;
import org.semanticweb.elk.reasoner.indexing.model.OccurrenceStore;
import org.semanticweb.elk.util.logging.LogLevel;
import org.slf4j.Logger;

class LoggingIncompletenessDueToPositiveOccurrenceOfObjectUnionOfMonitor
		extends LoggingIncompletenessDueToSingleOccurrenceMonitor {

	public LoggingIncompletenessDueToPositiveOccurrenceOfObjectUnionOfMonitor(
			final OccurrenceStore occurrences, final Logger logger,
			final LogLevel logLevel,
			final IncompletenessMessageProvider occurrencePrinter) {
		super(occurrences, logger, logLevel, occurrencePrinter);
	}

	@Override
	public Occurrence getOccurrence() {
		return Occurrence.POSITIVE_OCCURRENCE_OF_OBJECT_UNION_OF;
	}

	@Override
	public <O> O accept(
			final IncompletenessDueToSingleOccurrenceMonitor.Visitor<O> visitor) {
		return accept((Visitor<O>) visitor);
	}

	public <O> O accept(final Visitor<O> visitor) {
		return visitor.visit(this);
	}

	public static interface Visitor<O> {
		O visit(LoggingIncompletenessDueToPositiveOccurrenceOfObjectUnionOfMonitor monitor);
	}

}
