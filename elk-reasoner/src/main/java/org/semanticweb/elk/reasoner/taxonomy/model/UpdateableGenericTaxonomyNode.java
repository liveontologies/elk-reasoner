package org.semanticweb.elk.reasoner.taxonomy.model;

import java.util.Set;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkEntity;

/**
 * Generic taxonomy node that is modifiable.
 * 
 * @author Peter Skocovsky
 *
 * @param <T>
 *            The type of members of this node.
 * @param <N>
 *            The type of nodes with which this node can be associated.
 */
public interface UpdateableGenericTaxonomyNode<T extends ElkEntity, N extends UpdateableGenericTaxonomyNode<T, N>>
		extends UpdateableNode<T>, NonBottomTaxonomyNode<T> {

	Set<? extends N> getDirectNonBottomSuperNodes();
	
	Set<? extends N> getDirectNonBottomSubNodes();
	
	/**
	 * Associates this node with its direct super-node.
	 * 
	 * @param superNode
	 *            The super-node with which this node should be associated.
	 */
	void addDirectSuperNode(N superNode);

	/**
	 * Associates this node with its direct sub-node.
	 * 
	 * @param subNode
	 *            The sub-node with which this node should be associated.
	 */
	void addDirectSubNode(N subNode);

	/**
	 * Deletes the association between this node and the specified sub-node.
	 * 
	 * @param subNode
	 *            The sub-node with which this node should not be associated.
	 * @return <code>true</code> if and only if this node changed.
	 */
	boolean removeDirectSubNode(N subNode);

	/**
	 * Deletes the association between this node and the specified super-node.
	 * 
	 * @param superNode
	 *            The super-node with which this node should not be associated.
	 * @return <code>true</code> if and only if this node changed.
	 */
	boolean removeDirectSuperNode(N superNode);

}
