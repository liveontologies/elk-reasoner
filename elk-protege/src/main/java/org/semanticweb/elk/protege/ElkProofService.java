package org.semanticweb.elk.protege;

/*-
 * #%L
 * ELK Reasoner Protege Plug-in
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

import org.liveontologies.owlapi.proof.DelegatingOwlProof;
import org.liveontologies.owlapi.proof.DummyOWLProof;
import org.liveontologies.owlapi.proof.OWLProof;
import org.liveontologies.protege.explanation.proof.service.ProofService;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.semanticweb.elk.owlapi.ElkProof;
import org.semanticweb.elk.owlapi.ElkReasoner;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.UnsupportedEntailmentTypeException;

public class ElkProofService extends ProofService
		implements OWLModelManagerListener {

	private ElkReasoner reasoner_ = null;

	private final List<ChangeListener> listeners_ = new ArrayList<ChangeListener>();

	@Override
	public void initialise() throws Exception {
		changeReasoner();
		getEditorKit().getOWLModelManager().addListener(this);
	}

	@Override
	public void dispose() {
		reasoner_ = null;
		getEditorKit().getOWLModelManager().removeListener(this);
	}

	@Override
	public boolean hasProof(OWLAxiom entailment) {
		return (reasoner_ != null && entailment instanceof OWLSubClassOfAxiom
				|| entailment instanceof OWLEquivalentClassesAxiom);
	}

	@Override
	public OWLProof getProof(final OWLAxiom entailment)
			throws UnsupportedEntailmentTypeException {
		return new DynamicOwlProof(entailment);
	}

	@Override
	public void handleChange(OWLModelManagerChangeEvent event) {
		if (event.isType(EventType.REASONER_CHANGED)) {
			changeReasoner();
		}
	}

	void changeReasoner() {
		ElkReasoner newReasoner = null;
		OWLReasoner reasoner = getEditorKit().getOWLModelManager()
				.getOWLReasonerManager().getCurrentReasoner();
		if (reasoner instanceof ElkReasoner) {
			newReasoner = (ElkReasoner) reasoner;
		}
		if (newReasoner == reasoner_) {
			return;
		}
		// else
		if (reasoner_ != null && reasoner_.equals(newReasoner)) {
			return;
		}
		// else
		reasoner_ = newReasoner;
		for (ChangeListener listener : listeners_) {
			listener.reasonerChanged();
		}
	}

	private static interface ChangeListener {

		void reasonerChanged();

	}

	static OWLProof createProof(ElkReasoner reasoner, OWLAxiom entailment) {
		if (reasoner == null) {
			return new DummyOWLProof(entailment);
		} else {
			return new ElkProof(reasoner, entailment);
		}
	}

	private class DynamicOwlProof extends DelegatingOwlProof
			implements ChangeListener {

		private final OWLAxiom entailment_;

		DynamicOwlProof(OWLAxiom entailment) {
			super(createProof(reasoner_, entailment));
			this.entailment_ = entailment;
			ElkProofService.this.listeners_.add(this);
		}

		@Override
		public void reasonerChanged() {
			setDelegate(createProof(reasoner_, entailment_));
		}

		@Override
		public void dispose() {
			super.dispose();
			ElkProofService.this.listeners_.remove(this);
		}

	}

}
