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
 * An {@link OccurrenceCounter} that additionally keeps track of some recent
 * occurrences of {@link Feature}s.
 * 
 * @author Yevgeny Kazakov
 *
 */
public interface OccurrenceManager extends OccurrenceCounter {

	/**
	 * Prints some message about occurrences of the given {@link Feature}, which
	 * may include the the number of its occurrences or details about some
	 * recent occurrences
	 * 
	 * @param feature
	 * @param logger
	 */
	void logOccurrences(Feature feature, Logger logger);

	/**
	 * @param occurrence
	 * @return {@code true} if some new occurrences of the given {@link Feature}
	 *         may have appeared after the last call of
	 *         {@link #logOccurrences(Feature, Logger)}, and {@code false}
	 *         otherwise
	 */
	boolean hasNewOccurrencesOf(Feature occurrence);

}
