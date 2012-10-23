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

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

import org.semanticweb.elk.util.concurrent.collections.Stack.Node;

public class Stack<T> extends AtomicReference<Node<T>> implements Iterable<T> {

	private static final long serialVersionUID = -708254096267819322L;

	public boolean add(T element) {
		Node<T> next = new Node<T>(element);
		for (;;) {
			Node<T> nextNext = this.get();
			next.set(nextNext);
			if (this.compareAndSet(nextNext, next)) {
				if (nextNext == null)
					return true;
				else
					return false;
			}
		}
	}

	static class Node<T> extends AtomicReference<Node<T>> {
		private static final long serialVersionUID = 2100303034764294121L;
		private final T data_;

		public Node(T data) {
			this.data_ = data;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Node<?> dummyNode = new Node(null);

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {

			@SuppressWarnings("unchecked")
			@Override
			public boolean hasNext() {
				for (;;) {
					Node<T> next = Stack.this.get();
					if (next != dummyNode)
						return true;
					if (Stack.this.compareAndSet((Node<T>) dummyNode, null))
						return false;
				}
			}

			@SuppressWarnings("unchecked")
			@Override
			public T next() {
				for (;;) {
					Node<T> next = Stack.this.get();
					if (next == null)
						throw new NoSuchElementException();
					Node<T> nextNext = next.get();
					if (nextNext == null)
						nextNext = (Node<T>) dummyNode;
					if (Stack.this.compareAndSet(next, nextNext)) {
						return next.data_;
					}
				}
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

		};
	}
}
