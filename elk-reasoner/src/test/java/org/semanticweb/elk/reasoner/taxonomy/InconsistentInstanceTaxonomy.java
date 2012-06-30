/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
/**
 * 
 */
package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Collections;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class InconsistentInstanceTaxonomy<T extends ElkEntity, I extends ElkEntity> extends InconsistentTaxonomy<T> implements InstanceTaxonomy<T, I>{

	public InconsistentInstanceTaxonomy(T top, T bot) {
		super(top, bot);
	}	
	
	@Override
	public TypeNode<T, I> getTopNode() {
		return new Bottom(bottom.top, bottom.bot);
	}

	@Override
	public TypeNode<T, I> getBottomNode() {
		return new Bottom(bottom.top, bottom.bot);
	}


	@Override
	public TypeNode<T, I> getTypeNode(T elkObject) {
		return bottom.members.contains(elkObject) ? getBottomNode() : null;
	}

	@Override
	public Set<? extends TypeNode<T, I>> getTypeNodes() {
		return Collections.singleton(getBottomNode());
	}

	@Override
	public InstanceNode<T, I> getInstanceNode(
			I elkObject) {
		return null;
	}

	@Override
	public Set<? extends InstanceNode<T, I>> getInstanceNodes() {
		return Collections.emptySet();
	}

	class Bottom extends InconsistentTaxonomy<T>.Bottom implements TypeNode<T,I> {

		Bottom(T top, T bot) {
			super(top, bot);
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
		public Set<TypeNode<T, I>> getDirectSuperNodes() {
			return Collections.emptySet();
		}

		@Override
		public Set<TypeNode<T, I>> getAllSuperNodes() {
			return Collections.emptySet();
		}

		@Override
		public Set<TypeNode<T, I>> getDirectSubNodes() {
			return Collections.emptySet();
		}

		@Override
		public Set<TypeNode<T, I>> getAllSubNodes() {
			return Collections.emptySet();
		}

		@Override
		public InstanceTaxonomy<T, I> getTaxonomy() {
			return InconsistentInstanceTaxonomy.this;
		}
	}
}
