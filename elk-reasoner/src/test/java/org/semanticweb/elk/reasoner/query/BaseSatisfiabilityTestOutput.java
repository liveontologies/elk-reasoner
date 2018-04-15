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

/**
 * Simple implementation of {@link SatisfiabilityTestOutput}.
 * 
 * @author Peter Skocovsky
 */
public abstract class BaseSatisfiabilityTestOutput
		implements SatisfiabilityTestOutput {

	private final boolean isSatisfiable_;

	private final boolean isComplete_;

	public BaseSatisfiabilityTestOutput(final boolean isSatisfiable,
			boolean isComplete) {
		this.isSatisfiable_ = isSatisfiable;
		this.isComplete_ = isComplete;
	}

	@Override
	public Boolean getResult() {
		return isSatisfiable_;
	}

	@Override
	public boolean isComplete() {
		return isComplete_;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "("
				+ (isSatisfiable_ ? "satisfiable" : "unsatisfiable")
				+ ((isSatisfiable_ && !isComplete_) ? "?" : "") + ")";
	}

}
