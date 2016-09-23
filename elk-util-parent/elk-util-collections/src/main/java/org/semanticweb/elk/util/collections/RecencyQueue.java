/*-
 * #%L
 * ELK Utilities Collections
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
package org.semanticweb.elk.util.collections;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Contains each element at most once. Each time an element is added, it is
 * first removed from this queue, if it is already in it, and then it is added
 * to the tail.
 * 
 * <p>
 * This implementation is <strong>not</strong> thread safe.
 * 
 * @author Peter Skocovsky
 *
 * @param <E>
 *            The type of an element.
 */
public class RecencyQueue<E> extends AbstractQueue<E> {

	/**
	 * Maps each element of this queue to its meta-data {@link Entry}.
	 */
	private final Map<E, Entry<E>> entries_;
	/**
	 * The head of the write list, which is a cyclic double-linked list ordered
	 * in the order of additions of the entries. The end of the list
	 * ({@link Entry#getPrevious() head_.getPrevious()}) is the most recently
	 * added and the beginning ({@link Entry#getNext() head_.getNext()}) is the
	 * least recently added. {@link #head_} is always in the list, but does not
	 * point to any element, so number of entries in this list is
	 * {@code 1+}{@link #size()}.
	 */
	private final Entry<E> head_;

	/**
	 * Constructor.
	 */
	public RecencyQueue() {
		this.entries_ = new ArrayHashMap<E, Entry<E>>();
		this.head_ = new AbstractEntry<E>() {
			@Override
			public E getElement() {
				return null;
			}
		};
		head_.setNext(head_);
		head_.setPrevious(head_);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The element is removed from the queue before it is added to the tail.
	 */
	@Override
	public boolean offer(final E element) {
		// If the element is already in, update its meta-data.
		Entry<E> entry = entries_.get(element);
		if (entry == null) {

			// New element. Add its entry.
			entry = newEntry(element);
			entries_.put(element, entry);

		} else {

			// Old element. Disconnect its entry.
			connect(entry.getPrevious(), entry.getNext());

		}

		// Add the entry to the beginning of the write list.
		connect(head_.getPrevious(), entry);
		connect(entry, head_);

		return true;
	}

	@Override
	public boolean remove(final Object element) {
		final Entry<E> entry = entries_.remove(element);
		if (entry == null) {
			// Nothing was removed.
			return false;
		}

		// Disconnect.
		connect(entry.getPrevious(), entry.getNext());
		nullify(entry);

		return true;
	}

	@Override
	public E poll() {
		final Entry<E> entry = head_.getNext();
		if (entry == head_) {
			return null;
		}

		entries_.remove(entry.getElement());

		// Disconnect.
		connect(entry.getPrevious(), entry.getNext());
		nullify(entry);

		return entry.getElement();
	}

	@Override
	public E peek() {
		final Entry<E> entry = head_.getNext();
		if (entry == head_) {
			return null;
		} else {
			return entry.getElement();
		}
	}

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {

			private Entry<E> next_ = head_.getNext();

			@Override
			public boolean hasNext() {
				return next_ != head_;
			}

			@Override
			public E next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				final E result = next_.getElement();
				next_ = next_.getNext();
				return result;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

		};
	}

	@Override
	public int size() {
		return entries_.size();
	}

	@Override
	public boolean contains(final Object element) {
		return entries_.containsKey(element);
	}

	/**
	 * Meta-data of an element.
	 * 
	 * @author Peter Skocovsky
	 *
	 * @param <E>
	 *            The type of an element.
	 */
	private static interface Entry<E> {

		/**
		 * @return The element whose meta-data this is.
		 */
		E getElement();

		/**
		 * @return The previous element in the write list.
		 */
		Entry<E> getPrevious();

		/**
		 * @param entry
		 *            The previous element in the write list.
		 * @see #getPrevious()
		 */
		void setPrevious(Entry<E> entry);

		/**
		 * @return The next element in the write list.
		 */
		Entry<E> getNext();

		/**
		 * @param entry
		 *            The next element in the write list.
		 * @see #getNext()
		 */
		void setNext(Entry<E> entry);

	}

	/**
	 * Implements the double-linked list behaviour.
	 * 
	 * @author Peter Skocovsky
	 *
	 * @param <E>
	 *            The type of an element.
	 */
	private static abstract class AbstractEntry<E> implements Entry<E> {

		private Entry<E> previous_ = null;
		private Entry<E> next_ = null;

		@Override
		public Entry<E> getPrevious() {
			return previous_;
		}

		@Override
		public void setPrevious(final Entry<E> entry) {
			previous_ = entry;
		}

		@Override
		public Entry<E> getNext() {
			return next_;
		}

		@Override
		public void setNext(Entry<E> entry) {
			next_ = entry;
		}

	}

	private static <E> Entry<E> newEntry(final E element) {
		return new AbstractEntry<E>() {
			@Override
			public E getElement() {
				return element;
			}
		};
	}

	private static <E> void connect(final Entry<E> previous,
			final Entry<E> next) {
		previous.setNext(next);
		next.setPrevious(previous);
	}

	private static <E> void nullify(final Entry<E> entry) {
		entry.setNext(null);
		entry.setPrevious(null);
	}

}
