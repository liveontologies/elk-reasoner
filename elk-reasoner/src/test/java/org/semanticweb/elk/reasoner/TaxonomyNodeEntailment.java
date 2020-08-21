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
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;

public class TaxonomyNodeEntailment<M extends ElkEntity, N extends TaxonomyNode<M>>
		extends NodeEntailment<M, N> {

	public TaxonomyNodeEntailment(N node) {
		super(node);
	}

	public boolean containsDirectSubsumersOf(N other) {
		Set<M> otherDirectSubsumers = getDirectSubsumers(other);
		return getDirectSubsumers(getNode()).containsAll(otherDirectSubsumers)
				|| getAllSubsumers(getNode()).containsAll(otherDirectSubsumers);
	}

	public void reportMissingDirectSubsumersOf(N other, Listener<M> listener) {
		Set<M> allSubsumers = getAllSubsumers(getNode());
		Set<M> otherDirectSubsumers = getDirectSubsumers(other);
		for (M otherDirect : otherDirectSubsumers) {
			if (!allSubsumers.contains(otherDirect)) {
				listener.reportMissingDirectSubsumer(otherDirect);
			}
		}
	}

	static <M extends ElkEntity> Set<M> getDirectSubsumers(
			TaxonomyNode<M> node) {
		Set<? extends Node<M>> directSubsumers = node.getDirectSuperNodes();
		Set<M> result = new HashSet<M>(directSubsumers.size());
		for (Node<M> directSubsumer : directSubsumers) {
			result.add(directSubsumer.getCanonicalMember());
		}
		return result;
	}

	static <M extends ElkEntity> Set<M> getAllSubsumers(TaxonomyNode<M> node) {
		Set<? extends Node<M>> superNodes = node.getAllSuperNodes();
		Set<M> result = new HashSet<M>(node.size() + superNodes.size());
		for (M member : node) {
			result.add(member);
		}
		for (Node<M> superNode : superNodes) {
			for (M member : superNode) {
				result.add(member);
			}
		}
		return result;
	}

	public interface Listener<M extends ElkEntity>
			extends NodeEntailment.Listener<M> {

		public void reportMissingDirectSubsumer(M subsumer);

	}

}
