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
package org.semanticweb.elk.reasoner.taxonomy.model;

import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;

/**
 * Type node with parameterized type of nodes with which it may be associated.
 * 
 * @author Peter Skocovsky
 *
 * @param <T>
 *            The type of members of this node.
 * @param <I>
 *            The type of members of the related instance nodes.
 * @param <TN>
 *            The type of type nodes with which this node may be associated.
 * @param <IN>
 *            The type of instance nodes with which this node may be associated.
 */
public interface GenericTypeNode<T extends ElkEntity, I extends ElkEntity, TN extends GenericTypeNode<T, I, TN, IN>, IN extends GenericInstanceNode<T, I, TN, IN>>
		extends TypeNode<T, I>, GenericTaxonomyNode<T, TN> {

	@Override
	Set<? extends IN> getDirectInstanceNodes();

	@Override
	Set<? extends IN> getAllInstanceNodes();

	@Override
	Set<? extends TN> getDirectSuperNodes();

	@Override
	Set<? extends TN> getAllSuperNodes();

	@Override
	Set<? extends TN> getDirectSubNodes();

	@Override
	Set<? extends TN> getAllSubNodes();

	public static interface Projection<T extends ElkEntity, I extends ElkEntity>
			extends GenericTypeNode<T, I, Projection<T, I>, GenericInstanceNode.Projection<T, I>> {
		// Empty.
	}
	
}
