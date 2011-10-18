/*
 * #%L
 * ELK Utilities Collections
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
package org.semanticweb.elk.util.collections.entryset;

/**
 * An interface for entries with key values. An implementation of this interface
 * should redefine {@link equals()} and {@link hashCode()} methods to compare
 * the keys.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the keys for linked entries
 * 
 * @param <K>
 *            the type of the key of this entry
 * 
 */
public interface KeyEntry<T, K> extends Entry<KeyEntry<T, ? extends T>> {

	public K getKey();

}
