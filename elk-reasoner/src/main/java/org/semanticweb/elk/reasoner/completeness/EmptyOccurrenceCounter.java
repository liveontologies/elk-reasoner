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
 * An {@link OccurrenceCounter} that always returns 0 occurrences for every
 * {@link Feature}
 * 
 * @author Yevgeny Kazakov
 */
public class EmptyOccurrenceCounter implements OccurrenceCounter {

	private static OccurrenceCounter INSTANCE_ = new EmptyOccurrenceCounter();

	@Override
	public int getOccurrenceCount(Feature occurrence) {
		return 0;
	}

	public static OccurrenceCounter get() {
		return INSTANCE_;
	}

}
