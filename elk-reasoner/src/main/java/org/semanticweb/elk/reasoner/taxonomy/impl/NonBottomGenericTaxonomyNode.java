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
import org.semanticweb.elk.reasoner.taxonomy.model.GenericTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.hashing.HashGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A generic implementation of a mutable non-bottom node of an
 * {@link AbstractDistinctBottomTaxonomy}.
 * 
 * @author Peter Skocovsky
 *
 * @param <T>
 *            The type of members of this nodes.
 * @param <N>
 *            The immutable type of nodes with which this node may be
 *            associated.
 * @param <UN>
 *            The mutable type of nodes with which this node may be associated.
 */
public abstract class NonBottomGenericTaxonomyNode<
				T extends ElkEntity,
				N extends GenericTaxonomyNode<T, N>,
				UN extends UpdateableTaxonomyNode<T, N, UN>
		>
		extends SimpleUpdateableNode<T>
		implements GenericTaxonomyNode<T, N>,
		UpdateableTaxonomyNode<T, N, UN> {
	
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(NonBottomGenericTaxonomyNode.class);

	/** The taxonomy of this node. */
	protected final AbstractDistinctBottomTaxonomy<T, N, UN> taxonomy_;
	
	/**
	 * ElkClass nodes whose members are direct super-classes of the members of
	 * this node.
	 */
	protected final Set<UN> directSuperNodes_;
	/**
	 * ElkClass nodes, except for the bottom node, whose members are direct
	 * sub-classes of the members of this node.
	 */
	protected final Set<UN> directSubNodes_;
	
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
	public NonBottomGenericTaxonomyNode(
			final AbstractDistinctBottomTaxonomy<T, N, UN> taxonomy,
			final Iterable<? extends T> members, final int size) {
		super(members, size, taxonomy.getKeyProvider());
		this.taxonomy_ = taxonomy;
		this.directSubNodes_ = new ArrayHashSet<UN>();
		this.directSuperNodes_ = new ArrayHashSet<UN>();
	}

	@Override
	public Set<? extends N> getDirectSuperNodes() {
		return Collections.unmodifiableSet(taxonomy_.toTaxonomyNodes(
				directSuperNodes_));
	}

	@Override
	public Set<? extends UN> getDirectNonBottomSuperNodes() {
		return Collections.unmodifiableSet(directSuperNodes_);
	}
	
	@Override
	public Set<? extends N> getAllSuperNodes() {
		return TaxonomyNodeUtils.getAllSuperNodes(getDirectSuperNodes());
	}

	@Override
	public Set<? extends N> getDirectSubNodes() {
		if (!directSubNodes_.isEmpty()) {
			return Collections.unmodifiableSet(taxonomy_.toTaxonomyNodes(
					directSubNodes_));
		} else {
			return Collections.singleton(taxonomy_.getBottomNode());
		}
	}

	@Override
	public Set<? extends UN> getDirectNonBottomSubNodes() {
		return Collections.unmodifiableSet(directSubNodes_);
	}
	
	@Override
	public Set<? extends N> getAllSubNodes() {
		return TaxonomyNodeUtils.getAllSubNodes(getDirectSubNodes());
	}

	@Override
	public Taxonomy<T> getTaxonomy() {
		return taxonomy_;
	}

	@Override
	public synchronized void addDirectSuperNode(final UN superNode) {
		LOGGER_.trace("{}: new direct super-node {}", this, superNode);

		directSuperNodes_.add(superNode);
	}

	@Override
	public synchronized void addDirectSubNode(final UN subNode) {
		LOGGER_.trace("{}: new direct sub-node {}", this, subNode);

		if (directSubNodes_.isEmpty()) {
			taxonomy_.countNodesWithSubClasses_.incrementAndGet();
		}

		directSubNodes_.add(subNode);
	}

	@Override
	public synchronized boolean removeDirectSubNode(final UN subNode) {
		boolean changed = directSubNodes_.remove(subNode);

		if (changed)
			LOGGER_.trace("{}: removed direct sub-node {}", this, subNode);

		if (directSubNodes_.isEmpty()) {
			taxonomy_.countNodesWithSubClasses_.decrementAndGet();
		}

		return changed;
	}

	@Override
	public synchronized boolean removeDirectSuperNode(final UN superNode) {
		boolean changed = directSuperNodes_.remove(superNode);

		LOGGER_.trace("{}: removed direct super-node {}", this, superNode);

		return changed;
	}

	private final int hashCode_ = HashGenerator.generateNextHashCode();

	@Override
	public final int hashCode() {
		return hashCode_;
	}

	/**
	 * A subclass with fixed type parameters.
	 * 
	 * @author Peter Skocovsky
	 *
	 * @param <T>
	 *            The type of members of this nodes.
	 */
	public static class Projection<T extends ElkEntity>
			extends NonBottomGenericTaxonomyNode<T, GenericTaxonomyNode.Projection<T>, Projection<T>>
			implements GenericTaxonomyNode.Projection<T> {

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
						GenericTaxonomyNode.Projection<T>,
						NonBottomGenericTaxonomyNode.Projection<T>
				> taxonomy,
				final Iterable<? extends T> members, final int size) {
			super(taxonomy, members, size);
		}
		
	}

}
