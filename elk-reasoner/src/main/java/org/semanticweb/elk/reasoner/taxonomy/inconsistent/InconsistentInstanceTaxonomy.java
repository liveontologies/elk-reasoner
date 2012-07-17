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
package org.semanticweb.elk.reasoner.taxonomy.inconsistent;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.TypeNode;
import org.semanticweb.elk.util.collections.ArraySet;

/**
 * @author Frantisek Simancik
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of objects in the nodes of this taxonomy
 * @param <I>
 *            the type of instances of nodes of this taxonomy
 */
public class InconsistentInstanceTaxonomy<T extends ElkObject, I extends ElkObject>
		implements InstanceTaxonomy<T, I> {

	protected final Bottom bottom;

	public InconsistentInstanceTaxonomy(T top, T bot) {
		bottom = new Bottom(top, bot);
	}

	@Override
	public TypeNode<T, I> getTopNode() {
		return bottom;
	}

	@Override
	public TypeNode<T, I> getBottomNode() {
		return bottom;
	}

	@Override
	public TypeNode<T, I> getTypeNode(T elkObject) {
		return bottom.members.contains(elkObject) ? bottom : null;
	}

	@Override
	public Set<? extends TypeNode<T, I>> getTypeNodes() {
		return Collections.singleton(bottom);
	}

	@Override
	public InstanceNode<T, I> getInstanceNode(I elkObject) {
		return null;
	}

	@Override
	public Set<? extends InstanceNode<T, I>> getInstanceNodes() {
		return Collections.emptySet();
	}

	@Override
	public TaxonomyNode<T> getNode(T elkObject) {
		return getTypeNode(elkObject);
	}

	@Override
	public Set<? extends TaxonomyNode<T>> getNodes() {
		return getTypeNodes();
	}

	class Bottom implements TypeNode<T, I> {

		final ArraySet<T> members;

		@SuppressWarnings("unchecked")
		Bottom(T top, T bot) {
			members = new ArraySet<T>(Arrays.asList(bot, top));
		}

		@Override
		public Set<T> getMembers() {
			return Collections.unmodifiableSet(members);
		}

		@Override
		public T getCanonicalMember() {
			return members.get(0);
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
