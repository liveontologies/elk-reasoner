/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.saturation.markers;

import java.util.Set;

/**
 * Set of Entry<K> with the property that there can be at most one entry for
 * each key.
 * 
 * @author Frantisek Simancik
 * 
 */
public interface EntrySet<K, E extends Entry<K>> extends Set<E> {
	/**
	 * @param key
	 * @return the unique Entry with this key, or null if none
	 */
	E get(K key);

	/**
	 * @return a set view of all keys
	 */
	Set<K> keySet();

	/**
	 * @param obj
	 * @return true if an entry with this key occurs in the set
	 */
	boolean containsKey(Object obj);

	/**
	 * if the set already contains an element with key equal to
	 * entry.getEqual(), then that element is replaced by entry. Nothing happens
	 * when there is no such element.
	 * 
	 * @return true if successful
	 */
	boolean replace(E entry);
}