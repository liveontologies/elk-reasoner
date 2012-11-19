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
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.owl.util.Comparators;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TypeNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTypeNode;
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
class NonBottomClassNode implements
		UpdateableTypeNode<ElkClass, ElkNamedIndividual> {

	// logger for events
	private static final Logger LOGGER_ = Logger
			.getLogger(NonBottomClassNode.class);

	/**
	 * The link to the taxonomy to which this node belongs
	 */
	private final ConcurrentTaxonomy taxonomy_;

	/**
	 * Equivalent ElkClass objects that are representatives of this node.
	 */
	private final List<ElkClass> members_;
	/**
	 * ElkClass nodes whose members are direct super-classes of the members of
	 * this node.
	 */
	private final Set<TypeNode<ElkClass, ElkNamedIndividual>> directSuperNodes_;
	/**
	 * ElkClass nodes, except for the bottom node, whose members are direct
	 * sub-classes of the members of this node.
	 */
	private final Set<TypeNode<ElkClass, ElkNamedIndividual>> directSubNodes_;
	/**
	 * ElkNamedIndividual nodes whose members are instances of the members of
	 * this node.
	 */
	private final Set<InstanceNode<ElkClass, ElkNamedIndividual>> directInstanceNodes_;

	/**
	 * Constructing the class node for a given taxonomy and the set of
	 * equivalent classes.
	 * 
	 * @param taxonomy
	 *            the taxonomy to which this node belongs
	 * @param members
	 *            non-empty list of equivalent ElkClass objects
	 */
	//TODO think how to get rid of these unchecked casts
	protected NonBottomClassNode(ConcurrentTaxonomy taxonomy,
			Collection<ElkClass> members) {
		this.taxonomy_ = taxonomy;
		this.members_ = new ArrayList<ElkClass>(members);
		this.directSubNodes_ = new ArrayHashSet<TypeNode<ElkClass, ElkNamedIndividual>>();
		this.directSuperNodes_ = new ArrayHashSet<TypeNode<ElkClass, ElkNamedIndividual>>();
		this.directInstanceNodes_ = new ArrayHashSet<InstanceNode<ElkClass, ElkNamedIndividual>>();
		Collections.sort(this.members_, Comparators.ELK_CLASS_COMPARATOR);
	}

	/**
	 * Add a direct super-class node. This method is not thread safe.
	 * 
	 * @param superNode
	 *            node to add
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void addDirectSuperNode(UpdateableTaxonomyNode<ElkClass> superNode) {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace(this + ": new direct super-node " + superNode);
		try {
			directSuperNodes_.add((UpdateableTypeNode<ElkClass, ElkNamedIndividual>)superNode);
		}
		catch (ClassCastException e) {
			throw new IllegalArgumentException(superNode + " is not a type node!");
		}
	}

	/**
	 * Add a direct sub-class node. This method is not thread safe.
	 * 
	 * @param subNode
	 *            node to add
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void addDirectSubNode(UpdateableTaxonomyNode<ElkClass> subNode) {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace(this + ": new direct sub-node " + subNode);
		
		try {
			directSubNodes_.add((UpdateableTypeNode<ElkClass, ElkNamedIndividual>)subNode);
			
			if (directSubNodes_.isEmpty()) {
				this.taxonomy_.countNodesWithSubClasses.incrementAndGet();
			}
		}
		catch (ClassCastException e) {
			throw new IllegalArgumentException(subNode + " is not a type node!");
		}
	}

	/**
	 * Add a direct instance node. This method is not thread safe.
	 * 
	 * @param instanceNode
	 *            node to add
	 */
	@Override
	public void addDirectInstanceNode(
			UpdateableInstanceNode<ElkClass, ElkNamedIndividual> instanceNode) {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace(this + ": new direct instance-node " + instanceNode);
		directInstanceNodes_.add(instanceNode);
	}

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
	public Set<TypeNode<ElkClass, ElkNamedIndividual>> getDirectSuperNodes() {
		return Collections.unmodifiableSet(directSuperNodes_);
	}

	@Override
	public Set<TypeNode<ElkClass, ElkNamedIndividual>> getAllSuperNodes() {
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
	}

	@Override
	public Set<TypeNode<ElkClass, ElkNamedIndividual>> getDirectSubNodes() {
		if (!directSubNodes_.isEmpty()) {
			return Collections.unmodifiableSet(directSubNodes_);
		} else {
			Set<TypeNode<ElkClass, ElkNamedIndividual>> result = new ArrayHashSet<TypeNode<ElkClass, ElkNamedIndividual>>(
					1);
			result.add(this.taxonomy_.bottomClassNode);
			return Collections.unmodifiableSet(result);
		}
	}

	@Override
	public Set<TypeNode<ElkClass, ElkNamedIndividual>> getAllSubNodes() {
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
	}

	private final int hashCode_ = HashGenerator.generateNextHashCode();

	@Override
	public final int hashCode() {
		return hashCode_;
	}

	@Override
	public InstanceTaxonomy<ElkClass, ElkNamedIndividual> getTaxonomy() {
		return this.taxonomy_;
	}

	@Override
	public String toString() {
		return OwlFunctionalStylePrinter.toString(getCanonicalMember());
	}

	@Override
	public Set<InstanceNode<ElkClass, ElkNamedIndividual>> getDirectInstanceNodes() {
		return Collections.unmodifiableSet(directInstanceNodes_);
	}

	@Override
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
	}

	@Override
	public void clearMembers() {
		members_.clear();
	}
}
