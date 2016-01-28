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

import org.semanticweb.elk.owl.interfaces.ElkEntity;

/**
 * Basic interface for representing sets of equivalent ElkEntity with one
 * canonical representative. The notion of equivalence depends on the
 * application but will usually be implied extensional equality.
 * <p>
 * TODO: The entities are compared by their IRI-s!
 * 
 * @author Markus Kroetzsch
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of members of the node
 */
public interface Node<T extends ElkEntity> extends Iterable<T> {

	/**
	 * Returns <code>true</code> if this node contains a member
	 * with the same IRI as the specified member.
	 * 
	 * @param member The member whose IRI is used for the search.
	 * @return <code>true</code> if this node contains a member
	 * 		with the same IRI as the specified member.
	 */
	public boolean contains(T member);

	/**
	 * Returns the number of members in this node.
	 * 
	 * @return the number of members in this node.
	 */
	public int size();
	
	/**
	 * Get one object to canonically represent the classes in this Node.
	 * 
	 * It is guaranteed that the least object is the least one according to the
	 * ordering defined by PredefinedElkIri.compare().
	 * 
	 * TODO The above remark is a bit mysterious. Does the interface really make
	 * such guarantees?
	 * 
	 * @return canonical object
	 */
	public T getCanonicalMember();

}
