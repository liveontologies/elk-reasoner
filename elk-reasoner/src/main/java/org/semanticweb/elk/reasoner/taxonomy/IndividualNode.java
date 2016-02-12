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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.reasoner.taxonomy.model.ComparatorKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTypeNode;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * Class for storing information about a class in the context of classification.
 * It is the main data container for {@link InstanceTaxonomy} objects.
 * Like most such data containers in ELK, it is read-only for public access but
 * provides package-private ways of modifying it. Modifications of this class
 * happen in implementations of {@link InstanceTaxonomy} only.
 * 
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 * @author Peter Skocovsky
 */
public class IndividualNode implements
		UpdateableInstanceNode<ElkClass, ElkNamedIndividual> {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(IndividualNode.class);

	/**
	 * Equivalent ElkClass objects that are representatives of this node.
	 */
	private final List<ElkNamedIndividual> members_;
	/**
	 * provides keys that are used for hashing instead of the elkClasses
	 */
	private final ComparatorKeyProvider<ElkEntity> individualKeyProvider_;
	/**
	 * ElkClass nodes whose members are direct types of the members of this
	 * node.
	 */
	private final Set<UpdateableTypeNode<ElkClass, ElkNamedIndividual>> directTypeNodes_;
	/**
	 * <tt>true</tt> if the direct type nodes need to be recomputed
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
	protected IndividualNode(Collection<ElkNamedIndividual> members,
			final ComparatorKeyProvider<ElkEntity> individualKeyProvider) {

		this.members_ = new ArrayList<ElkNamedIndividual>(members);
		this.individualKeyProvider_ = individualKeyProvider;
		this.directTypeNodes_ = new ArrayHashSet<UpdateableTypeNode<ElkClass, ElkNamedIndividual>>();
		Collections.sort(this.members_, this.individualKeyProvider_.getComparator());
	}

	/**
	 * Add a direct super-class node. This method is not thread safe.
	 * 
	 * @param typeNode
	 *            node to add
	 */
	@Override
	public void addDirectTypeNode(UpdateableTypeNode<ElkClass, ElkNamedIndividual> typeNode) {
		LOGGER_.trace("{}: new direct type-node {}", this, typeNode);
		
		directTypeNodes_.add(typeNode);
	}

	@Override
	public ComparatorKeyProvider<ElkEntity> getKeyProvider() {
		return individualKeyProvider_;
	}
	
	@Override
	public Iterator<ElkNamedIndividual> iterator() {
		return members_.iterator();
	}
	
	@Override
	public boolean contains(ElkNamedIndividual member) {
		return (Collections.binarySearch(members_, member,
				individualKeyProvider_.getComparator()) >= 0);
	}
	
	@Override
	public int size() {
		return members_.size();
	}

	@Override
	public void setMembers(final Iterable<ElkNamedIndividual> members) {
		members_.clear();
		for (final ElkNamedIndividual elkIndividual : members) {
			members_.add(elkIndividual);
		}
		Collections.sort(this.members_, individualKeyProvider_.getComparator());
		LOGGER_.trace("updated members of {}", this);
	}
	
	@Override
	public ElkNamedIndividual getCanonicalMember() {
		return members_.get(0);
	}

	@Override
	public Set<UpdateableTypeNode<ElkClass, ElkNamedIndividual>> getDirectTypeNodes() {
		return Collections.unmodifiableSet(directTypeNodes_);
	}

	@Override
	public Set<UpdateableTypeNode<ElkClass, ElkNamedIndividual>> getAllTypeNodes() {
		Set<UpdateableTypeNode<ElkClass, ElkNamedIndividual>> result = new ArrayHashSet<UpdateableTypeNode<ElkClass, ElkNamedIndividual>>(
				directTypeNodes_.size());

		Queue<UpdateableTypeNode<ElkClass, ElkNamedIndividual>> todo = new LinkedList<UpdateableTypeNode<ElkClass, ElkNamedIndividual>>();

		todo.addAll(directTypeNodes_);

		while (!todo.isEmpty()) {
			UpdateableTypeNode<ElkClass, ElkNamedIndividual> next = todo.poll();

			if (result.add(next)) {
				for (UpdateableTypeNode<ElkClass, ElkNamedIndividual> nextSuperNode : next
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
	public String toString() {
		return getCanonicalMember().getIri().getFullIriAsString();
	}

	@Override
	public boolean trySetModified(boolean modified) {
		boolean result = modified_.compareAndSet(!modified, modified);
		
		if (result && LOGGER_.isTraceEnabled()) {
			LOGGER_.trace("node " + this + ": set "
					+ (modified ? "modified" : "not modifiled"));
		}
		
		return result;
	}

	@Override
	public boolean isModified() {
		return modified_.get();
	}


	@Override
	public void removeDirectTypeNode(
			UpdateableTypeNode<ElkClass, ElkNamedIndividual> typeNode) {
		LOGGER_.trace("{}: removing direct type node: {}", this, typeNode);
		
		directTypeNodes_.remove(typeNode);
	}
}
