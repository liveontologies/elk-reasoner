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
package org.semanticweb.elk.reasoner.query;

import java.util.Collection;
import java.util.Collections;

import org.semanticweb.elk.reasoner.entailments.impl.DerivedClassInclusionEntailsClassAssertionAxiomImpl;
import org.semanticweb.elk.reasoner.entailments.model.ClassAssertionAxiomEntailment;
import org.semanticweb.elk.reasoner.entailments.model.Entailment;
import org.semanticweb.elk.reasoner.entailments.model.EntailmentInference;
import org.semanticweb.elk.reasoner.entailments.model.EntailmentInferenceSet;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexedIndividual;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SaturationConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * Query whether an
 * {@link org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom
 * ElkClassAssertionAxiom} is entailed.
 * 
 * @author Peter Skocovsky
 */
public class ClassAssertionEntailmentQuery
		extends AbstractIndexedEntailmentQuery<ClassAssertionAxiomEntailment> {

	/**
	 * Indexed individual that should be an instance of
	 * {@link #classExpression_}.
	 */
	private final IndexedIndividual individual_;

	/**
	 * Indexed class expression that should be a type of {@link #individual_}.
	 */
	private final IndexedClassExpression classExpression_;

	/**
	 * @param query
	 *            What entailment is queried.
	 * @param individual
	 *            Indexed individual that should be an instance of
	 *            {@code classExpression}.
	 * @param classExpression
	 *            Indexed class expression that should be a type of
	 *            {@code individual}.
	 */
	public ClassAssertionEntailmentQuery(
			final ClassAssertionAxiomEntailment query,
			final IndexedIndividual individual,
			final IndexedClassExpression classExpression) {
		super(query);
		this.individual_ = individual;
		this.classExpression_ = classExpression;
	}

	@Override
	public Collection<? extends IndexedContextRoot> getNegativelyIndexed() {
		return Collections.singleton(individual_);
	}

	@Override
	public <C extends Context> EntailmentInferenceSet getEvidence(
			final boolean atMostOne, final SaturationState<C> saturationState,
			final SaturationConclusion.Factory conclusionFactory)
			throws ElkQueryException {
		return new EntailmentInferenceSet() {

			@Override
			public Collection<? extends EntailmentInference> getInferences(
					final Entailment conclusion) {

				if (!getQuery().equals(conclusion)) {
					return Collections.emptySet();
				}
				// else

				final C context = saturationState.getContext(individual_);
				if (context == null) {
					// not entailed
					return Collections.emptySet();
				}
				// else

				final SubClassInclusionComposed subsumption = conclusionFactory
						.getSubClassInclusionComposed(individual_,
								classExpression_);
				if (context.containsConclusion(subsumption)) {
					return Collections.singleton(
							new DerivedClassInclusionEntailsClassAssertionAxiomImpl(
									getQuery(), subsumption));
				}
				// else

				return Collections.emptySet();
			}

		};
	}

}
