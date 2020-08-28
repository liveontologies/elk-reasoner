package org.semanticweb.elk.reasoner.completeness;

/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2020 Department of Computer Science, University of Oxford
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
 * A template for creating test outputs for incomplete reasoning results so that
 * they can be compared with each other.
 * 
 * @author Yevgeny Kazakov
 *
 * @param <R>
 */
public class IncompleteTestOutput<R> {

	private final R value_;

	private final boolean isComplete_;

	public IncompleteTestOutput(IncompleteResult<? extends R> result) {
		value_ = result.getValue();
		isComplete_ = !result.getIncompletenessMonitor()
				.isIncompletenessDetected();
	}

	public IncompleteTestOutput(R output) {
		value_ = output;
		isComplete_ = true;
	}

	public R getValue() {
		return value_;
	}

	public boolean isComplete() {
		return isComplete_;
	}

}
