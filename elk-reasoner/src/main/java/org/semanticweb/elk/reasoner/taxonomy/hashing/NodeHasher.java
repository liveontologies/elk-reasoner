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
package org.semanticweb.elk.reasoner.taxonomy.hashing;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.semanticweb.elk.reasoner.taxonomy.nodes.Node;
import org.semanticweb.elk.util.hashing.HashGenerator;
import org.semanticweb.elk.util.hashing.Hasher;

/**
 * A helper class to compute the structural hash code for {@link Node} objects.
 * If two {@link Node} objects are structurally equivalent then
 * {@link #hash(Node)} is guaranteed to return the same values for them. Two
 * {@link Node}s are structurally equivalent if they have the same key sets for
 * their members.
 * 
 * @author Markus Kroetzsch
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 * @param <K>
 *            the type of the keys for the node members
 */
public class NodeHasher<K> implements Hasher<Node<K, ?>> {

	/**
	 * the hasher for keys of {@link Node}s
	 */
	private final Hasher<K> keyHasher_ = new Hasher<K>() {

		@Override
		public int hash(K key) {
			return key.hashCode();
		}
	};

	/**
	 * For nodes with at least this number of member classes, hash codes are
	 * cached in the hashCache.
	 */
	private final static int CACHE_NODE_SIZE_THRESHOLD_ = 50;

	/**
	 * Maximum number of large class nodes for which the hash value is cached.
	 */
	private final static int CACHE_MAX_SIZE_ = 100;

	/**
	 * A simple Least Recently Used cache for the hash codes of nodes that have
	 * a particularly large number of members. There should not be many such
	 * nodes in typical ontologies. Normally, this occurs if many classes are
	 * equivalent to owl:Nothing due to some modeling error.
	 */
	private final HashMap<Node<K, ?>, Integer> hashCache_ = new LinkedHashMap<Node<K, ?>, Integer>() {

		private static final long serialVersionUID = 2296974527757724276L;

		@Override
		protected boolean removeEldestEntry(
				Map.Entry<Node<K, ?>, Integer> eldest) {
			return size() > CACHE_MAX_SIZE_;
		}
	};

	/**
	 * Compute the hash for an node. This method implements a simple cache for
	 * nodes with unusually large numbers of members. This mainly covers the
	 * case where a huge number of classes is equal to owl:Nothing.
	 * 
	 * @param node
	 * @return the has for the given entity node
	 */
	@Override
	public int hash(Node<K, ?> node) {
		if (node.getMembersLookup().size() < CACHE_NODE_SIZE_THRESHOLD_)
			return HashGenerator.combineMultisetHash(true, node
					.getMembersLookup().keySet(), keyHasher_);
		// else caching the hash valuses
		if (hashCache_.containsKey(node))
			return hashCache_.get(node);
		// else
		int hash = HashGenerator.combineMultisetHash(true, node
				.getMembersLookup().keySet(), keyHasher_);
		hashCache_.put(node, hash);
		return hash;
	}

}