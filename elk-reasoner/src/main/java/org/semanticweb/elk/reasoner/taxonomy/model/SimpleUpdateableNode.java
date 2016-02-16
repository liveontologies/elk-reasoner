package org.semanticweb.elk.reasoner.taxonomy.model;

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
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleUpdateableNode<T> extends SimpleNode<T>
		implements UpdateableNode<T> {
	
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(SimpleUpdateableNode.class);
	/**
	 * <code>true</code> if the direct super-nodes of this node need to be
	 * recomputed
	 */
	private final AtomicBoolean modified_ = new AtomicBoolean(true);

	public SimpleUpdateableNode(final Iterable<T> members, final int size,
			final ComparatorKeyProvider<? super T> keyProvider) {
		super(members, size, keyProvider);
	}

	public SimpleUpdateableNode(
			final ComparatorKeyProvider<T> comparatorKeyProvider) {
		super(comparatorKeyProvider);
	}
	
	@Override
	public boolean trySetModified(boolean modified) {
		boolean result = modified_.compareAndSet(!modified, modified);
		if (result && LOGGER_.isTraceEnabled())
			LOGGER_.trace("node " + this + ": set "
					+ (modified ? "modified" : "not modifiled"));
		return result;
	}

	@Override
	public boolean isModified() {
		return modified_.get();
	}

	@Override
	public boolean add(final T member) {
		final int searchResult = Collections.binarySearch(members_, member,
				getKeyProvider().getComparator());
		if (searchResult >= 0) {
			return false;
		} else {
			final int index = -(searchResult + 1);
			members_.add(index, member);
			return true;
		}
	}
	
	@Override
	public boolean remove(final T member) {
		final int searchResult = Collections.binarySearch(members_, member,
				getKeyProvider().getComparator());
		if (searchResult >= 0) {
			final int index = searchResult;
			members_.remove(index);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void setMembers(final Iterable<T> members) {
		members_.clear();
		for (final T elkClass : members) {
			members_.add(elkClass);
		}
		Collections.sort(this.members_, getKeyProvider().getComparator());
		LOGGER_.trace("updated members of {}", this);
	}
	
}
