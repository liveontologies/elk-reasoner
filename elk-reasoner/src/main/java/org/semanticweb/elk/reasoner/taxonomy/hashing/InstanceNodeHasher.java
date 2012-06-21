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
import org.semanticweb.elk.reasoner.taxonomy.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.Node;
import org.semanticweb.elk.util.hashing.HashGenerator;
import org.semanticweb.elk.util.hashing.Hasher;

/**
 * Helper class for hashing InstanceNodes based on getMembers() and
 * getDirectTypeNodes().
 * 
 * @author Frantisek Simancik
 * 
 */
class InstanceNodeHasher implements
		Hasher<InstanceNode<? extends ElkEntity, ? extends ElkEntity>> {

	/**
	 * We use one static instance for hashing all nodes.
	 */
	public static InstanceNodeHasher INSTANCE = new InstanceNodeHasher();

	private InstanceNodeHasher() {
	}

	@Override
	public int hash(InstanceNode<? extends ElkEntity, ? extends ElkEntity> node) {
		int memberHash = NodeHasher.INSTANCE.hash(node);

		int typeHash = "typeOf".hashCode();
		for (Node<? extends ElkEntity> o : node.getDirectTypeNodes()) {
			typeHash = HashGenerator.combineMultisetHash(false, typeHash,
					NodeHasher.INSTANCE.hash(o));
		}

		return HashGenerator.combineListHash(memberHash, typeHash);
	}

}
