package org.semanticweb.elk.owlapi;

/*-
 * #%L
 * ELK OWL API Binding
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

import java.util.ArrayList;
import java.util.List;

import org.liveontologies.owlapi.proof.OWLProof;
import org.liveontologies.owlapi.proof.OWLProofNode;
import org.liveontologies.owlapi.proof.ProofChangeListener;
import org.semanticweb.elk.owl.inferences.ElkInferenceSet;
import org.semanticweb.elk.owl.inferences.ReasonerElkInferenceSet;
import org.semanticweb.elk.owl.inferences.rewriting.FlattenedElkInferenceSet;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owlapi.proofs.ElkOWLProofNode;
import org.semanticweb.elk.owlapi.wrapper.OwlClassAxiomConverterVisitor;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * An {@link OWLProof} for an {@link OWLAxiom} maintained by an
 * {@link ElkReasoner}. The inferences are computed as necessary by the reasoner
 * and the registered {@link ProofChangeListener}s receive notification when
 * such proofs are changed (e.g., due to the changes in the ontology with which
 * the reasoner works).
 * 
 * @author Yevgeny Kazakov
 *
 */
public class ElkProof implements OWLProof, ElkReasoner.ChangeListener {

	private final ElkReasoner elkReasoner_;

	private final ElkAxiom elkEntailment_;

	private final List<ProofChangeListener> listeners_ = new ArrayList<ProofChangeListener>();

	private OWLProofNode root_;

	/**
	 * if {@code true}, the inferences returned by the reasoner will be
	 * post-processed
	 */
	private final boolean flattenInferences_;

	public ElkProof(ElkReasoner elkReasoner, OWLAxiom entailment) {
		this.elkReasoner_ = elkReasoner;
		this.elkEntailment_ = entailment
				.accept(OwlClassAxiomConverterVisitor.getInstance());
		elkReasoner_.addListener(this);
		ReasonerConfiguration config = elkReasoner.getConfigurationOptions();
		this.flattenInferences_ = config.getParameterAsBoolean(
				ReasonerConfiguration.FLATTEN_INFERENCES);
	}

	@Override
	public OWLProofNode getRoot() {
		if (root_ == null) {
			// compute
			ReasonerElkInferenceSet reasonerInferences_ = new ReasonerElkInferenceSet(
					elkReasoner_.getInternalReasoner(), elkEntailment_,
					elkReasoner_.getElkObjectFactory());
			ElkInferenceSet processedInferences = reasonerInferences_;
			if (flattenInferences_) {
				processedInferences = new FlattenedElkInferenceSet(
						processedInferences);
			}
			root_ = new ElkOWLProofNode(elkEntailment_, processedInferences,
					elkReasoner_.getElkObjectFactory());
		}
		return root_;
	}

	@Override
	public void addListener(ProofChangeListener listener) {
		listeners_.add(listener);

	}

	@Override
	public void removeListener(ProofChangeListener listener) {
		listeners_.remove(listener);
	}

	@Override
	public void ontologyChanged() {
		root_ = null;
		for (ProofChangeListener listener : listeners_) {
			listener.proofChanged();
		}
	}

	@Override
	public void dispose() {
		elkReasoner_.removeListener(this);
	}

}
