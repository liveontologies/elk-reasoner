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
package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Collections;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.model.ComparatorKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;

/**
 * A {@link TaxonomyNode} that does not have any super nodes or sub nodes.
 * 
 * @author "Yevgeny Kazakov"
 * @author Peter Skocovsky
 * 
 * @param <T>
 *            the type of objects stored in the nodes
 * 
 * @see SingletoneTaxonomy
 */
public class OrphanTaxonomyNode<T extends ElkEntity> extends OrphanNode<T>
		implements TaxonomyNode<T> {

	/**
	 * the taxonomy this node belongs to
	 */
	private final Taxonomy<T> taxonomy_;

	public OrphanTaxonomyNode(
			final Iterable<? extends T> members,
			final int size,
			final T canonical,
			final Taxonomy<T> taxonomy) {
		super(members, size, canonical, taxonomy.getKeyProvider());
		this.taxonomy_ = taxonomy;
	}
	
	@Override
	public Taxonomy<T> getTaxonomy() {
		return taxonomy_;
	}

	@Override
	public ComparatorKeyProvider<? super T> getKeyProvider() {
		return taxonomy_.getKeyProvider();
	}

	@Override
	public Set<? extends TaxonomyNode<T>> getDirectSuperNodes() {
		return Collections.emptySet();
	}

	@Override
	public Set<? extends TaxonomyNode<T>> getAllSuperNodes() {
		return Collections.emptySet();
	}

	@Override
	public Set<? extends TaxonomyNode<T>> getDirectSubNodes() {
		return Collections.emptySet();
	}

	@Override
	public Set<? extends TaxonomyNode<T>> getAllSubNodes() {
		return Collections.emptySet();
	}

}
