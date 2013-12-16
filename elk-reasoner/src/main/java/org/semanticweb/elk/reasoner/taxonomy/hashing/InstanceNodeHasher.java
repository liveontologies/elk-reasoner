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

import org.semanticweb.elk.reasoner.taxonomy.nodes.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.Node;
import org.semanticweb.elk.util.hashing.HashGenerator;
import org.semanticweb.elk.util.hashing.Hasher;

/**
 * A helper class to compute the structural hash code for {@link InstanceNode}
 * objects. If two {@link InstanceNode} objects are structurally equivalent then
 * {@link #hash(InstanceNode)} is guaranteed to return the same values for them.
 * Two {@link InstanceNode}s are structurally equivalent if they are
 * structurally equivalent as defined in {@link NodeHasher}, and the set of
 * their direct type nodes are structurally equivalent. Two sets of {@link Node}
 * s are structurally equivalent if there is a bijection between the members in
 * the two sets mapping {@link Node}s to structurally equivalent ones as defined
 * in {@link NodeHasher}.
 * 
 * @see NodeHasher
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 * @param <K>
 *            the type of the keys for the node members
 * @param <KI>
 *            the type of the keys for the node instances
 */
public class InstanceNodeHasher<K, KI> implements
		Hasher<InstanceNode<K, ?, KI, ?>> {

	private final static int TYPE_OF_HASH_ = "typeOf".hashCode();
	/**
	 * the hasher for type {@link Node}s based on member keys
	 */
	private final Hasher<Node<K, ?>> typeMemberHasher_;
	/**
	 * the hasher for instance {@link Node}s based on member keys
	 */
	private final Hasher<Node<KI, ?>> instanceMemberHasher_;

	public InstanceNodeHasher(Hasher<Node<K, ?>> typeNodeHasher,
			Hasher<Node<KI, ?>> instanceNodeHasher) {
		this.typeMemberHasher_ = typeNodeHasher;
		this.instanceMemberHasher_ = instanceNodeHasher;
	}

	@Override
	public int hash(InstanceNode<K, ?, KI, ?> node) {
		int memberHash = instanceMemberHasher_.hash(node);
		int typeHash = TYPE_OF_HASH_;
		for (Node<K, ?> o : node.getDirectTypeNodes()) {
			typeHash = HashGenerator.combineMultisetHash(false, typeHash,
					typeMemberHasher_.hash(o));
		}

		return HashGenerator.combineListHash(memberHash, typeHash);
	}

}
