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
import org.semanticweb.elk.reasoner.taxonomy.FreshInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;

public class InstanceTaxonomyEntailment<M extends ElkEntity, I extends ElkEntity, T extends InstanceTaxonomy<M, I>, L extends InstanceTaxonomyEntailment.Listener<M, I>>
		extends TaxonomyEntailment<M, T, L> {

	public InstanceTaxonomyEntailment(T taxonomy) {
		super(taxonomy);
	}

	@Override
	public boolean containsEntitiesAndEntailmentsOf(T other) {
		if (!super.containsEntitiesAndEntailmentsOf(other)) {
			return false;
		}
		for (InstanceNode<M, I> otherNode : other.getInstanceNodes()) {
			I canonical = otherNode.getCanonicalMember();
			InstanceNode<M, I> node = getTaxonomy().getInstanceNode(canonical);
			if (node == null) {
				return false;
			}
			InstanceNodeEntailment<M, I, InstanceNode<M, I>> nodeEntailment = getInstanceNodeEntailment(
					node);
			if (!nodeEntailment.containsAllMembersOf(otherNode)) {
				return false;
			}
			if (!nodeEntailment.containsAllDirectTypesOf(otherNode)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void reportMissingEntitiesAndEntailmentsOf(T other, L listener) {
		super.reportMissingEntitiesAndEntailmentsOf(other, listener);
		for (InstanceNode<M, I> otherNode : other.getInstanceNodes()) {
			I canonical = otherNode.getCanonicalMember();
			InstanceNode<M, I> node = getTaxonomy().getInstanceNode(canonical);
			if (node == null) {
				if (otherNode.size() == 1 && otherNode.getDirectTypeNodes()
						.contains(other.getTopNode())) {
					// no non-trivial entailments for the instance
					listener.reportMissingInstance(canonical);
					continue;
				}
				node = new FreshInstanceNode<M, I>(canonical, other);
			}
			InstanceNodeEntailment<M, I, InstanceNode<M, I>> nodeEntailment = getInstanceNodeEntailment(
					node);
			InstanceNodeEntailment.Listener<M, I> nodeEntailmentListener = new MissingInstanceNodeEntailmentListenerAdapter<>(
					node, listener);
			nodeEntailment.reportMissingMembersOf(otherNode,
					nodeEntailmentListener);
			nodeEntailment.reportMissingDirectTypesOf(otherNode,
					nodeEntailmentListener);
		}
	}

	InstanceNodeEntailment<M, I, InstanceNode<M, I>> getInstanceNodeEntailment(
			InstanceNode<M, I> node) {
		return new InstanceNodeEntailment<M, I, InstanceNode<M, I>>(node);
	}

	public interface Listener<M extends ElkEntity, I extends ElkEntity>
			extends TaxonomyEntailment.Listener<M> {

		public void reportMissingInstance(I instance);

		public void reportMissingAssertion(I instance, M type);

		public void reportMissingSameInstances(I first, I second);

	}

}
