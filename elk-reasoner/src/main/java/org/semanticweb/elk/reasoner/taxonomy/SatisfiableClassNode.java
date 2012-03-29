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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.util.Comparators;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * A class to represent satisfiable class nodes, i.e., those that do not contain
 * <tt>owl:Nothing</tt> as one of the members.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class SatisfiableClassNode implements ClassNode {

	// logger for events
	private static final Logger LOGGER_ = Logger.getLogger(ClassNode.class);

	/**
	 * The link to the taxonomy to which this node belongs
	 */
	final ConcurrentClassTaxonomy taxonomy;

	/**
	 * Equivalent ElkClass objects that are representatives of this node.
	 */
	protected final List<ElkClass> members;
	/**
	 * ElkClass nodes whose members are direct super-classes of the members of
	 * this node.
	 */
	private final Set<SatisfiableClassNode> directSuperNodes;
	/**
	 * ElkClass nodes, except for the bottom node, whose members are direct
	 * sub-classes of the members of this node.
	 */
	private final Set<SatisfiableClassNode> directSubNodes;
	/**
	 * <tt>true</tt> if the direct super-nodes of this node need to be
	 * recomputed
	 */
	private final AtomicBoolean modified;

	/**
	 * Constructing the class node for a given taxonomy and the set of
	 * equivalent classes.
	 * 
	 * @param taxonomy
	 *            the taxonomy to which this node belongs
	 * @param members
	 *            non-empty list of equivalent ElkClass objects
	 */
	protected SatisfiableClassNode(ConcurrentClassTaxonomy taxonomy,
			Collection<ElkClass> members) {
		this.taxonomy = taxonomy;
		this.members = new ArrayList<ElkClass>(members);
		this.directSubNodes = new ArrayHashSet<SatisfiableClassNode>();
		this.directSuperNodes = new ArrayHashSet<SatisfiableClassNode>();
		Collections.sort(this.members, Comparators.ELK_CLASS_COMPARATOR);
		this.modified = new AtomicBoolean(true);
	}

	/**
	 * Add a direct super-class node.
	 * 
	 * @param superNode
	 *            node to add
	 */
	synchronized void addDirectSuperNode(SatisfiableClassNode superNode) {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace(this + ": new direct super-node " + superNode);
		directSuperNodes.add(superNode);
	}

	/**
	 * Remove a direct super-class node.
	 * 
	 * @param superNode
	 *            node to remove
	 */
	synchronized void removeDirectSuperNode(SatisfiableClassNode superNode) {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace(this + ": removed direct super-node " + superNode);
		directSuperNodes.remove(superNode);
	}

	/**
	 * Removes all direct super nodes of this node
	 */
	void clearSuperNodes() {
		directSuperNodes.clear();
	}

	/**
	 * Add a direct sub-class node.
	 * 
	 * @param subNode
	 *            node to add
	 */
	synchronized void addDirectSubNode(SatisfiableClassNode subNode) {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace(this + ": new direct sub-node " + subNode);
		if (directSubNodes.isEmpty()) {
			this.taxonomy.incrementCountNodesWithSubClasses();
		}
		directSubNodes.add(subNode);
	}

	/**
	 * Remove a direct sub-class node.
	 * 
	 * @param subNode
	 *            node to remove
	 */
	synchronized void removeDirectSubNode(SatisfiableClassNode subNode) {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace(this + ": removed direct sub-node " + subNode);
		directSubNodes.remove(subNode);
		if (directSubNodes.isEmpty()) {
			this.taxonomy.decrementCountNodesWithSubClasses();
		}
	}

	/**
	 * Removes all direct satisfiable sub-class nodes of this node
	 */
	synchronized void clearSatisfiableSubNodes() {
		directSubNodes.clear();
	}

	Set<SatisfiableClassNode> getDirectSatisfiableSubNodes() {
		return directSubNodes;
	}

	boolean trySetModified() {
		return modified.compareAndSet(false, true);
	}

	boolean trySetNotModified() {
		return modified.compareAndSet(true, false);
	}

	public boolean isModified() {
		return modified.get();
	}

	public Set<ElkClass> getMembers() {
		// create an unmodifiable set view of the members; alternatively, one
		// could have created a TreeSet, but it consumes more memory
		return new Set<ElkClass>() {

			public boolean add(ElkClass arg0) {
				throw new UnsupportedOperationException();
			}

			public boolean addAll(Collection<? extends ElkClass> arg0) {
				throw new UnsupportedOperationException();
			}

			public void clear() {
				throw new UnsupportedOperationException();
			}

			public boolean contains(Object arg0) {
				if (arg0 instanceof ElkClass)
					return (Collections.binarySearch(members, (ElkClass) arg0,
							Comparators.ELK_CLASS_COMPARATOR) >= 0);
				else
					return false;
			}

			public boolean containsAll(Collection<?> arg0) {
				for (Object element : arg0) {
					if (!this.contains(element))
						return false;
				}
				return true;
			}

			public boolean isEmpty() {
				return members.isEmpty();
			}

			public Iterator<ElkClass> iterator() {
				return members.iterator();
			}

			public boolean remove(Object arg0) {
				throw new UnsupportedOperationException();
			}

			public boolean removeAll(Collection<?> arg0) {
				throw new UnsupportedOperationException();
			}

			public boolean retainAll(Collection<?> arg0) {
				throw new UnsupportedOperationException();
			}

			public int size() {
				return members.size();
			}

			public Object[] toArray() {
				return members.toArray();
			}

			public <T> T[] toArray(T[] arg0) {
				return members.toArray(arg0);
			}
		};
	}

	public ElkClass getCanonicalMember() {
		return members.get(0);
	}

	public Set<SatisfiableClassNode> getDirectSuperNodes() {
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

	public Set<? extends ClassNode> getDirectSubNodes() {
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
