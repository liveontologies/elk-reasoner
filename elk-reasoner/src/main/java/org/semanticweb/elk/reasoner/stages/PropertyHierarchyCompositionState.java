/*
 * #%L
 * ELK Reasoner
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
package org.semanticweb.elk.reasoner.stages;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.elk.reasoner.indexing.model.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.properties.SaturatedPropertyChain;

/**
 * Manages information about property hierarchy.
 * <p>
 * Currently only manages listeners to property saturation.
 * 
 * @author Peter Skocovsky
 */
public class PropertyHierarchyCompositionState {

	private final List<Listener> listeners_;

	public PropertyHierarchyCompositionState() {
		this.listeners_ = new ArrayList<Listener>();
	}

	/**
	 * Registers the given {@link PropertyHierarchyCompositionState.Listener}.
	 * 
	 * @param listener
	 *            The listener that should be registered.
	 * @return {@code true} if the operation was successful and {@code false}
	 *         otherwise
	 */
	public boolean addListener(final Listener listener) {
		return listeners_.add(listener);
	}

	/**
	 * Removes the given {@link PropertyHierarchyCompositionState.Listener}.
	 * 
	 * @param listener
	 *            The listener that should be removed.
	 * @return {@code true} if the operation was successful and {@code false}
	 *         otherwise
	 */
	public boolean removeListener(final Listener listener) {
		return listeners_.remove(listener);
	}

	private final Dispatcher dispatcher_ = new Dispatcher() {

		public void firePropertyBecameSaturated(
				final IndexedPropertyChain chain) {
			for (final Listener listener : listeners_) {
				listener.propertyBecameSaturated(chain);
			}
		}

		public void firePropertyBecameNotSaturated(
				final IndexedPropertyChain chain) {
			for (final Listener listener : listeners_) {
				listener.propertyBecameNotSaturated(chain);
			}
		}

	};

	/**
	 * @return dispatcher of events over property hierarchy and compositions.
	 *         Listeners registered to this
	 *         {@link PropertyHierarchyCompositionState} will be notified about
	 *         these events.
	 */
	Dispatcher getDispatcher() {
		return dispatcher_;
	}

	/**
	 * Listener to changes in derived property hierarchy and compositions.
	 * 
	 * @author Peter Skocovsky
	 */
	public static interface Listener {

		/**
		 * Called immediately after {@link SaturatedPropertyChain#isClear()
		 * chain.getSaturated().isClear()} changes from <code>true</code> to
		 * <code>false</code>.
		 * 
		 * @param chain
		 */
		void propertyBecameSaturated(IndexedPropertyChain chain);

		/**
		 * Called immediately after {@link SaturatedPropertyChain#isClear()
		 * chain.getSaturated().isClear()} changes from <code>false</code> to
		 * <code>true</code>.
		 * 
		 * @param chain
		 */
		void propertyBecameNotSaturated(IndexedPropertyChain chain);

	}

	/**
	 * Dispatcher of events over property hierarchy and compositions.
	 * 
	 * @author Peter Skocovsky
	 */
	public static interface Dispatcher {

		/**
		 * fires {@link Listener#propertyBecameSaturated(IndexedPropertyChain)}
		 * on the passer argument
		 * 
		 * @param chain
		 */
		void firePropertyBecameSaturated(IndexedPropertyChain chain);

		/**
		 * fires
		 * {@link Listener#propertyBecameNotSaturated(IndexedPropertyChain)} on
		 * the passer argument
		 * 
		 * @param chain
		 */
		void firePropertyBecameNotSaturated(IndexedPropertyChain chain);

		/**
		 * Dispatcher that does not fire any events.
		 */
		public static final Dispatcher DUMMY = new Dispatcher() {

			@Override
			public void firePropertyBecameSaturated(
					final IndexedPropertyChain chain) {
				// Empty.
			}

			@Override
			public void firePropertyBecameNotSaturated(
					final IndexedPropertyChain chain) {
				// Empty.
			}

		};

	}

}
