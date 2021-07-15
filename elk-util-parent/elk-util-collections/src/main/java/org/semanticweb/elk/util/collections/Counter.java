package org.semanticweb.elk.util.collections;

/*-
 * #%L
 * ELK Utilities Collections
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2021 Department of Computer Science, University of Oxford
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
import java.util.NoSuchElementException;

/**
 * An iterator that iterates over all integer numbers between 0 (included)
 * and the given limit (execluded)
 * 
 * @author Yevgeny Kazakov
 *
 */
public class Counter implements Iterator<Integer> {

	private final int limit_;

	private int value_;

	/**
	 * @param limit
	 *            the limit on the values to be iterated; must be a positive
	 *            integer
	 */
	public Counter(int limit) {
		if (limit <= 0) {
			throw new IllegalArgumentException(
					"The limit should be a positive integer!");
		}
		this.limit_ = limit;
	}

	/**
	 * Resets the value of the counter
	 * 
	 * @return {@code true} if the counter has changed and {@code false}
	 *         otherwise
	 */
	public boolean reset() {
		if (this.value_ > 0) {
			value_ = 0;
			return true;
		}
		// else already reseted
		return false;
	}

	@Override
	public boolean hasNext() {
		return value_ < limit_;
	}

	@Override
	public Integer next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		return value_++;
	}

}
