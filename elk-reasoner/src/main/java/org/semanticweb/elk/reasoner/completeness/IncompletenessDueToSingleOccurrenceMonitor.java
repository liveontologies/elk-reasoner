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
import org.semanticweb.elk.reasoner.indexing.model.Occurrence;
import org.semanticweb.elk.reasoner.indexing.model.OccurrenceStore;

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

	public IncompletenessDueToSingleOccurrenceMonitor(
			final OccurrenceStore occurrences) {
		super(occurrences);
	}

	public abstract Occurrence getOccurrence();

	@Override
	public boolean isIncomplete() {
		final Collection<? extends ElkObject> occurrsIn = occurrences
				.occursIn(getOccurrence());
		if (occurrsIn != null && !occurrsIn.isEmpty()) {
			onIncompleteness();
			return true;
		}
		// else
		return false;
	}

	protected abstract void onIncompleteness();

	public abstract <O> O accept(Visitor<O> visitor);

	public static interface Visitor<O> extends
			LoggingIncompletenessDueToNegativeOccurrenceOfObjectComplementOfMonitor.Visitor<O>,
			LoggingIncompletenessDueToNegativeOccurrenceOfTopObjectPropertyMonitor.Visitor<O>,
			LoggingIncompletenessDueToOccurrenceOfDataHasValueMonitor.Visitor<O>,
			LoggingIncompletenessDueToOccurrenceOfDisjointUnionMonitor.Visitor<O>,
			LoggingIncompletenessDueToOccurrenceOfNominalMonitor.Visitor<O>,
			LoggingIncompletenessDueToOccurrenceOfUnsupportedExpressionMonitor.Visitor<O>,
			LoggingIncompletenessDueToPositiveOccurrenceOfBottomObjectPropertyMonitor.Visitor<O>,
			LoggingIncompletenessDueToPositiveOccurrenceOfObjectUnionOfMonitor.Visitor<O> {
		// Combined interface.
	}

}
