package org.semanticweb.elk.protege.proof;

import java.util.Collection;
import java.util.Set;

import org.liveontologies.owlapi.proof.OWLProver;
import org.liveontologies.protege.justification.proof.service.JustificationCompleteProof;
import org.liveontologies.protege.justification.proof.service.JustificationProofService;
import org.liveontologies.puli.Inference;
import org.liveontologies.puli.InferenceJustifier;
import org.liveontologies.puli.InferenceJustifiers;
import org.liveontologies.puli.Proof;
import org.liveontologies.puli.Proofs;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.elk.owlapi.ElkProver;
import org.semanticweb.elk.owlapi.ElkProverFactory;
import org.semanticweb.elk.owlapi.ElkReasoner;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

public class ElkOwlJustificationProofService extends JustificationProofService {

	@Override
	public void initialise() throws Exception {
	}

	@Override
	public void dispose() throws Exception {
	}

	@Override
	public JustificationCompleteProof<?> getJustificationCompleteProof(
			OWLAxiom entailment) {
		final OWLProver elkProver;
		OWLEditorKit kit = getEditorKit();
		OWLReasoner reasoner = kit.getOWLModelManager().getOWLReasonerManager()
				.getCurrentReasoner();
		if (reasoner instanceof ElkReasoner) {
			elkProver = new ElkProver((ElkReasoner) reasoner);
		} else {
			ElkProverFactory factory = new ElkProverFactory();
			elkProver = factory
					.createReasoner(kit.getModelManager().getActiveOntology());
		}
		final Proof<OWLAxiom> proof = Proofs.addAssertedInferences(
				elkProver.getProof(entailment),
				kit.getOWLModelManager().getActiveOntology().getAxioms());
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
