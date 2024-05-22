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

import org.semanticweb.elk.reasoner.entailments.impl.DerivedClassInclusionEntailsObjectPropertyDomainAxiomImpl;
import org.semanticweb.elk.reasoner.entailments.model.EntailmentInference;
import org.semanticweb.elk.reasoner.entailments.model.ObjectPropertyDomainAxiomEntailment;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.proof.ReasonerProof;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SaturationConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * Query whether an
 * {@link org.semanticweb.elk.owl.interfaces.ElkObjectPropertyDomainAxiom
 * ElkObjectPropertyDomainAxiom} is entailed.
 * 
 * @author Peter Skocovsky
 */
public class ObjectPropertyDomainEntailmentQuery extends
		AbstractIndexedEntailmentQuery<ObjectPropertyDomainAxiomEntailment> {

	/**
	 * Existential with property from the queried axiom and {@code owl:Thing} as
	 * filler.
	 */
	private final IndexedClassExpression existential_;

	/**
	 * The domain from the queried axiom.
	 */
	private final IndexedClassExpression domain_;

	/**
	 * @param query
	 *            What entailment is queried.
	 * @param existential
	 *            Indexed individual that corresponds to an Existential with
	 *            property from the queried axiom and {@code owl:Thing} as
	 *            filler.
	 * @param domain
	 *            Indexed class expression that corresponds to the domain from
	 *            the queried axiom.
	 */
	public ObjectPropertyDomainEntailmentQuery(
			final ObjectPropertyDomainAxiomEntailment query,
			final IndexedClassExpression existential,
			final IndexedClassExpression domain) {
		super(query);
		this.existential_ = existential;
		this.domain_ = domain;
	}

	@Override
	public Collection<? extends IndexedContextRoot> getPositivelyIndexed() {
		return Collections.singleton(existential_);
	}

	@Override
	public ReasonerProof<EntailmentInference> getEvidence(
			final boolean atMostOne, final SaturationState<?> saturationState,
			final SaturationConclusion.Factory conclusionFactory)
			throws ElkQueryException {
		return new ReasonerProof<EntailmentInference>() {

			@Override
			public Collection<? extends EntailmentInference> getInferences(
					final Object conclusion) {

				if (!getQuery().equals(conclusion)) {
					return Collections.emptySet();
				}
				// else

				final Context context = saturationState.getContext(existential_);
				if (context == null) {
					// not entailed
					return Collections.emptySet();
				}
				// else

				final SubClassInclusionComposed subsumption = conclusionFactory
						.getSubClassInclusionComposed(existential_, domain_);
				if (context.containsConclusion(subsumption)) {
					return Collections.singleton(
							new DerivedClassInclusionEntailsObjectPropertyDomainAxiomImpl(
									getQuery(), subsumption));
				}
				// else

				return Collections.emptySet();
			}

		};
	}

}
