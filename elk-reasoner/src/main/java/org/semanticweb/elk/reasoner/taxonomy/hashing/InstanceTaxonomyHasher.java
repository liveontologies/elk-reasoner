/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.taxonomy.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.nodes.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.Node;
import org.semanticweb.elk.reasoner.taxonomy.nodes.TypeNode;
import org.semanticweb.elk.util.hashing.HashGenerator;
import org.semanticweb.elk.util.hashing.Hasher;

/**
 * A helper class to compute the structural hash code for
 * {@link InstanceTaxonomy} objects. If two {@link InstanceTaxonomy} objects are
 * structurally equivalent then {@link #hash(InstanceTaxonomy)} is guaranteed to
 * return the same values for them. Two {@link InstanceTaxonomy} objects are
 * structurally equivalent if the sets of their {@link TypeNode}s and
 * {@link InstanceNode}s are structurally equivalent. Two sets of
 * {@link TypeNode}s are structurally equivalent if there is a bijection between
 * the members in the two sets mapping {@link TypeNode}s to structurally
 * equivalent ones as defined in {@link TypeNodeHasher}. Two sets of
 * {@link InstanceNode}s are structurally equivalent if there is a bijection
 * between the members in the two sets mapping {@link InstanceNode}s to
 * structurally equivalent ones as defined in {@link InstanceNodeHasher}.
 * 
 * @see TypeNodeHasher
 * @see InstanceNodeHasher
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 * @param <K>
 *            the type of the keys for the node members
 * @param <KI>
 *            the type of the keys for the node instances
 */
public class InstanceTaxonomyHasher<K, KI> implements
		Hasher<InstanceTaxonomy<K, ?, KI, ?>> {

	/**
	 * the hasher for {@link TypeNode}s
	 */
	private final Hasher<TypeNode<K, ?, KI, ?>> typeNodeHasher_;

	/**
	 * the hasher for {@link InstanceNode}s
	 */
	private final Hasher<InstanceNode<K, ?, KI, ?>> instanceNodeHasher_;

	public InstanceTaxonomyHasher() {
		this(new NodeHasher<K>(), new NodeHasher<KI>());
	}

	private InstanceTaxonomyHasher(Hasher<Node<K, ?>> typeMemberHasher,
			Hasher<Node<KI, ?>> instanceMemberHasher) {

		this.typeNodeHasher_ = new TypeNodeHasher<K, KI>(
				new TaxonomyNodeHasher<K>(typeMemberHasher),
				instanceMemberHasher);
		this.instanceNodeHasher_ = new InstanceNodeHasher<K, KI>(
				typeMemberHasher, instanceMemberHasher);
	}

	@Override
	public int hash(InstanceTaxonomy<K, ?, KI, ?> taxonomy) {
		int typeHash = HashGenerator.combineMultisetHash(true,
				taxonomy.getNodes(), typeNodeHasher_);
		int instanceHash = HashGenerator.combineMultisetHash(true,
				taxonomy.getInstanceNodes(), instanceNodeHasher_);
		return HashGenerator.combineListHash(typeHash, instanceHash);
	}

}
