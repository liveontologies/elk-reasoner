package org.semanticweb.elk.reasoner.taxonomy.model;
/*
 * #%L
 * ELK Reasoner
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

/**
 * A node that can be modified.
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 * @author Peter Skocovsky
 *
 * @param <T>
 *            The type of the members of this node.
 */
public interface UpdateableNode<T> extends Node<T> {

	/**
	 * Atomically sets the modification state of this node to the passed
	 * argument if the state is different.
	 * 
	 * @param modified
	 *            How to set the modification state.
	 * @return Whether the state changed.
	 */
	boolean trySetModified(boolean modified);

	/**
	 * Whether this node is marked as modified.
	 * 
	 * @return Whether this node is marked as modified.
	 */
	boolean isModified();

	/**
	 * Adds the specified member to this node.
	 * 
	 * @param member
	 *            The member to add.
	 * @return Whether the node changed.
	 */
	boolean add(T member);

	/**
	 * Removes the specified member from this node.
	 * 
	 * @param member
	 *            The node to remove.
	 * @return Whether the node changed.
	 */
	boolean remove(T member);

	/**
	 * Replaces the members of this node with the specified members.
	 * 
	 * @param members
	 *            The new members of this node.
	 */
	void setMembers(Iterable<? extends T> members);

}
