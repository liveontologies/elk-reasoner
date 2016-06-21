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
package org.semanticweb.elk.reasoner.taxonomy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.predefined.PredefinedElkObjectPropertyFactory;
import org.semanticweb.elk.reasoner.taxonomy.impl.AbstractDistinctBottomTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.impl.AbstractUpdateableGenericTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.impl.BottomGenericTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.impl.ConcurrentNodeStore;
import org.semanticweb.elk.reasoner.taxonomy.impl.NonBottomGenericTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.ComparatorKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.GenericTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.NonBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNodeFactory;

/**
 * Object property taxonomy suitable for concurrent construction or concurrent
 * cleaning.
 * <p>
 * <strong>CAUTION!</strong> This implementation of
 * {@link org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTaxonomy
 * UpdateableTaxonomy} adds and removes sub-nodes instead of super-nodes. This
 * is due to the way property taxonomy is constructed.
 * 
 * @author Peter Skocovsky
 * 
 * @see #setCreateDirectSupernodes(NonBottomTaxonomyNode, Iterable)
 * @see #removeDirectSupernodes(NonBottomTaxonomyNode)
 */
// @formatter:off
public class ConcurrentObjectPropertyTaxonomy
		extends AbstractUpdateableGenericTaxonomy<
				ElkObjectProperty,
				GenericTaxonomyNode.Projection<ElkObjectProperty>,
				NonBottomGenericTaxonomyNode.Projection<ElkObjectProperty>
		> {

	private final GenericTaxonomyNode.Projection<ElkObjectProperty> bottomNode_;

	public ConcurrentObjectPropertyTaxonomy(
			final PredefinedElkObjectPropertyFactory elkFactory,
			final ComparatorKeyProvider<ElkEntity> classKeyProvider) {
		super(
				new ConcurrentNodeStore<
						ElkObjectProperty,
						NonBottomGenericTaxonomyNode.Projection<ElkObjectProperty>
				>(classKeyProvider),
				new TaxonomyNodeFactory<
						ElkObjectProperty,
						NonBottomGenericTaxonomyNode.Projection<ElkObjectProperty>,
						AbstractDistinctBottomTaxonomy<
								ElkObjectProperty,
								GenericTaxonomyNode.Projection<ElkObjectProperty>,
								NonBottomGenericTaxonomyNode.Projection<ElkObjectProperty>
						>
				>() {
					@Override
					public NonBottomGenericTaxonomyNode.Projection<ElkObjectProperty> createNode(
							final Iterable<? extends ElkObjectProperty> members,
							final int size,
							final AbstractDistinctBottomTaxonomy<
									ElkObjectProperty,
									GenericTaxonomyNode.Projection<ElkObjectProperty>,
									NonBottomGenericTaxonomyNode.Projection<ElkObjectProperty>
							> taxonomy) {
						return new NonBottomGenericTaxonomyNode.Projection<ElkObjectProperty>(
								taxonomy, members, size);
					}
				},
				elkFactory.getOwlTopObjectProperty());
		this.bottomNode_ = new BottomGenericTaxonomyNode.Projection<ElkObjectProperty>(
				this, elkFactory.getOwlBottomObjectProperty());
	}
// @formatter:on

	@Override
	public GenericTaxonomyNode.Projection<ElkObjectProperty> getBottomNode() {
		return bottomNode_;
	}

	@Override
	protected Set<? extends GenericTaxonomyNode.Projection<ElkObjectProperty>> toTaxonomyNodes(
			final Set<? extends NonBottomGenericTaxonomyNode.Projection<ElkObjectProperty>> nodes) {
		return nodes;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <strong>CAUTION!</strong> This implementation creates sub-nodes instead
	 * of super-nodes. This is due to the way property taxonomy is constructed.
	 */
	@Override
	public boolean setCreateDirectSupernodes(
			final NonBottomTaxonomyNode<ElkObjectProperty> superNode,
			final Iterable<? extends Collection<? extends ElkObjectProperty>> subMemberSets) {
		/*
		 * Adding sub-nodes instead of super-nodes !!!
		 */

		final NonBottomGenericTaxonomyNode.Projection<ElkObjectProperty> node = toInternalNode(
				superNode);

		// If subMemberSets contain bottom, don't create anything.
		for (final Collection<? extends ElkObjectProperty> subMembers : subMemberSets) {
			if (subMembers.contains(getBottomNode().getCanonicalMember())) {
				// No event fired, because sub-nodes didn't change.
				return node.trySetAllParentsAssigned(true);
			}
		}

		boolean isSubMemberSetsEmpty = true;

		for (final Collection<? extends ElkObjectProperty> subMembers : subMemberSets) {
			final NonBottomGenericTaxonomyNode.Projection<ElkObjectProperty> subNode = getCreateNode(
					subMembers);
			isSubMemberSetsEmpty = false;
			addDirectRelation(node, subNode);
		}

		if (node.trySetAllParentsAssigned(true)) {
			if (!isSubMemberSetsEmpty) {
				fireDirectSupernodeAssignment(superNode,
						superNode.getDirectSuperNodes());
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <strong>CAUTION!</strong> This implementation removes sub-nodes instead
	 * of super-nodes. This is due to the way property taxonomy is constructed.
	 */
	@Override
	public boolean removeDirectSupernodes(
			final NonBottomTaxonomyNode<ElkObjectProperty> superNode) {
		/*
		 * Removing sub-nodes instead of super-nodes !!!
		 */

		final NonBottomGenericTaxonomyNode.Projection<ElkObjectProperty> node = toInternalNode(
				superNode);

		if (!node.trySetAllParentsAssigned(false)) {
			return false;
		}

		final List<NonBottomGenericTaxonomyNode.Projection<ElkObjectProperty>> subNodes = new ArrayList<NonBottomGenericTaxonomyNode.Projection<ElkObjectProperty>>();

		// remove all sub-node links
		synchronized (node) {
			subNodes.addAll(node.getDirectNonBottomSubNodes());
			// If there are no non-bottom sub nodes, do nothing.
			if (subNodes.isEmpty()) {
				return true;
			}
			for (final NonBottomGenericTaxonomyNode.Projection<ElkObjectProperty> subNode : subNodes) {
				node.removeDirectSubNode(subNode);
			}
		}

		for (final NonBottomGenericTaxonomyNode.Projection<ElkObjectProperty> subNode : subNodes) {
			synchronized (subNode) {
				subNode.removeDirectSuperNode(node);
			}
		}

		fireDirectSupernodeRemoval(superNode, subNodes);

		return true;
	}

}
