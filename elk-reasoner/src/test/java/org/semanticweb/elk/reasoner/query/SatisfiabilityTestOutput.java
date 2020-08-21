/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.query;

import org.semanticweb.elk.reasoner.completeness.IncompleteResult;
import org.semanticweb.elk.testing.DiffableOutput;

/**
 * A test output of a satisfiability check.
 * 
 * @author Peter Skocovsky
 * @author Yevgeny Kazakov
 */
public class SatisfiabilityTestOutput
		implements DiffableOutput<Boolean, SatisfiabilityTestOutput> {

	private final boolean isSatisfiable_;

	private final boolean isComplete_;

	public SatisfiabilityTestOutput(boolean isSatisfiable, boolean isComplete) {
		this.isSatisfiable_ = isSatisfiable;
		this.isComplete_ = isComplete;
	}

	public SatisfiabilityTestOutput(
			IncompleteResult<? extends Boolean> satisfiable) {
		this(satisfiable.getValue(), satisfiable.isComplete());
	}

	@Override
	public boolean containsAllElementsOf(SatisfiabilityTestOutput other) {
		return !isComplete_ || !other.isSatisfiable_ || isSatisfiable_;
	}

	@Override
	public void reportMissingElementsOf(SatisfiabilityTestOutput other,
			Listener<Boolean> listener) {
		if (containsAllElementsOf(other)) {
			return;
		}
		// else
		listener.missing(true);
	}

}
