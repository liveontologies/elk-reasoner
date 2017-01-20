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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.semanticweb.elk.reasoner.entailments.model.Entailment;
import org.semanticweb.elk.reasoner.entailments.model.EntailmentInference;
import org.semanticweb.elk.reasoner.entailments.model.EntailmentInferenceSet;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SaturationConclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.util.collections.ArrayHashMap;

/**
 * An {@link IndexedEntailmentQuery} that first checks entailment of its
 * premises.
 * <p>
 * {@link #getEvidence(boolean, SaturationState, SaturationConclusion.Factory)}
 * combines {@link EntailmentInferenceSet}s of the premises and adds
 * {@link EntailmentInference} returned by {@link #getEntailmentInference()}.
 * 
 * @author Peter Skocovsky
 *
 * @param <E>
 *            Type of the entailment that is queried.
 * @param <P>
 *            Type of the premises.
 */
public abstract class AbstractEntailmentQueryWithPremises<E extends Entailment, P extends IndexedEntailmentQuery<? extends Entailment>>
		extends AbstractIndexedEntailmentQuery<E> {

	/**
	 * The {@link IndexedEntailmentQuery}-ies on which this one depends.
	 */
	private final List<P> premises_;

	/**
	 * @param query
	 *            What entailment is queried.
	 * @param premises
	 *            The premises.
	 */
	public AbstractEntailmentQueryWithPremises(final E query,
			final List<P> premises) {
		super(query);
		this.premises_ = premises;
	}

	public List<P> getPremises() {
		return premises_;
	}

	@Override
	public Collection<? extends IndexedContextRoot> getPositivelyIndexed() {

		final Collection<IndexedContextRoot> result = new ArrayList<IndexedContextRoot>(
				premises_.size());

		for (final P subsumption : premises_) {
			result.addAll(subsumption.getPositivelyIndexed());
		}

		return result;
	}

	@Override
	public <C extends Context> EntailmentInferenceSet getEvidence(
			final boolean atMostOne, final SaturationState<C> saturationState,
			final SaturationConclusion.Factory conclusionFactory)
			throws ElkQueryException {

		final Map<Entailment, Collection<? extends EntailmentInference>> premiseEvidence = new ArrayHashMap<Entailment, Collection<? extends EntailmentInference>>();

		for (final P subsumption : premises_) {
			final Entailment entailment = subsumption.getQuery();
			final Collection<? extends EntailmentInference> infs = subsumption
					.getEvidence(atMostOne, saturationState, conclusionFactory)
					.getInferences(entailment);
			if (infs == null || infs.isEmpty()) {
				// Not entailed!
				return new EntailmentInferenceSet() {

					@Override
					public Collection<? extends EntailmentInference> getInferences(
							final Entailment conclusion) {
						return Collections.emptySet();
					}

				};
			}
			premiseEvidence.put(entailment, infs);
		}

		return new EntailmentInferenceSet() {

			@Override
			public Collection<? extends EntailmentInference> getInferences(
					final Entailment conclusion) {

				final Collection<? extends EntailmentInference> result = premiseEvidence
						.get(conclusion);
				if (result != null) {
					return result;
				}
				// else

				if (!getQuery().equals(conclusion)) {
					return Collections.emptyList();
				}
				// else

				return getEntailmentInference();
			}

		};
	}

	/**
	 * This is called only when all premises are entailed.
	 * 
	 * @return The collection of {@link EntailmentInference} that entail
	 *         {@link #getQuery()} from {@link #getPremises()}.
	 */
	protected abstract Collection<? extends EntailmentInference> getEntailmentInference();

}
