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
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.model.ComparatorKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.GenericTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.Condition;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * A generic implementation of a mutable bottom node of an
 * {@link AbstractDistinctBottomTaxonomy}.
 * <p>
 * This node does not store any members, the members are store in the taxonomy
 * of this node.
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
public class BottomGenericTaxonomyNode<
				T extends ElkEntity,
				N extends GenericTaxonomyNode<T, N>,
				UN extends UpdateableTaxonomyNode<T, N, UN>
		>
		implements GenericTaxonomyNode<T, N> {

	/** The taxonomy of this node. */
	protected final AbstractDistinctBottomTaxonomy<T, N, UN> taxonomy_;

	/** The canonical member of this node. */
	private final T bottomMember_;

	/**
	 * Constructor.
	 * 
	 * @param taxonomy
	 *            The taxonomy of this node.
	 * @param bottomMember
	 *            The canonical member of this node.
	 */
	public BottomGenericTaxonomyNode(
			final AbstractDistinctBottomTaxonomy<T, N, UN> taxonomy,
			final T bottomMember) {
		this.taxonomy_ = taxonomy;
		this.bottomMember_ = bottomMember;
		taxonomy_.unsatisfiableClasses_.put(getKeyProvider()
				.getKey(bottomMember_), bottomMember_);
	}

	@Override
	public ComparatorKeyProvider<? super T> getKeyProvider() {
		return taxonomy_.getKeyProvider();
	}

	@Override
	public boolean contains(final T member) {
		return taxonomy_.unsatisfiableClasses_
				.containsKey(getKeyProvider().getKey(member));
	}

	@Override
	public int size() {
		return taxonomy_.unsatisfiableClasses_.size();
	}

	@Override
	public T getCanonicalMember() {
		return bottomMember_;
	}

	@Override
	public Iterator<T> iterator() {
		return taxonomy_.unsatisfiableClasses_.values().iterator();
	}

	@Override
	public Set<? extends N> getDirectSuperNodes() {
		final Set<? extends N> nonBottomNodes = getAllSuperNodes();
		return Operations.filter(nonBottomNodes, new Condition<N>() {
			@Override
			public boolean holds(final N element) {
				return element.getDirectSubNodes()
						.contains(taxonomy_.getBottomNode());
			}
			/*
			 * the direct super nodes of the bottom node are all nodes except
			 * the nodes that have no non-bottom sub-classes and the bottom node
			 */
		}, nonBottomNodes.size() - taxonomy_.countNodesWithSubClasses_.get());
	}

	@Override
	public Set<? extends N> getAllSuperNodes() {
		return taxonomy_.toTaxonomyNodes(taxonomy_.getNonBottomNodes());
	}

	@Override
	public Set<? extends N> getDirectSubNodes() {
		return Collections.emptySet();
	}

	@Override
	public Set<? extends N> getAllSubNodes() {
		return Collections.emptySet();
	}

	@Override
	public Taxonomy<T> getTaxonomy() {
		return taxonomy_;
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
			extends BottomGenericTaxonomyNode<
					T,
					GenericTaxonomyNode.Projection<T>,
					NonBottomGenericTaxonomyNode.Projection<T>
			> implements GenericTaxonomyNode.Projection<T> {

		/**
		 * Constructor.
		 * 
		 * @param taxonomy
		 *            The taxonomy of this node.
		 * @param bottomMember
		 *            The canonical member of this node.
		 */
		public Projection(
				final AbstractDistinctBottomTaxonomy<
						T,
						GenericTaxonomyNode.Projection<T>,
						NonBottomGenericTaxonomyNode.Projection<T>
				> taxonomy,
				final T bottomMember) {
			super(taxonomy, bottomMember);
		}

	}

}
