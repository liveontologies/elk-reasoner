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
package org.semanticweb.elk.reasoner.taxonomy.impl;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

import org.semanticweb.elk.reasoner.taxonomy.model.ComparatorKeyProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple modifiable node. Implements methods for manipulation with this node.
 * 
 * @author Peter Skocovsky
 *
 * @param <T>
 *            The type of members of this node.
 */
public class SimpleUpdateableNode<T> extends SimpleNode<T>
		implements UpdateableNode<T> {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(SimpleUpdateableNode.class);
	/**
	 * <code>true</code> if the direct super-nodes of this node have been
	 * recomputed
	 */
	private final AtomicBoolean areAllParentsAssigned_ = new AtomicBoolean(
			false);

	/**
	 * Creates a node containing the specified members.
	 * 
	 * @param members
	 *            The members this node should contain.
	 * @param size
	 *            The number of the specified members.
	 * @param keyProvider
	 *            The key provider for the members.
	 */
	public SimpleUpdateableNode(final Iterable<? extends T> members,
			final int size,
			final ComparatorKeyProvider<? super T> keyProvider) {
		super(members, size, keyProvider);
	}

	/**
	 * Creates an empty node.
	 * 
	 * @param comparatorKeyProvider
	 *            The key provider for the members.
	 */
	public SimpleUpdateableNode(
			final ComparatorKeyProvider<T> comparatorKeyProvider) {
		super(comparatorKeyProvider);
	}

	@Override
	public boolean trySetAllParentsAssigned(boolean modified) {
		boolean result = areAllParentsAssigned_.compareAndSet(!modified,
				modified);
		if (result && LOGGER_.isTraceEnabled())
			LOGGER_.trace("node " + this + ": set "
					+ (modified ? "modified" : "not modifiled"));
		return result;
	}

	@Override
	public boolean areAllParentsAssigned() {
		return areAllParentsAssigned_.get();
	}

	@Override
	public void setMembers(final Iterable<? extends T> members) {
		members_.clear();
		for (final T elkClass : members) {
			members_.add(elkClass);
		}
		Collections.sort(this.members_, getKeyProvider().getComparator());
		LOGGER_.trace("updated members of {}", this);
	}

}
