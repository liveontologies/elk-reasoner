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
package org.semanticweb.elk.reasoner.taxonomy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.impl.AbstractTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.ComparatorKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.NodeStore;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.Operations.FunctorEx;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * A taxonomy that is reverse of the original taxonomy, i.e., top node is the
 * original bottom node, super-nodes are the original sub-nodes, and so on.
 * 
 * @author Peter Skocovsky
 * 
 * @param <T>
 *            The type of members of the nodes in this taxonomy.
 */
public class ReverseTaxonomy<T extends ElkEntity> extends AbstractTaxonomy<T> {

	protected final Taxonomy<T> original_;

	protected final List<Taxonomy.Listener<T>> taxonomyListeners_ = new ArrayList<Taxonomy.Listener<T>>();

	public ReverseTaxonomy(final Taxonomy<T> original) {
		this.original_ = original;
	}

	@Override
	public ComparatorKeyProvider<? super T> getKeyProvider() {
		return original_.getKeyProvider();
	}

	@Override
	public TaxonomyNode<T> getNode(final T elkEntity) {
		return wrapNode_.apply(original_.getNode(elkEntity));
	}

	@Override
	public Set<? extends TaxonomyNode<T>> getNodes() {
		return Operations.map(original_.getNodes(), wrapNode_);
	}

	@Override
	public TaxonomyNode<T> getTopNode() {
		return wrapNode_.apply(original_.getBottomNode());
	}

	@Override
	public TaxonomyNode<T> getBottomNode() {
		return wrapNode_.apply(original_.getTopNode());
	}

	@Override
	public boolean addListener(final NodeStore.Listener<T> listener) {
		return original_.addListener(listener);
	}

	@Override
	public boolean removeListener(final NodeStore.Listener<T> listener) {
		return original_.removeListener(listener);
	}

	@Override
	public boolean addListener(final Taxonomy.Listener<T> listener) {
		final boolean wasEmpty = taxonomyListeners_.isEmpty();
		final boolean ret = taxonomyListeners_.add(listener);
		if (wasEmpty && ret) {
			if (!original_.addListener(reverseListener_)) {
				taxonomyListeners_.remove(listener);
				return false;
			}
		}
		return ret;
	}

	@Override
	public boolean removeListener(final Taxonomy.Listener<T> listener) {
		final boolean ret = taxonomyListeners_.remove(listener);
		if (taxonomyListeners_.isEmpty() && ret) {
			if (!original_.removeListener(reverseListener_)) {
				taxonomyListeners_.add(listener);
				return false;
			}
		}
		return ret;
	}

	private final FunctorEx<TaxonomyNode<T>, ReverseTaxonomyNode> wrapNode_ = new FunctorEx<TaxonomyNode<T>, ReverseTaxonomyNode>() {

		@Override
		public ReverseTaxonomyNode apply(final TaxonomyNode<T> node) {
			if (node == null) {
				return null;
			} else {
				return new ReverseTaxonomyNode(node);
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public TaxonomyNode<T> deapply(final Object element) {
			if (element == null) {
				return null;
			} else if (element instanceof ReverseTaxonomy.ReverseTaxonomyNode) {
				return ((ReverseTaxonomyNode) element).originalNode_;
			} else {
				return null;
			}
		}

	};

	protected class ReverseTaxonomyNode implements TaxonomyNode<T> {

		protected final TaxonomyNode<T> originalNode_;

		public ReverseTaxonomyNode(final TaxonomyNode<T> original) {
			this.originalNode_ = original;
		}

		@Override
		public ComparatorKeyProvider<? super T> getKeyProvider() {
			return originalNode_.getKeyProvider();
		}

		@Override
		public boolean contains(final T member) {
			return originalNode_.contains(member);
		}

		@Override
		public int size() {
			return originalNode_.size();
		}

		@Override
		public T getCanonicalMember() {
			return originalNode_.getCanonicalMember();
		}

		@Override
		public Iterator<T> iterator() {
			return originalNode_.iterator();
		}

		@Override
		public Taxonomy<T> getTaxonomy() {
			return original_;
		}

		@Override
		public Set<? extends TaxonomyNode<T>> getDirectSuperNodes() {
			return Operations.map(originalNode_.getDirectSubNodes(), wrapNode_);
		}

		@Override
		public Set<? extends TaxonomyNode<T>> getAllSuperNodes() {
			return Operations.map(originalNode_.getAllSubNodes(), wrapNode_);
		}

		@Override
		public Set<? extends TaxonomyNode<T>> getDirectSubNodes() {
			return Operations.map(originalNode_.getDirectSuperNodes(),
					wrapNode_);
		}

		@Override
		public Set<? extends TaxonomyNode<T>> getAllSubNodes() {
			return Operations.map(originalNode_.getAllSuperNodes(), wrapNode_);
		}

		@Override
		public int hashCode() {
			return HashGenerator.combinedHashCode(getClass().hashCode(),
					originalNode_.hashCode());
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}

			if (getClass() != obj.getClass()) {
				return false;
			}

			@SuppressWarnings("unchecked")
			final ReverseTaxonomyNode other = (ReverseTaxonomyNode) obj;
			return originalNode_ == null ? other.originalNode_ == null
					: originalNode_.equals(other.originalNode_);
		}

		@Override
		public String toString() {
			return originalNode_.toString();
		}

	}

	private final Taxonomy.Listener<T> reverseListener_ = new Taxonomy.Listener<T>() {

		@Override
		public void directSuperNodesAppeared(final TaxonomyNode<T> subNode) {
			for (final Taxonomy.Listener<T> listener : taxonomyListeners_) {
				listener.directSubNodesAppeared(subNode);
			}
		}

		@Override
		public void directSuperNodesDisappeared(final TaxonomyNode<T> subNode) {
			for (final Taxonomy.Listener<T> listener : taxonomyListeners_) {
				listener.directSubNodesDisappeared(subNode);
			}
		}

		@Override
		public void directSubNodesAppeared(final TaxonomyNode<T> superNode) {
			for (final Taxonomy.Listener<T> listener : taxonomyListeners_) {
				listener.directSuperNodesAppeared(superNode);
			}
		}

		@Override
		public void directSubNodesDisappeared(final TaxonomyNode<T> superNode) {
			for (final Taxonomy.Listener<T> listener : taxonomyListeners_) {
				listener.directSuperNodesDisappeared(superNode);
			}
		}

	};

}
