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
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.model.BottomTypeNode;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNodeUtils;
import org.semanticweb.elk.reasoner.taxonomy.model.TypeNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableGenericInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableGenericTaxonomyTypeNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTaxonomyInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTaxonomyTypeNode;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NonBottomGenericTypeNode<T extends ElkEntity, I extends ElkEntity, TN extends UpdateableGenericTaxonomyTypeNode<T, I, TN, IN>, IN extends UpdateableGenericInstanceNode<T, I, TN, IN>, BN extends BottomTypeNode<T, I>>
		extends NonBottomGenericTaxonomyNode<T, TN, BN>
		implements UpdateableGenericTaxonomyTypeNode<T, I, TN, IN> {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(NonBottomGenericTypeNode.class);

	private final Set<IN> directInstanceNodes_;

	public NonBottomGenericTypeNode(
			final BN bottomNode,
			final Iterable<? extends T> members, final int size) {
		super(bottomNode, members, size);
		this.directInstanceNodes_ = new ArrayHashSet<IN>();
	}

	@Override
	public Set<? extends TypeNode<T, I>> getDirectSuperNodes() {
		return Collections.unmodifiableSet(directSuperNodes_);
	}

	@Override
	public Set<? extends TypeNode<T, I>> getAllSuperNodes() {
		return TaxonomyNodeUtils.getAllSuperNodes(this);
	}

	@Override
	public Set<? extends TypeNode<T, I>> getDirectSubNodes() {
		if (!directSubNodes_.isEmpty()) {
			return Collections.unmodifiableSet(directSubNodes_);
		} else {
			return Collections.singleton(bottomNode_);
		}
	}

	@Override
	public Set<? extends TypeNode<T, I>> getAllSubNodes() {
		return TaxonomyNodeUtils.getAllSubNodes(this);
	}

	@Override
	public synchronized void addDirectInstanceNode(final IN instanceNode) {
		LOGGER_.trace("{}: new direct instance-node {}", this, instanceNode);
		directInstanceNodes_.add(instanceNode);
	}

	@Override
	public synchronized void removeDirectInstanceNode(final IN instanceNode) {
		LOGGER_.trace("{}: direct instance node removed {}", this,
				instanceNode);
		directInstanceNodes_.remove(instanceNode);
	}

	@Override
	public Set<? extends InstanceNode<T, I>> getDirectInstanceNodes() {
		return Collections.unmodifiableSet(directInstanceNodes_);
	}

	@Override
	public Set<? extends InstanceNode<T, I>> getAllInstanceNodes() {
		// TODO: refactor TaxonomyNodeUtils
		Set<InstanceNode<T, I>> result;

		if (!getDirectSubNodes().isEmpty()) {
			result = new ArrayHashSet<InstanceNode<T, I>>();
			Queue<TypeNode<T, I>> todo = new LinkedList<TypeNode<T, I>>();

			todo.add(this);

			while (!todo.isEmpty()) {
				TypeNode<T, I> next = todo.poll();
				result.addAll(next.getDirectInstanceNodes());

				for (TypeNode<T, I> nextSubNode : next.getDirectSubNodes()) {
					todo.add(nextSubNode);
				}
			}

			return Collections.unmodifiableSet(result);

		}
		// else
		return Collections.unmodifiableSet(getDirectInstanceNodes());
	}

	public static class Projection<T extends ElkEntity, I extends ElkEntity>
			extends NonBottomGenericTypeNode<T, I, UpdateableTaxonomyTypeNode<T, I>, UpdateableTaxonomyInstanceNode<T, I>, BottomTypeNode<T, I>>
			implements UpdateableTaxonomyTypeNode<T, I> {

		public Projection(final BottomTypeNode<T, I> bottomNode,
				final Iterable<? extends T> members, final int size) {
			super(bottomNode, members, size);
		}
		
	}
	
}
