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
package org.semanticweb.elk.owl.inferences;

import java.util.Objects;

import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.ReasoningTestOutput;

/**
 * The test output for derivability checking of {@link ElkAxiom}s
 * 
 * @author Yevgeny Kazakov
 */
public class ElkQueryDerivabilityTestOutput
		implements ReasoningTestOutput<Boolean> {

	private final boolean isDerivable_;

	private final boolean isComplete_;

	public ElkQueryDerivabilityTestOutput(final boolean isDerivable,
			boolean isComplete) {
		this.isDerivable_ = isDerivable;
		this.isComplete_ = isComplete;
	}

	public ElkQueryDerivabilityTestOutput(Reasoner reasoner, ElkAxiom query)
			throws ElkException {
		// TODO: completeness
		this(TestUtils.getNonDerivable(reasoner, null, reasoner.getElkFactory(),
				query) != null, true);
	}

	@Override
	public Boolean getResult() {
		return isDerivable_;
	}

	@Override
	public boolean isComplete() {
		return isComplete_;
	}

	@Override
	public final int hashCode() {
		return Objects.hash(ElkQueryDerivabilityTestOutput.class, isDerivable_,
				isComplete_);
	}

	@Override
	public final boolean equals(final Object obj) {
		if (obj instanceof ElkQueryDerivabilityTestOutput) {
			ElkQueryDerivabilityTestOutput other = (ElkQueryDerivabilityTestOutput) obj;
			return this == obj || (isDerivable_ == other.isDerivable_
					&& isComplete_ == other.isComplete_);
		}
		// else
		return false;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + (isDerivable_ ? "" : "not ")
				+ "derivable" + (!isDerivable_ && !isComplete_ ? "?" : "")
				+ ")";
	}

}
