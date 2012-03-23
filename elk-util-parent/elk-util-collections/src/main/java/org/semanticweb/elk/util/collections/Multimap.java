/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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
package org.semanticweb.elk.util.collections;

import java.util.Collection;
import java.util.Set;

/**
 * Multimap from Keys to Collection of Values
 * 
 * @author Frantisek Simancik
 * @author Yevgeny Kazakov
 * 
 * @param <Key>import java.util.Set;
 * @param <Value>
 */

// TODO use Multimap from google.common instead

public interface Multimap<Key, Value> {

	/**
	 * Tests if the key-value pair occurs in this multimap
	 * 
	 * @param key
	 *            the key of the pair
	 * @param value
	 *            the value of the pair
	 * @return <tt>true</tt> if this multimap contains the pair, <tt>false</tt>
	 *         otherwise
	 */
	boolean contains(Object key, Object value);

	/**
	 * Stores the key-value pair in this multimap
	 * 
	 * @param key
	 *            the key of the pair
	 * @param value
	 *            the value of the pair
	 * @return <tt>true</tt> if the multimap has changed as a result of the
	 *         operation, <tt>fase</tt> otherwise
	 */
	boolean add(Key key, Value value);

	/**
	 * Removes the key-value pair from this multimap
	 * 
	 * @param key
	 *            the key of the pair
	 * @param value
	 *            the value of the pair
	 * @return <tt>true</tt> if the multimap has changed as a result of the
	 *         operation, <tt>fase</tt> otherwise
	 */
	boolean remove(Object key, Object value);

	/**
	 * Returns the collection of values associated with the given key in this
	 * multimap
	 * 
	 * @param key
	 *            the key for which to retrieve the values
	 * @return the collection of values associated with the given key in this
	 *         multimap
	 */
	Collection<Value> get(Object key);

	/**
	 * Checks if this multimap is empty
	 * 
	 * @return <tt>true</tt> if this multimap is empty, <tt>false</tt> otherwise
	 */
	boolean isEmpty();

	/**
	 * Returns the set of the keys contained in the key-value pairs of this
	 * multimap
	 * 
	 * @return the set of the keys contained in the key-value pairs of this
	 *         multimap
	 */
	Set<Key> keySet();

	/**
	 * Removes all key-value pairs of this multimap
	 */
	void clear();
}
