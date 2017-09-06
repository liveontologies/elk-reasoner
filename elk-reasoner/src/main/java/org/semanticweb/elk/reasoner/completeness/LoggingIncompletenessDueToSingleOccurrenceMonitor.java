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

import org.semanticweb.elk.reasoner.indexing.model.OccurrenceStore;
import org.semanticweb.elk.util.logging.LogLevel;
import org.semanticweb.elk.util.logging.LoggerWrap;
import org.slf4j.Logger;

abstract class LoggingIncompletenessDueToSingleOccurrenceMonitor
		extends IncompletenessDueToSingleOccurrenceMonitor {

	private final Logger logger_;
	private final LogLevel logLevel_;
	private final IncompletenessMessageProvider messageProvider_;

	public LoggingIncompletenessDueToSingleOccurrenceMonitor(
			final OccurrenceStore occurrences, final Logger logger,
			final LogLevel logLevel,
			final IncompletenessMessageProvider messageProvider) {
		super(occurrences);
		this.logger_ = logger;
		this.logLevel_ = logLevel;
		this.messageProvider_ = messageProvider;
	}

	@Override
	protected void onIncompleteness() {
		if (!LoggerWrap.isEnabledFor(logger_, logLevel_)) {
			return;
		}
		// else
		final StringBuilder message = new StringBuilder(
				accept(messageProvider_));
		message.append("\n");
		messageProvider_.printOccurrences(occurrences.occursIn(getOccurrence()),
				message);

		LoggerWrap.log(logger_, logLevel_,
				getOccurrence().getClass().getSimpleName(), message.toString());

	}

}
