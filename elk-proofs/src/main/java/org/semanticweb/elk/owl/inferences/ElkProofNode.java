package org.semanticweb.elk.owl.inferences;

/*-
 * #%L
 * ELK Proofs Package
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.liveontologies.owlapi.proof.util.ProofNode;
import org.liveontologies.owlapi.proof.util.ProofStep;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;

public class ElkProofNode implements ProofNode<ElkAxiom> {

	private final ElkAxiom conclusion_;

	private final ElkInferenceSet inferenceSet_;

	ElkProofNode(ElkAxiom conclusion, ElkInferenceSet inferenceSet) {
		this.conclusion_ = conclusion;
		this.inferenceSet_ = inferenceSet;
	}

	public ElkAxiom getConclusion() {
		return conclusion_;
	}

	public ElkInferenceSet getInferenceSet() {
		return inferenceSet_;
	}

	@Override
	public ElkAxiom getMember() {
		return conclusion_;
	}

	@Override
	public Collection<? extends ProofStep<ElkAxiom>> getInferences() {
		Collection<? extends ElkInference> inferences = inferenceSet_
				.get(conclusion_);
		List<ProofStep<ElkAxiom>> result = new ArrayList<ProofStep<ElkAxiom>>(
				inferences.size());
		for (ElkInference inf : inferences) {
			result.add(new ElkProofStep(inf, inferenceSet_));
		}
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ElkProofNode) {
			ElkProofNode other = (ElkProofNode) o;
			return conclusion_.equals(other.conclusion_)
					&& inferenceSet_.equals(other.inferenceSet_);
		}
		// else
		return false;
	}

	@Override
	public int hashCode() {
		return conclusion_.hashCode() + inferenceSet_.hashCode();
	}

	@Override
	public String toString() {
		return conclusion_.toString();
	}

}
