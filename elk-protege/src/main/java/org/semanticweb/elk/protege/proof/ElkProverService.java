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

import java.util.Set;

import org.liveontologies.protege.justification.proof.service.ProverService;
import org.liveontologies.puli.InferenceJustifier;
import org.liveontologies.puli.Proof;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.elk.owlapi.ElkReasoner;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.elk.owlapi.proofs.TracingProofAdapter;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

/**
 * Date: 27-02-2017
 */

public class ElkProverService extends ProverService {

	private TracingProofAdapter proofAdapter;

	@Override
	public Proof getProof(OWLEditorKit ek, OWLAxiom axiom) {
		OWLReasoner owlReasoner = ek.getOWLModelManager()
				.getOWLReasonerManager().getCurrentReasoner();
		ElkReasoner elkReasoner = (owlReasoner instanceof ElkReasoner)
				? (ElkReasoner) owlReasoner
				: new ElkReasonerFactory().createReasoner(
						ek.getModelManager().getActiveOntology());
		Reasoner reasoner = elkReasoner.getInternalReasoner();

		proofAdapter = new TracingProofAdapter(reasoner, axiom);

		return proofAdapter;
	}

	@Override
	public InferenceJustifier<TracingProofAdapter.ConclusionAdapter, Set<? extends OWLAxiom>> getJustifier() {
		return proofAdapter;
	}

	@Override
	public Object convertQuery(OWLAxiom entailment) {
		return null;
	}

	@Override
	public void initialise() throws Exception {
	}

	@Override
	public void dispose() throws Exception {
	}

	@Override
	public String getName() {
		return "Elk";
	}
}