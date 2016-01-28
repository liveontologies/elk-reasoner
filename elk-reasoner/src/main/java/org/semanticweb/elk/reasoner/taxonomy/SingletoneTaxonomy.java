package org.semanticweb.elk.reasoner.taxonomy;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import java.util.Collections;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;

/**
 * A {@link Taxonomy} consisting of a single node = top node = bottom node.
 * Typically, this is used to represent an inconsistent taxonomy.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of objects stored in this taxonomy
 * @param <N>
 *            the type of the node of this taxonomy
 * 
 * @see OrphanNode
 */
public class SingletoneTaxonomy<T extends ElkEntity, N extends OrphanNode<T>>
		implements Taxonomy<T> {

	final N node;

	/**
	 * Constructs a {@link SingletoneTaxonomy} containing the given
	 * {@link OrphanNode}
	 * 
	 * @param node
	 */
	public SingletoneTaxonomy(N node) {
		this.node = node;
	}

	@Override
	public TaxonomyNode<T> getNode(T elkEntity) {
		return node;
	}

	@Override
	public Set<? extends TaxonomyNode<T>> getNodes() {
		return Collections.singleton(node);
	}

	@Override
	public TaxonomyNode<T> getTopNode() {
		return node;
	}

	@Override
	public TaxonomyNode<T> getBottomNode() {
		return node;
	}

}
