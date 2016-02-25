package org.semanticweb.elk.reasoner.taxonomy.model;
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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.util.collections.ArrayHashSet;

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
	
	/**
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	interface GetSuccessors<O> {
		Set<? extends O> get(O node);
	}

	private static <O> Set<O> getAllReachable(
					Collection<? extends O> direct, GetSuccessors<O> succ) {
		
		Set<O> result = new ArrayHashSet<O>(direct.size());
		Queue<O> todo = new LinkedList<O>();
		
		todo.addAll(direct);
		
		while (!todo.isEmpty()) {
			O next = todo.poll();
			
			if (result.add(next)) {
				for (O nextSuperNode : succ.get(next)) {
					todo.add(nextSuperNode);
				}
			}
		}
		
		return Collections.unmodifiableSet(result);
	}
	
	public static <T extends ElkEntity, N extends GenericTaxonomyNode<T, N>>
			Set<? extends N> getAllSuperNodes(final GenericTaxonomyNode<T, N> tnode) {
		return getAllReachable(tnode.getDirectSuperNodes(), new GetSuccessors<N> () {

			@Override
			public Set<? extends N> get(final N node) {
				return node.getDirectSuperNodes();
			}});
	}
	
	public static <T extends ElkEntity, N extends GenericTaxonomyNode<T, N>>
			Set<? extends N> getAllSubNodes(final GenericTaxonomyNode<T, N> tnode) {
		return getAllReachable(tnode.getDirectSubNodes(), new GetSuccessors<N> () {

			@Override
			public Set<? extends N> get(final N node) {
				return node.getDirectSubNodes();
			}});
	}	
	
	public static <T extends ElkEntity>
			Set<? extends TaxonomyNode<T>> getAllSuperNodes(final TaxonomyNode<T> tnode) {
		return getAllReachable(tnode.getDirectSuperNodes(), new GetSuccessors<TaxonomyNode<T>> () {

			@Override
			public Set<? extends TaxonomyNode<T>> get(final TaxonomyNode<T> node) {
				return node.getDirectSuperNodes();
			}});
	}
	
	public static <T extends ElkEntity>
			Set<? extends TaxonomyNode<T>> getAllSubNodes(final TaxonomyNode<T> tnode) {
		return getAllReachable(tnode.getDirectSubNodes(), new GetSuccessors<TaxonomyNode<T>> () {

			@Override
			public Set<? extends TaxonomyNode<T>> get(final TaxonomyNode<T> node) {
				return node.getDirectSubNodes();
			}});
	}	

}
