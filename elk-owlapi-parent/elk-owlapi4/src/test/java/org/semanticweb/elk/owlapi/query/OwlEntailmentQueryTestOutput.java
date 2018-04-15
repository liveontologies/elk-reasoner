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
package org.semanticweb.elk.owlapi.query;

import java.util.Objects;

import org.semanticweb.elk.owlapi.ElkProver;
import org.semanticweb.elk.owlapi.ElkReasoner;
import org.semanticweb.elk.owlapi.proofs.ProofTestUtils;
import org.semanticweb.elk.reasoner.ReasoningTestOutput;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * The test output for entailment checking of {@link OWLAxiom}s
 * 
 * @author Yevgeny Kazakov
 */
public class OwlEntailmentQueryTestOutput
		implements ReasoningTestOutput<Boolean> {

	private final boolean isEntailed_;

	private final boolean isComplete_;

	public OwlEntailmentQueryTestOutput(final boolean isEntailed,
			boolean isComplete) {
		this.isEntailed_ = isEntailed;
		this.isComplete_ = isComplete;
	}

	public OwlEntailmentQueryTestOutput(ElkReasoner reasoner, OWLAxiom query) {
		// TODO: completeness
		this(reasoner.isEntailed(query), true);
	}

	public OwlEntailmentQueryTestOutput(ElkProver prover, OWLAxiom query) {
		// TODO: completeness
		this(ProofTestUtils.isDerivable(prover.getProof(query), query), true);
	}

	@Override
	public Boolean getResult() {
		return isEntailed_;
	}

	@Override
	public boolean isComplete() {
		return isComplete_;
	}

	@Override
	public final int hashCode() {
		return Objects.hash(OwlEntailmentQueryTestOutput.class, isEntailed_,
				isComplete_);
	}

	@Override
	public final boolean equals(final Object obj) {
		if (obj instanceof OwlEntailmentQueryTestOutput) {
			OwlEntailmentQueryTestOutput other = (OwlEntailmentQueryTestOutput) obj;
			return this == obj || (isEntailed_ == other.isEntailed_
					&& isComplete_ == other.isComplete_);
		}
		// else
		return false;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + (isEntailed_ ? "" : "not ")
				+ "entailed" + (!isEntailed_ && !isComplete_ ? "?" : "") + ")";
	}

}
