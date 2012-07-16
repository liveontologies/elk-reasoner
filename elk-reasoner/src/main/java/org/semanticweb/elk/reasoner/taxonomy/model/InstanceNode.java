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

import org.semanticweb.elk.owl.interfaces.ElkObject;

/**
 * A node of instances in an InstanceTaxonomy.
 * 
 * @author Frantisek Simancik
 * 
 * @param <T>
 *            the type of objects in this node
 * @param <I>
 *            the type of instances in this node
 * 
 * 
 */
public interface InstanceNode<T extends ElkObject, I extends ElkObject> extends
		Node<I> {
	/**
	 * Get an unmodifiable set of nodes for ElkObjects that are direct types of
	 * this Node.
	 * 
	 * @return list of nodes for direct types of this node's members
	 */
	public Set<? extends TypeNode<T, I>> getDirectTypeNodes();

	/**
	 * Get an unmodifiable set of nodes for ElkObjects that are (possibly
	 * indirect) types of this Node.
	 * 
	 * @return list of nodes for instances of this node's members
	 */
	public Set<? extends TypeNode<T, I>> getAllTypeNodes();

	public InstanceTaxonomy<T, I> getTaxonomy();

}
