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

import java.util.Collections;
import java.util.Set;

import org.semanticweb.elk.util.collections.ArrayHashSet;

/**
 * A {@link GenericTaxonomyNode} that is assigned with a (possibly empty) set of
 * {@link GenericInstanceNode}s storing instances of the node members.
 * 
 * @author Markus Kroetzsch
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
 *            the type of sub-nodes and super-nodes of this {@code TypeNode}
 * @param <IN>
 *            the type of instance nodes of this {@code TypeNode}
 */
public interface GenericTypeNode<K, M, KI, I, 
	TN extends GenericTypeNode<K, M, KI, I, TN, IN>,
	IN extends GenericInstanceNode<K, M, KI, I, TN, IN>>
		extends TypeNode<K, M, KI, I>, GenericTaxonomyNode<K, M, TN> {

	@Override
	public Set<? extends IN> getAllInstanceNodes();

	@Override
	public Set<? extends IN> getDirectInstanceNodes();

	public static class Helper extends GenericTaxonomyNode.Helper {

		public static <K, M, KI, I, 
				TN extends GenericTypeNode<K, M, KI, I, TN, IN>,
				IN extends GenericInstanceNode<K, M, KI, I, TN, IN>> 
		Set<? extends IN> getAllInstanceNodes(
				GenericTypeNode<K, M, KI, I, TN, IN> node) {

			Set<? extends TN> subNodes = node.getAllSubNodes();
			Set<IN> result = new ArrayHashSet<IN>();
			result.addAll(node.getDirectInstanceNodes());
			for (TN subNode : subNodes)
				result.addAll(subNode.getDirectInstanceNodes());
			return Collections.unmodifiableSet(result);
		}
	}

	interface Min<K, M, KI, I>
			extends
			GenericTypeNode<K, M, KI, I, 
				GenericTypeNode.Min<K, M, KI, I>,
				GenericInstanceNode.Min<K, M, KI, I>> {

	}

}
