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
package org.semanticweb.elk.reasoner.taxonomy.model;

import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;

/**
 * Extended {@link Taxonomy} that also provides instances for each of its
 * members.
 * 
 * @author Markus Kroetzsch
 * @author "Yevgeny Kazakov"
 * @author Peter Skocovsky
 * 
 * @param <T>
 *            the type of objects in the nodes of this taxonomy
 * @param <I>
 *            the type of instances of nodes of this taxonomy
 */
public interface InstanceTaxonomy<T extends ElkEntity, I extends ElkEntity>
		extends Taxonomy<T> {

	/**
	 * Returns the {@link ComparatorKeyProvider} that provides a key for each instance.
	 * These keys are used to compute hash codes and to compare the instances.
	 * 
	 * @return the {@link ComparatorKeyProvider} that provides a key for each instance.
	 */
	ComparatorKeyProvider<ElkEntity> getInstanceKeyProvider();
	
	/**
	 * @param elkEntity
	 *            {@link ElkEntity} for which the {@link InstanceNode} to be
	 *            computed
	 * @return the {@link InstanceNode} containing the given {@link ElkEntity}
	 *         as a member, or {@code null} if the input does not occur in the
	 *         taxonomy
	 */
	InstanceNode<T, I> getInstanceNode(I elkEntity);

	/**
	 * Obtain an unmodifiable Set of all instance nodes in this taxonomy.
	 * 
	 * @return an unmodifiable Set
	 */
	Set<? extends InstanceNode<T, I>> getInstanceNodes();

	@Override
	TypeNode<T, I> getNode(T elkEntity);

	@Override
	Set<? extends TypeNode<T, I>> getNodes();

	@Override
	TypeNode<T, I> getTopNode();

	@Override
	TypeNode<T, I> getBottomNode();

}
