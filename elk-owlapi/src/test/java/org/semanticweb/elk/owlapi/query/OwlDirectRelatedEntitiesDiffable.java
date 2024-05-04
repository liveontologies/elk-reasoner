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
package org.semanticweb.elk.owlapi.query;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.semanticweb.elk.reasoner.completeness.IncompleteResult;
import org.semanticweb.elk.reasoner.completeness.IncompleteTestOutput;
import org.semanticweb.elk.testing.Diffable;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.reasoner.Node;

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
public class OwlDirectRelatedEntitiesDiffable<E extends OWLEntity, O extends OwlDirectRelatedEntitiesDiffable<E, O>>
		extends IncompleteTestOutput<Collection<? extends Node<E>>>
		implements Diffable<O, OwlDirectRelatedEntitiesDiffable.Listener<E>> {

	private final Map<E, Node<E>> nodesByMembers_;

	OwlDirectRelatedEntitiesDiffable(
			IncompleteResult<? extends Collection<? extends Node<E>>> incompleteDisjointNodes) {
		super(incompleteDisjointNodes);
		nodesByMembers_ = new HashMap<>(getValue().size());
		getValue().forEach(n -> n.forEach(e -> nodesByMembers_.put(e, n)));
	}

	OwlDirectRelatedEntitiesDiffable(
			Collection<? extends Node<E>> disjointNodes) {
		super(disjointNodes);
		nodesByMembers_ = new HashMap<>(getValue().size());
		getValue().forEach(n -> n.forEach(e -> nodesByMembers_.put(e, n)));
	}

	Map<E, Node<E>> getNodesByMembers() {
		return this.nodesByMembers_;
	}

	@Override
	public boolean containsAllElementsOf(O other) {
		if (!isComplete()) {
			return true;
		}
		Map<E, Node<E>> otherNodesByMembers = other.getNodesByMembers();
		if (other.isComplete()
				&& otherNodesByMembers.size() > nodesByMembers_.size()) {
			return false;
		}
		for (E member : otherNodesByMembers.keySet()) {
			Node<E> otherNode = otherNodesByMembers.get(member);
			if (otherNode.getRepresentativeElement() != member) {
				// optimization: testing each node only once
				continue;
			}
			Node<E> node = nodesByMembers_.get(member);
			if (node == null) {
				if (other.isComplete()) {
					return false;
				}
				// else may be not directly related
				continue;
			}
			if (node.getSize() < otherNode.getSize()) {
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
		if (!isComplete()) {
			return;
		}
		Map<E, Node<E>> otherNodesByMembers = other.getNodesByMembers();
		for (E member : otherNodesByMembers.keySet()) {
			Node<E> otherNode = otherNodesByMembers.get(member);
			E representative = otherNode.getRepresentativeElement();
			if (representative != member) {
				// optimization: testing each node only once
				continue;
			}
			Node<E> node = getNodesByMembers().get(member);
			if (node == null) {
				if (other.isComplete()) {
					listener.missingCanonical(representative);
				}
				continue;
			}
			// else compare the nodes
			for (E otherMember : otherNode) {
				if (!node.contains(otherMember)) {
					listener.missingMember(representative, otherMember);
				}
			}
		}
	}

	public interface Listener<E> {

		void missingCanonical(E canonical);

		void missingMember(E canonical, E member);

	}

}
