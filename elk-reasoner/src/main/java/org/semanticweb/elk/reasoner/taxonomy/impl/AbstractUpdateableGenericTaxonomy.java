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
package org.semanticweb.elk.reasoner.taxonomy.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.model.ComparatorKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.GenericTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.elk.reasoner.taxonomy.model.NodeFactory;
import org.semanticweb.elk.reasoner.taxonomy.model.NodeStore;
import org.semanticweb.elk.reasoner.taxonomy.model.NonBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNodeFactory;
import org.semanticweb.elk.util.collections.LazySetUnion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A generic implementation of class taxonomy.
 * 
 * @author Peter Skocovsky
 *
 * @param <T>
 *            The type of members of the nodes in this taxonomy.
 * @param <N>
 *            The immutable type of nodes in this taxonomy.
 * @param <UN>
 *            The mutable type of nodes in this taxonomy.
 */
// @formatter:off
public abstract class AbstractUpdateableGenericTaxonomy<
				T extends ElkEntity,
				N extends GenericTaxonomyNode<T, N>,
				UN extends UpdateableTaxonomyNode<T, N, UN>
		>
		extends AbstractDistinctBottomTaxonomy<T, N, UN> {
// @formatter:on

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(AbstractUpdateableGenericTaxonomy.class);

	/** The factory creating the nodes of this taxonomy. */
	private final NodeFactory<T, UN> nodeFactory_;

	/** The store containing non-bottom nodes in this taxonomy. */
	protected final UpdateableNodeStore<T, UN> nodeStore_;

	/** The canonical member of the top node. */
	protected final T topMember_;

	/** The listeners notified about the changes to node store. */
	protected final List<NodeStore.Listener<T>> nodeStoreListeners_;

	/** The listeners notified about the changes to taxonomy. */
	protected final List<Taxonomy.Listener<T>> taxonomyListeners_;

	/**
	 * Constructor.
	 * 
	 * @param nodeStore
	 *            Node store for the nodes of this taxonomy.
	 * @param nodeFactory
	 *            Factory that creates nodes of this taxonomy.
	 * @param topMember
	 *            The canonical member of the top node.
	 */
	public AbstractUpdateableGenericTaxonomy(
			final UpdateableNodeStore<T, UN> nodeStore,
			final TaxonomyNodeFactory<T, UN, AbstractDistinctBottomTaxonomy<T, N, UN>> nodeFactory,
			final T topMember) {
		super();
		this.nodeStore_ = nodeStore;
		this.nodeFactory_ = new NodeFactory<T, UN>() {
			@Override
			public UN createNode(final Iterable<? extends T> members,
					final int size) {
				return nodeFactory.createNode(members, size,
						AbstractUpdateableGenericTaxonomy.this);
			}
		};
		this.topMember_ = topMember;
		this.taxonomyListeners_ = new ArrayList<Taxonomy.Listener<T>>();
		this.nodeStoreListeners_ = new ArrayList<NodeStore.Listener<T>>();
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
	public UN getNonBottomNode(final T elkEntity) {
		return nodeStore_.getNode(elkEntity);
	}

	@Override
	public Set<? extends TaxonomyNode<T>> getNodes() {
		return new LazySetUnion<TaxonomyNode<T>>(nodeStore_.getNodes(),
				Collections.singleton(getBottomNode()));
	}

	@Override
	public Set<? extends UN> getNonBottomNodes() {
		return nodeStore_.getNodes();
	}

	@Override
	public UN getTopNode() {
		return nodeStore_.getNode(topMember_);
	}

	@Override
	public abstract N getBottomNode();

	@Override
	public UN getCreateNode(final Collection<? extends T> members) {
		return nodeStore_.getCreateNode(members, members.size(), nodeFactory_);
	}

	@Override
	public boolean setCreateDirectSupernodes(
			final NonBottomTaxonomyNode<T> subNode,
			final Iterable<? extends Collection<? extends T>> superMemberSets) {

		final UN node = toInternalNode(subNode);

		// TODO: establish consistency by adding default parent to the nodes.

		boolean isSuperMemberSetsEmpty = true;

		for (final Collection<? extends T> superMembers : superMemberSets) {
			final UN superNode = getCreateNode(superMembers);
			isSuperMemberSetsEmpty = false;
			addDirectRelation(superNode, node);
		}

		if (node.trySetAllParentsAssigned(true)) {
			if (!isSuperMemberSetsEmpty) {
				fireDirectSupernodeAssignment(subNode,
						subNode.getDirectSuperNodes());
			}
			return true;
		} else {
			return false;
		}
	}

	protected void addDirectRelation(final UN superNode, final UN subNode) {
		subNode.addDirectSuperNode(superNode);
		superNode.addDirectSubNode(subNode);
	}

	@Override
	public boolean removeDirectSupernodes(
			final NonBottomTaxonomyNode<T> subNode) {

		final UN node = toInternalNode(subNode);

		if (!node.trySetAllParentsAssigned(false)) {
			return false;
		}

		final List<UN> superNodes = new ArrayList<UN>();

		// remove all super-class links
		synchronized (node) {
			superNodes.addAll(node.getDirectNonBottomSuperNodes());
			if (superNodes.isEmpty()) {
				return true;
			}
			for (final UN superNode : superNodes) {
				node.removeDirectSuperNode(superNode);
			}
		}

		for (final UN superNode : superNodes) {
			synchronized (superNode) {
				superNode.removeDirectSubNode(node);
			}
		}

		fireDirectSupernodeRemoval(subNode, superNodes);

		return true;
	}

	@Override
	public boolean removeNode(final T member) {
		if (nodeStore_.removeNode(member)) {
			LOGGER_.trace("removed node with member: {}", member);
			return true;
		}
		// else
		return false;
	}

	@Override
	public boolean addToBottomNode(final T member) {
		if (unsatisfiableClasses_.put(getKeyProvider().getKey(member),
				member) == null) {
			fireMemberForNodeAppeared(member, getBottomNode());
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean removeFromBottomNode(final T member) {
		if (unsatisfiableClasses_
				.remove(getKeyProvider().getKey(member)) != null) {
			fireMemberForNodeDisappeared(member, getBottomNode());
			return true;
		} else {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	protected UN toInternalNode(final NonBottomTaxonomyNode<T> node) {
		if (node.getTaxonomy() != this) {
			throw new IllegalArgumentException(
					"The sub-node must belong to this taxonomy: " + node);
		}
		// By construction, if the node is in this taxonomy, it is of type N.
		try {
			return (UN) node;
		} catch (final ClassCastException e) {
			throw new IllegalArgumentException(
					"The sub-node must belong to this taxonomy: " + node);
		}
	}

	@Override
	public boolean addListener(final NodeStore.Listener<T> listener) {
		return nodeStore_.addListener(listener)
				&& nodeStoreListeners_.add(listener);
	}

	@Override
	public boolean removeListener(final NodeStore.Listener<T> listener) {
		return nodeStore_.removeListener(listener)
				&& nodeStoreListeners_.remove(listener);
	}

	@Override
	public boolean addListener(final Taxonomy.Listener<T> listener) {
		return taxonomyListeners_.add(listener);
	}

	@Override
	public boolean removeListener(final Taxonomy.Listener<T> listener) {
		return taxonomyListeners_.remove(listener);
	}

	protected void fireMemberForNodeAppeared(final T member,
			final Node<T> node) {
		for (final NodeStore.Listener<T> listener : nodeStoreListeners_) {
			listener.memberForNodeAppeared(member, node);
		}
	}

	protected void fireMemberForNodeDisappeared(final T member,
			final Node<T> node) {
		for (final NodeStore.Listener<T> listener : nodeStoreListeners_) {
			listener.memberForNodeDisappeared(member, node);
		}
	}

	protected void fireDirectSupernodeAssignment(final TaxonomyNode<T> subNode,
			final Collection<? extends TaxonomyNode<T>> superNodes) {
		for (final Taxonomy.Listener<T> listener : taxonomyListeners_) {
			listener.directSupernodeAssignment(subNode, superNodes);
		}
	}

	protected void fireDirectSupernodeRemoval(final TaxonomyNode<T> subNode,
			final Collection<? extends TaxonomyNode<T>> superNodes) {
		for (final Taxonomy.Listener<T> listener : taxonomyListeners_) {
			listener.directSupernodeRemoval(subNode, superNodes);
		}
	}

}
