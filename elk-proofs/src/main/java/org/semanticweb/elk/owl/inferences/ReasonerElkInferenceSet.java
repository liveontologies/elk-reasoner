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

import java.util.Collection;

import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.exceptions.ElkRuntimeException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.entailments.model.EntailmentInferenceSet;
import org.semanticweb.elk.reasoner.query.ElkQueryException;
import org.semanticweb.elk.reasoner.query.EntailmentQueryResult;
import org.semanticweb.elk.reasoner.query.ProperEntailmentQueryResult;
import org.semanticweb.elk.reasoner.query.UnsupportedIndexingEntailmentQueryResult;
import org.semanticweb.elk.reasoner.query.UnsupportedQueryTypeEntailmentQueryResult;

/**
 * A set of inferences necessary to derive a given {@link ElkAxiom}s provided by
 * {@link Reasoner}.
 * 
 * @author Yevgeny Kazakov
 * @author Peter Skocovsky
 */
public class ReasonerElkInferenceSet extends ModifiableElkInferenceSetImpl {

	private final Reasoner reasoner_;

	private final ElkInference.Factory inferenceFactory_;

	public ReasonerElkInferenceSet(Reasoner reasoner, ElkAxiom goal,
			ElkObject.Factory elkFactory) {
		super(elkFactory);
		this.reasoner_ = reasoner;
		this.inferenceFactory_ = new ElkInferenceOptimizedProducingFactory(this,
				elkFactory);
		synchronized (this) {
			try {
				generateInferences(goal);
			} catch (final ElkException e) {
				// TODO: Maybe add throws declaration instead!
				throw new ElkRuntimeException(e);
			}
		}
	}

	@Override
	public synchronized Collection<? extends ElkInference> get(
			ElkAxiom conclusion) {
		return super.get(conclusion);
	}

	private void generateInferences(final ElkAxiom goal) throws ElkException {

		final EntailmentQueryResult result = reasoner_.isEntailed(goal);

		result.accept(new EntailmentQueryResult.Visitor<Void>() {

			@Override
			public Void visit(final ProperEntailmentQueryResult result)
					throws ElkQueryException {

				try {
					final EntailmentInferenceSet evidence = result
							.getEvidence(false);
					new ElkProofGenerator(evidence, reasoner_,
							inferenceFactory_).generate(result.getEntailment());
				} finally {
					result.unlock();
				}

				return null;
			}

			@Override
			public Void visit(
					final UnsupportedIndexingEntailmentQueryResult result) {
				/*
				 * Indexing of some subexpression of the entailment is not
				 * supported, so we can generate only empty proof. The warning
				 * should be logged during loading of the entailment query.
				 */
				return null;
			}

			@Override
			public Void visit(
					final UnsupportedQueryTypeEntailmentQueryResult result) {
				throw new ElkRuntimeException(
						"Cannot check entailment: " + goal);
			}

		});

	}

}
