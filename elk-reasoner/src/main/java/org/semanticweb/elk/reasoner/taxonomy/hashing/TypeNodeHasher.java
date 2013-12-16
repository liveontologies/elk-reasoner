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
import org.semanticweb.elk.reasoner.taxonomy.nodes.TypeNode;
import org.semanticweb.elk.util.hashing.HashGenerator;
import org.semanticweb.elk.util.hashing.Hasher;

/**
 * A helper class to compute the structural hash code for {@link TypeNode}
 * objects. If two {@link TypeNode} objects are structurally equivalent then
 * {@link #hash(TypeNode)} is guaranteed to return the same values for them. Two
 * {@link TypeNode}s are structurally equivalent if they are structurally
 * equivalent as defined in {@link TaxonomyNodeHasher}, and the set of their
 * instance nodes are structurally equivalent. Two sets of {@link Node}s are
 * structurally equivalent if there is a bijection between the members in the
 * two sets mapping {@link Node}s to structurally equivalent ones as defined in
 * {@link NodeHasher}.
 * 
 * @see TaxonomyNodeHasher
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 * @param <K>
 *            the type of the keys for the node members
 * @param <KI>
 *            the type of the keys for the node instances
 */
public class TypeNodeHasher<K, KI> implements Hasher<TypeNode<K, ?, KI, ?>> {

	private final static int INSTANCE_OF_HASH_ = "instanceOf".hashCode();

	/**
	 * the hasher for {@link TaxonomyNode}s
	 */
	private final Hasher<TaxonomyNode<K, ?>> taxonomyNodeHasher_;

	/**
	 * the hasher for instance nodes based on its members
	 */
	private final Hasher<Node<KI, ?>> instanceMemberHasher_;

	public TypeNodeHasher(Hasher<TaxonomyNode<K, ?>> taxonomyNodeHasher,
			Hasher<Node<KI, ?>> instanceMemberHasher) {
		this.taxonomyNodeHasher_ = taxonomyNodeHasher;
		this.instanceMemberHasher_ = instanceMemberHasher;
	}

	@Override
	public int hash(TypeNode<K, ?, KI, ?> node) {
		int taxonomyHash = taxonomyNodeHasher_.hash(node);

		int instanceHash = INSTANCE_OF_HASH_;
		for (Node<KI, ?> instanceNode : node.getDirectInstanceNodes()) {
			instanceHash = HashGenerator.combineMultisetHash(false,
					instanceHash, instanceMemberHasher_.hash(instanceNode));
		}
		return HashGenerator.combineListHash(taxonomyHash, instanceHash);
	}

}
