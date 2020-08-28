package org.semanticweb.elk.owlapi.query;

/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2018 Department of Computer Science, University of Oxford
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

import java.util.Collection;

import org.semanticweb.elk.reasoner.completeness.IncompleteResult;
import org.semanticweb.elk.reasoner.completeness.IncompleteTestOutput;
import org.semanticweb.elk.testing.DiffableOutput;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.reasoner.Node;

public abstract class OwlDirectRelatedEntitiesTestOutput<E extends OWLEntity, O extends OwlDirectRelatedEntitiesTestOutput<E, O>>
		extends IncompleteTestOutput<Collection<? extends Node<E>>>
		implements DiffableOutput<OWLAxiom, O> {

	private final ThisOwlDirectRelatedEntitiesDiffable<E> diffable_;

	OwlDirectRelatedEntitiesTestOutput(
			IncompleteResult<? extends Collection<? extends Node<E>>> incompleteDisjointNodes) {
		super(incompleteDisjointNodes);
		this.diffable_ = new ThisOwlDirectRelatedEntitiesDiffable<>(
				incompleteDisjointNodes);
	}

	OwlDirectRelatedEntitiesTestOutput(
			Collection<? extends Node<E>> disjointNodes) {
		super(disjointNodes);
		this.diffable_ = new ThisOwlDirectRelatedEntitiesDiffable<>(
				disjointNodes);
	}

	ThisOwlDirectRelatedEntitiesDiffable<E> getDiffable() {
		return this.diffable_;
	}

	@Override
	public boolean containsAllElementsOf(O other) {
		return diffable_.containsAllElementsOf(other.getDiffable());
	}

	@Override
	public void reportMissingElementsOf(O other, Listener<OWLAxiom> listener) {
		diffable_.reportMissingElementsOf(other.getDiffable(),
				adaptListener(listener));
	}

	protected abstract OwlDirectRelatedEntitiesDiffable.Listener<E> adaptListener(
			Listener<OWLAxiom> listener);

	static class ThisOwlDirectRelatedEntitiesDiffable<E extends OWLEntity>
			extends
			OwlDirectRelatedEntitiesDiffable<E, ThisOwlDirectRelatedEntitiesDiffable<E>> {

		ThisOwlDirectRelatedEntitiesDiffable(
				IncompleteResult<? extends Collection<? extends Node<E>>> incompleteDisjointNodes) {
			super(incompleteDisjointNodes);
		}

		ThisOwlDirectRelatedEntitiesDiffable(
				Collection<? extends Node<E>> disjointNodes) {
			super(disjointNodes);
		}
	}

}
