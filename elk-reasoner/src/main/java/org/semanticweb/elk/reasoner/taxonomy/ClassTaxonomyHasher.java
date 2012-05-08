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
package org.semanticweb.elk.reasoner.taxonomy;

import java.util.LinkedHashMap;
import java.util.Map;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.util.hashing.HashGenerator;
import org.semanticweb.elk.util.hashing.Hasher;

/**
 * A class that contains helper methods for computing the structural hash of a
 * ClassTaxonomy. This is mainly useful during testing to check if the reasoner
 * produces the same taxonomy.
 * 
 * 
 * @author Frantisek Simancik
 * @author Markus Kroetzsch
 */
public class ClassTaxonomyHasher {

	// logger for events
	// private static final Logger LOGGER_ = Logger
	// .getLogger(ClassTaxonomyHasher.class);

	public static Hasher<ElkClass> elkClassHasher = new Hasher<ElkClass>() {

		@Override
		public int hash(ElkClass elkClass) {
			return elkClass.getIri().hashCode();
		}
	};

	/**
	 * Helper class for hashing a node of a class taxonomy. Implements some
	 * caching to ensure good performance even when some nodes have unusually
	 * many members.
	 */
	protected static Hasher<ClassNode> classNodeHasher = new Hasher<ClassNode>() {

		/**
		 * For nodes with at least this number of member classes, hash codes are
		 * cached in the hashCache.
		 */
		final int cacheNodeMemberNo = 50;

		/**
		 * Maximum number of large class nodes for which the hash value is
		 * cached.
		 */
		final int cacheMaxSize = 100;

		/**
		 * A simple Least Recently Used cache for the hashcodes of nodes that
		 * have a particularly large number of members. There should not be many
		 * such nodes in typical ontologies. Normally, this occurs if many
		 * classes are equivalent to owl:Nothing due to some modelling error.
		 */
		final LinkedHashMap<ClassNode, Integer> hashCache = new LinkedHashMap<ClassNode, Integer>() {
			private static final long serialVersionUID = 1;

			@Override
			protected boolean removeEldestEntry(
					Map.Entry<ClassNode, Integer> eldest) {
				return size() > cacheMaxSize;
			}
		};

		@Override
		public int hash(ClassNode node) {
			int memberHash = HashGenerator.combineMultisetHash(true,
					node.getMembers(), elkClassHasher);

			int subClassHash = "subClassOf".hashCode();
			for (ClassNode o : node.getDirectSubNodes()) {
				subClassHash = HashGenerator.combineMultisetHash(false,
						subClassHash, getClassNodeMemberHash(o));
			}

			int superClassHash = "superClassOf".hashCode();
			for (ClassNode o : node.getDirectSuperNodes()) {
				superClassHash = HashGenerator.combineMultisetHash(false,
						superClassHash, getClassNodeMemberHash(o));
			}

			return HashGenerator.combineListHash(memberHash, subClassHash,
					superClassHash);
		}

		/**
		 * Compute the hash for a class node. This method implements a simple
		 * cache for nodes with unusually large numbers of members. This mainly
		 * covers the case where a huge number of classes is equal to
		 * owl:Nothing due to modelling errors.
		 * 
		 * @param classNode
		 * @return
		 */
		private final int getClassNodeMemberHash(ClassNode classNode) {
			if (classNode.getMembers().size() >= cacheNodeMemberNo) {
				if (hashCache.containsKey(classNode)) {
					return hashCache.get(classNode);
				} else {
					int hash = HashGenerator.combineMultisetHash(true,
							classNode.getMembers(), elkClassHasher);
					hashCache.put(classNode, hash);
					return hash;
				}
			} else {
				return HashGenerator.combineMultisetHash(true,
						classNode.getMembers(), elkClassHasher);
			}
		}
	};

	/**
	 * Helper class for hashing a class taxonomy.
	 */
	protected static Hasher<ClassTaxonomy> classTaxonomyHasher = new Hasher<ClassTaxonomy>() {

		@Override
		public int hash(ClassTaxonomy taxonomy) {
			return HashGenerator.combineMultisetHash(true, taxonomy.getNodes(),
					classNodeHasher);
		}
	};

	/**
	 * Compute the has code of a class taxonomy.
	 * 
	 * @param taxonomy
	 * @return hash
	 */
	public static int hash(ClassTaxonomy taxonomy) {
		return classTaxonomyHasher.hash(taxonomy);
	}

}
