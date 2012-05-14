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
import org.semanticweb.elk.owl.util.Comparators;
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
class ConcurrentClassTaxonomy extends IndividualClassTaxonomy {

	// logger for events
	private static final Logger LOGGER_ = Logger
			.getLogger(ConcurrentClassTaxonomy.class);

	/* thread safe map from class IRIs to class nodes */
	protected final ConcurrentMap<ElkIri, NonBottomClassNode> classNodeLookup;
	/* thread safe set of all class nodes */
	protected final Set<TypeNode<ElkClass,ElkNamedIndividual>> allClassNodes;
	/* counts the number of nodes which have non-bottom sub-classes */

	/* thread safe map from class IRIs to individual nodes */
	protected final ConcurrentMap<ElkIri, IndividualNode> individualNodeLookup;
	/* thread safe set of all individual nodes */
	protected final Set<InstanceNode<ElkClass,ElkNamedIndividual>> allIndividualNodes;

	protected final AtomicInteger countNodesWithSubClasses;
	/* thread safe set of unsatisfiable classes */
	protected final Set<ElkClass> unsatisfiableClasses;
	
	/**
	 * The bottom node.
	 */
	protected final BottomClassNode bottomClassNode;

	ConcurrentClassTaxonomy() {
		this.classNodeLookup = new ConcurrentHashMap<ElkIri, NonBottomClassNode>();
		this.allClassNodes = Collections
				.newSetFromMap(new ConcurrentHashMap<TypeNode<ElkClass,ElkNamedIndividual>, Boolean>());
		
		this.individualNodeLookup = new ConcurrentHashMap<ElkIri, IndividualNode>();
		this.allIndividualNodes = Collections
				.newSetFromMap(new ConcurrentHashMap<InstanceNode<ElkClass,ElkNamedIndividual>, Boolean>());
		
		this.bottomClassNode = new BottomClassNode();
		allClassNodes.add(this.bottomClassNode);
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
	 * Obtain a ClassNode object for a given {@link ElkClass}, <tt>null</tt> if
	 * none assigned
	 * 
	 * @param elkClass
	 * @return ClassNode object for elkClass, possibly still incomplete
	 */
	@Override
	public TypeNode<ElkClass,ElkNamedIndividual> getTypeNode(ElkClass elkClass) {
		NonBottomClassNode result = classNodeLookup.get(getKey(elkClass));
		if (result != null)
			return result;
		if (unsatisfiableClasses.contains(elkClass))
			return this.bottomClassNode;
		LOGGER_.error("No taxonomy node for class "
				+ elkClass.getIri().asString());
		return null;
	}
	
	@Override
	public InstanceNode<ElkClass, ElkNamedIndividual> getInstanceNode(
			ElkNamedIndividual individual) {
		IndividualNode result = individualNodeLookup.get(getKey(individual));
		if (result == null)
			LOGGER_.error("No taxonomy node for individual "
					+ individual.getIri().asString());
		return null;
	}
	
	@Override
	public TaxonomyNode<ElkClass> getNode(ElkClass elkClass) {
		return getTypeNode(elkClass);
	}
	
	@Override
	public Set<? extends TypeNode<ElkClass,ElkNamedIndividual>> getTypeNodes() {
		return Collections.unmodifiableSet(allClassNodes);
	}
	
	@Override
	public Set<? extends InstanceNode<ElkClass, ElkNamedIndividual>> getInstanceNodes() {
		return Collections.unmodifiableSet(allIndividualNodes);
	}

	@Override
	public Set<? extends TaxonomyNode<ElkClass>> getNodes() {
		return getTypeNodes();
	}
	
	@Override
	NonBottomClassNode getCreateClassNode(Collection<ElkClass> members) {
		ElkClass someMember = null;
		for (ElkClass member : members) {
			someMember = member;
			break;
		}

		NonBottomClassNode previous = classNodeLookup.get(getKey(someMember));
		if (previous != null)
			return previous;

		NonBottomClassNode node = new NonBottomClassNode(this, members);
		// we assign first for the node to the canonical member to avoid
		// concurrency problems
		ElkClass canonical = node.getCanonicalMember();
		previous = classNodeLookup.putIfAbsent(getKey(canonical), node);
		if (previous != null)
			return previous;

		allClassNodes.add(node);
		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace(canonical + ": node created");
		}
		for (ElkClass member : members) {
			if (member != canonical)
				classNodeLookup.put(getKey(member), node);
		}
		return node;
	}
	
	@Override
	IndividualNode getCreateIndividualNode(
			Collection<ElkNamedIndividual> members) {
		
		IndividualNode node = new IndividualNode(this, members);
		// we assign first for the node to the canonical member to avoid
		// concurrency problems
		ElkNamedIndividual canonical = node.getCanonicalMember();
		IndividualNode previous = individualNodeLookup.putIfAbsent(getKey(canonical), node);
		if (previous != null)
			return previous;

		allIndividualNodes.add(node);
		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace(canonical + ": node created");
		}
		for (ElkNamedIndividual member : members) {
			if (member != canonical)
				individualNodeLookup.put(getKey(member), node);
		}
		return node;
	}

	@Override
	void addUnsatisfiableClass(ElkClass elkClass) {
		unsatisfiableClasses.add(elkClass);
	}

	

	/**
	 * Special implementation for the bottom node in the taxonomy. Instead of
	 * storing its sub- and super-classes, the respective answers are computed
	 * or taken from the taxonomy object directly. This safes memory at the cost
	 * of some performance if somebody should wish to traverse an ontology
	 * bottom-up starting from this node.
	 */
	protected class BottomClassNode implements TypeNode<ElkClass,ElkNamedIndividual> {

		@Override
		public Set<ElkClass> getMembers() {
			return unsatisfiableClasses;
		}

		@Override
		public ElkClass getCanonicalMember() {
			return PredefinedElkClass.OWL_NOTHING;
		}

		@Override
		public Set<TypeNode<ElkClass,ElkNamedIndividual>> getDirectSuperNodes() {
			return Operations.filter(allClassNodes,
					new Condition<TaxonomyNode<ElkClass>>() {
						public boolean holds(TaxonomyNode<ElkClass> element) {
							return element.getDirectSubNodes().contains(
									bottomClassNode);
						}
						/*
						 * the direct super nodes of the bottom node are all
						 * nodes except the nodes that have no non-bottom
						 * sub-classes and the bottom node
						 */
					}, allClassNodes.size() - countNodesWithSubClasses.get() - 1);
		}

		@Override
		public Set<TypeNode<ElkClass,ElkNamedIndividual>> getAllSuperNodes() {
			/* all nodes except this one */
			return Operations.filter(allClassNodes, new Condition<Object>() {
				@Override
				public boolean holds(Object element) {
					return element != bottomClassNode;
				}
			}, allClassNodes.size() - 1);
		}

		@Override
		public Set<TypeNode<ElkClass,ElkNamedIndividual>> getDirectSubNodes() {
			return Collections.emptySet();
		}

		@Override
		public Set<TypeNode<ElkClass,ElkNamedIndividual>> getAllSubNodes() {
			return Collections.emptySet();
		}

		@Override
		public InstanceTaxonomy<ElkClass,ElkNamedIndividual> getTaxonomy() {
			return ConcurrentClassTaxonomy.this;
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