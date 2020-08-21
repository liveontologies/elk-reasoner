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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.hashing.InstanceTaxonomyEqualator;
import org.semanticweb.elk.reasoner.taxonomy.hashing.InstanceTaxonomyHasher;
import org.semanticweb.elk.reasoner.taxonomy.model.ComparatorKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.NodeStore;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNodeFactory;
import org.semanticweb.elk.reasoner.taxonomy.model.TypeNode;
import org.semanticweb.elk.util.collections.ArrayHashMap;

/**
 * An {@link InstanceTaxonomy} consisting of a single {@link TypeNode} = top
 * node = bottom node, which has a single {@link InstanceNode} containing all
 * individuals of this taxonomy. Typically, this is used to represent an
 * inconsistent {@link InstanceTaxonomy}.
 * 
 * @author "Yevgeny Kazakov"
 * @author Peter Skocovsky
 * 
 * @param <T>
 *            the type of objects stored in this taxonomy
 * @param <I>
 *            the type of instances of nodes of this taxonomy
 * @param <N>
 *            the type of the node of this taxonomy
 */
public class SingletoneInstanceTaxonomy<T extends ElkEntity, I extends ElkEntity, N extends OrphanTypeNode<T, I>>
		extends SingletoneTaxonomy<T, N> implements InstanceTaxonomy<T, I> {

	final Map<Object, InstanceNode<T, I>> instanceNodeLookup;
	/** provides keys that are used for hashing instead of the elkIndividuals */
	private final ComparatorKeyProvider<? super I> individualKeyProvider_;

	// TODO: get rid of node factory, use instances
	
	public SingletoneInstanceTaxonomy(
			final ComparatorKeyProvider<? super T> keyProvider,
			final Collection<? extends T> allMembers,
			final TaxonomyNodeFactory<T, N, Taxonomy<T>> nodeFactory,
			final ComparatorKeyProvider<? super I> individualKeyProvider) {
		super(keyProvider, allMembers, nodeFactory);
		this.individualKeyProvider_ = individualKeyProvider;
		this.instanceNodeLookup = new ArrayHashMap<Object, InstanceNode<T, I>>(
				node.getAllInstanceNodes().size());
		for (InstanceNode<T, I> instanceNode : node.getAllInstanceNodes()) {
			for (I instance : instanceNode) {
				instanceNodeLookup.put(individualKeyProvider_.getKey(instance),
						instanceNode);
			}
		}
	}

	@Override
	public ComparatorKeyProvider<? super I> getInstanceKeyProvider() {
		return individualKeyProvider_;
	}

	@Override
	public InstanceNode<T, I> getInstanceNode(I elkObject) {
		return instanceNodeLookup.get(individualKeyProvider_.getKey(elkObject));
	}

	@Override
	public Set<? extends InstanceNode<T, I>> getInstanceNodes() {
		return node.instanceNodes;
	}

	@Override
	public int hashCode() {
		return InstanceTaxonomyHasher.hash(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(final Object obj) {

		if (!(obj instanceof Taxonomy<?>)) {
			return false;
		}

		try {
			return InstanceTaxonomyEqualator.equals(this, (Taxonomy<T>) obj);
		} catch (ClassCastException e) {
			return false;
		}
	}

	@Override
	public boolean addInstanceListener(final NodeStore.Listener<I> listener) {
		// No events are ever fired.
		return true;
	}

	@Override
	public boolean removeInstanceListener(
			final NodeStore.Listener<I> listener) {
		// No events are ever fired.
		return true;
	}

	@Override
	public boolean addInstanceListener(
			final InstanceTaxonomy.Listener<T, I> listener) {
		// No events are ever fired.
		return true;
	}

	@Override
	public boolean removeInstanceListener(
			final InstanceTaxonomy.Listener<T, I> listener) {
		// No events are ever fired.
		return true;
	}

}
