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
package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkObject;

/**
 * Extended {@link: Taxonomy} that also provides instances for each of its
 * members.
 * 
 * @author Markus Kroetzsch
 * 
 * @param <T>
 *            the type of objects in the nodes of this taxonomy
 * @param <I>
 *            the type of instances of nodes of this taxonomy
 */
public interface InstanceTaxonomy<T extends ElkObject, I extends ElkObject>
		extends Taxonomy<T> {

	/**
	 * Returns the TypeNode containing the given elkObject as a member. Null
	 * if elkObject does not occur in the taxonomy.
	 */
	@Override
	public TypeNode<T, I> getNode(T elkObject);

	/**
	 * Obtain an unmodifiable Set of all type nodes in this taxonomy.
	 * 
	 * @return an unmodifiable Set
	 */
	@Override
	public Set<TypeNode<T, I>> getNodes();
	
	/**
	 * Returns the InstanceNode containing the given elkObject as a member. Null
	 * if elkObject does not occur in the taxonomy.
	 */
	public InstanceNode<T, I> getInstanceNode(I elkObject);
	
	
	/**
	 * Obtain an unmodifiable Set of all instance nodes in this taxonomy.
	 * 
	 * @return an unmodifiable Set
	 */
	public Set<InstanceNode<T, I>> getInstanceNodes();

}
