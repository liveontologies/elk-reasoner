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

import org.semanticweb.elk.reasoner.taxonomy.GenericInstanceTaxonomy;

/**
 * A {@link Node} that stores instance assignment. Every
 * {@link GenericInstanceNode} is assigned to a collection of
 * {@link GenericTypeNode}s that have this node as one of the instances.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 * @param <K>
 *            the type of the keys for the node members
 * @param <M>
 *            the type of node members
 * @param <KI>
 *            the type of the keys for the node instances
 * @param <I>
 *            the type of instances
 * @param <TN>
 *            the type of type-nodes of this {@code InstanceNode}
 * @param <IN>
 *            the type of instance nodes of type nodes of this
 *            {@code InstanceNode}
 * 
 * @see GenericTypeNode
 * @see GenericInstanceTaxonomy
 */
public interface GenericInstanceNode<K, M, KI, I, 
			TN extends GenericTypeNode<K, M, KI, I, TN, IN>,
			IN extends GenericInstanceNode<K, M, KI, I, TN, IN>>
		extends InstanceNode<K, M, KI, I> {

	@Override
	public Set<? extends TN> getAllTypeNodes();

	@Override
	public Set<? extends TN> getDirectTypeNodes();

	public class Helper extends Node.Helper {
		public static <K, M, KI, I, 
				TN extends GenericTypeNode<K, M, KI, I, TN, IN>,
				IN extends GenericInstanceNode<K, M, KI, I, TN, IN>> 		
		Set<? extends TN> getAllTypeNodes(
				GenericInstanceNode<K, M, KI, I, TN, IN> node) {
			return close(node.getDirectTypeNodes(),
					new SuccessorRelation<TN>() {
						@Override
						public Set<? extends TN> get(TN n) {
							return n.getDirectSuperNodes();
						}
					});
		}
	}

	interface Min<K, M, KI, I>
			extends
			GenericInstanceNode<K, M, KI, I, 
				GenericTypeNode.Min<K, M, KI, I>,
				GenericInstanceNode.Min<K, M, KI, I>> {

	}

}
