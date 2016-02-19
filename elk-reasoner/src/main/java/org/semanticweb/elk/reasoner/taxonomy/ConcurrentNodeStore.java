package org.semanticweb.elk.reasoner.taxonomy;

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

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.semanticweb.elk.reasoner.taxonomy.model.ComparatorKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.NodeFactory;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableGenericNodeStore;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableNode;

/**
 * An updateable generic node store providing some concurrency guarantees.
 * 
 * TODO: documentation
 * 
 * @author Peter Skocovsky
 *
 * @param <T>
 *            The type of members of the nodes in this store.
 * @param <N>
 *            The type of nodes in this store.
 */
public class ConcurrentNodeStore<T, N extends UpdateableNode<T>>
		implements UpdateableGenericNodeStore<T, N> {

	/**
	 * The key provider for members of the nodes in this node store.
	 */
	private final ComparatorKeyProvider<? super T> keyProvider_;
	/**
	 * The map from the member keys to the nodes containing the members.
	 */
	private final ConcurrentMap<Object, N> nodeLookup_;
	/**
	 * The set of all nodes.
	 */
	private final Set<N> allNodes_;

	/**
	 * Creates the node store.
	 * 
	 * @param keyProvider
	 *            The key provider for members of the nodes in this node store.
	 */
	public ConcurrentNodeStore(
			final ComparatorKeyProvider<? super T> keyProvider) {
		keyProvider_ = keyProvider;
		nodeLookup_ = new ConcurrentHashMap<Object, N>();
		allNodes_ = Collections
				.newSetFromMap(new ConcurrentHashMap<N, Boolean>());
	}

	@Override
	public N getNode(final T member) {
		return nodeLookup_.get(keyProvider_.getKey(member));
	}

	@Override
	public Set<N> getNodes() {
		return Collections.unmodifiableSet(allNodes_);
	}

	@Override
	public ComparatorKeyProvider<? super T> getKeyProvider() {
		return keyProvider_;
	}

	@Override
	public N getCreateNode(final Iterable<? extends T> members, final int size,
			final NodeFactory<T, N> factory) {
		for (final T member : members) {
			final N previous = getNode(member);
			if (previous != null) {
				synchronized (previous) {
					if (previous.size() < size) {
						previous.setMembers(members);
					} else {
						return previous;
					}
				}
				for (final T m : members) {
					nodeLookup_.put(keyProvider_.getKey(m), previous);
				}
				return previous;
			}
		}
		final N node = factory.createNode(members, size, keyProvider_);
		final T canonicalMember = node.getCanonicalMember();
		final N previous = nodeLookup_
				.putIfAbsent(keyProvider_.getKey(canonicalMember), node);
		if (previous != null) {
			return previous;
		}
		allNodes_.add(node);
		for (final T member : node) {
			if (member != canonicalMember) {
				nodeLookup_.put(keyProvider_.getKey(member), node);
			}
		}
		return node;
	}

	@Override
	public boolean removeNode(final T member) {

		final N node = getNode(member);
		if (node == null) {
			return false;
		}

		boolean changed = false;
		if (allNodes_.remove(node)) {
			for (final T m : node) {
				changed |= nodeLookup_.remove(keyProvider_.getKey(m)) != null;
			}
		}

		return changed;
	}

}
