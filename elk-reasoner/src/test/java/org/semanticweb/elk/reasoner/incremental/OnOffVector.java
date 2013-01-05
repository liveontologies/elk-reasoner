package org.semanticweb.elk.reasoner.incremental;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
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

import java.util.Iterator;
import java.util.Vector;

/**
 * A {@code Vector} whose elements can be turned "on" and "off". The status of
 * newly added elements is "on" and this status can be given the index of the
 * element. Removal and more advance operations are not supported.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 */
public class OnOffVector<T> extends Vector<T> {

	private static final long serialVersionUID = -7521093590463405223L;

	/**
	 * A {@link Vector} containing on-off values of the elements with the
	 * corresponding indices.
	 */
	private final Vector<Boolean> onOffValues_;

	public OnOffVector(int size) {
		super(size);
		this.onOffValues_ = new Vector<Boolean>(size);
	}

	@Override
	public boolean add(T element) {
		boolean result = super.add(element);
		if (result)
			onOffValues_.add(true);
		return result;
	}

	/**
	 * @param index
	 * @return {@code true} if the value of the element with the given index is
	 *         "on"
	 */
	public boolean isOn(int index) {
		return onOffValues_.get(index);
	}

	/**
	 * Sets the status of all elements to "on"
	 */
	public void setAllOn() {
		for (int i = 0; i < onOffValues_.size(); i++) {
			onOffValues_.set(i, true);
		}
	}

	/**
	 * Changes the on-off value of the element with the given index to the
	 * complementary one.
	 * 
	 * @param index
	 * @return {@code true} if the status was "on" before the change
	 */
	public boolean flipOnOff(int index) {
		boolean result = onOffValues_.get(index);
		onOffValues_.set(index, !result);
		return result;
	}

	/**
	 * @return the elements for which the flag is "on"
	 */
	public Iterable<T> getOnElements() {
		return new Iterable<T>() {

			public Iterator<T> iterator() {
				return new Iterator<T>() {

					int nextOnIndex = 0;

					{
						findNextOnIndex();
					}

					private void findNextOnIndex() {
						for (; nextOnIndex < onOffValues_.size(); nextOnIndex++) {
							if (onOffValues_.get(nextOnIndex))
								break;
						}
					}

					@Override
					public boolean hasNext() {
						return nextOnIndex < onOffValues_.size();
					}

					@Override
					public T next() {
						T result = get(nextOnIndex);
						nextOnIndex++;
						findNextOnIndex();
						return result;
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException(
								"Removal of elements not supported!");
					}

				};
			}
		};
	}

}
