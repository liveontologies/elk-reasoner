/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.taxonomy.nodes;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.semanticweb.elk.util.collections.ArrayHashSet;

/**
 * A {@link Node} stores elements (called members) that can be accessed using
 * keys of a specified type.
 * 
 * @author Markus Kroetzsch
 * @author "Yevgeny Kazakov"
 * 
 * @param <K>
 *            the type of the keys for the node members
 * @param <M>
 *            the type of node members
 */
public interface Node<K, M> {

	/**
	 * @return the unmodifiable map from keys to members of this {@link Node}.
	 */
	public Map<K, M> getMembersLookup();

	/**
	 * Some useful common utilities for nodes.
	 * 
	 * @author Pavel Klinov
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	static class Helper {

		public interface SuccessorRelation<N> {
			Set<? extends N> get(N node);
		}

		public static <N> Set<N> close(Collection<? extends N> input,
				SuccessorRelation<N> succ) {
			if (input.isEmpty())
				return Collections.emptySet();
			Set<N> result = new ArrayHashSet<N>(input.size());
			Queue<N> todo = new LinkedList<N>();

			todo.addAll(input);

			while (!todo.isEmpty()) {
				N next = todo.poll();

				if (result.add(next)) {
					for (N nextSuccessor : succ.get(next)) {
						todo.add(nextSuccessor);
					}
				}
			}
			return Collections.unmodifiableSet(result);
		}

	}

}
