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
import org.semanticweb.elk.reasoner.taxonomy.model.NodeFactory;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableGenericNodeStore;
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
 * @author Peter Skocovsky
 */
public class ConcurrentClassTaxonomy extends AbstractTaxonomy<ElkClass>
		implements UpdateableTaxonomy<ElkClass> {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ConcurrentClassTaxonomy.class);

	/** The store containing non-bottom nodes in this taxonomy. */
	private final UpdateableGenericNodeStore<ElkClass, NonBottomClassNode> nodeStore_;
	/** counts the number of nodes which have non-bottom sub-classes */
	final AtomicInteger countNodesWithSubClasses;
	/** thread safe set of unsatisfiable classes */
	private final ConcurrentMap<Object, ElkClass> unsatisfiableClasses_;

	/**
	 * The bottom node.
	 */
	private final BottomClassNode bottomClassNode_;

	public ConcurrentClassTaxonomy(
			final ComparatorKeyProvider<ElkEntity> classKeyProvider) {
		this.nodeStore_ = new ConcurrentNodeStore<ElkClass, NonBottomClassNode>(
				classKeyProvider);
		this.bottomClassNode_ = new BottomClassNode();
		this.countNodesWithSubClasses = new AtomicInteger(0);
		this.unsatisfiableClasses_ = new ConcurrentHashMap<Object, ElkClass>();
		this.unsatisfiableClasses_.put(
				classKeyProvider.getKey(PredefinedElkClass.OWL_NOTHING),
				PredefinedElkClass.OWL_NOTHING);
	}

	@Override
	public ComparatorKeyProvider<? super ElkClass> getKeyProvider() {
		return nodeStore_.getKeyProvider();
	}

	@Override
	public UpdateableTaxonomyNode<ElkClass> getNode(ElkClass elkClass) {
		UpdateableTaxonomyNode<ElkClass> result = nodeStore_.getNode(elkClass);

		if (result == null && unsatisfiableClasses_
				.containsKey(nodeStore_.getKeyProvider().getKey(elkClass))) {
			result = bottomClassNode_;
		}

		return result;
	}

	@Override
	public Set<? extends UpdateableTaxonomyNode<ElkClass>> getNodes() {

		return new LazySetUnion<UpdateableTaxonomyNode<ElkClass>>(
				nodeStore_.getNodes(),
				Collections.<UpdateableTaxonomyNode<ElkClass>> singleton(
						bottomClassNode_));
	}

	@Override
	public NonBottomClassNode getTopNode() {
		NonBottomClassNode top = nodeStore_
				.getNode(PredefinedElkClass.OWL_THING);
		if (top == null) {
			top = nodeStore_.getCreateNode(
					Operations.singleton(PredefinedElkClass.OWL_THING), 1,
					NON_BOTTOM_NODE_FACTORY);
		}
		return top;
	}

	@Override
	public UpdateableTaxonomyNode<ElkClass> getBottomNode() {
		return bottomClassNode_;
	}

	protected NonBottomClassNode getCreateNonBottomClassNode(
			final Collection<? extends ElkClass> members) {

		if (members == null || members.isEmpty()) {
			throw new IllegalArgumentException(
					"Empty class taxonomy nodes must not be created!");
		}

		final NonBottomClassNode node = nodeStore_.getCreateNode(members,
				members.size(), NON_BOTTOM_NODE_FACTORY);

		LOGGER_.trace("created node: {}", node);

		return node;
	}

	/**
	 * Node factory creating nodes of this taxonomy.
	 */
	private final NodeFactory<ElkClass, NonBottomClassNode> NON_BOTTOM_NODE_FACTORY = new NodeFactory<ElkClass, NonBottomClassNode>() {

		@Override
		public NonBottomClassNode createNode(
				final Iterable<? extends ElkClass> members, final int size,
				final ComparatorKeyProvider<? super ElkClass> keyProvider) {
			return new NonBottomClassNode(ConcurrentClassTaxonomy.this, members,
					size);
		}

	};

	@Override
	public void setCreateDirectSupernodes(
			final Collection<? extends ElkClass> members,
			final Iterable<? extends Collection<? extends ElkClass>> superMemberSets) {

		final NonBottomClassNode node = nodeStore_.getCreateNode(members,
				members.size(), NON_BOTTOM_NODE_FACTORY);
//		// TODO: establish consistency by adding default parent to the nodes.
//		// node may have default parent, e.g., if it was creates by this method.
//		for (final UpdateableTaxonomyNode<ElkClass> superNode : node
//				.getDirectSuperNodes()) {
//			if (superNode.equals(getTopNode())) {
//				removeDirectRelation(superNode, node);
//				break;
//			}
//		}

		for (final Collection<? extends ElkClass> superMembers : superMemberSets) {
			final NonBottomClassNode superNode = nodeStore_.getCreateNode(
					superMembers, superMembers.size(), NON_BOTTOM_NODE_FACTORY);
			addDirectRelation(superNode, node);
//
//			// give default parent to superNode
//			if (superNode.getDirectSuperNodes().isEmpty()
//					&& !superNode.equals(getTopNode())) {
//				addDirectRelation(getTopNode(), superNode);
//			}
		}
//
//		// give default parent to node
//		if (node.getDirectSuperNodes().isEmpty()
//				&& !node.equals(getTopNode())) {
//			addDirectRelation(getTopNode(), node);
//		}

		node.trySetModified(false);
	}

	private static void addDirectRelation(
			final UpdateableTaxonomyNode<ElkClass> superNode,
			final UpdateableTaxonomyNode<ElkClass> subNode) {
		subNode.addDirectSuperNode(superNode);
		superNode.addDirectSubNode(subNode);
	}
//
//	private static void removeDirectRelation(
//			final UpdateableTaxonomyNode<ElkClass> superNode,
//			final UpdateableTaxonomyNode<ElkClass> subNode) {
//		subNode.removeDirectSuperNode(superNode);
//		superNode.removeDirectSubNode(subNode);
//	}

	@Override
	public boolean removeNode(final ElkClass member) {
		if (nodeStore_.removeNode(member)) {
			LOGGER_.trace("removed node with member: {}", member);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean addToBottomNode(final ElkClass member) {
		return unsatisfiableClasses_.put(
				nodeStore_.getKeyProvider().getKey(member), member) == null;
	}
	
	@Override
	public boolean removeFromBottomNode(final ElkClass member) {
		return unsatisfiableClasses_
				.remove(nodeStore_.getKeyProvider().getKey(member)) != null;
	}
	
	/**
	 * Special implementation for the bottom node in the taxonomy. Instead of
	 * storing its sub- and super-classes, the respective answers are computed
	 * or taken from the taxonomy object directly. This saves memory at the cost
	 * of some performance if somebody should wish to traverse an ontology
	 * bottom-up starting from this node.
	 */
	protected class BottomClassNode
			implements UpdateableTaxonomyNode<ElkClass> {

		@Override
		public ComparatorKeyProvider<? super ElkClass> getKeyProvider() {
			return nodeStore_.getKeyProvider();
		}

		@Override
		public Iterator<ElkClass> iterator() {
			return unsatisfiableClasses_.values().iterator();
		}

		@Override
		public boolean contains(final ElkClass member) {
			return unsatisfiableClasses_
					.containsKey(nodeStore_.getKeyProvider().getKey(member));
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
			return Operations.filter(nodeStore_.getNodes(),
					new Condition<NonBottomClassNode>() {
						@Override
						public boolean holds(NonBottomClassNode element) {
							return element.getDirectSubNodes()
									.contains(bottomClassNode_);
						}
						/*
						 * the direct super nodes of the bottom node are all
						 * nodes except the nodes that have no non-bottom
						 * sub-classes and the bottom node
						 */
					}, nodeStore_.getNodes().size()
							- countNodesWithSubClasses.get());
		}

		@Override
		public Set<? extends NonBottomClassNode> getAllSuperNodes() {
			return nodeStore_.getNodes();
		}

		@Override
		public Set<UpdateableTaxonomyNode<ElkClass>> getDirectSubNodes() {
			return Collections.emptySet();
		}

		@Override
		public Set<UpdateableTaxonomyNode<ElkClass>> getAllSubNodes() {
			return Collections.emptySet();
		}

		@Override
		public boolean trySetModified(boolean modified) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isModified() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addDirectSuperNode(
				UpdateableTaxonomyNode<ElkClass> superNode) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addDirectSubNode(UpdateableTaxonomyNode<ElkClass> subNode) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeDirectSubNode(
				UpdateableTaxonomyNode<ElkClass> subNode) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeDirectSuperNode(
				UpdateableTaxonomyNode<ElkClass> superNode) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setMembers(Iterable<? extends ElkClass> members) {
			throw new UnsupportedOperationException();
		}

	}
}
