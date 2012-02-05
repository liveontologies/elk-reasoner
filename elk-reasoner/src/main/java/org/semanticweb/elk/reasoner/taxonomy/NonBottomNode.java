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
package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.ArraySet;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * Class for storing information about a class in the context of classification.
 * It is the main data container for ClassTaxonomy objects. Like most such data
 * containers in ELK, it is read-only for public access but provides
 * package-private ways of modifying it. Modifications of this class happen in
 * implementations of ClassTaxonomy only.
 * 
 * @author Yevgeny Kazakov
 */
public class NonBottomNode implements ClassNode {

	// logger for events
	private static final Logger LOGGER_ = Logger.getLogger(ClassNode.class);

	/**
	 * The link to the taxonomy to which this node belongs
	 */
	final ConcurrentClassTaxonomy taxonomy;

	/**
	 * Equivalent ElkClass objects that are representatives of this node. The
	 * first element is the least one according to the ordering defined by
	 * PredefinedElkIri.compare().
	 */
	private ArraySet<ElkClass> members;
	/**
	 * ElkClass nodes whose members are direct super-classes of the members of
	 * this node.
	 */
	private final Set<ClassNode> directSuperNodes;
	/**
	 * ElkClass nodes, except for the bottom node, whose members are direct
	 * sub-classes of the members of this node.
	 */
	private final Set<ClassNode> directSubNodes;

	/**
	 * Constructing a new class node for a given taxonomy
	 * 
	 * @param members
	 *            non-empty set of equivalent ElkClass objects
	 */
	protected NonBottomNode(ConcurrentClassTaxonomy taxonomy) {
		this.taxonomy = taxonomy;
		this.directSubNodes = new ArrayHashSet<ClassNode>();
		this.directSuperNodes = new ArrayHashSet<ClassNode>();
	}

	/**
	 * Constructing the class node for a given taxonomy and the set of
	 * equivalent classes.
	 * 
	 * @param members
	 *            non-empty set of equivalent ElkClass objects
	 */
	protected NonBottomNode(ConcurrentClassTaxonomy taxonomy,
			ArraySet<ElkClass> members) {
		this(taxonomy);
		setMembers(members);
	}

	/**
	 * Testing if the members have been set
	 * 
	 * @return <tt>true</tt> if the members are set, otherwise <tt>false</tt>
	 */
	protected boolean membersSet() {
		return members != null;
	}

	/**
	 * Setting the members of this class node.
	 * 
	 * @param members
	 *            the set of members to be set for this class node
	 */
	protected void setMembers(ArraySet<ElkClass> members) {
		this.members = members;
	}

	/**
	 * Add a direct super-class node. This method is not thread safe.
	 * 
	 * @param superNode
	 *            node to add
	 */
	void addDirectSuperNode(NonBottomNode superNode) {
		// TODO: members of superNode might not be assigned, so will not print
		// if (LOGGER_.isTraceEnabled())
		// LOGGER_.trace(this + ": new direct super-node " + superNode);
		directSuperNodes.add(superNode);
	}

	/**
	 * Add a direct sub-class node. This method is not thread safe.
	 * 
	 * @param subNode
	 *            node to add
	 */
	void addDirectSubNode(NonBottomNode subNode) {
		// TODO: members of subNode might not be assigned, so will not print
		// if (LOGGER_.isTraceEnabled())
		// LOGGER_.trace(this + ": new direct sub-node " + subNode);
		if (directSubNodes.isEmpty()) {
			this.taxonomy.countNodesWithSubClasses.incrementAndGet();
		}
		directSubNodes.add(subNode);
	}

	public Set<ElkClass> getMembers() {
		return Collections.unmodifiableSet(members);
	}

	public ElkClass getCanonicalMember() {
		return members.get(0);
	}

	public Set<ClassNode> getDirectSuperNodes() {
		return Collections.unmodifiableSet(directSuperNodes);
	}

	public Set<ClassNode> getAllSuperNodes() {
		Set<ClassNode> result = new ArrayHashSet<ClassNode>(
				directSuperNodes.size());
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

	public Set<ClassNode> getDirectSubNodes() {
		if (!directSubNodes.isEmpty()) {
			return Collections.unmodifiableSet(directSubNodes);
		} else {
			Set<ClassNode> result = new ArrayHashSet<ClassNode>(1);
			result.add(this.taxonomy);
			return Collections.unmodifiableSet(result);
		}
	}

	public Set<ClassNode> getAllSubNodes() {
		Set<ClassNode> result;
		if (!directSubNodes.isEmpty()) {
			result = new ArrayHashSet<ClassNode>(directSubNodes.size());
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
		} else {
			result = new ArrayHashSet<ClassNode>(1);
			result.add(this.taxonomy);
		}
		return Collections.unmodifiableSet(result);
	}

	private final int hashCode_ = HashGenerator.generateNextHashCode();

	public final int hashCode() {
		return hashCode_;
	}

	public ClassTaxonomy getTaxonomy() {
		return this.taxonomy;
	}

	public String toString() {
		return getCanonicalMember().getIri().asString();
	}
}
