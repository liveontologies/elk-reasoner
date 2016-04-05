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

import java.util.Iterator;
import java.util.Map;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.model.ComparatorKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.elk.util.collections.ArrayHashMap;

/**
 * A base {@link Node} whose subclasses are supposed to be associated with no
 * (or minimal number) of other nodes.
 * 
 * @author Peter Skocovsky
 * 
 * @param <T>
 *            the type of objects stored in the nodes
 * 
 * @see SingletoneTaxonomy
 */
public abstract class OrphanNode<T extends ElkEntity> implements Node<T> {

	/**
	 * the members of the node
	 */
	private final Map<Object, T> members;
	/**
	 * the representative of the node; should be among the members
	 */
	private final T canonical;

	public OrphanNode(final Iterable<? extends T> members, final int size,
			final T canonical, ComparatorKeyProvider<? super T> keyProvider) {
		this.members = new ArrayHashMap<Object, T>(size);
		for (T member : members) {
			this.members.put(keyProvider.getKey(member), member);
		}
		this.canonical = canonical;
	}

	@Override
	public Iterator<T> iterator() {
		return members.values().iterator();
	}

	@Override
	public boolean contains(final T member) {
		return members.containsKey(getKeyProvider().getKey(member));
	}

	@Override
	public int size() {
		return members.size();
	}

	@Override
	public T getCanonicalMember() {
		return canonical;
	}

}
