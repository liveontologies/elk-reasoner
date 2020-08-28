package org.semanticweb.elk.reasoner;

/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2020 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.completeness.IncompleteResult;
import org.semanticweb.elk.reasoner.completeness.IncompleteTestOutput;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.testing.DiffableOutput;

public abstract class AbstractTaxonomyTestOutput<E extends ElkEntity, O extends AbstractTaxonomyTestOutput<E, O>>
		extends IncompleteTestOutput<Taxonomy<E>>
		implements DiffableOutput<ElkAxiom, O> {

	private final TaxonomyEntailment<E, Taxonomy<E>, TaxonomyEntailment.Listener<E>> taxEntailment_;

	public AbstractTaxonomyTestOutput(
			IncompleteResult<? extends Taxonomy<E>> incompleteTaxonomy) {
		super(incompleteTaxonomy);
		this.taxEntailment_ = new TaxonomyEntailment<>(getValue());
	}

	public AbstractTaxonomyTestOutput(Taxonomy<E> taxonomy) {
		super(taxonomy);
		this.taxEntailment_ = new TaxonomyEntailment<>(getValue());
	}

	Taxonomy<E> getTaxonomy() {
		return taxEntailment_.getTaxonomy();
	}

	@Override
	public boolean containsAllElementsOf(O other) {
		return !isComplete() || taxEntailment_
				.containsEntitiesAndEntailmentsOf(other.getTaxonomy());
	}

	@Override
	public void reportMissingElementsOf(O other, Listener<ElkAxiom> listener) {
		if (isComplete()) {
			taxEntailment_.reportMissingEntitiesAndEntailmentsOf(
					other.getTaxonomy(), adapt(listener));
		}
	}

	abstract TaxonomyEntailment.Listener<E> adapt(Listener<ElkAxiom> listener);

}
