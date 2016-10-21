package org.semanticweb.elk.owl.inferences.rewriting;

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
import java.util.List;

import org.liveontologies.owlapi.proof.util.ProofNode;
import org.liveontologies.owlapi.proof.util.ProofStep;
import org.semanticweb.elk.owl.implementation.ElkObjectBaseFactory;
import org.semanticweb.elk.owl.inferences.ElkInference;
import org.semanticweb.elk.owl.inferences.ElkInferenceSet;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObject;

public class ElkProofStep implements ProofStep<ElkAxiom> {

	private static final ElkObject.Factory CONCLUSION_FACTORY_ = new ElkObjectBaseFactory();

	private final ElkInference inference_;

	private final ElkInferenceSet inferenceSet_;

	public ElkProofStep(ElkInference inference,
			ElkInferenceSet inferenceSet) {
		this.inference_ = inference;
		this.inferenceSet_ = inferenceSet;
	}

	public ElkInference getElkInference() {
		return inference_;
	}

	public ElkInferenceSet getInferenceSet() {
		return inferenceSet_;
	}

	@Override
	public String getName() {
		return inference_.getName();
	}

	@Override
	public ProofNode<ElkAxiom> getConclusion() {
		return new ElkProofNode(
				inference_.getConclusion(CONCLUSION_FACTORY_), inferenceSet_);
	}

	@Override
	public List<? extends ProofNode<ElkAxiom>> getPremises() {
		int n = inference_.getPremiseCount();
		List<ProofNode<ElkAxiom>> result = new ArrayList<ProofNode<ElkAxiom>>(
				n);
		for (int i = 0; i < n; i++) {
			result.add(new ElkProofNode(
					inference_.getPremise(i, CONCLUSION_FACTORY_),
					inferenceSet_));
		}
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ElkProofStep) {
			ElkProofStep other = (ElkProofStep) o;
			return inference_.equals(other.inference_)
					&& inferenceSet_.equals(other.inferenceSet_);
		}
		// else
		return false;
	}

	@Override
	public int hashCode() {
		return inference_.hashCode() + inferenceSet_.hashCode();
	}

	@Override
	public String toString() {
		return inference_.toString();
	}

}
