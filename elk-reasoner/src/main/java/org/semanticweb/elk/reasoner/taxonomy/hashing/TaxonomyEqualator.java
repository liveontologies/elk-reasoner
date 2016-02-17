package org.semanticweb.elk.reasoner.taxonomy.hashing;

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

import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;

public class TaxonomyEqualator {
	
	public static <T extends ElkEntity> boolean equals(
			final Taxonomy<T> taxonomy1, final Taxonomy<T> taxonomy2) {
		
		if (taxonomy1 == null) {
			return taxonomy2 == null;
		}
		if (taxonomy2 == null) {
			return false;
		}
		
		// Each node must have the same sets of members, parents and children
		
		final Set<? extends TaxonomyNode<T>> nodes1 = taxonomy1.getNodes();
		final Set<? extends TaxonomyNode<T>> nodes2 = taxonomy2.getNodes();
		
		if (nodes1.size() != nodes2.size()) {
			return false;
		}
		for (final TaxonomyNode<T> node1 : nodes1) {
			
			final T thisMember = node1.getCanonicalMember();
			final TaxonomyNode<T> node2 = taxonomy2.getNode(thisMember);
			if (node2 == null) {
				return false;
			}
			
			// Members
			if (node1.size() != node2.size()) {
				return false;
			}
			for (final T member : node1) {
				if (!node2.contains(member)) {
					return false;
				}
			}
			
			// Parents
			final Set<? extends TaxonomyNode<T>> parents1 = node1.getDirectSuperNodes();
			final Set<? extends TaxonomyNode<T>> parents2 = node2.getDirectSuperNodes();
			if (parents1.size() != parents2.size()) {
				return false;
			}
			for (final TaxonomyNode<T> parent1 : parents1) {
				// While all nodes must be the same, it is sufficient to compare canonical members.
				final TaxonomyNode<T> parent2 =
						taxonomy2.getNode(parent1.getCanonicalMember());
				/* 
				 * otherParent is a node from taxonomy2 (or null), so contains(Object) on
				 * a node set from taxonomy2 should work for it as expected.
				 */
				if (!parents2.contains(parent2)) {
					return false;
				}
			}
			
			// Children
			final Set<? extends TaxonomyNode<T>> children1 = node1.getDirectSubNodes();
			final Set<? extends TaxonomyNode<T>> children2 = node2.getDirectSubNodes();
			if (children1.size() != children2.size()) {
				return false;
			}
			for (final TaxonomyNode<T> child1 : children1) {
				// While all nodes must be the same, it is sufficient to compare canonical members.
				final TaxonomyNode<T> child2 =
						taxonomy2.getNode(child1.getCanonicalMember());
				/* 
				 * otherParent is a node from taxonomy2 (or null), so contains(Object) on
				 * a node set from taxonomy2 should work for it as expected.
				 */
				if (!children2.contains(child2)) {
					return false;
				}
			}
			
		}
		
		// No difference found ;-)
		return true;
	}
	
}
