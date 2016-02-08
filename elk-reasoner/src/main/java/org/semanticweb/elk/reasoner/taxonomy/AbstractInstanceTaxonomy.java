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
import org.semanticweb.elk.reasoner.taxonomy.hashing.InstanceTaxonomyHasher;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TypeNode;

public abstract class AbstractInstanceTaxonomy<T extends ElkEntity, I extends ElkEntity>
		extends AbstractTaxonomy<T> implements InstanceTaxonomy<T, I> {
	
	@Override
	public int hashCode() {
		return InstanceTaxonomyHasher.hash(this);
	}
	
	@Override
	public boolean equals(final Object obj) {
		
		final boolean superEquals = super.equals(obj);
		
		// The super class compares type nodes
		if (!superEquals) {
			return false;
		}
		
		if (!(obj instanceof InstanceTaxonomy<?, ?>)) {
			// If obj is just a Taxonomy, compare this only as a Taxonomy (ignore instances).
			// However, super.equals(Object) checks whether obj is a Taxonomy, so no need to repeat.
			return superEquals;
		}
		try {
			@SuppressWarnings("unchecked")
			final InstanceTaxonomy<T, I> otherTaxonomy = (InstanceTaxonomy<T, I>) obj;
			
			/* 
			 * Each instance node must have the same sets of members and types,
			 * each type node must have the same set of instances.
			 */
			
			final Set<? extends InstanceNode<T, I>> thisInstanceNodes = getInstanceNodes();
			final Set<? extends InstanceNode<T, I>> otherInstanceNodes =
					otherTaxonomy.getInstanceNodes();
			
			if (thisInstanceNodes.size() != otherInstanceNodes.size()) {
				return false;
			}
			for (final InstanceNode<T, I> thisInstanceNode : thisInstanceNodes) {
				
				final I thisMember = thisInstanceNode.getCanonicalMember();
				final InstanceNode<T, I> otherInstanceNode =
						otherTaxonomy.getInstanceNode(thisMember);
				if (otherInstanceNode == null) {
					return false;
				}
				
				// Members
				if (thisInstanceNode.size() != otherInstanceNode.size()) {
					return false;
				}
				for (final I member : thisInstanceNode) {
					if (!otherInstanceNode.contains(member)) {
						return false;
					}
				}
				
				// Types
				final Set<? extends TypeNode<T, I>> thisTypes =
						thisInstanceNode.getDirectTypeNodes();
				final Set<? extends TypeNode<T, I>> otherTypes =
						otherInstanceNode.getDirectTypeNodes();
				if (thisTypes.size() != otherTypes.size()) {
					return false;
				}
				for (final TypeNode<T, I> thisType : thisTypes) {
					// While all nodes must be the same, it is sufficient to compare canonical members.
					final TypeNode<T, I> otherType =
							otherTaxonomy.getTypeNode(thisType.getCanonicalMember());
					/* 
					 * otherType is a node from otherTaxonomy (or null), so contains(Object) on
					 * a node set from otherTaxonomy should work for it as expected.
					 */
					if (!otherTypes.contains(otherType)) {
						return false;
					}
				}
				
			}
			
			// Instances
			for (final TypeNode<T, I> thisTypeNode : getTypeNodes()) {
				
				final T thisMember = thisTypeNode.getCanonicalMember();
				final TypeNode<T, I> otherTypeNode = otherTaxonomy.getTypeNode(thisMember);
				
				final Set<? extends InstanceNode<T, I>> thisInstances =
						thisTypeNode.getDirectInstanceNodes();
				final Set<? extends InstanceNode<T, I>> otherInstances =
						otherTypeNode.getDirectInstanceNodes();
				if (thisInstances.size() != otherInstances.size()) {
					return false;
				}
				for (final InstanceNode<T, I> thisInstance : thisInstances) {
					// While all nodes must be the same, it is sufficient to compare canonical members.
					final InstanceNode<T, I> otherInstance =
							otherTaxonomy.getInstanceNode(thisInstance.getCanonicalMember());
					/* 
					 * otherType is a node from otherTaxonomy (or null), so contains(Object) on
					 * a node set from otherTaxonomy should work for it as expected.
					 */
					if (!otherInstances.contains(otherInstance)) {
						return false;
					}
				}
				
			}
			
		} catch (ClassCastException e) {
			// The other taxonomy contains members of different type than this one.
			return false;
		}
		
		return true;
	}

}
