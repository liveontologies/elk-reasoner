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

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.owl.util.Comparators;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.TypeNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTypeNode;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.Operations.Condition;

/**
 * Class taxonomy that is suitable for concurrent processing. Taxonomy objects
 * are only constructed for consistent ontologies, and some consequences of this
 * are hardcoded here.
 * 
 * @author Yevgeny Kazakov
 * @author Frantisek Simancik
 * @author Markus Kroetzsch
 */
class ConcurrentTaxonomy implements IndividualClassTaxonomy {

	// logger for events
	private static final Logger LOGGER_ = Logger
			.getLogger(ConcurrentTaxonomy.class);

	/** thread safe map from class IRIs to class nodes */
	private final ConcurrentMap<ElkIri, NonBottomClassNode> classNodeLookup_;
	/** thread safe set of all class nodes */
	private final Set<TypeNode<ElkClass, ElkNamedIndividual>> allClassNodes_;

	/** thread safe map from class IRIs to individual nodes */
	private final ConcurrentMap<ElkIri, IndividualNode> individualNodeLookup_;
	/** thread safe set of all individual nodes */
	private final Set<InstanceNode<ElkClass, ElkNamedIndividual>> allIndividualNodes_;

	/** counts the number of nodes which have non-bottom sub-classes */
	final AtomicInteger countNodesWithSubClasses;
	/** thread safe set of unsatisfiable classes */
	final Set<ElkClass> unsatisfiableClasses;

	/**
	 * The bottom node.
	 */
	final BottomClassNode bottomClassNode;

	ConcurrentTaxonomy() {
		this.classNodeLookup_ = new ConcurrentHashMap<ElkIri, NonBottomClassNode>();
		this.allClassNodes_ = Collections
				.newSetFromMap(new ConcurrentHashMap<TypeNode<ElkClass, ElkNamedIndividual>, Boolean>());

		this.individualNodeLookup_ = new ConcurrentHashMap<ElkIri, IndividualNode>();
		this.allIndividualNodes_ = Collections
				.newSetFromMap(new ConcurrentHashMap<InstanceNode<ElkClass, ElkNamedIndividual>, Boolean>());

		this.bottomClassNode = new BottomClassNode();
		this.allClassNodes_.add(this.bottomClassNode);
		this.countNodesWithSubClasses = new AtomicInteger(0);
		this.unsatisfiableClasses = Collections
				.synchronizedSet(new TreeSet<ElkClass>(
						Comparators.ELK_CLASS_COMPARATOR));
		this.unsatisfiableClasses.add(PredefinedElkClass.OWL_NOTHING);
	}

	/**
	 * Returns the IRI of the given ELK entity.
	 * 
	 * @return the IRI of the given ELK entity
	 */
	static ElkIri getKey(ElkEntity elkEntity) {
		return elkEntity.getIri();
	}

	/**
	 * Obtain a {@link TypeNode} object for a given {@link ElkClass}, or
	 * {@code null} if none assigned.
	 * 
	 * @param elkClass
	 * @return type node object for elkClass, possibly still incomplete
	 */
	@Override
	public TypeNode<ElkClass, ElkNamedIndividual> getTypeNode(ElkClass elkClass) {
		TypeNode<ElkClass, ElkNamedIndividual> result = classNodeLookup_
				.get(getKey(elkClass));
		if (result == null && unsatisfiableClasses.contains(elkClass))
			result = bottomClassNode;
		return result;
	}

	/**
	 * Obtain a {@link TypeNode} object for a given {@link ElkClass}, or
	 * {@code null} if none assigned.
	 * 
	 * @param individual
	 * @return instance node object for elkClass, possibly still incomplete
	 */
	@Override
	public InstanceNode<ElkClass, ElkNamedIndividual> getInstanceNode(
			ElkNamedIndividual individual) {
		return individualNodeLookup_.get(getKey(individual));
	}

	@Override
	public TaxonomyNode<ElkClass> getNode(ElkClass elkClass) {
		return getTypeNode(elkClass);
	}

	@Override
	public Set<? extends TypeNode<ElkClass, ElkNamedIndividual>> getTypeNodes() {
		return Collections.unmodifiableSet(allClassNodes_);
	}

	@Override
	public Set<? extends InstanceNode<ElkClass, ElkNamedIndividual>> getInstanceNodes() {
		return Collections.unmodifiableSet(allIndividualNodes_);
	}

	@Override
	public Set<? extends TaxonomyNode<ElkClass>> getNodes() {
		return getTypeNodes();
	}

	@Override
	public UpdateableTypeNode<ElkClass, ElkNamedIndividual> getTopNode() {
		return classNodeLookup_.get(getKey(PredefinedElkClass.OWL_THING));
	}

	@Override
	public TypeNode<ElkClass, ElkNamedIndividual> getBottomNode() {
		return bottomClassNode;
	}

	public void updateMembers(NonBottomClassNode node,
			Collection<ElkClass> members) {
		synchronized (node) {
			if (node.getMembers().size() == members.size())
				return;
			node.setMembers(members);
		}

	}

	@Override
	public NonBottomClassNode getCreateTypeNode(Collection<ElkClass> members) {

		NonBottomClassNode previous;

		// search if some node is already assigned to some member, and if so
		// use this node and update its members if necessary (members can only
		// increase during the incremental update)
		for (ElkClass member : members) {
			previous = classNodeLookup_.get(getKey(member));
			if (previous == null)
				continue;
			synchronized (previous) {
				if (previous.getMembers().size() < members.size())
					previous.setMembers(members);
				else
					return previous;
			}

			for (ElkClass newMember : members) {
				classNodeLookup_.put(getKey(newMember), previous);
			}
			return previous;
		}

		// otherwise create a new node
		NonBottomClassNode node = new NonBottomClassNode(this, members);

		// we first assign the node to the canonical member to avoid
		// concurrency problems
		ElkClass canonical = node.getCanonicalMember();
		previous = classNodeLookup_.putIfAbsent(getKey(canonical), node);
		if (previous != null)
			return previous;

		allClassNodes_.add(node);
		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace("node created: " + node);
		}
		for (ElkClass member : members) {
			if (member != canonical)
				classNodeLookup_.put(getKey(member), node);
		}

		return node;
	}

	@Override
	public IndividualNode getCreateIndividualNode(
			Collection<ElkNamedIndividual> members) {
        // TODO: use the same technique as for getCreateTypeNode for incremental update
		// TODO: avoid code duplication!
		
		IndividualNode node = new IndividualNode(this, members);
		// we first assign the node to the canonical member to avoid
		// concurrency problems
		ElkNamedIndividual canonical = node.getCanonicalMember();
		IndividualNode previous = individualNodeLookup_.putIfAbsent(
				getKey(canonical), node);
		if (previous != null)
			return previous;

		allIndividualNodes_.add(node);
		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace(OwlFunctionalStylePrinter.toString(canonical)
					+ ": node created");
		}
		for (ElkNamedIndividual member : members) {
			if (member != canonical)
				individualNodeLookup_.put(getKey(member), node);
		}
		return node;
	}

	@Override
	public boolean addToBottomNode(ElkClass elkClass) {
		return unsatisfiableClasses.add(elkClass);
	}

	@Override
	public UpdateableTaxonomyNode<ElkClass> getCreateNode(
			Collection<ElkClass> members) {
		return getCreateTypeNode(members);
	}

	@Override
	public boolean removeNode(UpdateableTaxonomyNode<ElkClass> node) {
		boolean changed = false;

		allClassNodes_.remove(node);
		// removing node assignment for members
		for (ElkClass member : node.getMembers()) {
			changed |= classNodeLookup_.remove(getKey(member)) != null;
		}
		if (changed && LOGGER_.isTraceEnabled()) {
			LOGGER_.trace(node + ": node removed");
		}

		return changed;
	}

	@Override
	public boolean removeInstanceNode(ElkNamedIndividual instance) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public UpdateableTaxonomyNode<ElkClass> getUpdateableNode(ElkClass elkObject) {
		return getUpdateableTypeNode(elkObject);
	}

	@Override
	public Iterable<? extends UpdateableTaxonomyNode<ElkClass>> getUpdateableNodes() {
		return classNodeLookup_.values();
	}

	@Override
	public UpdateableTypeNode<ElkClass, ElkNamedIndividual> getUpdateableTypeNode(
			ElkClass elkObject) {
		return classNodeLookup_.get(getKey(elkObject));
	}

	/**
	 * Special implementation for the bottom node in the taxonomy. Instead of
	 * storing its sub- and super-classes, the respective answers are computed
	 * or taken from the taxonomy object directly. This saves memory at the cost
	 * of some performance if somebody should wish to traverse an ontology
	 * bottom-up starting from this node.
	 */
	protected class BottomClassNode implements
			TypeNode<ElkClass, ElkNamedIndividual> {

		@Override
		public Set<ElkClass> getMembers() {
			return unsatisfiableClasses;
		}

		@Override
		public ElkClass getCanonicalMember() {
			return PredefinedElkClass.OWL_NOTHING;
		}

		@Override
		public Set<TypeNode<ElkClass, ElkNamedIndividual>> getDirectSuperNodes() {
			return Operations.filter(allClassNodes_,
					new Condition<TaxonomyNode<ElkClass>>() {
						@Override
						public boolean holds(TaxonomyNode<ElkClass> element) {
							return element.getDirectSubNodes().contains(
									bottomClassNode);
						}
						/*
						 * the direct super nodes of the bottom node are all
						 * nodes except the nodes that have no non-bottom
						 * sub-classes and the bottom node
						 */
					}, allClassNodes_.size() - countNodesWithSubClasses.get()
							- 1);
		}

		@Override
		public Set<TypeNode<ElkClass, ElkNamedIndividual>> getAllSuperNodes() {
			/* all nodes except this one */
			return Operations.filter(allClassNodes_, new Condition<Object>() {
				@Override
				public boolean holds(Object element) {
					return element != bottomClassNode;
				}
			}, allClassNodes_.size() - 1);
		}

		@Override
		public Set<TypeNode<ElkClass, ElkNamedIndividual>> getDirectSubNodes() {
			return Collections.emptySet();
		}

		@Override
		public Set<TypeNode<ElkClass, ElkNamedIndividual>> getAllSubNodes() {
			return Collections.emptySet();
		}

		@Override
		public InstanceTaxonomy<ElkClass, ElkNamedIndividual> getTaxonomy() {
			return ConcurrentTaxonomy.this;
		}

		@Override
		public Set<InstanceNode<ElkClass, ElkNamedIndividual>> getDirectInstanceNodes() {
			return Collections.emptySet();
		}

		@Override
		public Set<InstanceNode<ElkClass, ElkNamedIndividual>> getAllInstanceNodes() {
			return Collections.emptySet();
		}
	}
}