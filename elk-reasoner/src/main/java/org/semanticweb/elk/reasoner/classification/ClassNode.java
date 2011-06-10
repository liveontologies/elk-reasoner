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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.semanticweb.elk.reasoner.indexing.IndexedClass;
import org.semanticweb.elk.syntax.ElkClass;
import org.semanticweb.elk.util.HashGenerator;
import org.semanticweb.elk.util.StructuralHashObject;

/**
 * Class for storing information about a class in the context of classification.
 * It is the main data container for ClassTaxonomy objects. Like most such data
 * containers in ELK, it is read-only for public access but provides
 * package-private ways of modifying it. Modifications of this class happen in
 * implementations of ClassTaxonomy only.
 * 
 * @author Yevgeny Kazakov
 */
public class ClassNode implements StructuralHashObject {

	/**
	 * Members are ElkClass objects that are equivalent.
	 */
	final List<ElkClass> members;
	/**
	 * Parents are ElkClass objects that are immediate superclasses to the
	 * members without being equivalent.
	 */
	final List<ClassNode> parents;
	/**
	 * Children are ElkClass objects that are immediate subclasses to the
	 * members without being equivalent.
	 */
	final List<ClassNode> children;
	
	List<IndexedClass> parentIndexClasses;

	/**
	 * Constructor.
	 * 
	 * @param equivalent
	 *            non-empty list of equivalent ElkClass objects
	 */
	public ClassNode(final List<ElkClass> equivalent) {
		this.members = equivalent;
		this.children = new LinkedList<ClassNode>();
		this.parents = new ArrayList<ClassNode>();
	}


	/**
	 * Add a parent node.
	 * 
	 * @param parent node to add
	 */
	void addParent(ClassNode parent) {
		parents.add(parent);
	}

	/**
	 * Add a child node.
	 * 
	 * @param child node to add
	 */
	synchronized void addChild(ClassNode child) {
		children.add(child);
	}

	/**
	 * Get an unmodifiable list of ElkClass objects that this ClassNode
	 * represents.
	 * 
	 * @return list of equivalent ElkClass objects
	 */
	public List<ElkClass> getMembers() {
		return Collections.unmodifiableList(members);
	}

	/**
	 * Get one ElkClass object to canonically represent the classes in this
	 * ClassNode.
	 * 
	 * @return canonical ElkClass object
	 */
	public ElkClass getCanonicalMember() {
		return members.get(0);
	}

	/**
	 * Get an unmodifiable list of ElkClass objects that are direct parents of
	 * this ClassNode in the class hierarchy.
	 * 
	 * @return list of direct parent classes
	 */
	public List<ClassNode> getParents() {
		return Collections.unmodifiableList(parents);
	}

	/**
	 * Get an unmodifiable list of ElkClass objects that are direct children of
	 * this ClassNode in the class hierarchy.
	 * 
	 * @return list of direct child classes
	 */
	public List<ClassNode> getChildren() {
		return Collections.unmodifiableList(children);
	}

	public int structuralHashCode() {
		int memberHash = HashGenerator.combineMultisetHash(true, members);
		
		int subClassHash = "subClassOf".hashCode();
		for (ClassNode o : children) {
			int subMemberHash = HashGenerator.combineMultisetHash(true, o.getMembers());
			subClassHash = HashGenerator.combineMultisetHash(false,subClassHash,subMemberHash);
		}
		
		int superClassHash = "superClassOf".hashCode();
		for (ClassNode o : parents) {
			int superMemberHash = HashGenerator.combineMultisetHash(true, o.getMembers());
			superClassHash = HashGenerator.combineMultisetHash(false,superClassHash,superMemberHash);
		}

		return HashGenerator.combineListHash(memberHash, subClassHash, superClassHash);
	}

}
