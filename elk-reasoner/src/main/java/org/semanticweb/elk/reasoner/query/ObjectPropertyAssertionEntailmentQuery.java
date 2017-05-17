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

import org.semanticweb.elk.reasoner.entailments.impl.DerivedClassInclusionEntailsObjectPropertyAssertionAxiomImpl;
import org.semanticweb.elk.reasoner.entailments.model.Entailment;
import org.semanticweb.elk.reasoner.entailments.model.EntailmentInference;
import org.semanticweb.elk.reasoner.entailments.model.EntailmentProof;
import org.semanticweb.elk.reasoner.entailments.model.ObjectPropertyAssertionAxiomEntailment;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexedIndividual;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SaturationConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * Query whether an
 * {@link org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAssertionAxiom
 * ElkObjectPropertyAssertionAxiom} is entailed.
 * 
 * @author Peter Skocovsky
 */
public class ObjectPropertyAssertionEntailmentQuery extends
		AbstractIndexedEntailmentQuery<ObjectPropertyAssertionAxiomEntailment> {

	/**
	 * The subject of the queried property assertion.
	 */
	private final IndexedIndividual subject_;

	/**
	 * Existential, where property is from the queried property assertion and
	 * filler is the object from the queried property assertion.
	 */
	private final IndexedClassExpression objectExistential_;

	/**
	 * @param query
	 *            What entailment is queried.
	 * @param subject
	 *            Indexed individual that corresponds to the subject of the
	 *            queried property assertion.
	 * @param objectExistential
	 *            Indexed class expression that corresponds to the existential,
	 *            where property is from the queried property assertion and
	 *            filler is the object from the queried property assertion.
	 */
	public ObjectPropertyAssertionEntailmentQuery(
			final ObjectPropertyAssertionAxiomEntailment query,
			final IndexedIndividual subject,
			final IndexedClassExpression objectExistential) {
		super(query);
		this.subject_ = subject;
		this.objectExistential_ = objectExistential;
	}

	@Override
	public Collection<? extends IndexedContextRoot> getPositivelyIndexed() {
		return Collections.singleton(subject_);
	}

	@Override
	public <C extends Context> EntailmentProof getEvidence(
			final boolean atMostOne, final SaturationState<C> saturationState,
			final SaturationConclusion.Factory conclusionFactory)
			throws ElkQueryException {
		return new EntailmentProof() {

			@Override
			public Collection<? extends EntailmentInference> getInferences(
					final Entailment conclusion) {

				if (!getQuery().equals(conclusion)) {
					return Collections.emptySet();
				}
				// else

				final C context = saturationState.getContext(subject_);
				if (context == null) {
					// not entailed
					return Collections.emptySet();
				}
				// else

				final SubClassInclusionComposed subsumption = conclusionFactory
						.getSubClassInclusionComposed(subject_,
								objectExistential_);
				if (context.containsConclusion(subsumption)) {
					return Collections.singleton(
							new DerivedClassInclusionEntailsObjectPropertyAssertionAxiomImpl(
									getQuery(), subsumption));
				}
				// else

				return Collections.emptySet();
			}

		};
	}

}
