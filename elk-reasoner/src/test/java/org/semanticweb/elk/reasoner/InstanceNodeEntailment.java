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
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;

public class InstanceNodeEntailment<T extends ElkEntity, I extends ElkEntity, N extends InstanceNode<T, I>>
		extends NodeEntailment<I, N> {

	public InstanceNodeEntailment(N node) {
		super(node);
	}

	public boolean containsAllDirectTypesOf(N other) {
		Set<T> otherDirectTypes = getDirectTypes(other);
		return getDirectTypes(getNode()).containsAll(otherDirectTypes)
				|| getAllTypes(getNode()).containsAll(otherDirectTypes);
	}

	public void reportMissingDirectTypesOf(N other, Listener<T, I> listener) {
		Set<T> allTypes = getAllTypes(getNode());
		Set<T> otherDirectTypes = getDirectTypes(other);
		for (T otherDirectType : otherDirectTypes) {
			if (!allTypes.contains(otherDirectType)) {
				listener.reportMissingDirectType(otherDirectType);
			}
		}
	}

	static <M extends ElkEntity, I extends ElkEntity> Set<M> getDirectTypes(
			InstanceNode<M, I> node) {
		Set<? extends Node<M>> directTypes = node.getDirectTypeNodes();
		Set<M> result = new HashSet<M>(directTypes.size());
		for (Node<M> directSubsumer : directTypes) {
			result.add(directSubsumer.getCanonicalMember());
		}
		return result;
	}

	static <M extends ElkEntity, I extends ElkEntity> Set<M> getAllTypes(
			InstanceNode<M, I> node) {
		Set<? extends Node<M>> allTypeNodes = node.getAllTypeNodes();
		Set<M> result = new HashSet<M>(allTypeNodes.size());
		for (Node<M> superNode : allTypeNodes) {
			for (M member : superNode) {
				result.add(member);
			}
		}
		return result;
	}

	public interface Listener<T extends ElkEntity, I extends ElkEntity>
			extends NodeEntailment.Listener<I> {

		public void reportMissingDirectType(T type);

	}

}
