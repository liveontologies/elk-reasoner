package org.semanticweb.elk.util.collections;

/*
 * #%L
 * ELK Utilities Collections
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

/**
 * A helper interface indicating that the class can provide direct access to its
 * elements to optimize some operations, such as random access and iteration.
 * Typically used with collections of elements. Used internally, e.g., to
 * optimize {@link LazySetIntersection}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <E>
 */
interface DirectAccess<E> {

	/**
	 * @return the array storing the elements of this object; some elements in
	 *         the array could be {@link null}
	 */
	E[] getRawData();

}
