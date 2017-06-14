package org.semanticweb.elk.protege.proof;

import org.liveontologies.protege.justification.proof.service.JustificationCompleteProof;
import org.semanticweb.elk.owlapi.proofs.TracingProofAdapter;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.owlapi.model.OWLAxiom;

public class JustificationCompleteTracingProofAdapter extends TracingProofAdapter implements JustificationCompleteProof<TracingProofAdapter.ConclusionAdapter>{

	public JustificationCompleteTracingProofAdapter(Reasoner reasoner,
			OWLAxiom query) {
		super(reasoner, query);
	}

	@Override
	public ConclusionAdapter getGoal() {
		return getConvertedQuery();
	}
}