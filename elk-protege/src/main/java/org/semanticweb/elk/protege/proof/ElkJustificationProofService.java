package org.semanticweb.elk.protege.proof;

import org.liveontologies.protege.justification.proof.service.JustificationProofService;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.elk.owlapi.ElkReasoner;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

public abstract class ElkJustificationProofService
		extends JustificationProofService {

	@Override
	public void initialise() throws Exception {
	}

	@Override
	public void dispose() throws Exception {
	}
	
	@Override
	public boolean hasProof(OWLAxiom entailment) {
		return getCurrentElkReasoner() != null;
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
