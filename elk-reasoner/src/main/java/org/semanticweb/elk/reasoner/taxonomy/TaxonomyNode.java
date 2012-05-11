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
 * A node in a taxonomy. It is a {@link Node} that refers to its sub and super
 * nodes in a {@link Taxonomy}.
 * 
 * @author Markus Kroetzsch
 */
public interface TaxonomyNode<T extends ElkObject> extends Node<T> {
	/**
	 * Get an unmodifiable set of nodes for ElkObjects that are direct
	 * super-objects of this Node.
	 * 
	 * @return list of nodes for direct super-objects of this node's members
	 */
	public Set<TaxonomyNode<T>> getDirectSuperNodes();

	/**
	 * Computes an unmodifiable set of nodes for ElkObjects that are (possibly
	 * indirect) super-objects of members of this Node. This is the smallest set
	 * of nodes that contains all direct super-nodes of this node, and all
	 * direct super-nodes of every node in this set.
	 * 
	 * @return set of nodes for sub-objects of this node's members
	 */
	public Set<TaxonomyNode<T>> getAllSuperNodes();

	/**
	 * Get an unmodifiable set of nodes for ElkObjects that are direct
	 * sub-objects of this Node.
	 * 
	 * @return list of nodes for direct sub-objects of this node's members
	 */
	public Set<TaxonomyNode<T>> getDirectSubNodes();

	/**
	 * Computes an unmodifiable set of nodes for ElkObjects that are (possibly
	 * indirect) sub-objects of members of this Node. This is the smallest set
	 * of nodes that contains all direct sub-nodes of this node, and all direct
	 * sub-nodes of every node in this set.
	 * 
	 * @return set of nodes for sub-objects of this node's members
	 */
	public Set<TaxonomyNode<T>> getAllSubNodes();

	/**
	 * Returns the taxonomy to which this node belongs.
	 * 
	 * @return the taxonomy to which this node belongs
	 */
	public Taxonomy<T> getTaxonomy();
}
