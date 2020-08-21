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
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.reasoner.completeness.IncompleteResult;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.testing.DiffableOutput;

public class ObjectPropertyTaxonomyTestOutput
		implements DiffableOutput<ElkAxiom, ObjectPropertyTaxonomyTestOutput> {

	private final TaxonomyEntailment<ElkObjectProperty, Taxonomy<ElkObjectProperty>, TaxonomyEntailment.Listener<ElkObjectProperty>> taxEntailment_;

	private boolean isComplete_;

	public ObjectPropertyTaxonomyTestOutput(
			Taxonomy<ElkObjectProperty> taxonomy, boolean isComplete) {
		this.taxEntailment_ = new TaxonomyEntailment<>(taxonomy);
		this.isComplete_ = isComplete;
	}

	public ObjectPropertyTaxonomyTestOutput(
			IncompleteResult<? extends Taxonomy<ElkObjectProperty>> taxonomy) {
		this(taxonomy.getValue(), taxonomy.isComplete());
	}

	@Override
	public boolean containsAllElementsOf(
			ObjectPropertyTaxonomyTestOutput other) {
		return !isComplete_ || taxEntailment_.containsEntitiesAndEntailmentsOf(
				other.taxEntailment_.getTaxonomy());
	}

	@Override
	public void reportMissingElementsOf(ObjectPropertyTaxonomyTestOutput other,
			Listener<ElkAxiom> listener) {
		if (isComplete_) {
			taxEntailment_.reportMissingEntitiesAndEntailmentsOf(
					other.taxEntailment_.getTaxonomy(),
					new ElkObjectPropertyTaxonomyEntailmentAdapter(listener));
		}
	}

}
