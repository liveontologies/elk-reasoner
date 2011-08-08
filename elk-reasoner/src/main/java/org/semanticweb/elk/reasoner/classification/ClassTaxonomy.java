/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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
/**
 * @author Yevgeny Kazakov, May 15, 2011
 */
package org.semanticweb.elk.reasoner.classification;

import java.util.Set;

import org.semanticweb.elk.syntax.interfaces.ElkClass;
import org.semanticweb.elk.util.HashGenerator;
import org.semanticweb.elk.util.StructuralHashObject;

/**
 * Classes that implement this interface represent a class hierarchy based on
 * ElkClass objects. For each such object, the taxonomy holds a ClassNode object
 * from which direct sub- and superclasses can be retrieved.
 * 
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 */
public abstract class ClassTaxonomy implements StructuralHashObject {

	public abstract ClassNode getNode(ElkClass elkClass);

	/**
	 * Obtain an unmodifiable Set of all nodes in this ClassTaxonomy.
	 * 
	 * @return an unmodifiable Collection
	 */
	public abstract Set<ClassNode> getNodes();

	/**
	 * Compute a hash code based on all subclass relationships that are
	 * expressed through the children of all class nodes. The result is not the
	 * same as a hash over any set of axioms that represent the taxonomy.
	 * However, the result must always be the same as the result of
	 * getParentBasedTaxonomyHash().
	 * 
	 * @return children-based hash code representing the taxonomy
	 */
	public int getChildrenBasedTaxonomyHash() {
		int result = 0;
		int subClassHash = "subClassOf".hashCode();

		for (ClassNode classNode : getNodes()) {
			int memberHash = HashGenerator.combineMultisetHash(true, classNode
					.getMembers());
			for (ClassNode o : classNode.getDirectSubNodes()) {
				int subMemberHash = HashGenerator.combineMultisetHash(true, o
						.getMembers());
				int subClassAxiomHash = HashGenerator.combineListHash(
						subClassHash, subMemberHash, memberHash);
				result = HashGenerator.combineMultisetHash(false, result,
						subClassAxiomHash);
			}
		}

		return HashGenerator.combineListHash(result);
	}

	/**
	 * Compute a hash code based on all subclass relationships that are
	 * expressed through the parents of all class nodes. The result is not the
	 * same as a hash over any set of axioms that represent the taxonomy.
	 * However, the result must always be the same as the result of
	 * getChildrenBasedTaxonomyHash().
	 * 
	 * @return parent-based hash code representing the taxonomy
	 */
	public int getParentBasedTaxonomyHash() {
		int result = 0;
		int subClassHash = "subClassOf".hashCode();

		for (ClassNode classNode : getNodes()) {
			int memberHash = HashGenerator.combineMultisetHash(true, classNode
					.getMembers());
			for (ClassNode o : classNode.getDirectSuperNodes()) {
				int superMemberHash = HashGenerator.combineMultisetHash(true, o
						.getMembers());
				int subClassAxiomHash = HashGenerator.combineListHash(
						subClassHash, memberHash, superMemberHash);
				result = HashGenerator.combineMultisetHash(false, result,
						subClassAxiomHash);
			}
		}

		return HashGenerator.combineListHash(result);
	}

}
