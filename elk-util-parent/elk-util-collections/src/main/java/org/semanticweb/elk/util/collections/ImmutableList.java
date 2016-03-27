package org.semanticweb.elk.util.collections;

/*
 * #%L
 * ELK Utilities Collections
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import java.util.Iterator;

/**
 * A simple implementation of an {@link FList}.
 * 
 * @author "Yevgeny Kazakov"
 *
 * @param <E>
 */
public class ImmutableList<E> implements FList<E> {

	private final E head_;

	private final FList<E> tail_;

	public ImmutableList(E head, FList<E> tail) {
		if (head == null)
			throw new IllegalArgumentException("The head cannot be null!");
		this.head_ = head;
		this.tail_ = tail;
	}

	@Override
	public E getHead() {
		return head_;
	}

	@Override
	public FList<E> getTail() {
		return tail_;
	}

	@Override
	public Iterator<E> iterator() {
		return new FListIterator<E>(this);
	}
}
