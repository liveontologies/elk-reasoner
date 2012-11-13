package org.semanticweb.elk.util.collections.chains;
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
 * Elements that can be inserted and removed from {@link Chain}s
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the types of the elements in the chain
 */
public interface Chainable<T extends Reference<T>> {

	/**
	 * Adds this element to the given {@link Chain}
	 * 
	 * @param ruleChain
	 * @return {@code true} if the input {@link Chain} has been modified
	 * 
	 */
	public boolean addTo(Chain<T> chain);

	/**
	 * Removes this element from the given {@link Chain}
	 * 
	 * @param ruleChain
	 * @return {@code true} if the input {@link Chain} has been modified
	 */
	public boolean removeFrom(Chain<T> chain);

}
