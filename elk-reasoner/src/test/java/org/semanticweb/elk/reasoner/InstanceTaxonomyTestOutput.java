/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
/**
 * 
 */
package org.semanticweb.elk.reasoner;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.reasoner.completeness.IncompleteResult;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.testing.DiffableOutput;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author Yevgeny Kazakov
 */
public class InstanceTaxonomyTestOutput
		implements DiffableOutput<ElkAxiom, InstanceTaxonomyTestOutput> {

	private final InstanceTaxonomyEntailment<ElkClass, ElkNamedIndividual, InstanceTaxonomy<ElkClass, ElkNamedIndividual>, InstanceTaxonomyEntailment.Listener<ElkClass, ElkNamedIndividual>> taxEntailment_;

	private boolean isComplete_;

	public InstanceTaxonomyTestOutput(
			InstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy,
			boolean isComplete) {
		this.taxEntailment_ = new InstanceTaxonomyEntailment<>(taxonomy);
		this.isComplete_ = isComplete;
	}

	public InstanceTaxonomyTestOutput(
			IncompleteResult<? extends InstanceTaxonomy<ElkClass, ElkNamedIndividual>> taxonomy) {
		this(taxonomy.getValue(), taxonomy.isComplete());
	}

	@Override
	public boolean containsAllElementsOf(InstanceTaxonomyTestOutput other) {
		return !isComplete_ || taxEntailment_.containsEntitiesAndEntailmentsOf(
				other.taxEntailment_.getTaxonomy());
	}

	@Override
	public void reportMissingElementsOf(InstanceTaxonomyTestOutput other,
			Listener<ElkAxiom> listener) {
		if (isComplete_) {
			taxEntailment_.reportMissingEntitiesAndEntailmentsOf(
					other.taxEntailment_.getTaxonomy(),
					new ElkInstanceTaxonomyEntailmentAdapter(listener));
		}
	}

}
