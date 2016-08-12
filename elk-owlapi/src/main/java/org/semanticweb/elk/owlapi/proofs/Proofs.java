/**
 * 
 */
package org.semanticweb.elk.owlapi.proofs;
/*
 * #%L
 * ELK OWL API Binding
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import java.util.HashSet;
import java.util.Set;

import org.liveontologies.owlapi.proof.OWLProofNode;
import org.liveontologies.owlapi.proof.OWLProofStep;
import org.liveontologies.owlapi.proof.OWLProver;
import org.liveontologies.owlapi.proof.util.ProofNode;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.parameters.Imports;

/**
 * Utilities for working with proofs.
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 */
public class Proofs {

	/**
	 * @param reasoner
	 * @param entailment
	 * @param allProofs
	 * @return all ontology axioms used in all or some proof(s) for the given
	 *         entailment.
	 */
	public static Set<OWLAxiom> getUsedAxioms(OWLProver reasoner,
			OWLAxiom entailment, final boolean allProofs) {
		final Set<OWLAxiom> usedAxioms = new HashSet<OWLAxiom>();
		final Set<OWLAxiom> stated = reasoner.getRootOntology()
				.getAxioms(Imports.INCLUDED);

		ProofExplorer.visitInferences(reasoner.getProof(entailment),
				new ProofExplorer.Controller() {

					@Override
					public boolean nodeVisited(OWLProofNode node) {
						return false;
					}

					@Override
					public boolean inferenceVisited(OWLProofStep inference) {
						for (ProofNode<OWLAxiom> premise : inference
								.getPremises()) {
							OWLAxiom member = premise.getMember();
							if (stated.contains(member)) {
								usedAxioms.add(member);
							}
						}
						return !allProofs;
					}

				});

		return usedAxioms;
	}
}
