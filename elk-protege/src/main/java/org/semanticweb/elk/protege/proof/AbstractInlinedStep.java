package org.semanticweb.elk.protege.proof;

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

import java.util.ArrayList;
import java.util.List;

import org.liveontologies.puli.ProofNode;
import org.liveontologies.puli.ProofStep;
import org.semanticweb.elk.owl.inferences.ElkInference;
import org.semanticweb.elk.owl.inferences.ElkInferenceBaseFactory;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owlapi.wrapper.OwlAxiomConverterVisitor;
import org.semanticweb.owlapi.model.OWLAxiom;

abstract class AbstractInlinedStep implements ProofStep<OWLAxiom> {

	/**
	 * the factory for creating rewritten inferences
	 */
	final static ElkInference.Factory FACTORY = new ElkInferenceBaseFactory();

	private final ProofNode<OWLAxiom> conclusion_;

	/**
	 * a temporary list to accumulate the premises of the resulting inference
	 */
	private final List<ProofNode<OWLAxiom>> premises_;

	AbstractInlinedStep(ProofStep<OWLAxiom> step) {
		this.conclusion_ = step.getConclusion();
		this.premises_ = new ArrayList<ProofNode<OWLAxiom>>(
				step.getPremises().size());
		process(step);
	}

	@Override
	public ProofNode<OWLAxiom> getConclusion() {
		return conclusion_;
	}

	@Override
	public List<? extends ProofNode<OWLAxiom>> getPremises() {
		return premises_;
	}

	void addPremise(ProofNode<OWLAxiom> premise) {
		premises_.add(premise);
	}

	ElkAxiom convertNodeMember(ProofNode<OWLAxiom> node) {
		return node.getMember().accept(OwlAxiomConverterVisitor.getInstance());
	}

	abstract void process(ProofStep<OWLAxiom> step);

}
