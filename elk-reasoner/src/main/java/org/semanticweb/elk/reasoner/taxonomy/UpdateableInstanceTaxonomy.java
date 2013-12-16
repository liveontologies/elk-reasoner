/**
 * 
 */
package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Map;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericInstanceNode;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
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

/**
 * An {@link GenericInstanceTaxonomy} that is an {@link UpdateableTaxonomy}
 * whose {@link GenericInstanceNode}s can be added or removed
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
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
 */
public interface UpdateableInstanceTaxonomy<K, M, KI, I> extends
		InstanceTaxonomy<K, M, KI, I>,
		UpdateableTaxonomy<K, M> {

	/**
	 * Removes the given {@link GenericInstanceNode} from the taxonomy together
	 * with its relations with super and sub-nodes
	 * 
	 * @param node
	 *            the node to be removed from the taxonomy
	 * @return {@code true} if the set of the nodes of this taxonomy has changed
	 *         and {@code false} otherwise
	 */
	// public boolean removeInstanceNode(InstanceNode<T, I> instance);

	public void setDirectTypes(Map<KI, I> members,
			Iterable<? extends Map<K, M>> superMemberSets);
	
	/**
	 * 
	 * Adds the given instance assignment to the instance of the bottom node
	 * 
	 * @param key
	 *            the key of the instance assignment
	 * @param instance
	 *            the instance to be added
	 * @return the previous instance of the bottom node associated with the
	 *         given key or {@code null} if there was no such an instance
	 */
	public I addBottomInstance(KI key, I instance);

	
}
