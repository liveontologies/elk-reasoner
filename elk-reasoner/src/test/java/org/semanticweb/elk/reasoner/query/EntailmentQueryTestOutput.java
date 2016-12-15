/*-
 * #%L
 * ELK Reasoner Core
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

import java.util.Collections;
import java.util.Map;

import org.semanticweb.elk.testing.TestOutput;

public class EntailmentQueryTestOutput<A> implements TestOutput {

	private final Map<A, Boolean> output_;

	public EntailmentQueryTestOutput(final Map<A, Boolean> output) {
		this.output_ = output == null ? Collections.<A, Boolean> emptyMap()
				: output;
	}

	public Map<A, Boolean> getOutput() {
		return output_;
	}

	@Override
	public int hashCode() {
		return output_.hashCode();
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
		return output_.equals(((EntailmentQueryTestOutput<?>) obj).output_);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + output_ + ")";
	}

}
