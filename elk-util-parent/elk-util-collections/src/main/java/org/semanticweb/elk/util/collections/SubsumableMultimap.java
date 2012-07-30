package org.semanticweb.elk.util.collections;
/*
 * #%L
 * ELK Utilities Collections
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

/**
 * A multimap whose keys are subsumable elements. The main operation is to find
 * all values associated with the keys subsumed by the given key.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <K>
 *            the type of the keys in this multimap
 * @param <V>
 *            the type of the values in this multimap
 */
public interface SubsumableMultimap<K extends Subsumable<K>, V> {

	/**
	 * Add a given value for a given key to this multimap. There can be several
	 * values added with the same key.
	 * 
	 * @param key
	 *            the key for which to add the value
	 * @param value
	 *            the value to be added for the given key
	 */
	public void add(K key, V value);

	public void remove(K key, V value);

	/**
	 * Return all values associated with the keys that subsume the given key
	 * 
	 * @param key
	 *            the key for which to find the values associated with the
	 *            subsumed keys
	 * 
	 * @return all values associated with the keys that subsume the given key
	 */
	public Iterable<V> getSubsumingValues(K key);

}
