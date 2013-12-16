/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.taxonomy.nodes;

import java.util.Set;

/**
 * A {@code Node} which is assigned with (possibly empty) sets of sub-nodes and
 * super-nodes.
 * 
 * @author Markus Kroetzsch
 * @author "Yevgeny Kazakov"
 * 
 * @param <K>
 *            the type of the keys for the node members
 * @param <M>
 *            the type of node members
 * @param <N>
 *            the type of sub-nodes and super-nodes of this {@code TaxonomyNode}
 */
public interface GenericTaxonomyNode<K, M,
			N extends GenericTaxonomyNode<K, M, N>>
		extends TaxonomyNode<K, M> {

	@Override
	public Set<? extends N> getAllSuperNodes();

	@Override
	public Set<? extends N> getDirectSuperNodes();

	@Override
	public Set<? extends N> getAllSubNodes();

	@Override
	public Set<? extends N> getDirectSubNodes();
	
	<O> O accept(GenericTaxonomyNodeVisitor<K, M, N, O> visitor);
	
	static class Helper extends Node.Helper {

		public static <K, M, N extends GenericTaxonomyNode<K, M, N>> 
			Set<? extends N> getAllSuperNodes(GenericTaxonomyNode<K, M, N> node) {
			return close(node.getDirectSuperNodes(),
					new SuccessorRelation<N>() {
						@Override
						public Set<? extends N> get(N n) {
							return n.getDirectSuperNodes();
						}
					});
		}

		public static <K, M, N extends GenericTaxonomyNode<K, M, N>> 
			Set<? extends N> getAllSubNodes(GenericTaxonomyNode<K, M, N> node) {
			return close(node.getDirectSubNodes(), new SuccessorRelation<N>() {
				@Override
				public Set<? extends N> get(N n) {
					return n.getDirectSubNodes();
				}
			});
		}

	}	

	interface Min<K, M> 
			extends GenericTaxonomyNode<K, M, GenericTaxonomyNode.Min<K, M>> {	

	}
		
}
