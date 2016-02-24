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

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.model.ComparatorKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.GenericTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableGenericTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTaxonomyNode;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.Operations.Condition;
import org.semanticweb.elk.util.hashing.HashGenerator;

public class BottomGenericTaxonomyNode<T extends ElkEntity, N extends GenericTaxonomyNode<T, N>>
		implements UpdateableGenericTaxonomyNode<T, N> {

	private final AbstractDistinctBottomTaxonomy<T, N> taxonomy_;
	
	public BottomGenericTaxonomyNode(final AbstractDistinctBottomTaxonomy<T, N> taxonomy) {
		this.taxonomy_ = taxonomy;
		taxonomy_.unsatisfiableClasses_.put(
				getKeyProvider().getKey(taxonomy_.bottomMember_),
				taxonomy_.bottomMember_);
	}
	
	@Override
	public ComparatorKeyProvider<? super T> getKeyProvider() {
		return taxonomy_.getKeyProvider();
	}

	@Override
	public boolean contains(final T member) {
		return taxonomy_.unsatisfiableClasses_
				.containsKey(getKeyProvider().getKey(member));
	}

	@Override
	public int size() {
		return taxonomy_.unsatisfiableClasses_.size();
	}

	@Override
	public T getCanonicalMember() {
		return taxonomy_.bottomMember_;
	}

	@Override
	public Iterator<T> iterator() {
		return taxonomy_.unsatisfiableClasses_.values().iterator();
	}

	@Override
	public Set<? extends N> getDirectSuperNodes() {
		final Set<? extends N> nonBottomNodes = taxonomy_.getNonBottomNodes();
		return Operations.filter(nonBottomNodes,
				new Condition<N>() {
					@Override
					public boolean holds(final N element) {
						return element.getDirectSubNodes()
								.contains(taxonomy_.getBottomNode());
					}
					/*
					 * the direct super nodes of the bottom node are all
					 * nodes except the nodes that have no non-bottom
					 * sub-classes and the bottom node
					 */
				}, nonBottomNodes.size()
						- taxonomy_.countNodesWithSubClasses.get());
	}

	@Override
	public Set<? extends N> getAllSuperNodes() {
		return taxonomy_.getNonBottomNodes();
	}

	@Override
	public Set<? extends N> getDirectSubNodes() {
		return Collections.emptySet();
	}

	@Override
	public Set<? extends N> getAllSubNodes() {
		return Collections.emptySet();
	}

	@Override
	public Taxonomy<T> getTaxonomy() {
		return taxonomy_;
	}

	// The following methods do nothing. TODO: is there a way to remove them ?!?
	
	@Override
	public boolean trySetAllParentsAssigned(final boolean allParentsAssigned) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean areAllParentsAssigned() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setMembers(final Iterable<? extends T> members) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addDirectSuperNode(final N superNode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addDirectSubNode(final N subNode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeDirectSubNode(final N subNode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeDirectSuperNode(final N superNode) {
		throw new UnsupportedOperationException();
	}

	private final int hashCode_ = HashGenerator.generateNextHashCode();

	@Override
	public final int hashCode() {
		return hashCode_;
	}

	public static class Projection<T extends ElkEntity>
			extends BottomGenericTaxonomyNode<T, UpdateableTaxonomyNode<T>>
			implements UpdateableTaxonomyNode<T> {

		public Projection(
				AbstractDistinctBottomTaxonomy<T, UpdateableTaxonomyNode<T>> taxonomy) {
			super(taxonomy);
		}
		
	}

}
