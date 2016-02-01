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
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.reasoner.taxonomy.model.ComparatorKeyProvider;
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
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ConcurrentClassTaxonomy.class);

	/** provides keys that are used for hashing instead of the elkClasses */
	private final ComparatorKeyProvider<ElkEntity> classKeyProvider_;
	/** thread safe map from class IRIs to class nodes */
	private final ConcurrentMap<Object, NonBottomClassNode> classNodeLookup_;
	/** thread safe set of all satisfiable class nodes */
	private final Set<NonBottomClassNode> allSatisfiableClassNodes_;
	/** counts the number of nodes which have non-bottom sub-classes */
	final AtomicInteger countNodesWithSubClasses;
	/** thread safe set of unsatisfiable classes */
	private final ConcurrentMap<Object, ElkClass> unsatisfiableClasses_;

	/**
	 * The bottom node.
	 */
	private final BottomClassNode bottomClassNode_;

	public ConcurrentClassTaxonomy(final ComparatorKeyProvider<ElkEntity> classKeyProvider) {
		this.classKeyProvider_ = classKeyProvider;
		this.classNodeLookup_ = new ConcurrentHashMap<Object, NonBottomClassNode>();
		this.allSatisfiableClassNodes_ = Collections
				.newSetFromMap(new ConcurrentHashMap<NonBottomClassNode, Boolean>());
		this.bottomClassNode_ = new BottomClassNode();
		this.countNodesWithSubClasses = new AtomicInteger(0);
		this.unsatisfiableClasses_ = new ConcurrentHashMap<Object, ElkClass>();
		this.unsatisfiableClasses_.put(this.classKeyProvider_.getKey(PredefinedElkClass.OWL_NOTHING),
				PredefinedElkClass.OWL_NOTHING);
	}

	@Override
	public TaxonomyNode<ElkClass> getNode(ElkClass elkClass) {
		TaxonomyNode<ElkClass> result = classNodeLookup_.get(classKeyProvider_.getKey(elkClass));

		if (result == null && unsatisfiableClasses_.containsKey(classKeyProvider_.getKey(elkClass))) {
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
		return classNodeLookup_.get(classKeyProvider_.getKey(PredefinedElkClass.OWL_THING));
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
			previous = classNodeLookup_.get(classKeyProvider_.getKey(member));
			if (previous == null)
				continue;
			synchronized (previous) {
				if (previous.size() < members.size())
					previous.setMembers(members);
				else
					return previous;
			}

			for (ElkClass newMember : members) {
				classNodeLookup_.put(classKeyProvider_.getKey(newMember), previous);
			}
			return previous;
		}

		// otherwise create a new node
		NonBottomClassNode node = new NonBottomClassNode(this, members, classKeyProvider_);

		// we first assign the node to the canonical member to avoid
		// concurrency problems
		ElkClass canonical = node.getCanonicalMember();
		previous = classNodeLookup_.putIfAbsent(classKeyProvider_.getKey(canonical), node);
		if (previous != null)
			return previous;

		allSatisfiableClassNodes_.add(node);
		
		LOGGER_.trace("node created: {}", node);
		
		for (ElkClass member : members) {
			if (member != canonical)
				classNodeLookup_.put(classKeyProvider_.getKey(member), node);
		}

		return node;
	}

	@Override
	public boolean addToBottomNode(ElkClass elkClass) {
		return unsatisfiableClasses_.put(classKeyProvider_.getKey(elkClass), elkClass) == null;
	}

	@Override
	public boolean removeFromBottomNode(ElkClass member) {
		return unsatisfiableClasses_.remove(classKeyProvider_.getKey(member)) != null;
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
			for (ElkClass member : node) {
				changed |= classNodeLookup_.remove(classKeyProvider_.getKey(member)) != null;
			}

			if (changed) {
				LOGGER_.trace("{}: node removed", node);
			}
		}

		return changed;
	}

	@Override
	public UpdateableTaxonomyNode<ElkClass> getUpdateableNode(ElkClass elkObject) {
		return classNodeLookup_.get(classKeyProvider_.getKey(elkObject));
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
		public Iterator<ElkClass> iterator() {
			return unsatisfiableClasses_.values().iterator();
		}
		
		@Override
		public boolean contains(final ElkClass member) {
			return unsatisfiableClasses_.containsKey(classKeyProvider_.getKey(member));
		}
		
		@Override
		public int size() {
			return unsatisfiableClasses_.size();
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
