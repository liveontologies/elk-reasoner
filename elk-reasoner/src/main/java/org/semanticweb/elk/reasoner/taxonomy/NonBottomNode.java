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

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.util.Comparators;
import org.semanticweb.elk.util.collections.ArrayHashSet;
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
public class NonBottomNode implements TaxonomyClassNode {

	// logger for events
	private static final Logger LOGGER_ = Logger.getLogger(NonBottomNode.class);

	/**
	 * The link to the taxonomy to which this node belongs
	 */
	final ConcurrentClassTaxonomy taxonomy;

	/**
	 * Equivalent ElkClass objects that are representatives of this node.
	 */
	private final List<ElkClass> members;
	/**
	 * ElkClass nodes whose members are direct super-classes of the members of
	 * this node.
	 */
	private final Set<TaxonomyClassNode> directSuperNodes;
	/**
	 * ElkClass nodes, except for the bottom node, whose members are direct
	 * sub-classes of the members of this node.
	 */
	private final Set<TaxonomyClassNode> directSubNodes;

	/**
	 * Constructing the class node for a given taxonomy and the set of
	 * equivalent classes.
	 * 
	 * @param taxonomy
	 *            the taxonomy to which this node belongs
	 * @param members
	 *            non-empty list of equivalent ElkClass objects
	 */
	protected NonBottomNode(ConcurrentClassTaxonomy taxonomy,
			Collection<ElkClass> members) {
		this.taxonomy = taxonomy;
		this.members = new ArrayList<ElkClass>(members);
		this.directSubNodes = new ArrayHashSet<TaxonomyClassNode>();
		this.directSuperNodes = new ArrayHashSet<TaxonomyClassNode>();
		Collections.sort(this.members, Comparators.ELK_CLASS_COMPARATOR);
	}

	/**
	 * Add a direct super-class node. This method is not thread safe.
	 * 
	 * @param superNode
	 *            node to add
	 */
	void addDirectSuperNode(NonBottomNode superNode) {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace(this + ": new direct super-node " + superNode);
		directSuperNodes.add(superNode);
	}

	/**
	 * Add a direct sub-class node. This method is not thread safe.
	 * 
	 * @param subNode
	 *            node to add
	 */
	void addDirectSubNode(NonBottomNode subNode) {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace(this + ": new direct sub-node " + subNode);
		if (directSubNodes.isEmpty()) {
			this.taxonomy.countNodesWithSubClasses.incrementAndGet();
		}
		directSubNodes.add(subNode);
	}

	@Override
	public Set<ElkClass> getMembers() {
		// create an unmodifiable set view of the members; alternatively, one
		// could have created a TreeSet, but it consumes more memory
		return new Set<ElkClass>() {

			@Override
			public boolean add(ElkClass arg0) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean addAll(Collection<? extends ElkClass> arg0) {
				throw new UnsupportedOperationException();
			}

			@Override
			public void clear() {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean contains(Object arg0) {
				if (arg0 instanceof ElkClass)
					return (Collections.binarySearch(members, (ElkClass) arg0,
							Comparators.ELK_CLASS_COMPARATOR) >= 0);
				else
					return false;
			}

			@Override
			public boolean containsAll(Collection<?> arg0) {
				for (Object element : arg0) {
					if (!this.contains(element))
						return false;
				}
				return true;
			}

			@Override
			public boolean isEmpty() {
				return members.isEmpty();
			}

			@Override
			public Iterator<ElkClass> iterator() {
				return members.iterator();
			}

			@Override
			public boolean remove(Object arg0) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean removeAll(Collection<?> arg0) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean retainAll(Collection<?> arg0) {
				throw new UnsupportedOperationException();
			}

			@Override
			public int size() {
				return members.size();
			}

			@Override
			public Object[] toArray() {
				return members.toArray();
			}

			@Override
			public <T> T[] toArray(T[] arg0) {
				return members.toArray(arg0);
			}
		};
	}

	@Override
	public ElkClass getCanonicalMember() {
		return members.get(0);
	}

	@Override
	public Set<TaxonomyClassNode> getDirectSuperNodes() {
		return Collections.unmodifiableSet(directSuperNodes);
	}

	@Override
	public Set<TaxonomyClassNode> getAllSuperNodes() {
		Set<TaxonomyClassNode> result = new ArrayHashSet<TaxonomyClassNode>(
				directSuperNodes.size());
		Queue<TaxonomyClassNode> todo = new LinkedList<TaxonomyClassNode>();
		todo.add(this);
		for (;;) {
			TaxonomyClassNode next = todo.poll();
			if (next == null)
				break;
			for (TaxonomyClassNode nextSuperNode : next.getDirectSuperNodes()) {
				result.add(nextSuperNode);
				todo.add(nextSuperNode);
			}
		}
		return Collections.unmodifiableSet(result);
	}

	@Override
	public Set<TaxonomyClassNode> getDirectSubNodes() {
		if (!directSubNodes.isEmpty()) {
			return Collections.unmodifiableSet(directSubNodes);
		} else {
			Set<TaxonomyClassNode> result = new ArrayHashSet<TaxonomyClassNode>(1);
			result.add(this.taxonomy);
			return Collections.unmodifiableSet(result);
		}
	}

	@Override
	public Set<TaxonomyClassNode> getAllSubNodes() {
		Set<TaxonomyClassNode> result;
		if (!directSubNodes.isEmpty()) {
			result = new ArrayHashSet<TaxonomyClassNode>(directSubNodes.size());
			Queue<TaxonomyClassNode> todo = new LinkedList<TaxonomyClassNode>();
			todo.add(this);
			for (;;) {
				TaxonomyClassNode next = todo.poll();
				if (next == null)
					break;
				for (TaxonomyClassNode nextSubNode : next.getDirectSubNodes()) {
					result.add(nextSubNode);
					todo.add(nextSubNode);
				}
			}
		} else {
			result = new ArrayHashSet<TaxonomyClassNode>(1);
			result.add(this.taxonomy);
		}
		return Collections.unmodifiableSet(result);
	}

	private final int hashCode_ = HashGenerator.generateNextHashCode();

	@Override
	public final int hashCode() {
		return hashCode_;
	}

	@Override
	public ClassTaxonomy getTaxonomy() {
		return this.taxonomy;
	}

	@Override
	public String toString() {
		return getCanonicalMember().getIri().asString();
	}
}
