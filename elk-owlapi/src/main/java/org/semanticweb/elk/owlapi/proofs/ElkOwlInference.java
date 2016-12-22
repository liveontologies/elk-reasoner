package org.semanticweb.elk.owlapi.proofs;

/*-
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

import java.util.AbstractList;
import java.util.List;

import org.liveontologies.proof.util.Inference;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owlapi.ElkConverter;
import org.semanticweb.owlapi.model.OWLAxiom;

public class ElkOwlInference implements Inference<OWLAxiom> {

	private final Inference<ElkAxiom> elkInference_;

	public ElkOwlInference(Inference<ElkAxiom> elkInference) {
		this.elkInference_ = elkInference;
	}

	public Inference<ElkAxiom> getElkInference() {
		return elkInference_;
	}

	@Override
	public String getName() {
		return elkInference_.getName();
	}

	@Override
	public OWLAxiom getConclusion() {
		return convert(elkInference_.getConclusion());
	}

	@Override
	public List<? extends OWLAxiom> getPremises() {
		final List<? extends ElkAxiom> original = elkInference_.getPremises();
		return new AbstractList<OWLAxiom>() {

			@Override
			public OWLAxiom get(int index) {
				return convert(original.get(index));
			}

			@Override
			public int size() {
				return original.size();
			}

		};
	}

	@Override
	public int hashCode() {
		return ElkOwlInference.class.hashCode() + elkInference_.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		// else
		if (o instanceof ElkOwlInference) {
			return elkInference_.equals(((ElkOwlInference) o).elkInference_);
		}
		// else
		return false;
	}

	@Override
	public String toString() {
		return elkInference_.toString();
	}

	private static OWLAxiom convert(ElkAxiom axiom) {
		return ElkConverter.getInstance().convert(axiom);
	}

}
