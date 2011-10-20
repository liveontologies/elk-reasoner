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
package org.semanticweb.elk.reasoner.classification;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.util.hashing.HashGenerator;
import org.semanticweb.elk.util.hashing.Hasher;

/**
 * A class that contains helper methods for computing the structural hash 
 * of a ClassTaxonomy. This is mainly useful during testing to check
 * if the reasoner produces the same taxonomy. 
 *  
 * 
 * @author Frantisek Simancik
 */
public class ClassTaxonomyHasher {
	
	public static Hasher<ElkClass> elkClassHasher = 
		new Hasher<ElkClass>() {

			public int hash(ElkClass elkClass) {
				return elkClass.getIri().hashCode();
			}
	};
	
	public static Hasher<ClassNode> classNodeHasher = 
		new Hasher<ClassNode>() {
		
		
		public int hash(ClassNode node) {
			int memberHash = HashGenerator.combineMultisetHash(true,
					node.getMembers(), elkClassHasher);

			int subClassHash = "subClassOf".hashCode();
			for (ClassNode o : node.getDirectSubNodes()) {
				int subMemberHash = HashGenerator.combineMultisetHash(true,
						o.getMembers(), elkClassHasher);
				subClassHash = HashGenerator.combineMultisetHash(false,
						subClassHash, subMemberHash);
			}

			int superClassHash = "superClassOf".hashCode();
			for (ClassNode o : node.getDirectSuperNodes()) {
				int superMemberHash = HashGenerator.combineMultisetHash(true,
						o.getMembers(), elkClassHasher);
				superClassHash = HashGenerator.combineMultisetHash(false,
						superClassHash, superMemberHash);
			}

			return HashGenerator.combineListHash(memberHash, subClassHash,
					superClassHash);
		}
	};
	
	public static  Hasher<ClassTaxonomy> classTaxonomyHasher = 
		new Hasher<ClassTaxonomy>() {
		
		public int hash(ClassTaxonomy taxonomy) {
			return HashGenerator.combineMultisetHash(true,
				taxonomy.getNodes(), classNodeHasher);
		}
	};
	
	public static int hash(ClassTaxonomy taxonomy) {
		return classTaxonomyHasher.hash(taxonomy);
	}
	
}
