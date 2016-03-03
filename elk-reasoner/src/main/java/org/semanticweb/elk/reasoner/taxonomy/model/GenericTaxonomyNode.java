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
 * Taxonomy node with parameterized type of nodes with which it may be
 * associated.
 * 
 * @author Peter Skocovsky
 *
 * @param <T>
 *            The type of members of this node.
 * @param <N>
 *            The type of nodes with which this node can be associated.
 */
public interface GenericTaxonomyNode<T extends ElkEntity, N extends GenericTaxonomyNode<T, N>>
		extends TaxonomyNode<T> {

	@Override
	public Set<? extends N> getDirectSuperNodes();

	@Override
	public Set<? extends N> getAllSuperNodes();

	@Override
	public Set<? extends N> getDirectSubNodes();

	@Override
	public Set<? extends N> getAllSubNodes();

	public static interface Projection<T extends ElkEntity>
			extends GenericTaxonomyNode<T, Projection<T>> {
		// Empty.
	}

}
