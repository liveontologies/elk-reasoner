package org.semanticweb.elk.util.collections;
/*
 * #%L
 * ELK Utilities Collections
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
 * Boolean conditions over some type.
 * 
 * @param <T>
 *            the type of elements which can be used with this condition
 */
public interface Condition<T> {
	/**
	 * Checks if the condition holds for an element
	 * 
	 * @param element
	 *            the element for which to check the condition
	 * @return {@code true} if the condition holds for the element and
	 *         otherwise {@code false}
	 */
	public boolean holds(T element);
}
