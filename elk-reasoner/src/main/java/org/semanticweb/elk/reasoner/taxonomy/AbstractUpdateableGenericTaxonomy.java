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
import org.semanticweb.elk.reasoner.taxonomy.model.BottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.ComparatorKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.NonBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableGenericNodeStore;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableGenericTaxonomyNode;
import org.semanticweb.elk.util.collections.LazySetUnion;

/**
 * @author Peter Skocovsky
 */
public abstract class AbstractUpdateableGenericTaxonomy<
				T extends ElkEntity,
				N extends UpdateableGenericTaxonomyNode<T, N>,
				BN extends BottomTaxonomyNode<T>
		>
		extends AbstractDistinctBottomTaxonomy<T> {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(AbstractUpdateableGenericTaxonomy.class);
	
	private final InternalNodeFactoryFactory<T, N, BN> nodeFactoryFactory_;

	/** The store containing non-bottom nodes in this taxonomy. */
	protected final UpdateableGenericNodeStore<T, N> nodeStore_;

	protected final T topMember_;
	
	public AbstractUpdateableGenericTaxonomy(
			final UpdateableGenericNodeStore<T, N> nodeStore,
			final InternalNodeFactoryFactory<T, N, BN> internalNodeFactoryFactory,
			final T topMember) {
		super();
		this.nodeStore_ = nodeStore;
		this.nodeFactoryFactory_ = internalNodeFactoryFactory;
		this.topMember_ = topMember;
	}

	@Override
	public ComparatorKeyProvider<? super T> getKeyProvider() {
		return nodeStore_.getKeyProvider();
	}

	@Override
	public TaxonomyNode<T> getNode(final T elkClass) {
		TaxonomyNode<T> result = nodeStore_.getNode(elkClass);
		if (result == null && getBottomNode().contains(elkClass)) {
			result = getBottomNode();
		}
		return result;
	}

	@Override
	public N getNonBottomNode(final T elkEntity) {
		return nodeStore_.getNode(elkEntity);
	}
	
	@Override
	public Set<? extends TaxonomyNode<T>> getNodes() {
		return new LazySetUnion<TaxonomyNode<T>>(nodeStore_.getNodes(),
				Collections.singleton(getBottomNode()));
	}

	@Override
	public Set<? extends N> getNonBottomNodes() {
		return nodeStore_.getNodes();
	}
	
	@Override
	public N getTopNode() {
		N top = nodeStore_.getNode(topMember_);
		if (top == null) {
			top = getCreateNode(Collections.singleton(topMember_));
		}
		return top;
	}

	@Override
	public abstract BN getBottomNode();
	
	@Override
	public N getCreateNode(final Collection<? extends T> members) {
		return nodeStore_.getCreateNode(members, members.size(),
				nodeFactoryFactory_.createInternalNodeFactory(getBottomNode()));
	};

	@Override
	public boolean setCreateDirectSupernodes(final NonBottomTaxonomyNode<T> subNode,
			final Iterable<? extends Collection<? extends T>> superMemberSets) {

		final N node = toInternalNode(subNode);
		
		// TODO: establish consistency by adding default parent to the nodes.
		
		for (final Collection<? extends T> superMembers : superMemberSets) {
			final N superNode = getCreateNode(superMembers);
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
			for (final N superNode : superNodes) {
				node.removeDirectSuperNode(superNode);
			}
		}

		for (final N superNode : superNodes) {
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
		return getBottomNode().add(member);
	}

	@Override
	public boolean removeFromBottomNode(final T member) {
		return getBottomNode().remove(member);
	}

	@SuppressWarnings("unchecked")
	protected N toInternalNode(final NonBottomTaxonomyNode<T> node) {
		if (node.getTaxonomy() != this) {
			throw new IllegalArgumentException(
					"The sub-node must belong to this taxonomy: " + node);
		}
		// By construction, if the node is in this taxonomy, it is of type N.
		try {
			return (N) node;
		} catch (final ClassCastException e) {
			throw new IllegalArgumentException(
					"The sub-node must belong to this taxonomy: " + node);
		}
	}
	
}
