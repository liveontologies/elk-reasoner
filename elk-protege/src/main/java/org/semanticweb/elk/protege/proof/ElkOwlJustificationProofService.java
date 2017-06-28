/*-
 * #%L
 * ELK Reasoner Protege Plug-in
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2017 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.protege.proof;

import java.util.Collection;
import java.util.Set;

import org.liveontologies.protege.justification.proof.service.JustificationCompleteProof;
import org.liveontologies.puli.Inference;
import org.liveontologies.puli.InferenceJustifier;
import org.liveontologies.puli.InferenceJustifiers;
import org.liveontologies.puli.Proof;
import org.liveontologies.puli.Proofs;
import org.semanticweb.elk.owlapi.ElkProver;
import org.semanticweb.elk.owlapi.ElkReasoner;
import org.semanticweb.owlapi.model.OWLAxiom;

public class ElkOwlJustificationProofService
		extends ElkJustificationProofService {

	@Override
	public JustificationCompleteProof<?> computeProof(OWLAxiom entailment) {
		ElkReasoner elkReasoner = getCurrentElkReasoner();
		if (elkReasoner == null) {
			return null;
		}
		// else
		ElkProver elkProver = new ElkProver(elkReasoner);
		final Proof<OWLAxiom> proof = Proofs.addAssertedInferences(
				elkProver.getProof(entailment), getEditorKit()
						.getOWLModelManager().getActiveOntology().getAxioms());
		final InferenceJustifier<OWLAxiom, ? extends Set<? extends OWLAxiom>> justifier = InferenceJustifiers
				.justifyAssertedInferences();
		return new JustificationCompleteProof<OWLAxiom>() {

			@Override
			public Collection<? extends Inference<OWLAxiom>> getInferences(
					OWLAxiom conclusion) {
				Collection<? extends Inference<OWLAxiom>> result = proof
						.getInferences(conclusion);
				return result;
			}

			@Override
			public Set<? extends OWLAxiom> getJustification(
					Inference<OWLAxiom> inference) {
				return justifier.getJustification(inference);
			}

			@Override
			public OWLAxiom getGoal() {
				return entailment;
			}
		};
	}

	@Override
	public String getName() {
		return "ELK OWL Proof";
	}

}
