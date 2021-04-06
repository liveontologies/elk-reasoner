package org.semanticweb.elk.reasoner.indexing.model;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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
 * A {@link IndexedObjectCache} elements of which can be added, removed, and
 * retrieved modulo structural equality.
 * 
 * @author "Yevgeny Kazakov"
 */
public interface ModifiableIndexedObjectCache extends IndexedObjectCache {

	/**
	 * @return an {@link CachedIndexedSubObject.Filter} that produces
	 *         structurally equal {@link CachedIndexedSubObject}s contained in
	 *         this {@link IndexedObjectCache} or {@code null} if there is no
	 *         such an object
	 */
	CachedIndexedSubObject.Filter getResolver();
	
	/**
	 * Adds a given {@link CachedIndexedSubObject} to this
	 * {@link IndexedObjectCache}; this method should be used only if no
	 * {@link CachedIndexedSubObject} that is structurally equal to the given one
	 * occurs in this {@link IndexedObjectCache}
	 * 
	 * @param input
	 *            the {@link CachedIndexedSubObject} to be added
	 */
	void add(CachedIndexedSubObject input);

	/**
	 * Removes an object structurally equal to the given one from this
	 * {@link IndexedObjectCache}, if there is such an object
	 * 
	 * @param input
	 *            the {@link CachedIndexedSubObject} for which to remove the
	 *            structurally equal object
	 */
	void remove(CachedIndexedSubObject input);

}
