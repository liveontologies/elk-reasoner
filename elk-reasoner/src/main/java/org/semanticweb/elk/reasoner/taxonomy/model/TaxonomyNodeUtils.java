/**
 * 
 */
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

import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.util.collections.ArrayHashSet;

/**
 * A collection of utility methods, mostly for the frequent use case of
 * recursive traversal
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class TaxonomyNodeUtils {
	
	/**
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	interface GetSuccessors<T extends ElkObject, O extends TaxonomyNode<T>> {
		
		Set<? extends O> get(O node);
	}

	private static <T extends ElkObject, 	O extends TaxonomyNode<T>> Set<O> getAllReachable(Collection<? extends O> direct, GetSuccessors<T, O> succ) {		
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
	
	public static <T extends ElkObject> Set<? extends TaxonomyNode<T>> getAllSuperNodes(TaxonomyNode<T> node) {
		return getAllReachable(node.getDirectSuperNodes(), new GetSuccessors<T, TaxonomyNode<T>> () {

			@Override
			public Set<? extends TaxonomyNode<T>> get(TaxonomyNode<T> node) {
				return node.getDirectSuperNodes();
			}});
	}
	
	public static <T extends ElkObject> Set<? extends TaxonomyNode<T>> getAllSubNodes(TaxonomyNode<T> node) {
		return getAllReachable(node.getDirectSubNodes(), new GetSuccessors<T, TaxonomyNode<T>> () {

			@Override
			public Set<? extends TaxonomyNode<T>> get(TaxonomyNode<T> node) {
				return node.getDirectSubNodes();
			}});
	}	
	
	public static <T extends ElkObject> Set<? extends UpdateableTaxonomyNode<T>> getAllUpdateableSubNodes(UpdateableTaxonomyNode<T> node) {
		return getAllReachable(node.getDirectUpdateableSubNodes(), new GetSuccessors<T, UpdateableTaxonomyNode<T>> () {

			@Override
			public Set<? extends UpdateableTaxonomyNode<T>> get(UpdateableTaxonomyNode<T> node) {
				return node.getDirectUpdateableSubNodes();
			}});
	}
	
	public static <T extends ElkObject> Set<? extends TaxonomyNode<T>> getAllUpdateableSuperNodes(UpdateableTaxonomyNode<T> node) {
		return getAllReachable(node.getDirectUpdateableSuperNodes(), new GetSuccessors<T, UpdateableTaxonomyNode<T>> () {

			@Override
			public Set<? extends UpdateableTaxonomyNode<T>> get(UpdateableTaxonomyNode<T> node) {
				return node.getDirectUpdateableSuperNodes();
			}});
	}	
	
	public static <T extends ElkObject> Set<? extends UpdateableTaxonomyNode<T>> getAllUpdateableSuperNodes(UpdateableBottomNode<T> node) {
		return getAllReachable(node.getDirectUpdateableSuperNodes(), new GetSuccessors<T, UpdateableTaxonomyNode<T>> () {

			@Override
			public Set<? extends UpdateableTaxonomyNode<T>> get(UpdateableTaxonomyNode<T> node) {
				return node.getDirectUpdateableSuperNodes();
			}});
	}	
}
