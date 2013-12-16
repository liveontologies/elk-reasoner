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
package org.semanticweb.elk.reasoner.taxonomy;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.TaxonomyNode;

/**
 * A representation for some collection of {@link TaxonomyNode}s. If a
 * {@link GenericTaxonomy} contains a {@link TaxonomyNode}, it should also
 * contain all its sub-nodes and super-nodes. There should be exactly one
 * {@link TaxonomyNode} in this taxonomy that has no sub-nodes (the bottom node)
 * and exactly one {@link TaxonomyNode} that has not super-node (the top node).
 * These nodes could possibly be equal (in which case there cannot be any other
 * nodes in this {@link GenericTaxonomy}. The sets of members of the
 * {@link TaxonomyNode}s stored in the {@link GenericTaxonomy} should be
 * disjoint.
 * 
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 * @author Frantisek Simancik
 * 
 * @param <K>
 *            the type of the keys for the node members
 * @param <M>
 *            the type of node members
 * @param <N>
 *            the type of nodes in this taxonomy
 * 
 * @see Taxonomy
 */
public interface GenericTaxonomy<K, M, N extends GenericTaxonomyNode<K, M, N>>
		extends 
		GenericNodeStore<K, M, N>,
		Taxonomy<K, M> {
	
	@Override
	public N getTopNode();
	
	@Override
	public N getBottomNode();

	interface Min<K, M> 
	extends
	GenericTaxonomy<K, M, GenericTaxonomyNode.Min<K, M>> {
		
	}
	
}
