package org.semanticweb.elk.util.concurrent.collections;

/*
 * #%L
 * ELK Utilities for Concurrency
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

import java.util.concurrent.atomic.AtomicReference;

/**
 * A thread-safe implementation of stack based on the non-blocking Treiber's
 * Algorithm (Treiber, 1986). The implementation allows to check when the pushed
 * element is the first element in the stack. This stack does not allow storing
 * {@code null} values.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <E>
 *            the type of elements in the stack
 */
public class ActivationStack<E> {

	private final AtomicReference<Node<E>> top_ = new AtomicReference<Node<E>>();

	/**
	 * a special dummy node used to mark the end of the stack after it has been
	 * activated
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Node<?> dummyNode = new Node(null);

	/**
	 * Inserts the given element at the head of the stack. Returns {@code true}
	 * if this is the first element inserted after the stack has been cleared. A
	 * stack is cleared after it has been constructed or after {@link #pop()}
	 * returns {@code null}. Note that the stack might be empty in the sense
	 * that the next call to {@link #pop()} should return {@code null}, but not
	 * cleared. In this case the stack is cleared only after {@link #pop()}
	 * returns {@code null}.
	 * 
	 * @param element
	 * @return {@code true} if this is the first element inserted after the
	 *         stack has been cleared.
	 * @throws IllegalArgumentException
	 *             if null element is inserted
	 */
	@SuppressWarnings("unchecked")
	public boolean push(E element) {
		if (element == null)
			throw new IllegalArgumentException(
					"Elements in the stack cannot be null");
		Node<E> newHead = new Node<E>(element);
		Node<E> oldHead;
		for (;;) {
			oldHead = top_.get();
			if (oldHead == null)
				newHead.next = (Node<E>) dummyNode;
			else
				newHead.next = oldHead;
			if (top_.compareAndSet(oldHead, newHead)) {
				if (oldHead == null)
					return true;
				return false;
			}
		}
	}

	public E peek() {
		Node<E> head = top_.get();
		if (head == null)
			return null;
		return head.item;
	}

	/**
	 * Takes and removes the head element of the stack.
	 * 
	 * @return the head element in the stack or {@code null} if there are no
	 *         elements in the stack
	 */
	public E pop() {
		for (;;) {
			Node<E> oldHead = top_.get();
			Node<E> newHead;
			if (oldHead == null)
				return null;
			newHead = oldHead.next;
			if (top_.compareAndSet(oldHead, newHead))
				return oldHead.item;
		}
	}

	private static class Node<T> {
		public final T item;
		public Node<T> next;

		public Node(T item) {
			this.item = item;
		}
	}

}
