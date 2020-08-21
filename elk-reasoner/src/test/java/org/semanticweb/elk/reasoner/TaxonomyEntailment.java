package org.semanticweb.elk.reasoner;

/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2020 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.FreshTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;

public class TaxonomyEntailment<M extends ElkEntity, T extends Taxonomy<M>, L extends TaxonomyEntailment.Listener<M>> {

	private final T taxonomy_;

	public TaxonomyEntailment(T taxonomy) {
		this.taxonomy_ = taxonomy;
	}

	T getTaxonomy() {
		return taxonomy_;
	}

	public boolean containsEntitiesAndEntailmentsOf(T other) {
		for (final TaxonomyNode<M> otherNode : other.getNodes()) {
			M canonical = otherNode.getCanonicalMember();
			TaxonomyNode<M> node = getTaxonomy().getNode(canonical);
			if (node == null) {
				return false; // some entity is missing
			}
			TaxonomyNodeEntailment<M, TaxonomyNode<M>> nodeEntailment = getTaxonomyNodeEntailment(
					node);
			if (!nodeEntailment.containsAllMembersOf(otherNode)) {
				return false;
			}
			if (!getTaxonomy().getBottomNode().contains(canonical)
					&& !nodeEntailment.containsDirectSubsumersOf(otherNode)) {
				return false;
			}
		}
		return true;
	}

	public void reportMissingEntitiesAndEntailmentsOf(T other, L listener) {
		for (final TaxonomyNode<M> otherNode : other.getNodes()) {
			M canonical = otherNode.getCanonicalMember();
			TaxonomyNode<M> node = getTaxonomy().getNode(canonical);
			if (node == null) {
				if (otherNode.size() == 1 && otherNode.getDirectSuperNodes()
						.contains(other.getTopNode())) {
					// no non-trivial entailments for the instance
					listener.reportMissingEntity(canonical);
					continue;
				}
				node = new FreshTaxonomyNode<>(canonical, other);
			}
			TaxonomyNodeEntailment<M, TaxonomyNode<M>> nodeEntailment = getTaxonomyNodeEntailment(
					node);
			TaxonomyNodeEntailment.Listener<M> nodeEntailmentListener = new MissingTaxonomyNodeEntailmentListenerAdapter<>(
					node, listener);
			nodeEntailment.reportMissingMembersOf(otherNode,
					nodeEntailmentListener);
			if (!getTaxonomy().getBottomNode().contains(canonical)) {
				nodeEntailment.reportMissingDirectSubsumersOf(otherNode,
						nodeEntailmentListener);
			}
		}
	}

	TaxonomyNodeEntailment<M, TaxonomyNode<M>> getTaxonomyNodeEntailment(
			TaxonomyNode<M> node) {
		return new TaxonomyNodeEntailment<M, TaxonomyNode<M>>(node);
	}

	public interface Listener<M extends ElkEntity> {

		public void reportMissingEntity(M entity);

		public void reportMissingEquivalence(M first, M second);

		public void reportMissingSubsumption(M sub, M sup);

	}

}
