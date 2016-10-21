package org.semanticweb.elk.protege;

import java.util.Collection;
import java.util.Collections;

/*
 * #%L
 * ELK Reasoner Protege Plug-in
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

import org.liveontologies.owlapi.proof.OWLProofNode;
import org.liveontologies.owlapi.proof.OWLProofStep;
import org.liveontologies.owlapi.proof.OWLProver;
import org.liveontologies.owlapi.proof.util.LeafProofNode;
import org.liveontologies.protege.explanation.proof.service.ProofService;
import org.semanticweb.elk.owlapi.ElkProver;
import org.semanticweb.elk.owlapi.ElkReasoner;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.UnsupportedEntailmentTypeException;

public class ElkProofService extends ProofService {

	private OWLProver prover_ = null;

	@Override
	public void initialise() throws Exception {
		// nothing
	}

	@Override
	public void dispose() {
		prover_ = null;
	}

	@Override
	public boolean hasProof(OWLAxiom entailment) {
		OWLReasoner reasoner = getEditorKit().getOWLModelManager()
				.getOWLReasonerManager().getCurrentReasoner();
		if (reasoner instanceof ElkReasoner) {
			prover_ = new ElkProver((ElkReasoner) reasoner);
			return entailment instanceof OWLSubClassOfAxiom
					|| entailment instanceof OWLEquivalentClassesAxiom;
		}
		// else
		prover_ = null;
		return false;
	}

	@Override
	public OWLProofNode getProof(final OWLAxiom entailment)
			throws UnsupportedEntailmentTypeException {
		if (prover_ == null) {
			return new LeafOwlProofNode(entailment);
		}
		// else
		return prover_.getProof(entailment);
	}

	static class LeafOwlProofNode extends LeafProofNode<OWLAxiom>
			implements OWLProofNode {

		public LeafOwlProofNode(OWLAxiom member) {
			super(member);
		}

		@Override
		public Collection<? extends OWLProofStep> getInferences() {
			return Collections.emptyList();
		}

		@Override
		public void addListener(ChangeListener listener) {
			// ignore, nothing can change anyway
		}

		@Override
		public void removeListener(ChangeListener listener) {
			// ignore, nothing can change anyway
		}

	}

}
