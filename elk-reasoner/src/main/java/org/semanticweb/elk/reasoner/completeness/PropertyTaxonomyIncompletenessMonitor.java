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

import java.util.Collection;

import org.slf4j.Logger;

public class PropertyTaxonomyIncompletenessMonitor
		extends CombinedIncompletenessMonitor {

	private static final Feature[] UNSUPPORTED_FEATURES_ = {
			// supported features that may cause unsatisfiability of properties
			Feature.OWL_NOTHING_POSITIVE,
			//
			Feature.DISJOINT_CLASSES,
			//
			Feature.OBJECT_COMPLEMENT_OF_POSITIVE };

	private static final Feature[][] UNSUPPORTED_COMBINATIONS_OF_FEATURES_ = {
			// incomplete for property classification
			{ Feature.REFLEXIVE_OBJECT_PROPERTY,
					//
					Feature.OBJECT_PROPERTY_CHAIN } };

	static Collection<IncompletenessMonitor> getMonitors(
			OccurrenceManager occurrences) {
		Collection<IncompletenessMonitor> monitors = OntologySatisfiabilityIncompletenessMonitor
				.getMonitors(occurrences);
		for (Feature feature : UNSUPPORTED_FEATURES_) {
			monitors.add(new IncompletenessDueToUnsupportedFeatures(occurrences,
					feature));
		}
		for (Feature[] combination : UNSUPPORTED_COMBINATIONS_OF_FEATURES_) {
			monitors.add(new IncompletenessDueToUnsupportedFeatures(occurrences,
					combination));
		}
		return monitors;
	}

	private final OntologySatisfiabilityIncompletenessMonitor ontologySatisfiabilityMonitor_;

	public PropertyTaxonomyIncompletenessMonitor(
			OntologySatisfiabilityIncompletenessMonitor ontologySatisfiabilityMonitor) {
		super(getMonitors(
				ontologySatisfiabilityMonitor.getOccurrencesInOntology()));
		this.ontologySatisfiabilityMonitor_ = ontologySatisfiabilityMonitor;
	}

	public boolean checkCompleteness(Logger logger) {
		if (!ontologySatisfiabilityMonitor_.checkCompleteness(logger)) {
			// ontology can be already unsatisfiable
			return false;
		}
		// else
		if (hasNewExplanation()) {
			logger.warn(
					"Object property reasoning may be incomplete! See INFO for more details.");
			explainIncompleteness(logger);
		}
		return !isIncompletenessDetected();
	}

}
