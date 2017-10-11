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
package org.semanticweb.elk.owlapi.proofs;

import java.util.Collection;

import org.liveontologies.puli.BaseProof;
import org.liveontologies.puli.DynamicProof;
import org.liveontologies.puli.Proof;
import org.liveontologies.puli.Proofs;
import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.exceptions.ElkRuntimeException;
import org.semanticweb.elk.owl.inferences.ElkInference;
import org.semanticweb.elk.owl.inferences.ElkInferenceOptimizedProducingFactory;
import org.semanticweb.elk.owl.inferences.ElkInferenceProducer;
import org.semanticweb.elk.owl.inferences.ElkProofGenerator;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owlapi.ElkConverter;
import org.semanticweb.elk.owlapi.ElkReasoner;
import org.semanticweb.elk.owlapi.wrapper.OwlConverter;
import org.semanticweb.elk.reasoner.entailments.model.EntailmentInference;
import org.semanticweb.elk.reasoner.query.EntailmentQueryResult;
import org.semanticweb.elk.reasoner.query.ProperEntailmentQueryResult;
import org.semanticweb.elk.reasoner.query.UnsupportedIndexingEntailmentQueryResult;
import org.semanticweb.elk.reasoner.query.UnsupportedQueryTypeEntailmentQueryResult;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.reasoner.UnsupportedEntailmentTypeException;

public class ElkOwlProof extends BaseProof<ElkOwlInference>
		implements ElkReasoner.ChangeListener {

	private final ElkReasoner elkReasoner_;

	private final OWLAxiom owlEntailment_;

	/**
	 * true if the inferences in this set are in sync with the inferences
	 * deriving {@link #owlEntailment_} by {@link #elkReasoner_}
	 */
	private boolean inSync_ = false;

	/**
	 * use {@link #create(ElkReasoner, OWLAxiom)}
	 * 
	 * @param elkReasoner
	 */
	private ElkOwlProof(ElkReasoner elkReasoner, OWLAxiom elkEntailment) {
		this.elkReasoner_ = elkReasoner;
		this.owlEntailment_ = elkEntailment;
		elkReasoner.addListener(this);
	}

	@Override
	public synchronized Collection<? extends ElkOwlInference> getInferences(
			Object conclusion) {
		ensureSync();
		return super.getInferences(conclusion);
	}

	synchronized void ensureSync() {
		if (!inSync_) {
			clear();
			generate();
			inSync_ = true;
		}
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

	private void generate() {
		final OwlConverter owlConverter = OwlConverter.getInstance();
		final ElkConverter elkConverter = ElkConverter.getInstance();
		try {

			final ElkAxiom elkAxiom = owlConverter.convert(owlEntailment_);
			final EntailmentQueryResult result = elkReasoner_
					.getInternalReasoner().isEntailed(elkAxiom);
			result.accept(ENTAILMENT_QUERY_RESULT_VISITOR);

		} catch (final ElkException e) {
			throw elkConverter.convert(e);
		} catch (final ElkRuntimeException e) {
			throw elkConverter.convert(e);
		}

	}

	private final EntailmentQueryResult.Visitor<Void, ElkException> ENTAILMENT_QUERY_RESULT_VISITOR = new EntailmentQueryResult.Visitor<Void, ElkException>() {

		@Override
		public Void visit(final ProperEntailmentQueryResult properResult)
				throws ElkException {
			try {
				final ElkInferenceProducer producer = new ElkInferenceConvertingProducer(
						ElkOwlProof.this);
				final ElkInference.Factory factory = new ElkInferenceOptimizedProducingFactory(
						producer);
				final Proof<EntailmentInference> evidence = properResult
						.getEvidence(false);
				new ElkProofGenerator(evidence,
						elkReasoner_.getInternalReasoner(), factory)
								.generate(properResult.getEntailment());
				return null;
			} finally {
				properResult.unlock();
			}
		}

		@Override
		public Void visit(
				final UnsupportedIndexingEntailmentQueryResult unsupportedIndexing) {
			/*
			 * Indexing of some subexpression of the entailment is not
			 * supported, so we can generate only empty proof. The warning
			 * should be logged during loading of the entailment query.
			 */
			return null;
		}

		@Override
		public Void visit(
				final UnsupportedQueryTypeEntailmentQueryResult unsupportedQueryType) {
			throw new UnsupportedEntailmentTypeException(owlEntailment_);
		}

	};

	public static DynamicProof<ElkOwlInference> create(ElkReasoner reasoner,
			OWLAxiom entailment) throws UnsupportedEntailmentTypeException {
		if (reasoner == null) {
			return Proofs.emptyProof();
		}
		// else
		final ElkOwlProof proof = new ElkOwlProof(reasoner, entailment);
		// If the entailment is not supported, throw the exceptions now.
		proof.ensureSync();
		return proof;
	}

}
