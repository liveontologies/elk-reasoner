/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.taxonomy.model;

import java.util.Comparator;

/**
 * A {@link KeyProvider} that provides also a {@link Comparator} for objects of
 * type <code>T</code>.
 * 
 * @author Peter Skocovsky
 *
 * @param <T>
 *            The type of the objects for which the comparator should be
 *            provided.
 */
public interface ComparatorKeyProvider<T> extends KeyProvider<T> {

	/**
	 * Returns the comparator that should be used for comparison of the objects
	 * of type <code>T</code>. This comparator <strong>must</strong> be
	 * consistent with {@link #getKey(Object) getKey(Object).equals(Object)}!
	 * 
	 * @return the comparator that should be used for comparison of the objects
	 *         of type <code>T</code>.
	 */
	Comparator<? super T> getComparator();

}
