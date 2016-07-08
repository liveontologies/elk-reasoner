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
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.impl.SimpleNode;
import org.semanticweb.elk.reasoner.taxonomy.model.ComparatorKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;
import org.semanticweb.elk.util.collections.ArrayHashSet;

public class AnonymousTaxonomyNode<T extends ElkEntity> extends SimpleNode<T>
		implements TaxonomyNode<T> {

	protected final Set<TaxonomyNode<T>> directSuperNodes_;
	protected final Set<TaxonomyNode<T>> directSubNodes_;

	public AnonymousTaxonomyNode(final Iterable<? extends T> members,
			final int size,
			final ComparatorKeyProvider<? super T> keyProvider) {
		super(members, size, keyProvider);
		this.directSuperNodes_ = new ArrayHashSet<TaxonomyNode<T>>();
		this.directSubNodes_ = new ArrayHashSet<TaxonomyNode<T>>();
	}

	@Override
	public Taxonomy<T> getTaxonomy() {
		return null;
	}

	@Override
	public Set<? extends TaxonomyNode<T>> getDirectSuperNodes() {
		return Collections.unmodifiableSet(directSuperNodes_);
	}

	@Override
	public Set<? extends TaxonomyNode<T>> getAllSuperNodes() {
		final Set<TaxonomyNode<T>> result = new ArrayHashSet<TaxonomyNode<T>>();
		final Queue<TaxonomyNode<T>> todo = new LinkedList<TaxonomyNode<T>>();
		result.addAll(getDirectSuperNodes());
		todo.addAll(getDirectSuperNodes());

		while (!todo.isEmpty()) {
			final TaxonomyNode<T> next = todo.poll();

			for (final TaxonomyNode<T> succNode : next.getDirectSuperNodes()) {
				if (result.add(succNode)) {
					todo.add(succNode);
				}
			}
		}

		return Collections.unmodifiableSet(result);
	}

	@Override
	public Set<? extends TaxonomyNode<T>> getDirectSubNodes() {
		return directSubNodes_;
	}

	@Override
	public Set<? extends TaxonomyNode<T>> getAllSubNodes() {
		final Set<TaxonomyNode<T>> result = new ArrayHashSet<TaxonomyNode<T>>();
		final Queue<TaxonomyNode<T>> todo = new LinkedList<TaxonomyNode<T>>();
		result.addAll(getDirectSubNodes());
		todo.addAll(getDirectSubNodes());

		while (!todo.isEmpty()) {
			final TaxonomyNode<T> next = todo.poll();

			for (final TaxonomyNode<T> succNode : next.getDirectSubNodes()) {
				if (result.add(succNode)) {
					todo.add(succNode);
				}
			}
		}

		return Collections.unmodifiableSet(result);
	}

	public void addDirectSuperNode(final TaxonomyNode<T> superNode) {
		directSuperNodes_.add(superNode);
	}

	public void addDirectSubNode(final TaxonomyNode<T> subNode) {
		directSubNodes_.add(subNode);
	}

}
