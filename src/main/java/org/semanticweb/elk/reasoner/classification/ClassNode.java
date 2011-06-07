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
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

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
	final ArrayList<ElkClass> members;
	/**
	 * Parents are ElkClass objects that are immediate superclasses to the
	 * members without being equivalent.
	 */
	final ArrayList<ClassNode> parents;
	/**
	 * Children are ElkClass objects that are immediate subclasses to the
	 * members without being equivalent.
	 */
	final ArrayList<ClassNode> children;

	/**
	 * A thread-safe queue to remember children that are still to be written
	 * into
	 * {@link org.semanticweb.elk.reasoner.classification.ClassNode#children
	 * children}.
	 */
	final Queue<ClassNode> childQueue;

	/**
	 * True if
	 * {@link org.semanticweb.elk.reasoner.classification.ClassNode#childQueue
	 * childQueue} is not empty.
	 */
	private AtomicBoolean isActive;

	/**
	 * Constructor.
	 * 
	 * @param equivalent
	 *            non-empty list of equivalent ElkClass objects
	 */
	public ClassNode(final ArrayList<ElkClass> equivalent) {
		this.members = equivalent;
		this.children = new ArrayList<ClassNode>();
		this.parents = new ArrayList<ClassNode>();
		this.childQueue = new ConcurrentLinkedQueue<ClassNode>();
		this.isActive = new AtomicBoolean(false);
	}

	/**
	 * Try to set the activation flag from false to true and return true if this
	 * was successful (i.e. if the flag was not true already).
	 * 
	 * @return true if activation state changed
	 */
	boolean tryActivate() {
		return isActive.compareAndSet(false, true);
	}

	/**
	 * Try to set the activation flag from true to false and return true if this
	 * was successful (i.e. if the flag was not true already).
	 * 
	 * @return true if activation state changed
	 */
	boolean tryDeactivate() {
		return isActive.compareAndSet(true, false);
	}

	/**
	 * Add a parent node.
	 * 
	 * @param parent
	 *            node to add
	 */
	synchronized void addParent(ClassNode parent) {
		this.parents.add(parent);
	}

	/**
	 * Add a child class to the queue. This method can operate asynchronously,
	 * but
	 * {@link org.semanticweb.elk.reasoner.classification.ClassNode#processQueuedChildren
	 * processQueuedChildren()} must be called eventually to write the data into
	 * the children array.
	 * 
	 * @param child
	 */
	void enqueueChild(ClassNode child) {
		childQueue.add(child);
	}

	/**
	 * Process the queued children of to write them to the children array.
	 */
	synchronized void processQueuedChildren() {
		for (;;) {
			ClassNode child = childQueue.poll();
			if (child == null) {
				break;
			}
			addChild(child);
		}
	}

	/**
	 * Add a child node. This method is synchronized. To queue children for
	 * being added, the method
	 * {@link org.semanticweb.elk.reasoner.classification.ClassNode#enqueueChild
	 * enqueueChild()} should be used.
	 * 
	 * @param child
	 *            node to add
	 */
	private void addChild(ClassNode child) {
		this.children.add(child);
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
