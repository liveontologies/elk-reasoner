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
	 * Adds a given {@link StructuralIndexedSubObject} to this
	 * {@link IndexedObjectCache}; this method should be used only if no
	 * {@link StructuralIndexedSubObject} that is structurally equal to the given
	 * one occurs in this {@link IndexedObjectCache}
	 * 
	 * @param input
	 *            the {@link StructuralIndexedSubObject} to be added
	 * 
	 */
	void add(StructuralIndexedSubObject<?> input);

	/**
	 * Removes an object structurally equal to the given one from this
	 * {@link IndexedObjectCache}, if there is such an object
	 * 
	 * @param input
	 *            the {@link StructuralIndexedSubObject} for which to remove the
	 *            structurally equal object
	 * 
	 */
	void remove(StructuralIndexedSubObject<?> input);

	/**
	 * Returns an object structurally equal to the given occurring in this
	 * {@link IndexedObjectCache}
	 * 
	 * @param <T>
	 *            the type of the objects to be resolved
	 * 
	 * @param input
	 *            the {@link StructuralIndexedSubObject} to be resolved
	 * @return an {@link IndexedSubObject} that is structurally equal to the
	 *         given one occurs in this {@link IndexedObjectCache}, if there is
	 *         one, or {@code null} if there is no such object
	 * 
	 */
	<T extends StructuralIndexedSubObject<T>> T resolve(T input);

}
