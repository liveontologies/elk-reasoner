/*
 * #%L
 * ELK OWL Object Interfaces
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
package org.semanticweb.elk.owl.views;

import org.semanticweb.elk.owl.interfaces.ElkObject;

/**
 * The interface to specify a viewer for elk objects. The viewer specifies
 * methods for computing an object view together with other helper functions.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface ElkObjectViewer {

	/**
	 * Calculate a view for a given elk object. The result should be the elk
	 * object of the same type (e.g., ElkClass for an ElkClass input).
	 * 
	 * @param object
	 * @return
	 */
	<T extends ElkObject> T getView(T object);

	/**
	 * Compute hash code for unordered collection of elk objects using their
	 * hash codes. It is not specified if the resulting hash code depends on the
	 * order of the objects. The function must be compatible with {@link
	 * naryEquals()} as usual, i.e., if two collections are equal then they
	 * should have the same code.
	 * 
	 * @param objects
	 *            the input collection of elk objects
	 * @return the combined hash code
	 */
	<T extends ElkObject> int naryHashCode(Iterable<? extends T> objects);

	/**
	 * Compare two collections of elk objects using their comparison functions.
	 * If the method returns true then both collections contain the same
	 * elements (as sets) w.r.t. the object's equality functions, but the
	 * converse is not guaranteed. Thus, a particular implementation may compare
	 * the elements in the order in which they go. The function must be
	 * compatible with {@link naryHashCode()}: if two collections are equal,
	 * they should have the same hash code.
	 * 
	 * @param objects
	 *            the input collection of elk objects
	 * @param others
	 *            the other collection of elk objects it needs to be compared
	 *            with
	 * @return whether the two collections are equal
	 */
	<T extends ElkObject> boolean naryEquals(Iterable<? extends T> objects,
			Iterable<? extends T> others);
}
