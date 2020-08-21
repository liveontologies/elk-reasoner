/*
 * #%L
 * ELK Reasoner
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
import java.util.HashMap;
import java.util.Map;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.elk.testing.Diffable;

/**
 * A test output of a query for related entities.
 * 
 * @author Peter Skocovsky
 * @author Yevgeny Kazakov
 *
 * @param <E>
 *            the type of entities contained in the output
 *
 * @param <O>
 *            the type of the output which this output can be compared
 */
public class ElkDirectRelatedEntitiesDiffable<E extends ElkEntity, O extends ElkDirectRelatedEntitiesDiffable<E, O>>
		implements Diffable<O, ElkDirectRelatedEntitiesDiffable.Listener<E>> {

	private final Map<ElkIri, Node<E>> nodesByCanonical_;

	private Map<ElkIri, Node<E>> nodesByElements_ = null;

	private final boolean isComplete_;

	ElkDirectRelatedEntitiesDiffable(Iterable<? extends Node<E>> disjointNodes,
			int estimatedSize, boolean isComplete) {
		nodesByCanonical_ = new HashMap<>(estimatedSize);
		disjointNodes.forEach(
				n -> nodesByCanonical_.put(n.getCanonicalMember().getIri(), n));
		this.isComplete_ = isComplete;
	}

	ElkDirectRelatedEntitiesDiffable(
			Collection<? extends Node<E>> disjointNodes, boolean isComplete) {
		this(disjointNodes, disjointNodes.size(), isComplete);
	}

	Map<ElkIri, Node<E>> getNodesByCanonical() {
		return this.nodesByCanonical_;
	}

	boolean isComplete() {
		return isComplete_;
	}

	synchronized Map<ElkIri, Node<E>> getNodesByElements() {
		// lazy initialization
		if (this.nodesByElements_ == null) {
			nodesByElements_ = new HashMap<>();
			nodesByCanonical_.values().forEach(
					n -> n.forEach(e -> nodesByElements_.put(e.getIri(), n)));
		}
		return this.nodesByElements_;
	}

	@Override
	public boolean containsAllElementsOf(O other) {
		if (!isComplete_) {
			return true;
		}
		Map<ElkIri, Node<E>> otherNodesByCanonical = other
				.getNodesByCanonical();
		if (other.isComplete()
				&& otherNodesByCanonical.size() > nodesByCanonical_.size()) {
			return false;
		}
		for (ElkIri iri : otherNodesByCanonical.keySet()) {
			Node<E> node = nodesByCanonical_.get(iri);
			if (node == null) {
				if (other.isComplete()) {
					return false;
				}
				// else may be not directly related
				continue;
			}
			Node<E> otherNode = otherNodesByCanonical.get(iri);
			if (node.size() < otherNode.size()) {
				return false;
			}
			for (E otherMember : otherNode) {
				if (!node.contains(otherMember)) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public void reportMissingElementsOf(O other, Listener<E> listener) {
		if (!isComplete_) {
			return;
		}
		Map<ElkIri, Node<E>> otherNodesByCanonical = other
				.getNodesByCanonical();
		for (ElkIri iri : otherNodesByCanonical.keySet()) {
			Node<E> otherNode = otherNodesByCanonical.get(iri);
			E canonical = otherNode.getCanonicalMember();
			Node<E> node = getNodesByElements().get(iri);
			if (node == null) {
				if (other.isComplete()) {
					listener.missingCanonical(canonical);
				}
				continue;
			}
			// else compare the nodes
			for (E otherMember : otherNode) {
				if (!node.contains(otherMember)) {
					listener.missingMember(canonical, otherMember);
				}
			}
		}
	}

	public interface Listener<E> {

		void missingCanonical(E canonical);

		void missingMember(E canonical, E member);

	}

}
