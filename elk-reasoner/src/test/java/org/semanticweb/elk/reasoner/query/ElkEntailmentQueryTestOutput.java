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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.ReasoningTestOutput;

/**
 * The test output for entailment checking of {@link ElkAxiom}s
 * 
 * @author Yevgeny Kazakov
 */
public class ElkEntailmentQueryTestOutput
		implements ReasoningTestOutput<Map<ElkAxiom, Boolean>> {

	private final Map<ElkAxiom, Boolean> output_;

	// TODO completeness is separate per query!
	private final boolean isComplete_;

	public ElkEntailmentQueryTestOutput(final Map<ElkAxiom, Boolean> output,
			boolean isComplete) {
		this.output_ = output == null
				? Collections.<ElkAxiom, Boolean> emptyMap()
				: output;
		this.isComplete_ = isComplete;
	}

	public ElkEntailmentQueryTestOutput(Reasoner reasoner,
			Collection<ElkAxiom> query) throws ElkQueryException, ElkException {
		// TODO: completeness
		this(resultToOutput(reasoner.isEntailed(query)), true);
	}

	static Map<ElkAxiom, Boolean> resultToOutput(
			final Map<ElkAxiom, EntailmentQueryResult> result)
			throws ElkQueryException {
		final Map<ElkAxiom, Boolean> output = new HashMap<ElkAxiom, Boolean>();
		for (final Map.Entry<ElkAxiom, EntailmentQueryResult> e : result
				.entrySet()) {
			output.put(e.getKey(), e.getValue().accept(RESULT_VISITOR));
		}
		return output;
	}

	@Override
	public Map<ElkAxiom, Boolean> getResult() {
		return output_;
	}

	@Override
	public boolean isComplete() {
		return isComplete_;
	}

	@Override
	public final int hashCode() {
		return Objects.hash(ElkEntailmentQueryTestOutput.class, output_,
				isComplete_);
	}

	@Override
	public final boolean equals(final Object obj) {
		if (obj instanceof ElkEntailmentQueryTestOutput) {
			ElkEntailmentQueryTestOutput other = (ElkEntailmentQueryTestOutput) obj;
			return this == obj || (output_.equals(other.output_)
					&& isComplete_ == other.isComplete_);
		}
		// else
		return false;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + output_ + ")";
	}

	private static final EntailmentQueryResult.Visitor<Boolean, ElkQueryException> RESULT_VISITOR = new EntailmentQueryResult.Visitor<Boolean, ElkQueryException>() {

		@Override
		public Boolean visit(
				final ProperEntailmentQueryResult properEntailmentQueryResult)
				throws ElkQueryException {
			try {
				return properEntailmentQueryResult.isEntailed();
			} finally {
				properEntailmentQueryResult.unlock();
			}
		}

		@Override
		public Boolean visit(
				final UnsupportedIndexingEntailmentQueryResult unsupportedIndexingEntailmentQueryResult) {
			// TODO: this may be an important information for the test
			return false;
		}

		@Override
		public Boolean visit(
				final UnsupportedQueryTypeEntailmentQueryResult unsupportedQueryTypeEntailmentQueryResult) {
			// TODO: this may be an important information for the test
			return false;
		}

	};

}
