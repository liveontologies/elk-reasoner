package org.semanticweb.elk.reasoner.taxonomy;

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

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.model.BottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.ComparatorKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTaxonomy;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.Operations.Condition;
import org.semanticweb.elk.util.hashing.HashGenerator;

public class BottomGenericTaxonomyNode<T extends ElkEntity, Tax extends UpdateableTaxonomy<T>>
		implements BottomTaxonomyNode<T> {

	protected final Tax taxonomy_;

	private final T bottomMember_;

	/** thread safe set of unsatisfiable classes */
	private final ConcurrentMap<Object, T> unsatisfiableClasses_;
	
	/** counts the number of nodes which have non-bottom sub-classes */
	private final AtomicInteger countNodesWithSubClasses;
	
	public BottomGenericTaxonomyNode(final Tax taxonomy, final T bottomMember) {
		this.taxonomy_ = taxonomy;
		this.bottomMember_ = bottomMember;
		this.unsatisfiableClasses_ = new ConcurrentHashMap<Object, T>();
		this.countNodesWithSubClasses = new AtomicInteger(0);
		unsatisfiableClasses_.put(
				getKeyProvider().getKey(bottomMember_), bottomMember_);
	}
	
	@Override
	public ComparatorKeyProvider<? super T> getKeyProvider() {
		return taxonomy_.getKeyProvider();
	}

	@Override
	public boolean contains(final T member) {
		return unsatisfiableClasses_
				.containsKey(getKeyProvider().getKey(member));
	}

	@Override
	public int size() {
		return unsatisfiableClasses_.size();
	}

	@Override
	public T getCanonicalMember() {
		return bottomMember_;
	}

	@Override
	public Iterator<T> iterator() {
		return unsatisfiableClasses_.values().iterator();
	}

	@Override
	public boolean add(final T member) {
		return unsatisfiableClasses_.put(
				getKeyProvider().getKey(member), member) == null;
	}

	@Override
	public boolean remove(final T member) {
		return unsatisfiableClasses_
				.remove(getKeyProvider().getKey(member)) != null;
	}

	@Override
	public void incrementCountOfNodesWithSubClasses() {
		countNodesWithSubClasses.incrementAndGet();
	}

	@Override
	public void decrementCountOfNodesWithSubClasses() {
		countNodesWithSubClasses.decrementAndGet();
	}

	@Override
	public int getCountOfNodesWithSubClasses() {
		return countNodesWithSubClasses.get();
	}

	@Override
	public Set<? extends TaxonomyNode<T>> getDirectSuperNodes() {
		final Set<? extends TaxonomyNode<T>> nonBottomNodes = taxonomy_.getNonBottomNodes();
		return Operations.filter(nonBottomNodes,
				new Condition<TaxonomyNode<T>>() {
					@Override
					public boolean holds(final TaxonomyNode<T> element) {
						return element.getDirectSubNodes()
								.contains(taxonomy_.getBottomNode());
					}
					/*
					 * the direct super nodes of the bottom node are all
					 * nodes except the nodes that have no non-bottom
					 * sub-classes and the bottom node
					 */
				}, nonBottomNodes.size()
						- countNodesWithSubClasses.get());
	}
	
	@Override
	public Set<? extends TaxonomyNode<T>> getAllSuperNodes() {
		return taxonomy_.getNonBottomNodes();
	}

	@Override
	public Set<? extends TaxonomyNode<T>> getDirectSubNodes() {
		return Collections.emptySet();
	}
	
	@Override
	public Set<? extends TaxonomyNode<T>> getAllSubNodes() {
		return Collections.emptySet();
	}

	@Override
	public Taxonomy<T> getTaxonomy() {
		return taxonomy_;
	}

	private final int hashCode_ = HashGenerator.generateNextHashCode();

	@Override
	public final int hashCode() {
		return hashCode_;
	}

}
