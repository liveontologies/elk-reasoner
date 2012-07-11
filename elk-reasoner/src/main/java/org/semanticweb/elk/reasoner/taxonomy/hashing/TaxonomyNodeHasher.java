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

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;
import org.semanticweb.elk.util.hashing.HashGenerator;
import org.semanticweb.elk.util.hashing.Hasher;

/**
 * Helper class for hashing TaxonomyNodes based on getMembers(),
 * getDirectSubNodes(), and getDirectSuperNodes().
 * 
 * @author Frantisek Simancik
 * 
 */
public class TaxonomyNodeHasher implements Hasher<TaxonomyNode<? extends ElkEntity>> {

	/**
	 * We use one static instance for hashing all nodes.
	 */
	public static TaxonomyNodeHasher INSTANCE = new TaxonomyNodeHasher();

	private TaxonomyNodeHasher() {
	}

	@Override
	public int hash(TaxonomyNode<? extends ElkEntity> node) {
		int memberHash = NodeHasher.INSTANCE.hash(node);

		int subClassHash = "subClassOf".hashCode();
		for (Node<? extends ElkEntity> o : node.getDirectSubNodes()) {
			subClassHash = HashGenerator.combineMultisetHash(false,
					subClassHash, NodeHasher.INSTANCE.hash(o));
		}

		int superClassHash = "superClassOf".hashCode();
		for (Node<? extends ElkEntity> o : node.getDirectSuperNodes()) {
			superClassHash = HashGenerator.combineMultisetHash(false,
					superClassHash, NodeHasher.INSTANCE.hash(o));
		}

		return HashGenerator.combineListHash(memberHash, subClassHash,
				superClassHash);
	}

}
