package org.semanticweb.elk.util.concurrent.collections;

/*
 * #%L
 * ELK Utilities for Concurrency
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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
 * A thread-safe stack that can additionally signal when it becomes empty and
 * non-empty. Elements of the stack should not be {@code null}.
 * 
 * @author "Yevgeny Kazakov"
 *
 * @param <E>
 *            the type of elements with which this stack works
 */
public interface ActivationStack<E> {

	/**
	 * Inserts the given element at the head of the stack. Returns {@code true}
	 * if this is the first element inserted after the stack has been cleared. A
	 * stack is clear in the beginning and is cleared every time {@link #pop()}
	 * returns {@code null}. Note that the stack might be empty in the sense
	 * that {@link #pop()} would return {@code null}, but not cleared.
	 * 
	 * @param element
	 *            the element to be inserted at the head of the stack
	 * @return {@code true} if this is the first element inserted after the
	 *         stack has been cleared.
	 * @throws IllegalArgumentException
	 *             if {@code null} element is inserted
	 */
	public boolean push(E element);

	/**
	 * Takes and removes the head element of the stack.
	 * 
	 * @return the head element in the stack or {@code null} if there are no
	 *         elements in the stack
	 */
	public E pop();

	/**
	 * Returns but does not remove the head element of the stack. This method
	 * does not modify the stack and calling this method does not have any
	 * effect on the results of the other methods.
	 * 
	 * @return the head element in the stack or {@code null} if there are no
	 *         elements in the stack
	 */
	public E peek();

}
