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
 * @author Markus Kroetzsch
 */
public class IndividualNode implements
		InstanceNode<ElkClass, ElkNamedIndividual> {

	// logger for events
	private static final Logger LOGGER_ = Logger
			.getLogger(IndividualNode.class);

	/**
	 * The link to the taxonomy to which this node belongs
	 */
	final ConcurrentTaxonomy taxonomy;

	/**
	 * Equivalent ElkClass objects that are representatives of this node.
	 */
	private final List<ElkNamedIndividual> members;
	/**
	 * ElkClass nodes whose members are direct types of the members of this
	 * node.
	 */
	private final Set<TypeNode<ElkClass, ElkNamedIndividual>> directTypeNodes;

	/**
	 * Constructing the class node for a given taxonomy and the set of
	 * equivalent classes.
	 * 
	 * @param taxonomy
	 *            the taxonomy to which this node belongs
	 * @param members
	 *            non-empty list of equivalent ElkClass objects
	 */
	protected IndividualNode(ConcurrentTaxonomy taxonomy,
			Collection<ElkNamedIndividual> members) {
		this.taxonomy = taxonomy;
		this.members = new ArrayList<ElkNamedIndividual>(members);
		this.directTypeNodes = new ArrayHashSet<TypeNode<ElkClass, ElkNamedIndividual>>();
		Collections.sort(this.members,
				Comparators.ELK_NAMED_INDIVIDUAL_COMPARATOR);
	}

	/**
	 * Add a direct super-class node. This method is not thread safe.
	 * 
	 * @param superNode
	 *            node to add
	 */
	void addDirectTypeNode(NonBottomClassNode typeNode) {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace(this + ": new direct type-node " + typeNode);
		directTypeNodes.add(typeNode);
	}

	@Override
	public Set<ElkNamedIndividual> getMembers() {
		// create an unmodifiable set view of the members; alternatively, one
		// could have created a TreeSet, but it consumes more memory
		return new AbstractSet<ElkNamedIndividual>() {

			@Override
			public boolean contains(Object arg) {
				if (arg instanceof ElkNamedIndividual)
					return (Collections.binarySearch(members,
							(ElkNamedIndividual) arg,
							Comparators.ELK_NAMED_INDIVIDUAL_COMPARATOR) >= 0);
				return false;
			}

			@Override
			public boolean isEmpty() {
				return members.isEmpty();
			}

			@Override
			public Iterator<ElkNamedIndividual> iterator() {
				return members.iterator();
			}

			@Override
			public int size() {
				return members.size();
			}

		};
	}

	@Override
	public ElkNamedIndividual getCanonicalMember() {
		return members.get(0);
	}

	@Override
	public Set<TypeNode<ElkClass, ElkNamedIndividual>> getDirectTypeNodes() {
		return Collections.unmodifiableSet(directTypeNodes);
	}

	@Override
	public Set<TypeNode<ElkClass, ElkNamedIndividual>> getAllTypeNodes() {
		Set<TypeNode<ElkClass, ElkNamedIndividual>> result = new ArrayHashSet<TypeNode<ElkClass, ElkNamedIndividual>>(
				directTypeNodes.size());

		Queue<TypeNode<ElkClass, ElkNamedIndividual>> todo = new LinkedList<TypeNode<ElkClass, ElkNamedIndividual>>();
		todo.addAll(directTypeNodes);
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

	private final int hashCode_ = HashGenerator.generateNextHashCode();

	@Override
	public final int hashCode() {
		return hashCode_;
	}

	@Override
	public InstanceTaxonomy<ElkClass, ElkNamedIndividual> getTaxonomy() {
		return this.taxonomy;
	}

	@Override
	public String toString() {
		return getCanonicalMember().getIri().asString();
	}

}
