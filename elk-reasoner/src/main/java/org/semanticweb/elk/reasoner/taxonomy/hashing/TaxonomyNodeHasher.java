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

import org.semanticweb.elk.reasoner.taxonomy.nodes.Node;
import org.semanticweb.elk.reasoner.taxonomy.nodes.TaxonomyNode;
import org.semanticweb.elk.util.hashing.HashGenerator;
import org.semanticweb.elk.util.hashing.Hasher;

/**
 * A helper class to compute the structural hash code for {@link TaxonomyNode}
 * objects. If two {@link TaxonomyNode} objects are structurally equivalent then
 * {@link #hash(TaxonomyNode)} is guaranteed to return the same values for them.
 * Two {@link TaxonomyNode}s are structurally equivalent if they are
 * structurally equivalent as defined in {@link NodeHasher}, and the set of
 * their direct sub nodes and super nodes are structurally equivalent. Two sets
 * of {@link Node}s are structurally equivalent if there is a bijection between
 * the members in the two sets mapping {@link Node}s to structurally equivalent
 * ones as defined in {@link NodeHasher}.
 * 
 * @see NodeHasher
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 * @param <K>
 *            the type of the keys for the node members
 * 
 */
public class TaxonomyNodeHasher<K> implements Hasher<TaxonomyNode<K, ?>> {

	private final static int SUB_CLASS_HASH_ = "subClassOf".hashCode();
	private final static int SUPER_CLASS_HASH_ = "superClassOf".hashCode();

	/**
	 * the hasher for {@link Node}s based on member keys
	 */
	private final Hasher<Node<K, ?>> nodeHasher_;

	public TaxonomyNodeHasher(Hasher<Node<K, ?>> nodeHasher) {
		this.nodeHasher_ = nodeHasher;
	}

	@Override
	public int hash(TaxonomyNode<K, ?> node) {
		int memberHash = nodeHasher_.hash(node);

		int subClassHash = SUB_CLASS_HASH_;
		for (TaxonomyNode<K, ?> o : node.getDirectSubNodes()) {
			subClassHash = HashGenerator.combineMultisetHash(false,
					subClassHash, nodeHasher_.hash(o));
		}

		int superClassHash = SUPER_CLASS_HASH_;
		for (TaxonomyNode<K, ?> o : node.getDirectSuperNodes()) {
			superClassHash = HashGenerator.combineMultisetHash(false,
					superClassHash, nodeHasher_.hash(o));
		}

		return HashGenerator.combineListHash(memberHash, subClassHash,
				superClassHash);
	}

}
