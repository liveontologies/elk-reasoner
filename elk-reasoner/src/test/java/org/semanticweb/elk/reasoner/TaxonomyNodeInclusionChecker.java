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

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;

class TaxonomyNodeInclusionChecker<T extends ElkEntity, N extends TaxonomyNode<T>> {

	private final N node_;

	private final Listener<? super T> listener_;

	private Set<T> superMembers_ = null;

	TaxonomyNodeInclusionChecker(N node, Listener<? super T> listener) {
		this.node_ = node;
		this.listener_ = listener;
	}

	boolean checkLogicalInclusion(N otherNode) {
		boolean result = true;
		T canonical = node_.getCanonicalMember();
		for (T member : otherNode) {
			if (!node_.contains(member)) {
				result = false;
				listener_.missingEquivalence(canonical, member);
			}
			if (listener_.abort()) {
				return result;
			}
		}
		// else
		for (TaxonomyNode<T> otherSuperNodes : otherNode
				.getDirectSuperNodes()) {
			T otherSuperCanonical = otherSuperNodes.getCanonicalMember();
			if (containsSuperMember(otherSuperCanonical)) {
				result = false;
				listener_.missingSubsumption(canonical, otherSuperCanonical);
			}
			if (listener_.abort()) {
				return result;
			}
		}
		return result;
	}

	private boolean containsSuperMember(T member) {
		int nDirectSuper = node_.getDirectSuperNodes().size();
		if (superMembers_ == null) {
			// first fill with canonical members of direct super nodes,
			// which are most likely candidates
			superMembers_ = new HashSet<T>(nDirectSuper);
			for (TaxonomyNode<T> direct : node_.getDirectSuperNodes()) {
				superMembers_.add(direct.getCanonicalMember());
			}
		}
		if (superMembers_.contains(member)) {
			return true;
		}
		// else fill with all members of all super nodes, if not already done so
		if (superMembers_.size() > nDirectSuper) {
			// already filled and not found
			return false;
		}
		// else
		for (T equivalent : node_) {
			superMembers_.add(equivalent); // will add at least one new element!
		}
		for (TaxonomyNode<T> sup : node_.getAllSuperNodes()) {
			for (T supMember : sup) {
				superMembers_.add(supMember);
			}
		}
		return superMembers_.contains(member);
	}

	interface Listener<M extends ElkEntity> {

		/**
		 * @return {@code true} if the inclusion checking should be aborted and
		 *         {@code false} if it should continue
		 */
		boolean abort();

		/**
		 * Reports missing equivalence between the given entities. That is, the
		 * given entities are expected to be members of the same node, but they
		 * are not
		 * 
		 * @param first
		 * @param second
		 */
		void missingEquivalence(M first, M second);

		/**
		 * Reports missing equivalence between the given entities. That is, the
		 * first entity is expected to appear below the second entity in the
		 * taxonomy, but it does not
		 * 
		 * @param sub
		 * @param sup
		 */
		void missingSubsumption(M sub, M sup);

	}

}
