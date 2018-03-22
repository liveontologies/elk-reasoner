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
 * The top level {@link IncompletenessMonitor} aggregating information from all
 * existing {@link IncompletenessMonitor}s.
 * 
 * @author Yevgeny Kazakov
 */
public class TopIncompletenessMonitor extends CombinedIncompletenessMonitor {

	TopIncompletenessMonitor(OccurrenceManager manager) {
		super(IncompletenessDueToUnsupportedFeatures.getMonitor(manager));
	}

	@Override
	public void explainIncompleteness(Logger logger) {
		if (hasNewExplanation()) {
			logger.warn(
					"Reasoning may be incomplete! See INFO for more details.");
			super.explainIncompleteness(logger);
		}
	}

	public static IncompletenessMonitor getMonitor(OccurrenceManager manager) {
		return new TopIncompletenessMonitor(manager);
	}

	public static void checkCompleteness(OccurrenceManager manager,
			Logger logger) {
		getMonitor(manager).explainIncompleteness(logger);
	}

}
