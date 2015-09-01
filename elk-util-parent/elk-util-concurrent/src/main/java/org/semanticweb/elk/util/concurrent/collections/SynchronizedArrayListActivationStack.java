package org.semanticweb.elk.util.concurrent.collections;

/*
 * #%L
 * ELK Utilities for Concurrency
 * $Id:$
 * $HeadURL:$
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

import java.util.ArrayList;

/**
 * A simple implementation of {@link ActivationStack} using an {@link ArrayList}
 * for storing elements; addition and removal of elements are synchronized.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <E>
 *            the type of elements in the stack
 */
public class SynchronizedArrayListActivationStack<E> implements
		ActivationStack<E> {

	/**
	 * The elements stored in this {@link ActivationStack}
	 */
	private ArrayList<E> elements_ = null;

	@Override
	public synchronized boolean push(E element) {
		if (element == null)
			throw new IllegalArgumentException(
					"Elements in the stack cannot be null");
		boolean result = false;
		if (elements_ == null) {
			elements_ = new ArrayList<E>(16);
			result = true;
		}
		elements_.add(element);
		return result;
	}

	@Override
	public synchronized E pop() {
		for (;;) {
			if (elements_ == null)
				return null;
			if (elements_.isEmpty()) {
				elements_ = null;
				return null;
			}
			return elements_.remove(elements_.size() - 1);
		}
	}

	@Override
	public E peek() {
		if (elements_ == null)
			return null;
		return elements_.get(elements_.size() - 1);
	}

}
