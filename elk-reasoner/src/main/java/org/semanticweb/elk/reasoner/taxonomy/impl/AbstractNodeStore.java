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
package org.semanticweb.elk.reasoner.taxonomy.impl;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.elk.reasoner.taxonomy.model.NodeStore;

public abstract class AbstractNodeStore<T, N extends Node<T>>
		implements NodeStore<T, N> {

	/** The listeners notified about the changes. */
	protected final List<Listener<T>> listeners_;

	public AbstractNodeStore() {
		this.listeners_ = new ArrayList<Listener<T>>();
	}

	@Override
	public boolean addListener(final Listener<T> listener) {
		return listeners_.add(listener);
	}

	@Override
	public boolean removeListener(final Listener<T> listener) {
		return listeners_.remove(listener);
	}

	protected void fireMemberForNodeAppeared(final Node<T> node) {
		for (final Listener<T> listener : listeners_) {
			for (final T member : node) {
				listener.memberForNodeAppeared(member, node);
			}
		}
	}

	protected void fireMemberForNodeDisappeared(final Node<T> node) {
		for (final Listener<T> listener : listeners_) {
			for (final T member : node) {
				listener.memberForNodeDisappeared(member, node);
			}
		}
	}

}
