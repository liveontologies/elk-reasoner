/*
 * #%L
 * ELK Proofs Package
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
package org.semanticweb.elk.owl.inferences;

import org.liveontologies.puli.ChronologicalProof;
import org.liveontologies.puli.DynamicProof;
import org.liveontologies.puli.Proof;
import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.entailments.model.EntailmentInference;
import org.semanticweb.elk.reasoner.query.VerifiableQueryResult;

/**
 * A set of inferences necessary to derive a given {@link ElkAxiom}s provided by
 * {@link Reasoner}.
 * 
 * @author Yevgeny Kazakov
 * @author Peter Skocovsky
 */
public class ReasonerElkProof extends ChronologicalProof<ElkInference>
		implements ModifiableElkProof {

	private final Reasoner reasoner_;

	private final ElkInference.Factory inferenceFactory_;

	private ReasonerElkProof(Reasoner reasoner, ElkAxiom goal,
			ElkObject.Factory elkFactory) throws ElkException {
		this.reasoner_ = reasoner;
		this.inferenceFactory_ = new ElkInferenceOptimizedProducingFactory(this,
				elkFactory);
	}

	public static DynamicProof<ElkInference> create(
			final Reasoner reasoner, final ElkAxiom goal,
			final ElkObject.Factory elkFactory) throws ElkException {
		final ReasonerElkProof proof = new ReasonerElkProof(reasoner, goal,
				elkFactory);
		synchronized (proof) {
			proof.generateInferences(goal);
		}
		return proof;
	}

	private void generateInferences(final ElkAxiom goal) throws ElkException {

		final VerifiableQueryResult result = reasoner_.isEntailed(goal);
		try {
			final Proof<EntailmentInference> evidence = result.getEvidence(false);
			new ElkProofGenerator(evidence, reasoner_,
					inferenceFactory_).generate(result.getEntailment());
		} finally {
			result.unlock();
		}

	}

}
