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
 * An {@link OccurrenceManager} that aggregates information about several other
 * {@link OccurrenceManager}s, as if all occurrences of the {@link Feature}s are
 * taken together.
 * 
 * @author Yevgeny Kazakov
 */
public class CombinedOccurrenceManager implements OccurrenceManager {

	OccurrenceManager[] managers_;

	public CombinedOccurrenceManager(OccurrenceManager... managers) {
		this.managers_ = managers;
	}

	@Override
	public int getOccurrenceCount(Feature occurrence) {
		int sum = 0;
		for (OccurrenceManager manager : managers_) {
			sum += manager.getOccurrenceCount(occurrence);
		}
		return sum;
	}

	@Override
	public void logOccurrences(Feature occurrence, Logger logger) {
		for (OccurrenceManager manager : managers_) {
			manager.logOccurrences(occurrence, logger);
		}
	}

	@Override
	public boolean hasNewOccurrencesOf(Feature occurrence) {
		for (OccurrenceManager manager : managers_) {
			if (manager.hasNewOccurrencesOf(occurrence)) {
				return true;
			}
		}
		// else
		return false;
	}

}
