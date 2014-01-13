/**
 * 
 */
package org.semanticweb.elk.util.collections;
/*
 * #%L
 * ELK Utilities Collections
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import java.util.Collection;
import java.util.Set;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class MultimapOperations {
	
	private static final String IMMUTABLE_ERR_MSG = "Filtered multimap is immutable";
	
	private static <V> V immutableError() {
		throw new UnsupportedOperationException(IMMUTABLE_ERR_MSG);
	}

	public static <K, V> Multimap<K, V> union(final Multimap<K, V> first, final Multimap<K, V> second) {
		return new Multimap<K, V>() {

			@Override
			public boolean contains(K key, V value) {
				return first.contains(key, value) || second.contains(key, value);
			}

			@Override
			public boolean add(K key, V value) {
				return immutableError();
			}

			@Override
			public Collection<V> get(K key) {
				return new LazyCollectionUnion<V>(first.get(key), second.get(key));
			}

			@Override
			public boolean remove(Object key, Object value) {
				return immutableError();
			}

			@Override
			public boolean isEmpty() {
				return first.isEmpty() && second.isEmpty();
			}

			@Override
			public Set<K> keySet() {
				return new LazySetUnion<K>(first.keySet(), second.keySet());
			}

			@Override
			public void clear() {
				immutableError();
			}
			
		};
	}
	
	public static <K, V> Multimap<K, V> keyFilter(final Multimap<K, V> input, final Condition<? super K> condition) {
		return new Multimap<K, V>() {


			@Override
			public boolean contains(K key, V value) {
				return condition.holds(key) && input.contains(key, value);
			}

			@Override
			public boolean add(K key, V value) {
				return immutableError();
			}

			@Override
			public Collection<V> get(K key) {
				return condition.holds(key) ? input.get(key) : null;
			}

			@Override
			public boolean remove(Object key, Object value) {
				return immutableError();
			}

			@Override
			public boolean isEmpty() {
				return keySet().isEmpty();
			}

			@Override
			public Set<K> keySet() {
				return Operations.filter(input.keySet(), condition, input.keySet().size());
			}

			@Override
			public void clear() {
				immutableError();
			}
			
		};
	}
	
	public static <K, V> Multimap<K, V> valueFilter(final Multimap<K, V> input, final Condition<? super V> condition) {
		return new Multimap<K, V>() {


			@Override
			public boolean contains(K key, V value) {
				return condition.holds(value) && input.contains(key, value);
			}

			@Override
			public boolean add(K key, V value) {
				return immutableError();
			}

			@Override
			public Collection<V> get(K key) {
				Collection<V> values = input.get(key);
				
				return Operations.getCollection(Operations.filter(values, condition), values.size());
			}

			@Override
			public boolean remove(Object key, Object value) {
				return immutableError();
			}

			@Override
			public boolean isEmpty() {
				return keySet().isEmpty();
			}

			@Override
			public Set<K> keySet() {
				return Operations.filter(input.keySet(), new Condition<K>(){

					@Override
					public boolean holds(K key) {
						return Operations.exists(input.get(key), condition);
					}
					
				}, input.keySet().size());
			}

			@Override
			public void clear() {
				immutableError();
			}
			
		};
	}

}
