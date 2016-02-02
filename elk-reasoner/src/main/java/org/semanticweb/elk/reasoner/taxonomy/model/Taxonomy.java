/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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
/**
 * @author Yevgeny Kazakov, May 15, 2011
 */
package org.semanticweb.elk.reasoner.taxonomy.model;

import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;

/**
 * A hierarchy of certain ElkEntities.
 * For each such entity, the taxonomy holds a {@link TaxonomyNode} object
 * from which direct sub- and super- nodes can be retrieved.
 * 
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 * @author Frantisek Simancik
 * @param <T>
 *            the type of objects stored in this taxonomy
 */
public interface Taxonomy<T extends ElkEntity> {

	/**
	 * Returns the {@link ComparatorKeyProvider} that provides a key for each node member.
	 * These keys are used to compute hash codes and to compare the members.
	 * 
	 * @return the {@link ComparatorKeyProvider} that provides a key for each node member.
	 */
	public ComparatorKeyProvider<ElkEntity> getKeyProvider();

	/**
	 * Returns the {@link TaxonomyNode} containing the given {@link ElkEntity}
	 * as a member or {@code null} if the input does not occur in the taxonomy.
	 * 
	 * @param elkEntity
	 *            the {@link ElkEntity} for which to return the
	 *            {@link TaxonomyNode}
	 * @return the {@link TaxonomyNode} for the specified input
	 *         {@link ElkEntity}
	 */
	public TaxonomyNode<T> getNode(T elkEntity);

	/**
	 * Obtain an unmodifiable Set of all nodes in this taxonomy.
	 * 
	 * @return an unmodifiable Collection
	 */
	public Set<? extends TaxonomyNode<T>> getNodes();

	/**
	 * 
	 * @return the node of this taxonomy that has no parent nodes
	 */
	public TaxonomyNode<T> getTopNode();

	/**
	 * 
	 * @return the node of this taxonomy that has no child nodes
	 */
	public TaxonomyNode<T> getBottomNode();

}
