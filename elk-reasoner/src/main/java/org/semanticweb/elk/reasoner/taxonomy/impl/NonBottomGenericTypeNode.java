/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import java.util.Collections;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.TaxonomyNodeUtils;
import org.semanticweb.elk.reasoner.taxonomy.model.GenericInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.GenericTypeNode;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A generic implementation of a mutable non-bottom type node of an
 * {@link AbstractDistinctBottomTaxonomy}.
 *
 * @param <T>
 *            The type of members of this nodes.
 * @param <I>
 *            The type of members of associated instance nodes.
 * @param <TN>
 *            The immutable type of type nodes with which this node may be
 *            associated.
 * @param <IN>
 *            The immutable type of instance nodes with which this node may be
 *            associated.
 * @param <UTN>
 *            The mutable type of type nodes with which this node may be
 *            associated.
 * @param <UIN>
 *            The mutable type of instance nodes with which this node may be
 *            associated.
 */
public abstract class NonBottomGenericTypeNode<
				T extends ElkEntity,
				I extends ElkEntity,
				TN extends GenericTypeNode<T, I, TN, IN>,
				IN extends GenericInstanceNode<T, I, TN, IN>,
				UTN extends UpdateableTaxonomyTypeNode<T, I, TN, IN, UTN, UIN>,
				UIN extends UpdateableInstanceNode<T, I, TN, IN, UTN, UIN>
		>
		extends NonBottomGenericTaxonomyNode<T, TN, UTN>
		implements GenericTypeNode<T, I, TN, IN>,
		UpdateableTaxonomyTypeNode<T, I, TN, IN, UTN, UIN> {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(NonBottomGenericTypeNode.class);

	/** The instance nodes associated with this node. */
	private final Set<UIN> directInstanceNodes_;

	/**
	 * Constructs the node for the supplied equivalent members.
	 * 
	 * @param taxonomy
	 *            The taxonomy to which this node belongs.
	 * @param members
	 *            Non-empty sequence of equivalent members.
	 * @param size
	 *            The number of equivalent members.
	 */
	public NonBottomGenericTypeNode(
			final AbstractDistinctBottomTaxonomy<T, TN, UTN> taxonomy,
			final Iterable<? extends T> members, final int size) {
		super(taxonomy, members, size);
		this.directInstanceNodes_ = new ArrayHashSet<UIN>();
	}

	@Override
	public synchronized void addDirectInstanceNode(final UIN instanceNode) {
		LOGGER_.trace("{}: new direct instance-node {}", this, instanceNode);
		directInstanceNodes_.add(instanceNode);
	}

	@Override
	public synchronized void removeDirectInstanceNode(final UIN instanceNode) {
		LOGGER_.trace("{}: direct instance node removed {}", this,
				instanceNode);
		directInstanceNodes_.remove(instanceNode);
	}

	@Override
	public Set<? extends IN> getDirectInstanceNodes() {
		return Collections.unmodifiableSet(toInstanceNodes(directInstanceNodes_));
	}

	@Override
	public Set<? extends IN> getAllInstanceNodes() {
		return TaxonomyNodeUtils.getAllInstanceNodes(this);
	}

	/**
	 * Returns the supplied set of mutable nodes as a set of immutable nodes.
	 * 
	 * @param nodes
	 *            The set of mutable nodes that should be returned.
	 * @return The supplied set of mutable nodes as a set of immutable nodes.
	 */
	protected abstract Set<? extends IN> toInstanceNodes(
			final Set<? extends UIN> nodes);
	
	/**
	 * A subclass with fixed type parameters.
	 * 
	 * @author Peter Skocovsky
	 *
	 * @param <T>
	 *            The type of members of this nodes.
	 * @param <I>
	 *            The type of members of associated instance this nodes.
	 */
	public static class Projection<T extends ElkEntity, I extends ElkEntity>
			extends NonBottomGenericTypeNode<
					T,
					I,
					GenericTypeNode.Projection<T, I>,
					GenericInstanceNode.Projection<T, I>,
					Projection<T, I>,
					IndividualNode.Projection2<T, I>
			> implements GenericTypeNode.Projection<T, I> {

		/**
		 * Constructs the node for the supplied equivalent members.
		 * 
		 * @param taxonomy
		 *            The taxonomy to which this node belongs.
		 * @param members
		 *            Non-empty sequence of equivalent members.
		 * @param size
		 *            The number of equivalent members.
		 */
		public Projection(
				final AbstractDistinctBottomTaxonomy<
						T,
						GenericTypeNode.Projection<T, I>,
						NonBottomGenericTypeNode.Projection<T, I>
				> taxonomy,
				final Iterable<? extends T> members, final int size) {
			super(taxonomy, members, size);
		}

		@Override
		protected Set<? extends GenericInstanceNode.Projection<T, I>> toInstanceNodes(
				final Set<? extends IndividualNode.Projection2<T, I>> nodes) {
			return nodes;
		}
		
	}
	
}
