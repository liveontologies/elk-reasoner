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
package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Collections;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.model.GenericTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.SimpleUpdateableNode;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNodeUtils;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableGenericTaxonomyNode;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.hashing.HashGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class NonBottomGenericTaxonomyNode<
				T extends ElkEntity,
				N extends GenericTaxonomyNode<T, N>,
				UN extends UpdateableGenericTaxonomyNode<T, N, UN>
		>
		extends SimpleUpdateableNode<T>
		implements GenericTaxonomyNode<T, N>,
		UpdateableGenericTaxonomyNode<T, N, UN> {
	
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(NonBottomGenericTaxonomyNode.class);

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
		return Collections.unmodifiableSet(toTaxonomyNodes(directSuperNodes_));
	}

	@Override
	public Set<? extends UN> getDirectNonBottomSuperNodes() {
		return Collections.unmodifiableSet(directSuperNodes_);
	}
	
	@Override
	public Set<? extends N> getAllSuperNodes() {
		return TaxonomyNodeUtils.getAllSuperNodes(this);
	}

	@Override
	public Set<? extends N> getDirectSubNodes() {
		if (!directSubNodes_.isEmpty()) {
			return Collections.unmodifiableSet(toTaxonomyNodes(directSubNodes_));
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
		return TaxonomyNodeUtils.getAllSubNodes(this);
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
			taxonomy_.countNodesWithSubClasses.incrementAndGet();
		}

		directSubNodes_.add(subNode);
	}

	@Override
	public synchronized boolean removeDirectSubNode(final UN subNode) {
		boolean changed = directSubNodes_.remove(subNode);

		if (changed)
			LOGGER_.trace("{}: removed direct sub-node {}", this, subNode);

		if (directSubNodes_.isEmpty()) {
			taxonomy_.countNodesWithSubClasses.decrementAndGet();
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

	protected abstract Set<? extends N> toTaxonomyNodes(Set<? extends UN> nodes);

	public static class Projection<T extends ElkEntity>
			extends NonBottomGenericTaxonomyNode<T, GenericTaxonomyNode.Projection<T>, Projection<T>>
			implements GenericTaxonomyNode.Projection<T> {

		protected Projection(
				final AbstractDistinctBottomTaxonomy<T, GenericTaxonomyNode.Projection<T>, NonBottomGenericTaxonomyNode.Projection<T>> taxonomy,
				final Iterable<? extends T> members, final int size) {
			super(taxonomy, members, size);
		}

		@Override
		protected Set<? extends GenericTaxonomyNode.Projection<T>> toTaxonomyNodes(
				final Set<? extends NonBottomGenericTaxonomyNode.Projection<T>> nodes) {
			return nodes;
		}
		
	}

}
