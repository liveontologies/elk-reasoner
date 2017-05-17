/*-
 * #%L
 * ELK Reasoner Core
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
package org.semanticweb.elk.reasoner.entailments;

import java.util.Collection;
import java.util.Collections;

import org.liveontologies.puli.Inference;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.entailments.impl.OntologyInconsistencyEntailsAnyAxiomImpl;
import org.semanticweb.elk.reasoner.entailments.impl.OntologyInconsistencyImpl;
import org.semanticweb.elk.reasoner.entailments.model.AxiomEntailment;
import org.semanticweb.elk.reasoner.entailments.model.Entailment;
import org.semanticweb.elk.reasoner.entailments.model.EntailmentInference;
import org.semanticweb.elk.reasoner.entailments.model.EntailmentProof;

/**
 * This class is intended to be a wrapper of the proof returned by
 * {@link org.semanticweb.elk.reasoner.consistency.ConsistencyCheckingState#getEvidence(boolean)
 * ConsistencyCheckingState.getEvidence(boolean)}. It adds
 * {@link org.semanticweb.elk.reasoner.entailments.model.OntologyInconsistencyEntailsAnyAxiom
 * OntologyInconsistencyEntailsAnyAxiom} having the
 * {@link org.semanticweb.elk.reasoner.entailments.model.OntologyInconsistency
 * OntologyInconsistency} derived by
 * {@link org.semanticweb.elk.reasoner.consistency.ConsistencyCheckingState#getEvidence(boolean)
 * ConsistencyCheckingState.getEvidence(boolean)} as a premise (if it is
 * derived).
 * 
 * @author Peter Skocovsky
 */
public class InconsistencyProofWrapper implements EntailmentProof {

	private final EntailmentProof inconsistencyEvidence_;

	public InconsistencyProofWrapper(
			final EntailmentProof inconsistencyEvidence) {
		this.inconsistencyEvidence_ = inconsistencyEvidence;
	}

	@Override
	public Collection<? extends EntailmentInference> getInferences(
			final Entailment conclusion) {
		final Collection<? extends Inference<Entailment>> infs = inconsistencyEvidence_
				.getInferences(OntologyInconsistencyImpl.INSTANCE);
		if (infs == null || infs.isEmpty()) {
			/*
			 * If ontology inconsistency is not entailed, this proof is empty.
			 */
			return Collections.emptyList();
		}
		// else
		return conclusion.accept(GET_INFERENCES);
	}

	private final Entailment.Visitor<Collection<? extends EntailmentInference>> GET_INFERENCES = new DefaultEntailmentVisitor<Collection<? extends EntailmentInference>>() {

		@Override
		public Collection<? extends EntailmentInference> defaultVisit(
				final Entailment entailment) {
			return inconsistencyEvidence_.getInferences(entailment);
		};

		@Override
		public <A extends ElkAxiom> Collection<? extends EntailmentInference> defaultAxiomEntailmentVisit(
				final AxiomEntailment<A> axiomEntailment) {
			return Collections
					.singleton(new OntologyInconsistencyEntailsAnyAxiomImpl(
							axiomEntailment));
		};

	};

}
