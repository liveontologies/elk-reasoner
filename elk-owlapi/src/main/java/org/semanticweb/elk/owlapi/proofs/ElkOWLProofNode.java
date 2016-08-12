package org.semanticweb.elk.owlapi.proofs;

import java.util.Collection;

import org.liveontologies.owlapi.proof.OWLProofNode;
import org.liveontologies.owlapi.proof.OWLProofStep;

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

import org.semanticweb.elk.owl.inferences.ElkInference;
import org.semanticweb.elk.owl.inferences.ElkInferenceSet;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owlapi.wrapper.OwlConverter;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.Operations.Transformation;
import org.semanticweb.owlapi.model.OWLAxiom;

public class ElkOWLProofNode implements OWLProofNode {

	private final OWLAxiom member_;

	private final ElkInferenceSet elkInferences_;

	private final ElkObject.Factory elkFactory_;

	private int hash_ = 0;

	public ElkOWLProofNode(OWLAxiom member, ElkInferenceSet elkInferences,
			ElkObject.Factory elkFactory) {
		this.member_ = member.getAxiomWithoutAnnotations();
		this.elkInferences_ = elkInferences;
		this.elkFactory_ = elkFactory;
	}

	@Override
	public OWLAxiom getMember() {
		return member_;
	}

	@Override
	public Collection<? extends OWLProofStep> getInferences() {
		return Operations.map(
				elkInferences_.get(OwlConverter.getInstance().convert(member_)),
				new Transformation<ElkInference, OWLProofStep>() {

					@Override
					public OWLProofStep transform(ElkInference element) {
						return new ElkOWLProofStep(element, elkInferences_,
								elkFactory_);
					}
				});
	}

	@Override
	public int hashCode() {
		if (hash_ == 0) {
			hash_ = member_.hashCode() + elkInferences_.hashCode();
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
		if (o instanceof ElkOWLProofNode) {
			ElkOWLProofNode other = (ElkOWLProofNode) o;
			return member_.equals(other.member_)
					&& elkInferences_.equals(other.elkInferences_);
		}
		// else
		return false;
	}

	@Override
	public String toString() {
		return member_.toString();
	}

}
