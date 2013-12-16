/**
 * 
 */
package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Map;

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
 * A {@link GenericTaxonomy} whose {@link UpdateableGenericTaxonomyNode}s can be
 * added, removed, or modified
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
 */
public interface UpdateableTaxonomy<K, M> extends Taxonomy<K, M> {
	
	/**
	 * Set the direct super-node relations for the node with the given member
	 * assignment; the node will be created if necessary. The method should be
	 * executed at most once for the given members.
	 * 
	 * @param members
	 *            the member assignment of the node for which to set the
	 *            super-node relations
	 * @param superMemberSets
	 *            the member sets of the nodes that should be direct super-nodes
	 */
	public void setDirectRelations(Map<K, M> members,
			Iterable<? extends Map<K, M>> superMemberSets);

	/**
	 * Removes the given {@link TaxonomyNode} from the taxonomy together with
	 * its relations with super and sub-nodes
	 * 
	 * @param node
	 *            the {@link TaxonomyNode} to be removed from the taxonomy
	 * @return {@code true} if the set of the nodes of this taxonomy has changed
	 *         and {@code false} otherwise
	 */
	// public boolean removeNode(TaxonomyNode<K, T> node);
	
	/**
	 * Adds the given member to the members of the bottom node
	 * 
	 * @param member
	 * @return {@code true} if the members of the bottom node have changed and
	 *         {@code false} otherwise
	 */
	public M addBottomMember(K key, M member);


}
