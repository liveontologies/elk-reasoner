package org.semanticweb.elk.protege.proof;

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

import org.liveontologies.protege.justification.proof.service.JustificationCompleteProof;
import org.semanticweb.elk.owlapi.ElkReasoner;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * Date: 27-02-2017
 */
public class ElkInternalJustificationProofService
		extends ElkJustificationProofService {

	@Override
	public JustificationCompleteProof<?> computeProof(OWLAxiom entailment) {
		ElkReasoner elkReasoner = getCurrentElkReasoner();
		if (elkReasoner == null) {
			return null;
		}
		// else
		return new JustificationCompleteTracingProofAdapter(
				elkReasoner.getInternalReasoner(), entailment);
	}

	@Override
	public String getName() {
		return "ELK Internal Proof";
	}

}