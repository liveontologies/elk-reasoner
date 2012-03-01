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
 * An interface for creating key entries from the key values.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the keys of the entries and of all linked entries
 */
public interface KeyEntryFactory<T> {

	/**
	 * Creates the key entry of the given type for the given key. It is
	 * important that the method returns a new object. Reuse of existing entries
	 * can result in incorrect behavior of hash sets where these entries are
	 * stored.
	 * 
	 * @param key
	 * @return
	 */
	KeyEntry<T, ? extends T> createEntry(T key);

}
