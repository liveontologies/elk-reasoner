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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
public class OrphanNode<T extends ElkEntity> implements TaxonomyNode<T> {

	/**
	 * the members of the node
	 */
	private final Map<Object, T> members;
	/**
	 * the representative of the node; should be among the members
	 */
	private final T canonical;
	/**
	 * provides keys that are used for hashing instead of the members
	 */
	private final ComparatorKeyProvider<ElkEntity> keyProvider_;
	/**
	 * the taxonomy this node belongs to
	 */
	private Taxonomy<T> taxonomy_;

	public OrphanNode(Set<T> members, T canonical,
			ComparatorKeyProvider<ElkEntity> keyProvider) {
		this.members = new HashMap<Object, T>();
		for (T member : members) {
			this.members.put(keyProvider.getKey(member), member);
		}
		this.canonical = canonical;
		this.keyProvider_ = keyProvider;
	}

	void setTaxonomy(final Taxonomy<T> taxonomy) {// TODO: this node should be created in the constructor of SingletoneTaxonomy
		this.taxonomy_ = taxonomy;
	}
	
	@Override
	public Taxonomy<T> getTaxonomy() {
		return taxonomy_;
	}
	
	@Override
	public ComparatorKeyProvider<ElkEntity> getKeyProvider() {
		return keyProvider_;
	}

	@Override
	public Iterator<T> iterator() {
		return members.values().iterator();
	}

	@Override
	public boolean contains(final T member) {
		return members.containsKey(keyProvider_.getKey(member));
	}

	@Override
	public int size() {
		return members.size();
	}

	@Override
	public T getCanonicalMember() {
		return canonical;
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
