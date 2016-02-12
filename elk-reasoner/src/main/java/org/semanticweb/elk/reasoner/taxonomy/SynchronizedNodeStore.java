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
import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.reasoner.taxonomy.model.ComparatorKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableGenericNodeStore;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableNode;
import org.semanticweb.elk.util.collections.ArrayHashMap;
import org.semanticweb.elk.util.collections.ArrayHashSet;

public class SynchronizedNodeStore<T, N extends UpdateableNode<T>>
		implements UpdateableGenericNodeStore<T, N> {

	private final ComparatorKeyProvider<? super T> keyProvider_;
	private final Map<Object, N> nodeLookup_;
	private final Set<N> allNodes_;
	
	public SynchronizedNodeStore(final int capacity,
			final ComparatorKeyProvider<? super T> keyProvider) {
		keyProvider_ = keyProvider;
		nodeLookup_ = new ArrayHashMap<Object, N>(capacity);
		allNodes_ = new ArrayHashSet<N>(capacity);
	}
	
	public SynchronizedNodeStore(
			final ComparatorKeyProvider<? super T> keyProvider) {
		this(127, keyProvider);
	}
	
	@Override
	public synchronized N getNode(final T member) {
		return nodeLookup_.get(keyProvider_.getKey(member));
	}

	@Override
	public synchronized Set<N> getNodes() {
		return Collections.unmodifiableSet(allNodes_);
	}

	@Override
	public ComparatorKeyProvider<? super T> getKeyProvider() {
		return keyProvider_;
	}

	@Override
	public synchronized N putIfAbsent(final N node) {
		for (final T member : node) {
			final N previous = getNode(member);
			if (previous != null) {
				synchronized (previous) {
					if (previous.size() < node.size()) {
						previous.setMembers(node);
					} else {
						return previous;
					}
				}
				for (final T m : node) {
					nodeLookup_.put(keyProvider_.getKey(m), previous);
				}
				return previous;
			}
		}
		final N previous = nodeLookup_.get(
				keyProvider_.getKey(node.getCanonicalMember()));
		if (previous != null) {
			return previous;
		}
		allNodes_.add(node);
		for (T member : node) {
			nodeLookup_.put(keyProvider_.getKey(member), node);
		}
		return null;
	}

	@Override
	public synchronized boolean removeNode(final T member) {
		
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
