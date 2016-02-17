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
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TypeNode;

public class InstanceTaxonomyEqualator {
	
	public static <T extends ElkEntity, I extends ElkEntity> boolean equals(
			final Taxonomy<T> taxonomy1, final Taxonomy<T> taxonomy2) {
		
		// Check null and type nodes
		final boolean areTaxonomiesEqual =
				TaxonomyEqualator.equals(taxonomy1, taxonomy2);
		
		if (!areTaxonomiesEqual) {
			return false;
		}
		
		if (!(taxonomy1 instanceof InstanceTaxonomy<?, ?>)
				|| !(taxonomy2 instanceof InstanceTaxonomy<?, ?>)) {
			// If some of the arguments is just a Taxonomy, compare them only as
			// Taxonomies (ignore instances).
			return areTaxonomiesEqual;
		}
		try {
			@SuppressWarnings("unchecked")
			final InstanceTaxonomy<T, I> instanceTaxonomy1 =
					(InstanceTaxonomy<T, I>) taxonomy1;
			@SuppressWarnings("unchecked")
			final InstanceTaxonomy<T, I> instanceTaxonomy2 =
					(InstanceTaxonomy<T, I>) taxonomy2;
			
			/* 
			 * Each instance node must have the same sets of members and types,
			 * each type node must have the same set of instances.
			 */
			
			final Set<? extends InstanceNode<T, I>> instanceNodes1 =
					instanceTaxonomy1.getInstanceNodes();
			final Set<? extends InstanceNode<T, I>> instanceNodes2 =
					instanceTaxonomy2.getInstanceNodes();
			
			if (instanceNodes1.size() != instanceNodes2.size()) {
				return false;
			}
			for (final InstanceNode<T, I> instanceNode1 : instanceNodes1) {
				
				final I member1 = instanceNode1.getCanonicalMember();
				final InstanceNode<T, I> instanceNode2 =
						instanceTaxonomy2.getInstanceNode(member1);
				if (instanceNode2 == null) {
					return false;
				}
				
				// Members
				if (instanceNode1.size() != instanceNode2.size()) {
					return false;
				}
				for (final I member : instanceNode1) {
					if (!instanceNode2.contains(member)) {
						return false;
					}
				}
				
				// Types
				final Set<? extends TypeNode<T, I>> types1 =
						instanceNode1.getDirectTypeNodes();
				final Set<? extends TypeNode<T, I>> types2 =
						instanceNode2.getDirectTypeNodes();
				if (types1.size() != types2.size()) {
					return false;
				}
				for (final TypeNode<T, I> type1 : types1) {
					// While all nodes must be the same, it is sufficient to compare canonical members.
					final TypeNode<T, I> type2 =
							instanceTaxonomy2.getNode(
									type1.getCanonicalMember());
					/* 
					 * otherType is a node from otherTaxonomy (or null), so contains(Object) on
					 * a node set from otherTaxonomy should work for it as expected.
					 */
					if (!types2.contains(type2)) {
						return false;
					}
				}
				
			}
			
			// Instances
			for (final TypeNode<T, I> typeNode1 : instanceTaxonomy1.getNodes()) {
				
				final T member1 = typeNode1.getCanonicalMember();
				final TypeNode<T, I> typeNode2 = instanceTaxonomy2.getNode(member1);
				
				final Set<? extends InstanceNode<T, I>> instances1 =
						typeNode1.getDirectInstanceNodes();
				final Set<? extends InstanceNode<T, I>> instances2 =
						typeNode2.getDirectInstanceNodes();
				if (instances1.size() != instances2.size()) {
					return false;
				}
				for (final InstanceNode<T, I> instance1 : instances1) {
					// While all nodes must be the same, it is sufficient to compare canonical members.
					final InstanceNode<T, I> instance2 =
							instanceTaxonomy2.getInstanceNode(instance1.getCanonicalMember());
					/* 
					 * otherType is a node from otherTaxonomy (or null), so contains(Object) on
					 * a node set from otherTaxonomy should work for it as expected.
					 */
					if (!instances2.contains(instance2)) {
						return false;
					}
				}
				
			}
			
		} catch (ClassCastException e) {
			// Some taxonomy contains members of unexpected type.
			return false;
		}
		
		return true;
	}
	
}
