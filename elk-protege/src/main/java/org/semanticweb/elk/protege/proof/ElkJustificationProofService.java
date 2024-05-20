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

import org.liveontologies.puli.Inference;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.elk.owlapi.ElkReasoner;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import io.github.liveontologies.protege.justification.proof.service.JustificationProofService;

public abstract class ElkJustificationProofService
		extends JustificationProofService<Inference<?>> {

	@Override
	public void initialise() throws Exception {
	}

	@Override
	public void dispose() throws Exception {
	}

	@Override
	public boolean hasProof(OWLAxiom entailment) {
		final ElkReasoner elkReasoner = getCurrentElkReasoner();
		return elkReasoner != null && elkReasoner
				.isEntailmentCheckingSupported(entailment.getAxiomType());
	}

	/**
	 * @return the current reasoner if it is an instance of {@link ElkReasoner}
	 *         and {@code null} otherwise
	 */
	ElkReasoner getCurrentElkReasoner() {
		OWLEditorKit kit = getEditorKit();
		OWLReasoner owlReasoner = kit.getOWLModelManager()
				.getOWLReasonerManager().getCurrentReasoner();
		if (owlReasoner instanceof ElkReasoner) {
			return (ElkReasoner) owlReasoner;
		} else {
			return null;
		}
	}

}
