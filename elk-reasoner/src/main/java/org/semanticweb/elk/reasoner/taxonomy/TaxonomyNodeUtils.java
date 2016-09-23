/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.model.GenericInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.GenericTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.GenericTypeNode;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.Operations.Functor;

/**
 * A collection of utility methods, mostly for the frequent use case of
 * recursive traversal
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author Peter Skocovsky
 */
public class TaxonomyNodeUtils {
	
	public static <N> Set<N> getAllReachable(
					final Collection<? extends N> direct,
					final Functor<N, Set<? extends N>> succ) {
		
		final Set<N> result = new ArrayHashSet<N>(direct.size());
		result.addAll(direct);
		final Queue<N> todo = new LinkedList<N>(direct);
		
		while (!todo.isEmpty()) {
			final N next = todo.poll();
			
			for (final N succNode : succ.apply(next)) {
				if (result.add(succNode)) {
					todo.add(succNode);
				}
			}
		}
		
		return Collections.unmodifiableSet(result);
	}
	
	public static <N, O> Set<O> collectFromAllReachable(
					final Collection<? extends N> direct,
					final Collection<? extends O> init,
					final Functor<N, Set<? extends N>> succ,
					final Functor<N, Set<? extends O>> collect) {
		
		final Set<O> result = new ArrayHashSet<O>();
		result.addAll(init);
		final Set<N> queued = new ArrayHashSet<N>();
		queued.addAll(direct);
		final Queue<N> todo = new LinkedList<N>(direct);
		
		while (!todo.isEmpty()) {
			final N next = todo.poll();
			
			result.addAll(collect.apply(next));
			
			for (final N succNode : succ.apply(next)) {
				if (queued.add(succNode)) {
					todo.add(succNode);
				}
			}
		}
		
		return Collections.unmodifiableSet(result);
	}
	
	/**
	 * Returns all super-nodes of a node whose direct super-nodes are
	 * <code>direct</code>.
	 * 
	 * @param direct
	 *            The direct super-nodes.
	 * @return all super-nodes of a node whose direct super-nodes are
	 *         <code>direct</code>.
	 */
	public static <T extends ElkEntity, N extends GenericTaxonomyNode<T, N>>
			Set<? extends N> getAllSuperNodes(final Collection<? extends N> direct) {
		return getAllReachable(direct, new Functor<N, Set<? extends N>>() {

			@Override
			public Set<? extends N> apply(final N node) {
				return node.getDirectSuperNodes();
			}});
	}
	
	/**
	 * Returns all sub-nodes of a node whose direct sub-nodes are
	 * <code>direct</code>.
	 * 
	 * @param direct
	 *            The direct sub-nodes.
	 * @return all sub-nodes of a node whose direct sub-nodes are
	 *         <code>direct</code>.
	 */
	public static <T extends ElkEntity, N extends GenericTaxonomyNode<T, N>>
			Set<? extends N> getAllSubNodes(final Collection<? extends N> direct) {
		return getAllReachable(direct, new Functor<N, Set<? extends N>>() {

			@Override
			public Set<? extends N> apply(final N node) {
				return node.getDirectSubNodes();
			}});
	}
	
	/**
	 * Returns all instance nodes of the specified type node and all its
	 * sub-nodes.
	 * 
	 * @param node
	 *            The type node whose instances should be returned.
	 * @return all instance nodes of the specified type node and all its
	 *         sub-nodes.
	 */
	public static <T extends ElkEntity, I extends ElkEntity, TN extends GenericTypeNode<T, I, TN, IN>, IN extends GenericInstanceNode<T, I, TN, IN>>
			Set<? extends IN> getAllInstanceNodes(final GenericTypeNode<T, I, TN, IN> node) {
		return TaxonomyNodeUtils.collectFromAllReachable(
				node.getDirectSubNodes(),
				node.getDirectInstanceNodes(),
				new Operations.Functor<GenericTypeNode<T, I, TN, IN>, Set<? extends GenericTypeNode<T, I, TN, IN>>>() {
					@Override
					public Set<? extends TN> apply(final GenericTypeNode<T, I, TN, IN> node) {
						return node.getDirectSubNodes();
					}
				},
				new Operations.Functor<GenericTypeNode<T, I, TN, IN>, Set<? extends IN>>() {
					@Override
					public Set<? extends IN> apply(final GenericTypeNode<T, I, TN, IN> node) {
						return node.getDirectInstanceNodes();
					}
				});
	}
	
}
