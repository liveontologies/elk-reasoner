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

import org.slf4j.Logger;

/**
 * Monitors incompleteness triggered by a certain combination of
 * {@link Feature}s.
 * 
 * @author Peter Skocovsky
 * @author Yevgeny Kazakov
 */
class IncompletenessDueToUnsupportedFeatures extends DelegatingOccurrenceManager
		implements IncompletenessMonitor {

	/**
	 * The {@link Feature}s whose combination triggers potential incompleteness
	 */
	private final Feature[] unsupportedFeatures_;

	private String description_;

	/**
	 * {@code true} if this type of incompleteness was already explained (which
	 * implies that incompleteness was triggered before)
	 */
	boolean incompletenessExplained_ = false;

	public IncompletenessDueToUnsupportedFeatures(
			final OccurrenceManager occurrences,
			Feature... unsupportedOccurrences) {
		super(occurrences);
		this.unsupportedFeatures_ = unsupportedOccurrences;
	}

	@Override
	public boolean isIncompletenessDetected() {
		for (Feature occurrence : unsupportedFeatures_) {
			if (getOccurrenceCount(occurrence) <= 0) {
				return false;
			}
		}
		// else
		incompletenessExplained_ = false; // explain next time
		return true;
	}

	@Override
	public boolean hasNewExplanation() {
		if (!isIncompletenessDetected() || incompletenessExplained_) {
			return false;
		}
		for (Feature feature : unsupportedFeatures_) {
			if (hasNewOccurrencesOf(feature)) {
				return true;
			}
		}
		// else
		return false;
	}

	@Override
	public void explainIncompleteness(final Logger logger) {
		if (!hasNewExplanation()) {
			// nothing new to explain
			return;
		}
		logger.info(getDescription());
		for (Feature occurrence : unsupportedFeatures_) {
			super.logOccurrences(occurrence, logger);
		}
		incompletenessExplained_ = true;
	}

	String getDescription() {
		if (description_ == null) {
			// lazy initialization
			StringBuilder descriptionBuilder = new StringBuilder();
			descriptionBuilder
					.append("Potential incompleteness due to occurrences of ");
			for (int i = 0; i < unsupportedFeatures_.length; i++) {
				if (i > 0) {
					descriptionBuilder.append(" and ");
				}
				descriptionBuilder
						.append(unsupportedFeatures_[i].getConstructor());
			}
			description_ = descriptionBuilder.toString();
		}
		return description_;
	}

	private static final Feature[] UNSUPPORTED_FEATURES_ = {
			Feature.ANONYMOUS_INDIVIDUAL,
			//
			Feature.ASYMMETRIC_OBJECT_PROPERTY,
			//
			Feature.DATA_ALL_VALUES_FROM,
			//
			Feature.DATA_EXACT_CARDINALITY,
			//
			Feature.DATA_MAX_CARDINALITY,
			//
			Feature.DATA_MIN_CARDINALITY,
			//
			Feature.DATA_PROPERTY,
			//
			Feature.DATA_PROPERTY_ASSERTION,
			//
			Feature.DATA_PROPERTY_DOMAIN,
			//
			Feature.DATA_PROPERTY_RANGE,
			//
			Feature.DATA_SOME_VALUES_FROM,
			//
			Feature.DATATYPE,
			//
			Feature.DATATYPE_DEFINITION,
			//
			Feature.DISJOINT_DATA_PROPERTIES,
			//
			Feature.DISJOINT_OBJECT_PROPERTIES,
			//
			Feature.EQUIVALENT_DATA_PROPERTIES,
			//
			Feature.FUNCTIONAL_DATA_PROPERTY,
			//
			Feature.FUNCTIONAL_OBJECT_PROPERTY,
			//
			Feature.HAS_KEY,
			//
			Feature.INVERSE_FUNCTIONAL_OBJECT_PROPERTY,
			//
			Feature.INVERSE_OBJECT_PROPERTIES,
			//
			Feature.IRREFLEXIVE_OBJECT_PROPERTY,
			//
			Feature.NEGATIVE_DATA_PROPERTY_ASSERTION,
			//
			Feature.NEGATIVE_OBJECT_PROPERTY_ASSERTION,
			//
			Feature.NEGATIVE_OCCURRENCE_OF_OBJECT_COMPLEMENT_OF,
			//
			Feature.NEGATIVE_OCCURRENCE_OF_TOP_OBJECT_PROPERTY,
			//
			Feature.OBJECT_ALL_VALUES_FROM,
			//
			Feature.OBJECT_EXACT_CARDINALITY,
			//
			Feature.OBJECT_HAS_SELF,
			//
			Feature.OBJECT_INVERSE_OF,
			//
			Feature.OBJECT_MAX_CARDINALITY,
			//
			Feature.OBJECT_MIN_CARDINALITY,
			//
			Feature.OBJECT_ONE_OF,
			//
			Feature.OCCURRENCE_OF_DATA_HAS_VALUE,
			//
			Feature.OCCURRENCE_OF_DISJOINT_UNION,
			//
			Feature.POSITIVE_OCCURRENCE_OF_BOTTOM_OBJECT_PROPERTY,
			//
			Feature.POSITIVE_OCCURRENCE_OF_OBJECT_UNION_OF,
			//
			Feature.SUB_DATA_PROPERTY_OF,
			//
			Feature.SWRL_RULE,
			//
			Feature.SYMMETRIC_OBJECT_PROPERTY };

	private static final Feature[][] UNSUPPORTED_COMBINATIONS_OF_FEATURES_ = { { //
			Feature.OCCURRENCE_OF_OBJECT_PROPERTY_RANGE,
			//
			Feature.OCCURRENCE_OF_OBJECT_PROPERTY_ASSERTION } };

	public static IncompletenessMonitor getMonitor(OccurrenceManager manager) {
		IncompletenessMonitor[] monitors = new IncompletenessMonitor[UNSUPPORTED_FEATURES_.length
				+ UNSUPPORTED_COMBINATIONS_OF_FEATURES_.length];
		int pos = 0;
		for (Feature feature : UNSUPPORTED_FEATURES_) {
			monitors[pos++] = new IncompletenessDueToUnsupportedFeatures(
					manager, feature);
		}
		for (Feature[] combination : UNSUPPORTED_COMBINATIONS_OF_FEATURES_) {
			monitors[pos++] = new IncompletenessDueToUnsupportedFeatures(
					manager, combination);
		}
		return new CombinedIncompletenessMonitor(monitors);
	}

}
