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

import java.util.LinkedHashMap;
import java.util.Map;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.elk.util.hashing.HashGenerator;
import org.semanticweb.elk.util.hashing.Hasher;

/**
 * Helper class for hashing Nodes based on getMembers(). Implements some caching
 * to ensure good performance even when some nodes have unusually many members.
 * 
 * @author Markus Kroetzsch
 * @author Frantisek Simancik
 * 
 */
public class NodeHasher implements Hasher<Node<? extends ElkEntity>> {

	/**
	 * We use one static instance for hashing (and caching!) all nodes.
	 */
	public static NodeHasher INSTANCE = new NodeHasher();

	private NodeHasher() {
	}

	/**
	 * IRI-based hasher used to cache members of a node.
	 */
	final Hasher<ElkEntity> elkEntityHasher = new Hasher<ElkEntity>() {

		@Override
		public int hash(ElkEntity elkEntity) {
			return elkEntity.getIri().hashCode();
		}
	};

	/**
	 * For nodes with at least this number of member classes, hash codes are
	 * cached in the hashCache.
	 */
	final int cacheNodeMemberNo = 50;

	/**
	 * Maximum number of large class nodes for which the hash value is cached.
	 */
	final int cacheMaxSize = 100;

	/**
	 * A simple Least Recently Used cache for the hashcodes of nodes that have a
	 * particularly large number of members. There should not be many such nodes
	 * in typical ontologies. Normally, this occurs if many classes are
	 * equivalent to owl:Nothing due to some modelling error.
	 */
	final LinkedHashMap<Node<? extends ElkEntity>, Integer> hashCache = new LinkedHashMap<Node<? extends ElkEntity>, Integer>() {
		private static final long serialVersionUID = 1;

		@Override
		protected boolean removeEldestEntry(
				Map.Entry<Node<? extends ElkEntity>, Integer> eldest) {
			return size() > cacheMaxSize;
		}
	};

	/**
	 * Compute the hash for an entity node. This method implements a simple
	 * cache for nodes with unusually large numbers of members. This mainly
	 * covers the case where a huge number of classes is equal to owl:Nothing
	 * due to modelling errors.
	 * 
	 * @param node
	 *            the entity node for which to compute the hash
	 * @return the resulting hash
	 */
	@Override
	public int hash(Node<? extends ElkEntity> node) {
		if (node.size() >= cacheNodeMemberNo) {
			if (hashCache.containsKey(node)) {
				return hashCache.get(node);
			}
			// else
			int hash = HashGenerator.combineMultisetHash(true, node,
					elkEntityHasher);
			hashCache.put(node, hash);
			return hash;
		}
		// else
		return HashGenerator.combineMultisetHash(true, node, elkEntityHasher);
	}
}