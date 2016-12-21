package org.semanticweb.elk.owlapi.proofs;

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

import java.util.Collection;

import org.liveontologies.proof.util.BaseInferenceSet;
import org.liveontologies.proof.util.DynamicInferenceSet;
import org.liveontologies.proof.util.Inference;
import org.semanticweb.elk.owl.inferences.ElkInference;
import org.semanticweb.elk.owl.inferences.ElkInferenceOptimizedProducingFactory;
import org.semanticweb.elk.owl.inferences.ElkInferenceProducer;
import org.semanticweb.elk.owl.inferences.ElkProofGenerator;
import org.semanticweb.elk.owl.interfaces.ElkClassAxiom;
import org.semanticweb.elk.owlapi.ElkReasoner;
import org.semanticweb.elk.owlapi.wrapper.OwlClassAxiomConverterVisitor;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.reasoner.UnsupportedEntailmentTypeException;

public class ElkOwlInferenceSet extends BaseInferenceSet<OWLAxiom>
		implements DynamicInferenceSet<OWLAxiom>, ElkReasoner.ChangeListener {

	private final ElkReasoner elkReasoner_;

	private final ElkClassAxiom elkEntailment_;

	/**
	 * true if the inferences in this set are in sync with the inferences
	 * deriving {@link #elkEntailment_} by {@link #elkReasoner_}
	 */
	private boolean inSync_ = false;

	/**
	 * use {@link #create(ElkReasoner, OWLAxiom)}
	 * 
	 * @param elkReasoner
	 */
	private ElkOwlInferenceSet(ElkReasoner elkReasoner,
			ElkClassAxiom elkEntailment) {
		this.elkReasoner_ = elkReasoner;
		this.elkEntailment_ = elkEntailment;
		elkReasoner.addListener(this);
	}

	@Override
	public synchronized Collection<? extends Inference<OWLAxiom>> getInferences(
			OWLAxiom conclusion) {
		if (!inSync_) {
			clear();
			generate();
			inSync_ = true;
		}
		return super.getInferences(conclusion);
	}

	@Override
	public synchronized void ontologyChanged() {
		inSync_ = false;
		fireChanged();
	}

	@Override
	public void dispose() {
		super.dispose();
		elkReasoner_.removeListener(this);
	}

	void generate() {
		ElkInferenceProducer producer = new ElkInferenceConvertingProducer(
				this);
		ElkInference.Factory factory = new ElkInferenceOptimizedProducingFactory(
				producer);
		elkEntailment_.accept(new ElkProofGenerator(
				elkReasoner_.getInternalReasoner(), factory));
	}

	public static ElkOwlInferenceSet create(ElkReasoner reasoner,
			OWLAxiom entailment) throws UnsupportedEntailmentTypeException {
		try {
			return new ElkOwlInferenceSet(reasoner, entailment
					.accept(OwlClassAxiomConverterVisitor.getInstance()));
		} catch (IllegalArgumentException e) {
			throw new UnsupportedEntailmentTypeException(entailment);
		}

	}

}
