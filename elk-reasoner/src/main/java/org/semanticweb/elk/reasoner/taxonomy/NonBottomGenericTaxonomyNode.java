package org.semanticweb.elk.reasoner.taxonomy;

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

import java.util.Collections;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.model.GenericTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.SimpleUpdateableNode;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNodeUtils;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableGenericTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTaxonomyNode;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.hashing.HashGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NonBottomGenericTaxonomyNode<T extends ElkEntity, N extends GenericTaxonomyNode<T, N>>
		extends SimpleUpdateableNode<T>
		implements UpdateableGenericTaxonomyNode<T, N> {
	
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(NonBottomGenericTaxonomyNode.class);
	
	private final AbstractDistinctBottomTaxonomy<T, N> taxonomy_;

	/**
	 * ElkClass nodes whose members are direct super-classes of the members of
	 * this node.
	 */
	private final Set<N> directSuperNodes_;
	/**
	 * ElkClass nodes, except for the bottom node, whose members are direct
	 * sub-classes of the members of this node.
	 */
	private final Set<N> directSubNodes_;
	
	protected NonBottomGenericTaxonomyNode(final AbstractDistinctBottomTaxonomy<T, N> taxonomy,
			final Iterable<? extends T> members, final int size) {
		super(members, size, taxonomy.getKeyProvider());
		this.taxonomy_ = taxonomy;
		this.directSubNodes_ = new ArrayHashSet<N>();
		this.directSuperNodes_ = new ArrayHashSet<N>();
	}

	@Override
	public Set<? extends N> getDirectSuperNodes() {
		return Collections.unmodifiableSet(directSuperNodes_);
	}

	@Override
	public Set<? extends N> getAllSuperNodes() {
		return TaxonomyNodeUtils.getAllSuperNodes(this);
	}

	@Override
	public Set<? extends N> getDirectSubNodes() {
		if (!directSubNodes_.isEmpty()) {
			return Collections.unmodifiableSet(directSubNodes_);
		} else {
			return Collections.singleton(taxonomy_.getBottomNode());
		}
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
	public synchronized void addDirectSuperNode(final N superNode) {
		LOGGER_.trace("{}: new direct super-node {}", this, superNode);

		directSuperNodes_.add(superNode);
	}

	@Override
	public synchronized void addDirectSubNode(final N subNode) {
		LOGGER_.trace("{}: new direct sub-node {}", this, subNode);

		if (directSubNodes_.isEmpty()) {
			this.taxonomy_.countNodesWithSubClasses.incrementAndGet();
		}

		directSubNodes_.add(subNode);
	}

	@Override
	public synchronized boolean removeDirectSubNode(final N subNode) {
		boolean changed = directSubNodes_.remove(subNode);

		if (changed)
			LOGGER_.trace("{}: removed direct sub-node {}", this, subNode);

		if (directSubNodes_.isEmpty()) {
			taxonomy_.countNodesWithSubClasses.decrementAndGet();
		}

		return changed;
	}

	@Override
	public synchronized boolean removeDirectSuperNode(final N superNode) {
		boolean changed = directSuperNodes_.remove(superNode);

		LOGGER_.trace("{}: removed direct super-node {}", this, superNode);

		return changed;
	}

	private final int hashCode_ = HashGenerator.generateNextHashCode();

	@Override
	public final int hashCode() {
		return hashCode_;
	}

	public static class Projection<T extends ElkEntity>
			extends NonBottomGenericTaxonomyNode<T, UpdateableTaxonomyNode<T>>
			implements UpdateableTaxonomyNode<T> {

		protected Projection(
				AbstractDistinctBottomTaxonomy<T, UpdateableTaxonomyNode<T>> taxonomy,
				Iterable<? extends T> members, int size) {
			super(taxonomy, members, size);
		}
		
	}

}
