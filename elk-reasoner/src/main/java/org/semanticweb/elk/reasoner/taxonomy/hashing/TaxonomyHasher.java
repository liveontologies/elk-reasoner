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

import org.semanticweb.elk.reasoner.taxonomy.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.nodes.Node;
import org.semanticweb.elk.reasoner.taxonomy.nodes.TaxonomyNode;
import org.semanticweb.elk.util.hashing.HashGenerator;
import org.semanticweb.elk.util.hashing.Hasher;

/**
 * A helper class to compute the structural hash code for {@link Taxonomy}
 * objects. If two {@link Taxonomy} objects are structurally equivalent then
 * {@link #hash(Taxonomy)} is guaranteed to return the same values for them. Two
 * {@link Taxonomy}s are structurally equivalent if the sets of their
 * {@link TaxonomyNode}s are structurally equivalent. Two sets of
 * {@link TaxonomyNode}s are structurally equivalent if there is a bijection
 * between the members in the two sets mapping {@link TaxonomyNode}s to
 * structurally equivalent ones as defined in {@link TaxonomyNodeHasher}.
 * 
 * @see TaxonomyNodeHasher
 * 
 * @author Frantisek Simancik
 * @author Markus Kroetzsch
 * @author "Yevgeny Kazakov"
 * 
 * @param <K>
 *            the type of the keys for the node members
 */
public class TaxonomyHasher<K> implements Hasher<Taxonomy<K, ?>> {

	/**
	 * the hasher for {@link TaxonomyNode}s
	 */
	private final Hasher<TaxonomyNode<K, ?>> taxonomyNodeHasher_;

	private TaxonomyHasher(Hasher<Node<K, ?>> nodeHasher) {
		this.taxonomyNodeHasher_ = new TaxonomyNodeHasher<K>(nodeHasher);
	}

	public TaxonomyHasher() {
		this(new NodeHasher<K>());
	}

	@Override
	public int hash(Taxonomy<K, ?> taxonomy) {
		return HashGenerator.combineMultisetHash(true, taxonomy.getNodes(),
				taxonomyNodeHasher_);
	}

}
