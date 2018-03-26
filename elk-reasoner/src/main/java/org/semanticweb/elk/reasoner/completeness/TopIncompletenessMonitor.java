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

/**
 * The top level {@link IncompletenessMonitor} aggregating information from all
 * existing {@link IncompletenessMonitor}s.
 * 
 * @author Yevgeny Kazakov
 */
public class TopIncompletenessMonitor extends CombinedIncompletenessMonitor {

	private static final Feature[][] UNSUPPORTED_COMBINATIONS_OF_FEATURES_ = {
			{ Feature.OBJECT_PROPERTY_RANGE,
					//
					Feature.OBJECT_PROPERTY_ASSERTION },
			// incomplete for property classification
			{ Feature.REFLEXIVE_OBJECT_PROPERTY,
					//
					Feature.OBJECT_PROPERTY_CHAIN }, };

	private static final Feature[] UNSUPPORTED_FEATURES_ = {
			Feature.ANONYMOUS_INDIVIDUAL,
			//
			Feature.ASYMMETRIC_OBJECT_PROPERTY,
			//
			Feature.BOTTOM_OBJECT_PROPERTY_POSITIVE,
			//
			Feature.DATA_ALL_VALUES_FROM,
			//
			Feature.DATA_EXACT_CARDINALITY,
			//
			Feature.DATA_HAS_VALUE,
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
			Feature.DISJOINT_UNION,
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
			Feature.OBJECT_ALL_VALUES_FROM,
			//
			Feature.OBJECT_COMPLEMENT_OF_NEGATIVE,
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
			Feature.OBJECT_UNION_OF_POSITIVE,
			//
			Feature.SUB_DATA_PROPERTY_OF,
			//
			Feature.SWRL_RULE,
			//
			Feature.SYMMETRIC_OBJECT_PROPERTY,
			//
			Feature.TOP_OBJECT_PROPERTY_NEGATIVE };

	private static IncompletenessMonitor[] getMonitors(
			OccurrenceManager occurrences) {
		IncompletenessMonitor[] monitors = new IncompletenessMonitor[UNSUPPORTED_FEATURES_.length
				+ UNSUPPORTED_COMBINATIONS_OF_FEATURES_.length];
		int pos = 0;
		for (Feature feature : UNSUPPORTED_FEATURES_) {
			monitors[pos++] = new IncompletenessDueToUnsupportedFeatures(
					occurrences, feature);
		}
		for (Feature[] combination : UNSUPPORTED_COMBINATIONS_OF_FEATURES_) {
			monitors[pos++] = new IncompletenessDueToUnsupportedFeatures(
					occurrences, combination);
		}
		return monitors;
	}

	TopIncompletenessMonitor(OccurrenceManager occurences) {
		super(getMonitors(occurences));
	}

}
