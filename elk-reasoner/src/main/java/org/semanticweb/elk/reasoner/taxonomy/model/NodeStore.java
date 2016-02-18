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
package org.semanticweb.elk.reasoner.taxonomy.model;

import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;

/**
 * An immutable storage of nodes.
 * <p>
 * The way members of each node are hashed and compared is controlled by
 * {@link ComparatorKeyProvider} that is returned by {@link #getKeyProvider()}.
 * For more information see {@link Node}.
 * 
 * @author Peter Skocovsky
 *
 * @param <T>
 *            The type of members of nodes in this store.
 */
public interface NodeStore<T> {

	/**
	 * Returns the {@link ComparatorKeyProvider} that provides a key for each
	 * node member. These keys are used to compute hash codes and to compare the
	 * members.
	 * 
	 * @return the {@link ComparatorKeyProvider} that provides a key for each
	 *         node member.
	 */
	public ComparatorKeyProvider<? super T> getKeyProvider();

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
	public Node<T> getNode(T member);

	/**
	 * Obtain an unmodifiable Set of all nodes in this taxonomy.
	 * 
	 * @return an unmodifiable Collection
	 */
	public Set<? extends Node<T>> getNodes();

}
