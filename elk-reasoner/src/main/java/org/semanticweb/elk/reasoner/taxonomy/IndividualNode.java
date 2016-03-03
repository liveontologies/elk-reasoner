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

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.SimpleUpdateableNode;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TypeNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableGenericInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableGenericTypeNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTypeNode;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * Class for storing information about a class in the context of classification.
 * It is the main data container for {@link InstanceTaxonomy} objects. Like most
 * such data containers in ELK, it is read-only for public access but provides
 * package-private ways of modifying it. Modifications of this class happen in
 * implementations of {@link InstanceTaxonomy} only.
 * 
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 * @author Peter Skocovsky
 */
public class IndividualNode<
				T extends ElkEntity,
				I extends ElkEntity,
				TN extends UpdateableGenericTypeNode<T, I, TN, IN>,
				IN extends UpdateableGenericInstanceNode<T, I, TN, IN>
		>
		extends SimpleUpdateableNode<I>
		implements UpdateableGenericInstanceNode<T, I, TN, IN> {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(IndividualNode.class);

	final InstanceTaxonomy<T, I> taxonomy_;

	/**
	 * ElkClass nodes whose members are direct types of the members of this
	 * node.
	 */
	private final Set<TN> directTypeNodes_;

	/**
	 * Constructing the individual node for the set of same individuals.
	 * 
	 * @param taxonomy
	 *            The taxonomy to which this node belongs.
	 * @param members
	 *            non-empty list of equivalent ElkNamedIndividual objects
	 * @param size
	 *            The number of equivalent ElkNamedIndividual objects
	 */
	protected IndividualNode(final InstanceTaxonomy<T, I> taxonomy,
			final Iterable<? extends I> members, final int size) {
		super(members, size, taxonomy.getInstanceKeyProvider());
		this.taxonomy_ = taxonomy;
		this.directTypeNodes_ = new ArrayHashSet<TN>();
	}

	/**
	 * Add a direct super-class node. This method is not thread safe.
	 * 
	 * @param typeNode
	 *            node to add
	 */
	@Override
	public void addDirectTypeNode(final TN typeNode) {
		LOGGER_.trace("{}: new direct type-node {}", this, typeNode);

		directTypeNodes_.add(typeNode);
	}

	@Override
	public Set<? extends TN> getDirectTypeNodes() {
		return Collections.unmodifiableSet(directTypeNodes_);
	}

	@Override
	public Set<? extends TypeNode<T, I>> getAllTypeNodes() {
		// TODO: refactor TaxonomyNodeUtils
		Set<TypeNode<T, I>> result = new ArrayHashSet<TypeNode<T, I>>(
				directTypeNodes_.size());

		Queue<TypeNode<T, I>> todo = new LinkedList<TypeNode<T, I>>();

		todo.addAll(directTypeNodes_);

		while (!todo.isEmpty()) {
			TypeNode<T, I> next = todo.poll();

			if (result.add(next)) {
				for (TypeNode<T, I> nextSuperNode : next.getDirectSuperNodes())
					todo.add(nextSuperNode);
			}
		}

		return Collections.unmodifiableSet(result);
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

	@Override
	public String toString() {
		return getCanonicalMember().getIri().getFullIriAsString();
	}

	@Override
	public void removeDirectTypeNode(final TN typeNode) {
		LOGGER_.trace("{}: removing direct type node: {}", this, typeNode);

		directTypeNodes_.remove(typeNode);
	}

	@Override
	public Set<? extends TN> getDirectNonBottomTypeNodes() {
		return getDirectTypeNodes();
	}

	public static class Projection<T extends ElkEntity, I extends ElkEntity>
			extends
			IndividualNode<T, I, UpdateableTypeNode<T, I>, UpdateableInstanceNode<T, I>>
			implements UpdateableInstanceNode<T, I> {

		protected Projection(final InstanceTaxonomy<T, I> taxonomy,
				final Iterable<? extends I> members, final int size) {
			super(taxonomy, members, size);
		}

	}

}
