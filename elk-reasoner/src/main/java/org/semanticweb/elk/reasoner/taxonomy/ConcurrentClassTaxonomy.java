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
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.owl.util.Comparators;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableBottomNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTaxonomyNode;
import org.semanticweb.elk.util.collections.LazySetUnion;
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
 * @author Pavel Klinov
 */
public class ConcurrentClassTaxonomy implements UpdateableTaxonomy<ElkClass> {

	// logger for events
	private static final Logger LOGGER_ = Logger
			.getLogger(ConcurrentClassTaxonomy.class);

	/** thread safe map from class IRIs to class nodes */
	private final ConcurrentMap<ElkIri, NonBottomClassNode> classNodeLookup_;
	/** thread safe set of all satisfiable class nodes */
	private final Set<NonBottomClassNode> allSatisfiableClassNodes_;
	/** counts the number of nodes which have non-bottom sub-classes */
	final AtomicInteger countNodesWithSubClasses;
	/** thread safe set of unsatisfiable classes */
	private final Set<ElkClass> unsatisfiableClasses_;

	/**
	 * The bottom node.
	 */
	private final BottomClassNode bottomClassNode_;

	public ConcurrentClassTaxonomy() {
		this.classNodeLookup_ = new ConcurrentHashMap<ElkIri, NonBottomClassNode>();
		this.allSatisfiableClassNodes_ = Collections
				.newSetFromMap(new ConcurrentHashMap<NonBottomClassNode, Boolean>());
		this.bottomClassNode_ = new BottomClassNode();
		this.countNodesWithSubClasses = new AtomicInteger(0);
		this.unsatisfiableClasses_ = Collections
				.synchronizedSet(new TreeSet<ElkClass>(
						Comparators.ELK_CLASS_COMPARATOR));
		this.unsatisfiableClasses_.add(PredefinedElkClass.OWL_NOTHING);
	}

	/**
	 * Returns the IRI of the given ELK entity.
	 * 
	 * @return the IRI of the given ELK entity
	 */
	static ElkIri getKey(ElkEntity elkEntity) {
		return elkEntity.getIri();
	}

	@Override
	public TaxonomyNode<ElkClass> getNode(ElkClass elkClass) {
		TaxonomyNode<ElkClass> result = classNodeLookup_.get(getKey(elkClass));

		if (result == null && unsatisfiableClasses_.contains(elkClass)) {
			result = bottomClassNode_;
		}

		return result;
	}

	@Override
	public Set<? extends TaxonomyNode<ElkClass>> getNodes() {
		Set<? extends TaxonomyNode<ElkClass>> allNodes = allSatisfiableClassNodes_;
		Set<? extends TaxonomyNode<ElkClass>> bottom = Collections
				.<TaxonomyNode<ElkClass>> singleton(bottomClassNode_);

		return new LazySetUnion<TaxonomyNode<ElkClass>>(allNodes, bottom);	}

	@Override
	public NonBottomClassNode getTopNode() {
		return classNodeLookup_.get(getKey(PredefinedElkClass.OWL_THING));
	}

	@Override
	public TaxonomyNode<ElkClass> getBottomNode() {
		return getUpdateableBottomNode();
	}
	
	@Override
	public UpdateableBottomNode<ElkClass> getUpdateableBottomNode() {
		return bottomClassNode_;
	}

	protected NonBottomClassNode getCreateNonBottomClassNode(
			Collection<ElkClass> members) {

		if (members == null || members.isEmpty()) {
			throw new IllegalArgumentException("Empty class taxonomy nodes must not be created!");
		}
		
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

		allSatisfiableClassNodes_.add(node);
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
	public boolean addToBottomNode(ElkClass elkClass) {
		return unsatisfiableClasses_.add(elkClass);
	}

	@Override
	public UpdateableTaxonomyNode<ElkClass> getCreateNode(
			Collection<ElkClass> members) {
		return getCreateNonBottomClassNode(members);
	}

	@Override
	public boolean removeNode(UpdateableTaxonomyNode<ElkClass> node) {
		boolean changed = false;

		if (allSatisfiableClassNodes_.remove(node)) {
			// removing node assignment for members
			for (ElkClass member : node.getMembers()) {
				changed |= classNodeLookup_.remove(getKey(member)) != null;
			}

			if (changed && LOGGER_.isTraceEnabled()) {
				LOGGER_.trace(node + ": node removed");
			}
		}

		return changed;
	}

	@Override
	public UpdateableTaxonomyNode<ElkClass> getUpdateableNode(ElkClass elkObject) {
		return classNodeLookup_.get(getKey(elkObject));
	}
	
	@Override
	public UpdateableTaxonomyNode<ElkClass> getUpdateableTopNode() {
		return getTopNode();
	}
	
	@Override
	public Set<? extends UpdateableTaxonomyNode<ElkClass>> getUpdateableNodes() {
		return Collections.unmodifiableSet(allSatisfiableClassNodes_);
	}


	/**
	 * Special implementation for the bottom node in the taxonomy. Instead of
	 * storing its sub- and super-classes, the respective answers are computed
	 * or taken from the taxonomy object directly. This saves memory at the cost
	 * of some performance if somebody should wish to traverse an ontology
	 * bottom-up starting from this node.
	 */
	protected class BottomClassNode implements UpdateableBottomNode<ElkClass> {

		@Override
		public Set<ElkClass> getMembers() {
			return unsatisfiableClasses_;
		}

		@Override
		public ElkClass getCanonicalMember() {
			return PredefinedElkClass.OWL_NOTHING;
		}

		@Override
		public Set<NonBottomClassNode> getDirectSuperNodes() {
			return Operations.filter(allSatisfiableClassNodes_,
					new Condition<NonBottomClassNode>() {
						@Override
						public boolean holds(NonBottomClassNode element) {
							return element.getDirectSubNodes().contains(
									bottomClassNode_);
						}
						/*
						 * the direct super nodes of the bottom node are all
						 * nodes except the nodes that have no non-bottom
						 * sub-classes and the bottom node
						 */
					}, allSatisfiableClassNodes_.size() - countNodesWithSubClasses.get());
		}

		@Override
		public Set<NonBottomClassNode> getAllSuperNodes() {
			return allSatisfiableClassNodes_;
		}

		@Override
		public Set<TaxonomyNode<ElkClass>> getDirectSubNodes() {
			return Collections.emptySet();
		}

		@Override
		public Set<TaxonomyNode<ElkClass>> getAllSubNodes() {
			return Collections.emptySet();
		}

		@Override
		public Set<? extends UpdateableTaxonomyNode<ElkClass>> getDirectUpdateableSuperNodes() {
			return getDirectSuperNodes();
		}
	}
}