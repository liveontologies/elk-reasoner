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

import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * Simple implementation of {@link SatisfiabilityTestOutput}.
 * 
 * @author Peter Skocovsky
 */
public class BaseSatisfiabilityTestOutput implements SatisfiabilityTestOutput {

	private final boolean isSatisfiable_;

	public BaseSatisfiabilityTestOutput(final boolean isSatisfiable) {
		this.isSatisfiable_ = isSatisfiable;
	}

	@Override
	public boolean isSatisfiable() {
		return isSatisfiable_;
	}

	@Override
	public int hashCode() {
		return HashGenerator.combinedHashCode(getClass(), isSatisfiable_);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		return isSatisfiable_ == ((SatisfiabilityTestOutput) obj)
				.isSatisfiable();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + isSatisfiable_ + ")";
	}

}
