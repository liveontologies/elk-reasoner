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
 * Updateable instance taxonomy. Extends the functionality of
 * {@link UpdateableTaxonomy} to type nodes and instance nodes.
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 * @author Peter Skocovsky
 *
 * @param <T>
 *            The type of members of the type nodes in this taxonomy.
 * @param <I>
 *            The type of members of the instance nodes in this taxonomy.
 */
public interface UpdateableInstanceTaxonomy<T extends ElkEntity, I extends ElkEntity>
		extends InstanceTaxonomy<T, I>, UpdateableTaxonomy<T> {

	@Override
	NonBottomTypeNode<T, I> getNonBottomNode(T elkEntity);

	@Override
	Set<? extends NonBottomTypeNode<T, I>> getNonBottomNodes();

	@Override
	NonBottomTypeNode<T, I> getCreateNode(Collection<? extends T> members);

	/**
	 * Returns the instance node that contains the members provided in
	 * arguments. If such a node is not in this taxonomy, it is created and
	 * inserted into this taxonomy.
	 * 
	 * @param instances
	 *            The members of the returned node.
	 * @return The instance node containing the provided members.
	 */
	InstanceNode<T, I> getCreateInstanceNode(Collection<? extends I> instances);

	/**
	 * Associates type nodes containing the specified members with the supplied
	 * instance node. If the type nodes do not exist, they are created.
	 * 
	 * TODO: More consistent contract and concurrency!
	 * 
	 * @param instanceNode
	 *            The node with which the type nodes are to be associated.
	 * @param typeSets
	 *            A collection of collections that should be the members of the
	 *            type nodes.
	 * @return <code>true</code> iff the job was successfully finished by the
	 *         current thread.
	 */
	boolean setCreateDirectTypes(InstanceNode<T, I> instanceNode,
			Iterable<? extends Collection<? extends T>> typeSets);

	/**
	 * Removes the association between the supplied instance node and its type
	 * nodes.
	 * 
	 * @param instanceNode
	 *            The node whose association with type nodes should be removed.
	 * @return <code>true</code> iff the job was successfully finished by the
	 *         current thread.
	 */
	boolean removeDirectTypes(InstanceNode<T, I> instanceNode);

	/**
	 * Removes the instance node containing the specified member from the
	 * taxonomy.
	 * 
	 * @param instance 
	 * @param member
	 *            The member whose instance node should be removed.
	 * @return <code>true</code> if and only if some node was removed.
	 */
	boolean removeInstanceNode(I instance);

}
