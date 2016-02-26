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
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.model.BottomTypeNode;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.TypeNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableInstanceTaxonomy;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.Operations.Condition;

public class BottomGenericTypeNode<T extends ElkEntity, I extends ElkEntity, Tax extends UpdateableInstanceTaxonomy<T, I>>
		extends BottomGenericTaxonomyNode<T, Tax> implements BottomTypeNode<T, I> {

	public BottomGenericTypeNode(final Tax taxonomy,
			final T bottomMember) {
		super(taxonomy, bottomMember);
	}

	@Override
	public Set<? extends InstanceNode<T, I>> getDirectInstanceNodes() {
		return Collections.emptySet();
	}

	@Override
	public Set<? extends InstanceNode<T, I>> getAllInstanceNodes() {
		return Collections.emptySet();
	}

	@Override
	public Set<? extends TypeNode<T, I>> getDirectSuperNodes() {
		final Set<? extends TypeNode<T, I>> nonBottomNodes = taxonomy_.getNonBottomNodes();
		return Operations.filter(nonBottomNodes,
				new Condition<TypeNode<T, I>>() {
					@Override
					public boolean holds(final TypeNode<T, I> element) {
						return element.getDirectSubNodes()
								.contains(taxonomy_.getBottomNode());
					}
					/*
					 * the direct super nodes of the bottom node are all
					 * nodes except the nodes that have no non-bottom
					 * sub-classes and the bottom node
					 */
				}, nonBottomNodes.size()
						- getCountOfNodesWithSubClasses());
	}
	
	@Override
	public Set<? extends TypeNode<T, I>> getAllSuperNodes() {
		return taxonomy_.getNonBottomNodes();
	}

	@Override
	public Set<? extends TypeNode<T, I>> getDirectSubNodes() {
		return Collections.emptySet();
	}
	
	@Override
	public Set<? extends TypeNode<T, I>> getAllSubNodes() {
		return Collections.emptySet();
	}

}
