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
import java.util.Queue;
import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.IndexedClass;
import org.semanticweb.elk.syntax.interfaces.ElkClass;
import org.semanticweb.elk.util.ArrayHashSet;
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
	// TODO: return members, sub-nodes and super-nodes in the methods as sets
	/**
	 * Equivalent ElkClass objects that are representatives of this node.
	 */
	final List<ElkClass> members;
	/**
	 * ElkClass nodes whose members are direct super-classes of the members of
	 * this node.
	 */
	final List<ClassNode> directSuperNodes;
	/**
	 * ElkClass nodes whose members are direct sub-classes of the members of
	 * this node.
	 */
	final List<ClassNode> directSubNodes;

	List<IndexedClass> parentIndexClasses;

	/**
	 * Constructor.
	 * 
	 * @param equivalent
	 *            non-empty list of equivalent ElkClass objects
	 */
	public ClassNode(final List<ElkClass> equivalent) {
		this.members = equivalent;
		this.directSubNodes = new ArrayList<ClassNode>();
		this.directSuperNodes = new ArrayList<ClassNode>();
	}

	/**
	 * Add a direct super-class node. This method is not thread safe.
	 * 
	 * @param superNode
	 *            node to add
	 */
	void addDirectSuperNode(ClassNode superNode) {
		directSuperNodes.add(superNode);
	}

	/**
	 * Add a direct sub-class node. This method is thread safe.
	 * 
	 * @param subNode
	 *            node to add
	 */
	synchronized void addDirectSubNode(ClassNode subNode) {
		directSubNodes.add(subNode);
	}

	/**
	 * Get an unmodifiable list of ElkClass objects that this ClassNode
	 * represents.
	 * 
	 * @return collection of equivalent ElkClass objects
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
	 * Get an unmodifiable list of nodes for ElkClass objects that are direct
	 * sub-classes of this ClassNode.
	 * 
	 * @return list of nodes for direct super-classes of this node members
	 */
	public List<ClassNode> getDirectSuperNodes() {
		return Collections.unmodifiableList(directSuperNodes);
	}

	/**
	 * Computes an unmodifiable set of nodes for ElkClass objects that are
	 * (possibly indirect) super-classes of members of this ClassNode. This is
	 * the smallest set of nodes that contains all direct super-nodes of this
	 * node, and all direct super-nodes of every node in this set.
	 * 
	 * @return set of nodes for sub-classes of this node members
	 */
	public Set<ClassNode> getAllSuperNodes() {
		Set<ClassNode> result = new ArrayHashSet<ClassNode>();
		Queue<ClassNode> todo = new LinkedList<ClassNode>();
		todo.add(this);
		for (;;) {
			ClassNode next = todo.poll();
			if (next == null)
				break;
			for (ClassNode nextSuperNode : next.getDirectSuperNodes()) {
				result.add(nextSuperNode);
				todo.add(nextSuperNode);
			}
		}
		return Collections.unmodifiableSet(result);
	}

	/**
	 * Get an unmodifiable list of nodes for ElkClass objects that are direct
	 * super-classes of this ClassNode.
	 * 
	 * @return list of nodes for direct sub-classes of this node members
	 */
	public List<ClassNode> getDirectSubNodes() {
		return Collections.unmodifiableList(directSubNodes);
	}

	/**
	 * Computes an unmodifiable set of nodes for ElkClass objects that are
	 * (possibly indirect) sub-classes of members of this ClassNode. This is the
	 * smallest set of nodes that contains all direct sub-nodes of this node,
	 * and all direct sub-nodes of every node in this set.
	 * 
	 * @return set of nodes for sub-classes of this node members
	 */
	public Set<ClassNode> getAllSubNodes() {
		Set<ClassNode> result = new ArrayHashSet<ClassNode>();
		Queue<ClassNode> todo = new LinkedList<ClassNode>();
		todo.add(this);
		for (;;) {
			ClassNode next = todo.poll();
			if (next == null)
				break;
			for (ClassNode nextSubNode : next.getDirectSubNodes()) {
				result.add(nextSubNode);
				todo.add(nextSubNode);
			}
		}
		return Collections.unmodifiableSet(result);
	}

	public int structuralHashCode() {
		int memberHash = HashGenerator.combineMultisetHash(true, members);

		int subClassHash = "subClassOf".hashCode();
		for (ClassNode o : directSubNodes) {
			int subMemberHash = HashGenerator.combineMultisetHash(true,
					o.getMembers());
			subClassHash = HashGenerator.combineMultisetHash(false,
					subClassHash, subMemberHash);
		}

		int superClassHash = "superClassOf".hashCode();
		for (ClassNode o : directSuperNodes) {
			int superMemberHash = HashGenerator.combineMultisetHash(true,
					o.getMembers());
			superClassHash = HashGenerator.combineMultisetHash(false,
					superClassHash, superMemberHash);
		}

		return HashGenerator.combineListHash(memberHash, subClassHash,
				superClassHash);
	}

	private final int hashCode_ = HashGenerator.generateNextHashCode();

	public final int hashCode() {
		return hashCode_;
	}

}
