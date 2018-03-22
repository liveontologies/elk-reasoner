package org.semanticweb.elk.reasoner.completeness;

import java.util.Collections;

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

import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.slf4j.Logger;

/**
 * An {@link OccurrenceManager} that provides information about occurrences of
 * {@link Feature}s in the current ontology of the reasoner.
 * 
 * @author Yevgeny Kazakov
 */
public class OccurrencesInOntology
		implements ElkAxiomProcessor, OccurrenceListener, OccurrenceManager {

	/**
	 * the default maximal number of recent occurrences to keep track of
	 */
	private static int DEFAULT_RECENT_OCCURRENCE_LIMIT_ = 3;

	private final OccurrenceRegistry occurrenceRegistry_ = new OccurrenceRegistry();

	private final Map<Feature, Set<ElkAxiom>> recentOccurrences_ = new EnumMap<>(
			Feature.class);

	/**
	 * the maximal number of recent occurrences to keep track of
	 */
	private final int recentOccurrenceLimit_;

	/**
	 * the axiom, changes in occurrences of which are notified by
	 * {@link #occurrenceChanged(Feature, int)}
	 */
	private ElkAxiom currentAxiom_;

	public OccurrencesInOntology() {
		this(DEFAULT_RECENT_OCCURRENCE_LIMIT_);
	}

	OccurrencesInOntology(int recentOccurrenceLimit) {
		this.recentOccurrenceLimit_ = recentOccurrenceLimit;
	}

	@Override
	public void visit(ElkAxiom elkAxiom) {
		currentAxiom_ = elkAxiom;
	}

	@Override
	public void occurrenceChanged(Feature occurrence, int increment) {
		occurrenceRegistry_.occurrenceChanged(occurrence, increment);
		Set<ElkAxiom> recent = recentOccurrences_.get(occurrence);
		if (recent == null) {
			recent = new LinkedHashSet<ElkAxiom>(recentOccurrenceLimit_);
			recentOccurrences_.put(occurrence, recent);
		}
		if (increment > 0) {
			recent.add(currentAxiom_);
			if (recent.size() > recentOccurrenceLimit_) {
				// removing the oldest element
				Iterator<?> i = recent.iterator();
				i.next();
				i.remove();
			}
		} else {
			recent.remove(currentAxiom_);
			if (recent.isEmpty()) {
				recentOccurrences_.remove(occurrence);
			}
		}
	}

	@Override
	public int getOccurrenceCount(Feature occurrence) {
		return occurrenceRegistry_.getOccurrenceCount(occurrence);
	}

	Set<ElkAxiom> getRecentOccurrences(Feature occurrence) {
		Set<ElkAxiom> result = recentOccurrences_.get(occurrence);
		if (result != null) {
			return result;
		}
		// else
		return Collections.emptySet();
	}

	@Override
	public void logOccurrences(Feature occurrence, Logger logger) {
		int count = getOccurrenceCount(occurrence);
		if (count == 0) {
			return;
		}
		String occurrences = count == 1 ? "occurrence" : "occurrences";
		String polarity = "";
		switch (occurrence.getPolarity()) {
		case POSITIVE:
			polarity = "positive ";
			break;
		case NEGATIVE:
			polarity = "negative ";
		default:
			break;
		}
		logger.info(
				"{} {}{} of {} found in the current ontology. See DEBUG for more detail",
				count, polarity, occurrences, occurrence.getConstructor());
		if (logger.isDebugEnabled()) {
			Set<ElkAxiom> axioms = getRecentOccurrences(occurrence);
			for (ElkAxiom axiom : axioms) {
				logger.debug(OwlFunctionalStylePrinter.toString(axiom));
			}
			if (count > axioms.size()) {
				logger.debug("...");
			}
		}
		recentOccurrences_.remove(occurrence);
	}

	@Override
	public boolean hasNewOccurrencesOf(Feature occurrence) {
		return !getRecentOccurrences(occurrence).isEmpty();
	}

}
