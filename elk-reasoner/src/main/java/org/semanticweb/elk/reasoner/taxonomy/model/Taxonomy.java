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

import org.semanticweb.elk.owl.interfaces.ElkObject;

/**
 * A hierarchy of certain ElkObjects. For each such object, the taxonomy holds a
 * TaxonomyNode object from which direct sub- and supernodes can be retrieved.
 * 
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 * @author Frantisek Simancik
 * 
 */
public interface Taxonomy<T extends ElkObject> {

	/**
	 * Returns the TaxonomyNode containing the given elkObject as a member. Null
	 * if elkObject does not occur in the taxonomy.
	 */
	public TaxonomyNode<T> getNode(T elkObject);

	/**
	 * Obtain an unmodifiable Set of all nodes in this taxonomy.
	 * 
	 * @return an unmodifiable Collection
	 */
	public Set<? extends TaxonomyNode<T>> getNodes();
	
	/**
	 * Returns the top node of the taxonomy.
	 */
	public TaxonomyNode<T> getTopNode();
	
	/**
	 * Returns the bottom node of the taxonomy.
	 */
	public TaxonomyNode<T> getBottomNode();

}
