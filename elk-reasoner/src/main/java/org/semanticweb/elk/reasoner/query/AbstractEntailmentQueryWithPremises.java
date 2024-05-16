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

import org.liveontologies.puli.Proof;
import org.liveontologies.puli.Proofs;
import org.semanticweb.elk.reasoner.entailments.model.Entailment;
import org.semanticweb.elk.reasoner.entailments.model.EntailmentInference;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SaturationConclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * An {@link IndexedEntailmentQuery} that first checks entailment of its
 * premises.
 * <p>
 * {@link #getEvidence(boolean, SaturationState, SaturationConclusion.Factory)}
 * combines {@link Proof}s over {@link EntailmentInference}s of the premises and
 * adds {@link EntailmentInference} returned by
 * {@link #getEntailmentInference()}.
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
	public Proof<EntailmentInference> getEvidence(final boolean atMostOne,
			final SaturationState<?> saturationState,
			final SaturationConclusion.Factory conclusionFactory)
			throws ElkQueryException {

		final Collection<Proof<EntailmentInference>> proofs = new ArrayList<Proof<EntailmentInference>>();

		for (final P premise : premises_) {
			proofs.add(premise.getEvidence(atMostOne, saturationState,
					conclusionFactory));
		}

		proofs.add(new Proof<EntailmentInference>() {

			@Override
			public Collection<? extends EntailmentInference> getInferences(
					final Object conclusion) {

				if (!getQuery().equals(conclusion)) {
					return Collections.emptyList();
				}
				// else

				return getEntailmentInference();
			}

		});

		return Proofs.union(proofs);
	}

	/**
	 * This is called only when all premises are entailed.
	 * 
	 * @return The collection of {@link EntailmentInference} that entail
	 *         {@link #getQuery()} from {@link #getPremises()}.
	 */
	public abstract Collection<? extends EntailmentInference> getEntailmentInference();

}
