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
package org.semanticweb.elk.reasoner.taxonomy.model;

import java.util.Collection;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;

/**
 * Updateable taxonomy. The bottom node of this taxonomy can be modified via
 * dedicated methods {@link #addToBottomNode(ElkEntity)} and
 * {@link #removeFromBottomNode(ElkEntity)}, whereas all other nodes must be
 * created containing their final set of members by
 * {@link #getCreateNode(Collection)} and they can be associated with their
 * super-nodes by
 * {@link #setCreateDirectSupernodes(NonBottomTaxonomyNode, Iterable)}.
 * <p>
 * The implementations are guaranteed to be thread-safe for concurrent additions
 * <strong>xor</strong> concurrent deletions, but not necessarily for concurrent
 * additions and deletions at the same time.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author Peter Skocovsky
 *
 * @param <T>
 *            The type of members of the nodes in this taxonomy.
 */
public interface UpdateableTaxonomy<T extends ElkEntity> extends Taxonomy<T> {

	/**
	 * Returns a non-bottom node that contains the supplied member. If the
	 * member is not in the taxonomy or it is only in the bottom node, returns
	 * <code>null</code>.
	 * 
	 * @param member
	 *            The member whose node should be returned.
	 * @return a non-bottom node that contains the supplied member. If the
	 *         member is not in the taxonomy or it is only in the bottom node,
	 *         returns <code>null</code>.
	 */
	NonBottomTaxonomyNode<T> getNonBottomNode(T member);

	/**
	 * Returns all nodes in this taxonomy except the bottom node.
	 * 
	 * @return all nodes in this taxonomy except the bottom node.
	 */
	Set<? extends NonBottomTaxonomyNode<T>> getNonBottomNodes();

	/**
	 * Returns the node that contains the members provided in arguments. If such
	 * a node is not in this taxonomy, it is created and inserted into this
	 * taxonomy.
	 * 
	 * @param members
	 *            The members of the returned node.
	 * @return The node containing the provided members.
	 */
	NonBottomTaxonomyNode<T> getCreateNode(Collection<? extends T> members);

	/**
	 * Associates super-nodes containing the specified members with the supplied
	 * node. If the super-nodes do not exist, they are created.
	 * 
	 * TODO: More consistent contract and concurrency!
	 * 
	 * @param subNode
	 *            The node with which the super-nodes are to be associated.
	 * @param superMemberSets
	 *            A collection of collections that should be the members of the
	 *            super-nodes.
	 * @return <code>true</code> iff the job was successfully finished by the
	 *         current thread.
	 */
	boolean setCreateDirectSupernodes(NonBottomTaxonomyNode<T> subNode,
			Iterable<? extends Collection<? extends T>> superMemberSets);

	/**
	 * Removes the association between the supplied node and its super-nodes.
	 * 
	 * @param subNode
	 *            The node whose association with super-nodes should be removed.
	 * @return <code>true</code> iff the job was successfully finished by the
	 *         current thread.
	 */
	boolean removeDirectSupernodes(NonBottomTaxonomyNode<T> subNode);

	/**
	 * Removes the node containing the specified member from the taxonomy.
	 * 
	 * @param member
	 *            The member whose node should be removed.
	 * @return <code>true</code> if and only if some node was removed.
	 */
	boolean removeNode(T member);

	/**
	 * Adds the specified member to the bottom node of this taxonomy.
	 * 
	 * @param member
	 *            The member to add.
	 * @return Whether the bottom node changed.
	 */
	boolean addToBottomNode(T member);

	/**
	 * Removes the specified member from the bottom node of this taxonomy.
	 * 
	 * @param member
	 *            The node to remove.
	 * @return Whether the bottom node changed.
	 */
	boolean removeFromBottomNode(T member);

}
