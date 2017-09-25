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
import java.util.Collections;
import java.util.Set;

import org.liveontologies.protege.justification.proof.service.JustificationCompleteProof;
import org.liveontologies.puli.Inference;
import org.liveontologies.puli.Inferences;
import org.liveontologies.puli.Proof;
import org.liveontologies.puli.Proofs;
import org.semanticweb.elk.owlapi.ElkProver;
import org.semanticweb.elk.owlapi.ElkReasoner;
import org.semanticweb.owlapi.model.OWLAxiom;

import com.google.common.base.Function;

public class ElkOwlJustificationProofService
		extends ElkJustificationProofService {

	@Override
	public JustificationCompleteProof computeProof(OWLAxiom entailment) {
		ElkReasoner elkReasoner = getCurrentElkReasoner();
		if (elkReasoner == null) {
			return null;
		}
		// else
		ElkProver elkProver = new ElkProver(elkReasoner);
		final Proof<Object> proof = Proofs.transform(
				elkProver.getProof(entailment),
				new Function<OWLAxiom, Object>() {
					@Override
					public Object apply(final OWLAxiom input) {
						return input;
					}
				}, new Function<Object, OWLAxiom>() {
					@Override
					public OWLAxiom apply(final Object input) {
						// This is safe, because only OWLAxioms will be
						// queried.
						return (OWLAxiom) input;
					}
				});
		return new JustificationCompleteProof() {

			@Override
			public Collection<? extends Inference<Object>> getInferences(
					Object conclusion) {
				if (!(conclusion instanceof OWLAxiom)) {
					return Collections.emptyList();
				}
				// else
				return proof.getInferences((OWLAxiom) conclusion);
			}

			@Override
			public Set<? extends OWLAxiom> getJustification(
					Inference<Object> inference) {

				if (Inferences.isAsserted(inference)) {
					Object conclusion = inference.getConclusion();
					if (conclusion instanceof OWLAxiom) {
						return Collections.singleton(
								(OWLAxiom) inference.getConclusion());
					}
				}
				// else
				return Collections.emptySet();
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
