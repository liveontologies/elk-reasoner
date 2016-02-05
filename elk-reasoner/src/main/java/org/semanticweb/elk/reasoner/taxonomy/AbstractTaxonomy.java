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

import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.hashing.TaxonomyHasher;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;

public abstract class AbstractTaxonomy<T extends ElkEntity> implements Taxonomy<T> {
	
	@Override
	public int hashCode() {
		return TaxonomyHasher.hash(this);
	}
	
	@Override
	public boolean equals(final Object obj) {
		
		if (obj == null) {
			return false;
		}
		
		if (!(obj instanceof Taxonomy<?>)) {
			return false;
		}
		try {
//			final Taxonomy<?> otherTaxonomy = (Taxonomy<?>) obj;// TODO .: try this !!!
			final Taxonomy<T> otherTaxonomy = (Taxonomy<T>) obj;
			
			// Each node must have the same sets of members, parents and children
			
			final Set<? extends TaxonomyNode<T>> thisNodes = getNodes();
//			final Set<? extends TaxonomyNode<?>> otherNodes = otherTaxonomy.getNodes();
			final Set<? extends TaxonomyNode<T>> otherNodes = otherTaxonomy.getNodes();
			
			if (thisNodes.size() != otherNodes.size()) {
				return false;
			}
			for (final TaxonomyNode<T> thisNode : thisNodes) {
				
				final T thisMember = thisNode.getCanonicalMember();
				final TaxonomyNode<T> otherNode = otherTaxonomy.getNode(thisMember);
				if (otherNode == null) {
					return false;
				}
				
				// Members
				if (thisNode.size() != otherNode.size()) {
					return false;
				}
				for (final T member : thisNode) {
					if (!otherNode.contains(member)) {
						return false;
					}
				}
				
				// Parents
				final Set<? extends TaxonomyNode<T>> thisParents = thisNode.getDirectSuperNodes();
				final Set<? extends TaxonomyNode<T>> otherParents = otherNode.getDirectSuperNodes();
				if (thisParents.size() != otherParents.size()) {
					return false;
				}
				for (final TaxonomyNode<T> thisParent : thisParents) {
					// While all nodes must be the same, it is sufficient to compare canonical members.
					final TaxonomyNode<T> otherParent =
							otherTaxonomy.getNode(thisParent.getCanonicalMember());
					/* 
					 * otherParent is a node from otherTaxonomy (or null), so contains(Object) on
					 * a node set from otherTaxonomy should work for it as expected.
					 */
					if (!otherParents.contains(otherParent)) {
						return false;
					}
				}
				
				// Children
				final Set<? extends TaxonomyNode<T>> thisChildren = thisNode.getDirectSubNodes();
				final Set<? extends TaxonomyNode<T>> otherChildren = otherNode.getDirectSubNodes();
				if (thisChildren.size() != otherChildren.size()) {
					return false;
				}
				for (final TaxonomyNode<T> thisChild : thisChildren) {
					// While all nodes must be the same, it is sufficient to compare canonical members.
					final TaxonomyNode<T> otherChild =
							otherTaxonomy.getNode(thisChild.getCanonicalMember());
					/* 
					 * otherParent is a node from otherTaxonomy (or null), so contains(Object) on
					 * a node set from otherTaxonomy should work for it as expected.
					 */
					if (!otherChildren.contains(otherChild)) {
						return false;
					}
				}
				
			}
		} catch (ClassCastException e) {
			// Some contains() received an argument of a wrong type.
			return false;
		} catch (NullPointerException e) {
			// Some set does not support null elements.
			return false;
		}
		
		return true;
	}
	
}
