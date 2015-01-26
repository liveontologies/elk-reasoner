package org.semanticweb.elk.util.collections.chains;

/*
 * #%L
 * ELK Reasoner
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
 * A linked list of elements together with helper functions to find, create if
 * not found, and remove elements in the chain.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the types of the elements in the chain
 * @see Matcher
 */
public interface Chain<T extends ModifiableLink<T>> extends ModifiableLink<T> {

	/**
	 * Finds the first element in the chain that satisfies the provided
	 * {@link Matcher}. This function does not modify the chain. If the chain is
	 * modified during calling of this function, the behavior of the function is
	 * not specified.
	 * 
	 * @param matcher
	 *            the object describing an element to search for
	 * @return the object contained in the chain that satisfies the provided
	 *         {@link Matcher} or {@code null} if no such element is found
	 */
	public <S extends T> S find(Matcher<? super T, S> matcher);

	/**
	 * Finds an element in the chain satisfies the provided {@link Matcher}, or
	 * if no such element is found, creates a new element using the provided
	 * {@link ReferenceFactory} and inserts it into the chain. In the letter
	 * case, the chain is modified.
	 * 
	 * @param matcher
	 *            the object describing the element to search for
	 * @param factory
	 *            the factory for creating references
	 * @return the object that satisfies the provided {@link Matcher} if found
	 *         in the chain, or the newly created and inserted element object
	 *         otherwise
	 */
	public <S extends T> S getCreate(Matcher<? super T, S> matcher,
			ReferenceFactory<T, S> factory);

	/**
	 * Removes the first element in the chain that satisfies the provided
	 * {@link Matcher}. If such element is found, the chain is modified.
	 * 
	 * @param descriptor
	 *            the object describing the element to search for
	 * @return the removed element, if found, or {@code null} if not found
	 */
	public <S extends T> S remove(Matcher<? super T, S> descriptor);

}
