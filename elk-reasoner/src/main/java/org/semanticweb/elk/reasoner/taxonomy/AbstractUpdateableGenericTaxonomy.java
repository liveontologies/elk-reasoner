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
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.model.ComparatorKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.NodeFactory;
import org.semanticweb.elk.reasoner.taxonomy.model.NonBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableGenericNodeStore;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableGenericTaxonomyNode;
import org.semanticweb.elk.util.collections.LazySetUnion;
import org.semanticweb.elk.util.collections.Operations;

/**
 * @author Peter Skocovsky
 */
public abstract class AbstractUpdateableGenericTaxonomy<
				T extends ElkEntity,
				N extends UpdateableGenericTaxonomyNode<T, N>
		>
		extends AbstractDistinctBottomTaxonomy<T> {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(AbstractUpdateableGenericTaxonomy.class);
	
	private final NodeFactory<T, N> nodeFactory_;

	/** The store containing non-bottom nodes in this taxonomy. */
	private final UpdateableGenericNodeStore<T, N> nodeStore_;

	private final T topMember_;
	
	/** The bottom node. */
	private final TaxonomyNode<T> bottomClassNode_;

	public AbstractUpdateableGenericTaxonomy(
			final UpdateableGenericNodeStore<T, N> nodeStore,
			final InternalNodeFactoryFactory<T, N> internalNodeFactoryFactory,
			final InternalNodeFactoryFactory<T, TaxonomyNode<T>> bottomNodeFactoryFactory,
			final T topMember) {
		super();
		this.nodeStore_ = nodeStore;
		this.nodeFactory_ = internalNodeFactoryFactory.createInternalNodeFactory(this);
		this.topMember_ = topMember;
		this.bottomClassNode_ = bottomNodeFactoryFactory.createInternalNodeFactory(this).createNode(Collections.<T>emptyList(), 0, getKeyProvider());
	}

	@Override
	public ComparatorKeyProvider<? super T> getKeyProvider() {
		return nodeStore_.getKeyProvider();
	}

	@Override
	public TaxonomyNode<T> getNode(T elkClass) {
		TaxonomyNode<T> result = nodeStore_.getNode(elkClass);
		if (result == null && bottomClassNode_.contains(elkClass)) {
			result = bottomClassNode_;
		}
		return result;
	}

	@Override
	public NonBottomTaxonomyNode<T> getNonBottomNode(final T elkEntity) {
		return nodeStore_.getNode(elkEntity);
	}
	
	@Override
	public Set<? extends TaxonomyNode<T>> getNodes() {
		return new LazySetUnion<TaxonomyNode<T>>(nodeStore_.getNodes(),
				Collections.singleton(bottomClassNode_));
	}

	@Override
	public Set<? extends N> getNonBottomNodes() {
		return nodeStore_.getNodes();
	}
	
	@Override
	public NonBottomTaxonomyNode<T> getTopNode() {
		NonBottomTaxonomyNode<T> top = nodeStore_.getNode(topMember_);
		if (top == null) {
			top = nodeStore_.getCreateNode(
					Operations.singleton(topMember_), 1,
					nodeFactory_);
		}
		return top;
	}

	@Override
	public TaxonomyNode<T> getBottomNode() {
		return bottomClassNode_;
	}

	@Override
	public NonBottomTaxonomyNode<T> getCreateNode(final Collection<? extends T> members) {
		return nodeStore_.getCreateNode(members, members.size(),
				nodeFactory_);
	};

	@Override
	public boolean setCreateDirectSupernodes(final NonBottomTaxonomyNode<T> subNode,
			final Iterable<? extends Collection<? extends T>> superMemberSets) {

		final N node = toInternalNode(subNode);
		
		// TODO: establish consistency by adding default parent to the nodes.
		
		for (final Collection<? extends T> superMembers : superMemberSets) {
			final N superNode = nodeStore_.getCreateNode(
					superMembers, superMembers.size(), nodeFactory_);
			addDirectRelation(superNode, node);
		}

		return node.trySetAllParentsAssigned(true);
	}

	private void addDirectRelation(
			final N superNode,
			final N subNode) {
		subNode.addDirectSuperNode(superNode);
		superNode.addDirectSubNode(subNode);
	}

	@Override
	public boolean removeDirectSupernodes(final NonBottomTaxonomyNode<T> subNode) {

		final N node = toInternalNode(subNode);

		if (!node.trySetAllParentsAssigned(false)) {
			return false;
		}

		final List<N> superNodes = new ArrayList<N>();

		// remove all super-class links
		synchronized (node) {
			superNodes.addAll(node.getDirectNonBottomSuperNodes());
			for (N superNode : superNodes) {
				node.removeDirectSuperNode(superNode);
			}
		}

		for (N superNode : superNodes) {
			synchronized (superNode) {
				superNode.removeDirectSubNode(node);
			}
		}

		return true;
	}

	@Override
	public boolean removeNode(final T member) {
		if (nodeStore_.removeNode(member)) {
			LOGGER_.trace("removed node with member: {}", member);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean addToBottomNode(final T member) {
		return unsatisfiableClasses_.put(
				nodeStore_.getKeyProvider().getKey(member), member) == null;
	}

	@Override
	public boolean removeFromBottomNode(final T member) {
		return unsatisfiableClasses_
				.remove(nodeStore_.getKeyProvider().getKey(member)) != null;
	}

	@SuppressWarnings("unchecked")
	private N toInternalNode(final TaxonomyNode<T> node) {
		if (!(node instanceof UpdateableGenericTaxonomyNode)) {
			throw new IllegalArgumentException(
					"The sub-node must belong to this taxonomy: " + node);
		}
		if (node.getTaxonomy() != this) {
			throw new IllegalArgumentException(
					"The sub-node must belong to this taxonomy: " + node);
		}
		// By construction, if the node is in this taxonomy, it is of type N.
		return (N) node;
	}
	
}
