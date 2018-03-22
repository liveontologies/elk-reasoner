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
 * An {@link OccurrenceManager} that delegates all method calls to a given
 * {@link OccurrenceManager}
 * 
 * @author Yevgeny Kazakov
 */
public class DelegatingOccurrenceManager extends DelegatingOccurrenceCounter
		implements OccurrenceManager {

	private OccurrenceManager delegate_;

	public DelegatingOccurrenceManager(OccurrenceManager delegate) {
		super(delegate);
		this.delegate_ = delegate;
	}

	@Override
	public void logOccurrences(Feature occurrence, Logger logger) {
		delegate_.logOccurrences(occurrence, logger);
	}

	@Override
	public boolean hasNewOccurrencesOf(Feature occurrence) {
		return delegate_.hasNewOccurrencesOf(occurrence);
	}

}
