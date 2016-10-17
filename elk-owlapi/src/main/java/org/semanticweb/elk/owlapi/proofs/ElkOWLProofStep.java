package org.semanticweb.elk.owlapi.proofs;

/*
 * #%L
 * ELK OWL API Binding
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

import org.liveontologies.owlapi.proof.OWLProofNode;
import org.liveontologies.owlapi.proof.OWLProofStep;
import org.semanticweb.elk.owl.inferences.ElkInference;
import org.semanticweb.elk.owl.inferences.ElkInferenceSet;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObject;

public class ElkOWLProofStep implements OWLProofStep {

	private final ElkInference elkInference_;

	private final ElkInferenceSet elkInferences_;

	private final ElkObject.Factory elkFactory_;

	private int hash_ = 0;

	ElkOWLProofStep(ElkInference elkInference, ElkInferenceSet elkInferences,
			ElkObject.Factory elkFactory) {
		this.elkInference_ = elkInference;
		this.elkInferences_ = elkInferences;
		this.elkFactory_ = elkFactory;
	}

	@Override
	public String getName() {
		return elkInference_.getName();
	}

	@Override
	public OWLProofNode getConclusion() {
		return convert(elkInference_.getConclusion(elkFactory_));
	}

	@Override
	public List<? extends OWLProofNode> getPremises() {
		List<OWLProofNode> result = new ArrayList<OWLProofNode>();
		for (int i = 0; i < elkInference_.getPremiseCount(); i++) {
			result.add(convert(elkInference_.getPremise(i, elkFactory_)));
		}
		return result;
	}

	@Override
	public int hashCode() {
		if (hash_ == 0) {
			hash_ = elkInference_.hashCode() + elkInferences_.hashCode();
		}
		return hash_;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}
		if (o instanceof ElkOWLProofStep) {
			ElkOWLProofStep other = (ElkOWLProofStep) o;
			return elkInference_.equals(other.elkInference_)
					&& elkInferences_.equals(other.elkInferences_);
		}
		// else
		return false;
	}

	@Override
	public String toString() {
		return elkInference_.toString();
	}

	private OWLProofNode convert(ElkAxiom conclusion) {
		return new ElkOWLProofNode(conclusion, elkInferences_, elkFactory_);
	}

}
