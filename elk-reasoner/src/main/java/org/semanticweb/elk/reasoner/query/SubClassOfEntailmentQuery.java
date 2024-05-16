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
import org.semanticweb.elk.reasoner.entailments.impl.DerivedClassInclusionEntailsSubClassOfAxiomImpl;
import org.semanticweb.elk.reasoner.entailments.impl.SubClassInconsistencyEntailsSubClassOfAxiomImpl;
import org.semanticweb.elk.reasoner.entailments.model.EntailmentInference;
import org.semanticweb.elk.reasoner.entailments.model.SubClassOfAxiomEntailment;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassInconsistency;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SaturationConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * Query whether an {@link org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom
 * ElkSubClassOfAxiom} is entailed.
 * 
 * @author Peter Skocovsky
 */
public class SubClassOfEntailmentQuery
		extends AbstractIndexedEntailmentQuery<SubClassOfAxiomEntailment> {

	/**
	 * Indexed class that should be a subclass of {@link #superClass_}.
	 */
	private final IndexedClassExpression subClass_;

	/**
	 * Indexed class that should be a superclass of {@link #subClass_}.
	 */
	private final IndexedClassExpression superClass_;

	/**
	 * @param query
	 *            What entailment is queried.
	 * @param subClass
	 *            Indexed class that should be a subclass of {@code superClass}.
	 * @param superClass
	 *            Indexed class that should be a superclass of {@code subClass}.
	 */
	public SubClassOfEntailmentQuery(final SubClassOfAxiomEntailment query,
			final IndexedClassExpression subClass,
			final IndexedClassExpression superClass) {
		super(query);
		this.subClass_ = subClass;
		this.superClass_ = superClass;
	}

	@Override
	public Collection<? extends IndexedContextRoot> getPositivelyIndexed() {
		return Collections.singleton(subClass_);
	}

	@Override
	public Proof<EntailmentInference> getEvidence(
			final boolean atMostOne, final SaturationState<?> saturationState,
			final SaturationConclusion.Factory conclusionFactory)
			throws ElkQueryException {
		return new Proof<EntailmentInference>() {

			@Override
			public Collection<? extends EntailmentInference> getInferences(
					final Object conclusion) {

				final List<EntailmentInference> result = new ArrayList<EntailmentInference>(
						2);
				if (!getQuery().equals(conclusion)) {
					return result;
				}
				// else

				final Context context = saturationState.getContext(subClass_);
				if (context == null) {
					// not entailed
					return result;
				}
				// else

				final ClassInconsistency contradiction = conclusionFactory
						.getContradiction(subClass_);
				final SubClassInclusionComposed subsumption = conclusionFactory
						.getSubClassInclusionComposed(subClass_, superClass_);
				if (context.containsConclusion(contradiction)) {
					result.add(
							new SubClassInconsistencyEntailsSubClassOfAxiomImpl(
									getQuery(), contradiction));
					if (atMostOne) {
						return result;
					}
					// else
				}
				if (context.containsConclusion(subsumption)) {
					result.add(
							new DerivedClassInclusionEntailsSubClassOfAxiomImpl(
									getQuery(), subsumption));
				}

				return result;
			}

		};
	}

}
