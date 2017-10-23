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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.reasoner.indexing.model.Occurrence;
import org.semanticweb.elk.reasoner.indexing.model.OccurrenceStore;
import org.semanticweb.elk.util.logging.LogLevel;
import org.semanticweb.elk.util.logging.LoggerWrap;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import com.google.common.collect.ImmutableSet;

/**
 * Reports incompleteness when particular occurrence (returned by
 * {@link #getOccurrence()}) occurs in the provided {@link OccurrenceStore}.
 * <p>
 * Subclasses naturally correspond to some of the {@link Occurrence}s, but not
 * all {@link Occurrence}s cause incompleteness on their own. Thus, the
 * {@link Visitor} is over {@link IncompletenessDueToSingleOccurrenceMonitor}s
 * corresponding to {@link Occurrence}s that cause incompleteness on their own.
 * 
 * @author Peter Skocovsky
 */
abstract class IncompletenessDueToSingleOccurrenceMonitor
		extends IncompletenessDueToOccurrencesMonitor {

	private final LogLevel logLevel_;
	private final IncompletenessMessageProvider messageProvider_;
	private final Marker marker_ = MarkerFactory
			.getMarker(getOccurrence().toString());

	private Set<? extends ElkObject> lastOccursIn_ = Collections.emptySet();

	public IncompletenessDueToSingleOccurrenceMonitor(
			final OccurrenceStore occurrences, final LogLevel logLevel,
			final IncompletenessMessageProvider messageProvider) {
		super(occurrences);
		this.logLevel_ = logLevel;
		this.messageProvider_ = messageProvider;
	}

	public abstract Occurrence getOccurrence();

	@Override
	public boolean isIncomplete() {
		final Collection<? extends ElkObject> occurrsIn = occurrences
				.occursIn(getOccurrence());
		if (occurrsIn != null && !occurrsIn.isEmpty()) {
			return true;
		}
		// else
		return false;
	}

	@Override
	public boolean logNewIncompletenessReasons(final Logger logger) {
		final ImmutableSet.Builder<ElkObject> currentOccursIn = ImmutableSet
				.<ElkObject> builder();
		final List<ElkObject> newOccursIn = new ArrayList<ElkObject>();
		for (final ElkObject elkObject : occurrences
				.occursIn(getOccurrence())) {
			currentOccursIn.add(elkObject);
			if (!lastOccursIn_.contains(elkObject)) {
				newOccursIn.add(elkObject);
			}
		}
		lastOccursIn_ = currentOccursIn.build();
		if (!newOccursIn.isEmpty()) {
			if (LoggerWrap.isEnabledFor(logger, logLevel_)) {
				final StringBuilder message = new StringBuilder(
						accept(messageProvider_));
				message.append("\n");
				messageProvider_.printOccurrences(
						occurrences.occursIn(getOccurrence()), message);
				LoggerWrap.log(logger, logLevel_, marker_, message.toString());
			}
			return true;
		}
		// else
		return false;
	}

	public abstract <O> O accept(Visitor<O> visitor);

	public static interface Visitor<O> extends
			IncompletenessDueToNegativeOccurrenceOfObjectComplementOfMonitor.Visitor<O>,
			IncompletenessDueToNegativeOccurrenceOfTopObjectPropertyMonitor.Visitor<O>,
			IncompletenessDueToOccurrenceOfDataHasValueMonitor.Visitor<O>,
			IncompletenessDueToOccurrenceOfDisjointUnionMonitor.Visitor<O>,
			IncompletenessDueToOccurrenceOfNominalMonitor.Visitor<O>,
			IncompletenessDueToOccurrenceOfUnsupportedExpressionMonitor.Visitor<O>,
			IncompletenessDueToPositiveOccurrenceOfBottomObjectPropertyMonitor.Visitor<O>,
			IncompletenessDueToPositiveOccurrenceOfObjectUnionOfMonitor.Visitor<O> {
		// Combined interface.
	}

}
