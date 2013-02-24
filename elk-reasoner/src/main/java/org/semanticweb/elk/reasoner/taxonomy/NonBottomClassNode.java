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

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.util.Comparators;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNodeUtils;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTaxonomyNode;
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
 * @author Markus Kroetzsch
 */
class NonBottomClassNode implements UpdateableTaxonomyNode<ElkClass> {

	// logger for events
	private static final Logger LOGGER_ = Logger
			.getLogger(NonBottomClassNode.class);

	/**
	 * The link to the taxonomy to which this node belongs
	 */
	private final ConcurrentClassTaxonomy taxonomy_;

	/**
	 * Equivalent ElkClass objects that are representatives of this node.
	 */
	private final List<ElkClass> members_;
	/**
	 * ElkClass nodes whose members are direct super-classes of the members of
	 * this node.
	 */
	private final Set<UpdateableTaxonomyNode<ElkClass>> directSuperNodes_;
	/**
	 * ElkClass nodes, except for the bottom node, whose members are direct
	 * sub-classes of the members of this node.
	 */
	private final Set<UpdateableTaxonomyNode<ElkClass>> directSubNodes_;
	/**
	 * ElkNamedIndividual nodes whose members are instances of the members of
	 * this node.
	 */
	//private final Set<InstanceNode<ElkClass, ElkNamedIndividual>> directInstanceNodes_;

	/**
	 * <tt>true</tt> if the direct super-nodes of this node need to be
	 * recomputed
	 */
	private final AtomicBoolean modified_ = new AtomicBoolean(true);

	/**
	 * Constructing the class node for a given taxonomy and the set of
	 * equivalent classes.
	 * 
	 * @param taxonomy
	 *            the taxonomy to which this node belongs
	 * @param members
	 *            non-empty list of equivalent ElkClass objects
	 */
	protected NonBottomClassNode(ConcurrentClassTaxonomy taxonomy,
			Collection<ElkClass> members) {
		this.taxonomy_ = taxonomy;
		this.members_ = new ArrayList<ElkClass>(members);
		this.directSubNodes_ = new ArrayHashSet<UpdateableTaxonomyNode<ElkClass>>();
		this.directSuperNodes_ = new ArrayHashSet<UpdateableTaxonomyNode<ElkClass>>();
		//this.directInstanceNodes_ = new ArrayHashSet<InstanceNode<ElkClass, ElkNamedIndividual>>();
		Collections.sort(this.members_, Comparators.ELK_CLASS_COMPARATOR);
	}

	/**
	 * Add a direct super-class node. This method is not thread safe.
	 * 
	 * @param superNode
	 *            node to add
	 */
	@Override
	public synchronized void addDirectSuperNode(UpdateableTaxonomyNode<ElkClass> superNode) {
		
		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace(this + ": new direct super-node " + superNode);
		}
		
		directSuperNodes_.add(superNode);
	}

	/**
	 * Add a direct sub-class node. This method is not thread safe.
	 * 
	 * @param subNode
	 *            node to add
	 */
	@Override
	public synchronized void addDirectSubNode(UpdateableTaxonomyNode<ElkClass> subNode) {
		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace(this + ": new direct sub-node " + subNode);
		}

		if (directSubNodes_.isEmpty()) {
			this.taxonomy_.countNodesWithSubClasses.incrementAndGet();
		}

		directSubNodes_.add(subNode);
	}

	/**
	 * Add a direct instance node. This method is not thread safe.
	 * 
	 * @param instanceNode
	 *            node to add
	 */
	/*@Override
	public void addDirectInstanceNode(
			UpdateableInstanceNode<ElkClass, ElkNamedIndividual> instanceNode) {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace(this + ": new direct instance-node " + instanceNode);
		directInstanceNodes_.add(instanceNode);
	}*/

	// TODO: change the type of the output to SortedSet
	@Override
	public Set<ElkClass> getMembers() {
		// create an unmodifiable set view of the members; alternatively, one
		// could have created a TreeSet, but it consumes more memory
		return new AbstractSet<ElkClass>() {

			@Override
			public boolean contains(Object arg) {
				if (arg instanceof ElkClass)
					return (Collections.binarySearch(members_, (ElkClass) arg,
							Comparators.ELK_CLASS_COMPARATOR) >= 0);
				return false;
			}

			@Override
			public boolean isEmpty() {
				return members_.isEmpty();
			}

			@Override
			public Iterator<ElkClass> iterator() {
				return members_.iterator();
			}

			@Override
			public int size() {
				return members_.size();
			}

		};
	}

	@Override
	public ElkClass getCanonicalMember() {
		return members_.get(0);
	}

	@Override
	public Set<UpdateableTaxonomyNode<ElkClass>> getDirectSuperNodes() {
		return getDirectUpdateableSuperNodes();
	}

	@Override
	public Set<UpdateableTaxonomyNode<ElkClass>> getDirectUpdateableSuperNodes() {
		return Collections.unmodifiableSet(directSuperNodes_);
	}

	@Override
	/*public Set<TypeNode<ElkClass, ElkNamedIndividual>> getAllSuperNodes() {
		Set<TypeNode<ElkClass, ElkNamedIndividual>> result = new ArrayHashSet<TypeNode<ElkClass, ElkNamedIndividual>>(
				directSuperNodes_.size());
		Queue<TypeNode<ElkClass, ElkNamedIndividual>> todo = new LinkedList<TypeNode<ElkClass, ElkNamedIndividual>>();
		todo.addAll(directSuperNodes_);
		while (!todo.isEmpty()) {
			TypeNode<ElkClass, ElkNamedIndividual> next = todo.poll();
			if (result.add(next)) {
				for (TypeNode<ElkClass, ElkNamedIndividual> nextSuperNode : next
						.getDirectSuperNodes())
					todo.add(nextSuperNode);
			}
		}
		return Collections.unmodifiableSet(result);
	}*/
	public Set<? extends TaxonomyNode<ElkClass>> getAllSuperNodes() {
		return TaxonomyNodeUtils.getAllSuperNodes(this);
	}

	@Override
	/*public Set<TypeNode<ElkClass, ElkNamedIndividual>> getDirectSubNodes() {
		if (!directSubNodes_.isEmpty()) {
			return Collections
					.<TypeNode<ElkClass, ElkNamedIndividual>> unmodifiableSet(directSubNodes_);
		} else {
			Set<TypeNode<ElkClass, ElkNamedIndividual>> result = new ArrayHashSet<TypeNode<ElkClass, ElkNamedIndividual>>(
					1);
			result.add(this.taxonomy_.bottomClassNode);
			return Collections.unmodifiableSet(result);
		}
	}*/
	public Set<TaxonomyNode<ElkClass>> getDirectSubNodes() {
		if (!directSubNodes_.isEmpty()) {
			return Collections.<TaxonomyNode<ElkClass>> unmodifiableSet(directSubNodes_);
		} else {
			Set<TaxonomyNode<ElkClass>> result = new ArrayHashSet<TaxonomyNode<ElkClass>>(1);
			result.add(this.taxonomy_.bottomClassNode);
			
			return Collections.unmodifiableSet(result);
		}
	}

	@Override
	public Set<UpdateableTaxonomyNode<ElkClass>> getDirectUpdateableSubNodes() {
		if (!directSubNodes_.isEmpty()) {
			return Collections.unmodifiableSet(directSubNodes_);
		} else {
			return Collections.emptySet();
		}
	}

	@Override
	/*public Set<TypeNode<ElkClass, ElkNamedIndividual>> getAllSubNodes() {
		Set<TypeNode<ElkClass, ElkNamedIndividual>> result;
		if (!directSubNodes_.isEmpty()) {
			result = new ArrayHashSet<TypeNode<ElkClass, ElkNamedIndividual>>(
					directSubNodes_.size());
			Queue<TypeNode<ElkClass, ElkNamedIndividual>> todo = new LinkedList<TypeNode<ElkClass, ElkNamedIndividual>>();
			todo.addAll(directSubNodes_);
			while (!todo.isEmpty()) {
				TypeNode<ElkClass, ElkNamedIndividual> next = todo.poll();
				if (result.add(next)) {
					for (TypeNode<ElkClass, ElkNamedIndividual> nextSubNode : next
							.getDirectSubNodes())
						todo.add(nextSubNode);
				}
			}
		} else {
			result = new ArrayHashSet<TypeNode<ElkClass, ElkNamedIndividual>>(1);
			result.add(this.taxonomy_.bottomClassNode);
		}
		return Collections.unmodifiableSet(result);
	}*/
	public Set<? extends TaxonomyNode<ElkClass>> getAllSubNodes() {
		return TaxonomyNodeUtils.getAllSubNodes(this);
	}

	private final int hashCode_ = HashGenerator.generateNextHashCode();

	@Override
	public final int hashCode() {
		return hashCode_;
	}

	@Override
	public String toString() {
		return members_.toString();
	}

	/*@Override
	public Set<InstanceNode<ElkClass, ElkNamedIndividual>> getDirectInstanceNodes() {
		return Collections.unmodifiableSet(directInstanceNodes_);
	}*/

	/*@Override
	public Set<InstanceNode<ElkClass, ElkNamedIndividual>> getAllInstanceNodes() {
		Set<InstanceNode<ElkClass, ElkNamedIndividual>> result;
		if (!directSubNodes_.isEmpty()) {
			result = new ArrayHashSet<InstanceNode<ElkClass, ElkNamedIndividual>>(
					directInstanceNodes_.size());
			Queue<TypeNode<ElkClass, ElkNamedIndividual>> todo = new LinkedList<TypeNode<ElkClass, ElkNamedIndividual>>();
			todo.add(this);
			while (!todo.isEmpty()) {
				TypeNode<ElkClass, ElkNamedIndividual> next = todo.poll();
				result.addAll(next.getDirectInstanceNodes());
				for (TypeNode<ElkClass, ElkNamedIndividual> nextSubNode : next
						.getDirectSubNodes()) {
					todo.add(nextSubNode);
				}
			}
		} else {
			result = getDirectInstanceNodes();
		}
		return Collections.unmodifiableSet(result);
	}*/

	@Override
	public void clearMembers() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace(this + ": clear members");
		members_.clear();
	}

	public void setMembers(Collection<ElkClass> members) {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace(this + ": updating members to " + members);
		members_.clear();
		members_.addAll(members);
		Collections.sort(this.members_, Comparators.ELK_CLASS_COMPARATOR);
	}

	@Override
	public boolean trySetModified(boolean modified) {
		boolean result = modified_.compareAndSet(!modified, modified);
		if (result && LOGGER_.isTraceEnabled())
			LOGGER_.trace("node " + this + ": set "
					+ (modified ? "modified" : "not modifiled"));
		return result;
	}

	@Override
	public boolean isModified() {
		return modified_.get();
	}

	@Override
	public synchronized boolean removeDirectSubNode(
			UpdateableTaxonomyNode<ElkClass> subNode) {
		boolean changed = directSubNodes_.remove(subNode);

		if (changed && LOGGER_.isTraceEnabled())
			if (LOGGER_.isTraceEnabled())
				LOGGER_.trace(this + ": removed direct sub-node " + subNode);

		if (directSubNodes_.isEmpty()) {
			taxonomy_.countNodesWithSubClasses.decrementAndGet();
		}

		return changed;
	}

	@Override
	public synchronized boolean removeDirectSuperNode(
			UpdateableTaxonomyNode<ElkClass> superNode) {
		boolean changed = directSuperNodes_.remove(superNode);

		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace(this + ": removed direct super-node " + superNode);

		return changed;
	}
}
