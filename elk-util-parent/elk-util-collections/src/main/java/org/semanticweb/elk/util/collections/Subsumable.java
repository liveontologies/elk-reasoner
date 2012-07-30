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
 * Objects that can be subsumed by other objects.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the subsumed objects
 */
public interface Subsumable<T> {

	/**
	 * @param o
	 *            the object to be tested on the subsumption
	 * @return {@code true} if this object is subsumed by the given object
	 */
	public boolean isSubsumedBy(T o);

}
