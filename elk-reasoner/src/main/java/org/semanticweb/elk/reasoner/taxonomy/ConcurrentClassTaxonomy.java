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
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
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
	private final ConcurrentMap<ElkIri, NonBottomNode> nodeLookup;
	/* thread safe set of all nodes */
	private final Set<TaxonomyInstanceNode<ElkClass,ElkIndividual>> allNodes;
	/* counts the number of nodes which have non-bottom sub-classes */
	protected final AtomicInteger countNodesWithSubClasses;
	/* thread safe set of unsatisfiable classes */
	protected final Set<ElkClass> unsatisfiableClasses;
	/**
	 * The bottom node.
	 */
	protected final BottomNode bottomNode;

	ConcurrentClassTaxonomy() {
		this.nodeLookup = new ConcurrentHashMap<ElkIri, NonBottomNode>();
		this.allNodes = Collections
				.newSetFromMap(new ConcurrentHashMap<TaxonomyInstanceNode<ElkClass,ElkIndividual>, Boolean>());
		this.bottomNode = new BottomNode();
		allNodes.add(this.bottomNode);
		this.countNodesWithSubClasses = new AtomicInteger(0);
		this.unsatisfiableClasses = Collections
				.synchronizedSet(new TreeSet<ElkClass>(
						Comparators.ELK_CLASS_COMPARATOR));
		this.unsatisfiableClasses.add(PredefinedElkClass.OWL_NOTHING);
	}

	@Override
	public Set<TaxonomyInstanceNode<ElkClass,ElkIndividual>> getNodes() {
		return Collections.unmodifiableSet(allNodes);
	}

	/**
	 * Returns the IRI of the given ELK class.
	 * 
	 * @return the IRI of the given ELK class
	 */
	static ElkIri getKey(ElkClass elkClass) {
		return elkClass.getIri();
	}

	/**
	 * Get non-bottom node assigned to the given {@link ElkClass}, or
	 * <tt>null</tt> if none is assigned.
	 * 
	 * @param elkClass
	 *            the class for which to find the node non-bottom
	 * @return the non-bottom node for the given {@link ElkClass}
	 */
	protected NonBottomNode getNonBottomNode(ElkClass elkClass) {
		return nodeLookup.get(getKey(elkClass));
	}

	final ElkObjectFactory objectFactory = new ElkObjectFactoryImpl();

	/**
	 * Obtain a ClassNode object for a given {@link ElkClass}, <tt>null</tt> if
	 * none assigned
	 * 
	 * @param elkClass
	 * @return ClassNode object for elkClass, possibly still incomplete
	 */
	@Override
	public TaxonomyInstanceNode<ElkClass,ElkIndividual> getNode(ElkClass elkClass) {
		TaxonomyInstanceNode<ElkClass,ElkIndividual> result = getNonBottomNode(elkClass);
		if (result != null)
			return result;
		if (unsatisfiableClasses.contains(elkClass))
			return this.bottomNode;
		LOGGER_.error("No taxonomy node for class "
				+ elkClass.getIri().asString());
		return null;
	}

	NonBottomNode getCreate(Collection<ElkClass> members) {
		ElkClass someMember = null;
		for (ElkClass member : members) {
			someMember = member;
			break;
		}

		NonBottomNode previous = nodeLookup.get(getKey(someMember));
		if (previous != null)
			return previous;

		NonBottomNode node = new NonBottomNode(this, members);
		// we assign first for the node to the canonical member to avoid
		// concurrency problems
		ElkClass canonical = node.getCanonicalMember();
		previous = nodeLookup.putIfAbsent(getKey(canonical), node);
		if (previous != null)
			return previous;

		allNodes.add(node);
		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace(canonical + ": node created");
		}
		for (ElkClass member : members) {
			if (member != canonical)
				nodeLookup.put(getKey(member), node);
		}
		return node;
	}
	
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
	protected class BottomNode implements TaxonomyInstanceNode<ElkClass,ElkIndividual> {

		@Override
		public Set<ElkClass> getMembers() {
			return unsatisfiableClasses;
		}

		@Override
		public ElkClass getCanonicalMember() {
			return PredefinedElkClass.OWL_NOTHING;
		}

		@Override
		public Set<TaxonomyInstanceNode<ElkClass,ElkIndividual>> getDirectSuperNodes() {
			return Operations.filter(allNodes,
					new Condition<TaxonomyNode<ElkClass>>() {
						public boolean holds(TaxonomyNode<ElkClass> element) {
							return element.getDirectSubNodes().contains(
									bottomNode);
						}
						/*
						 * the direct super nodes of the bottom node are all
						 * nodes except the nodes that have no non-bottom
						 * sub-classes and the bottom node
						 */
					}, allNodes.size() - countNodesWithSubClasses.get() - 1);
		}

		@Override
		public Set<TaxonomyInstanceNode<ElkClass,ElkIndividual>> getAllSuperNodes() {
			/* all nodes except this one */
			return Operations.filter(allNodes, new Condition<Object>() {
				@Override
				public boolean holds(Object element) {
					return element != bottomNode;
				}
			}, allNodes.size() - 1);
		}

		@Override
		public Set<TaxonomyInstanceNode<ElkClass,ElkIndividual>> getDirectSubNodes() {
			return Collections.emptySet();
		}

		@Override
		public Set<TaxonomyInstanceNode<ElkClass,ElkIndividual>> getAllSubNodes() {
			return Collections.emptySet();
		}

		@Override
		public InstanceTaxonomy<ElkClass,ElkIndividual> getTaxonomy() {
			return ConcurrentClassTaxonomy.this;
		}

		@Override
		public Set<Node<ElkIndividual>> getDirectInstances() {
			return Collections.emptySet();
		}

		@Override
		public Set<Node<ElkIndividual>> getInstances() {
			return Collections.emptySet();
		}
	}

}