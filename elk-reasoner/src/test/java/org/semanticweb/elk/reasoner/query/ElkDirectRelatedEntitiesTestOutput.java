package org.semanticweb.elk.reasoner.query;

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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.elk.testing.DiffableOutput;

public abstract class ElkDirectRelatedEntitiesTestOutput<E extends ElkEntity, O extends ElkDirectRelatedEntitiesTestOutput<E, O>>
		implements DiffableOutput<ElkAxiom, O> {

	private final ThisElkDirectRelatedEntitiesDiffable<E> diffable_;

	ElkDirectRelatedEntitiesTestOutput(
			Collection<? extends Node<E>> disjointNodes, boolean isComplete) {
		this.diffable_ = new ThisElkDirectRelatedEntitiesDiffable<>(
				disjointNodes, isComplete);
	}

	ThisElkDirectRelatedEntitiesDiffable<E> getDiffable() {
		return this.diffable_;
	}

	@Override
	public boolean containsAllElementsOf(O other) {
		return diffable_.containsAllElementsOf(other.getDiffable());
	}

	@Override
	public void reportMissingElementsOf(O other, Listener<ElkAxiom> listener) {
		diffable_.reportMissingElementsOf(other.getDiffable(),
				adaptListener(listener));
	}

	protected abstract ElkDirectRelatedEntitiesDiffable.Listener<E> adaptListener(
			Listener<ElkAxiom> listener);

	static class ThisElkDirectRelatedEntitiesDiffable<E extends ElkEntity>
			extends
			ElkDirectRelatedEntitiesDiffable<E, ThisElkDirectRelatedEntitiesDiffable<E>> {

		ThisElkDirectRelatedEntitiesDiffable(
				Collection<? extends Node<E>> disjointNodes,
				boolean isComplete) {
			super(disjointNodes, isComplete);
		}
	}

}
