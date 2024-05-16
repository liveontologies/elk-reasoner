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
package org.semanticweb.elk.reasoner.taxonomy.model;

/**
 * Basic interface for representing sets of equivalent ElkEntity with one
 * canonical representative. The notion of equivalence depends on the
 * application but will usually be implied extensional equality.
 * <p>
 * The way members of each node are hashed and compared is controlled by
 * {@link ComparatorKeyProvider} that is returned by {@link #getKeyProvider()}.
 * Each time {@link Object#hashCode()} or {@link Object#equals(Object)} needs to
 * be computed for a member, it is computed for its key returned by the key
 * provider instead. If two members need to be compared, the comparator returned
 * by the key provider is used.
 * 
 * @author Markus Kroetzsch
 * @author "Yevgeny Kazakov"
 * @author Peter Skocovsky
 * 
 * @param <T>
 *            the type of members of the node
 */
public interface Node<T> extends Iterable<T> {

	/**
	 * Returns the {@link ComparatorKeyProvider} that provides a key for each
	 * member of this node. These keys are used to compute hash codes and to
	 * compare the members.
	 * 
	 * @return the {@link ComparatorKeyProvider} that provides a key for each
	 *         member of this node.
	 */
	public ComparatorKeyProvider<? super T> getKeyProvider();

	/**
	 * Returns <code>true</code> iff this node contains a member whose key is
	 * the same as the key of the provided <code>member</code>.
	 * 
	 * @param member
	 *            The member whose key is used for the search.
	 * @return <code>true</code> if this node contains a member whose key is the
	 *         same as the key of the provided <code>member</code>.
	 */
	public boolean contains(T member);

	/**
	 * Returns the number of members in this node.
	 * 
	 * @return the number of members in this node.
	 */
	public int size();

	/**
	 * Get one object to canonically represent the members in this Node.
	 * <p>
	 * It is guaranteed that as long as the set of members of this node did not
	 * change, this method returns always the same member.
	 * 
	 * @return canonical member
	 */
	public T getCanonicalMember();

}
