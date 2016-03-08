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
package org.semanticweb.elk.reasoner.taxonomy.impl;

import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.model.GenericInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.GenericTypeNode;
import org.semanticweb.elk.reasoner.taxonomy.model.NonBottomTypeNode;

/**
 * Updateable generic type node that can be related with
 * {@link UpdateableTypeNode} and {@link UpdateableInstanceNode}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author Peter Skocovsky
 *
 * @param <T>
 *            The type of members of this node.
 * @param <I>
 *            The type of members of the related instance nodes.
 * @param <UTN>
 *            The type of type nodes with which this node may be associated.
 * @param <UIN>
 *            The type of instance nodes with which this node may be associated.
 */
public interface UpdateableTypeNode<
				T extends ElkEntity,
				I extends ElkEntity,
				TN extends GenericTypeNode<T, I, TN, IN>,
				IN extends GenericInstanceNode<T, I, TN, IN>,
				UTN extends UpdateableTypeNode<T, I, TN, IN, UTN, UIN>,
				UIN extends UpdateableInstanceNode<T, I, TN, IN, UTN, UIN>
		>
		extends NonBottomTypeNode<T, I> {

	@Override
	Set<? extends UTN> getDirectNonBottomSuperNodes();
	
	@Override
	Set<? extends UTN> getDirectNonBottomSubNodes();

	/**
	 * Associates this node with its direct instance node.
	 * 
	 * @param instanceNode
	 *            The instance node with which this node should be associated.
	 */
	void addDirectInstanceNode(UIN instanceNode);

	/**
	 * Deletes the association between this node and the specified instance
	 * node.
	 * 
	 * @param instanceNode
	 *            The instance node with which this node should not be
	 *            associated.
	 */
	void removeDirectInstanceNode(UIN instanceNode);

	public static interface Projection<T extends ElkEntity, I extends ElkEntity>
			extends UpdateableTypeNode<
					T,
					I,
					GenericTypeNode.Projection<T, I>,
					GenericInstanceNode.Projection<T, I>,
					Projection<T, I>,
					UpdateableInstanceNode.Projection<T, I>
			>, GenericTypeNode.Projection<T, I> {
		// Empty.
	}
	
}