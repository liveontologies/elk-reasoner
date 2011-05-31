/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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
package org.semanticweb.elk.reasoner.indexing;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.semanticweb.elk.syntax.ElkObjectProperty;
import org.semanticweb.elk.util.ArrayHashSet;
import org.semanticweb.elk.util.HashMultimap;
import org.semanticweb.elk.util.Multimap;

/**
 * Represents all occurrences of an ElkObjectPropertyExpression in an ontology.
 * To this end, objects of this class keeps a list of super property
 * expressions. The data structures are optimized for quickly retrieving the
 * relevant relationships during inferencing.
 * 
 * This class is mainly a data container that provides direct public access to
 * its content. The task of updating index structures consistently in a global
 * sense is left to callers.
 * 
 * @author Frantisek Simancik
 * @author Markus Kroetzsch
 */

public class IndexedObjectProperty {
	/** The represented object property expression. */
	public final ElkObjectProperty objectProperty;

	// TODO Consistently rename "role" into "object property" in the code.
	/**
	 * A list of all indexed class expressions for (told) subroles of this
	 * object property expression.
	 */
	public final List<IndexedObjectProperty> subObjectProperties;

	/**
	 * Cache for the subproperties that this object property expression has
	 * based on the transitive closure of the property hierarchy.
	 */
	protected Set<IndexedObjectProperty> inferredSubObjectProperties = null;

	public final Multimap<IndexedClassExpression, IndexedClassExpression> negExistentialsByClassExpression;

	/**
	 * Creates an object representing an ElkObjectPropertyExpression.
	 * 
	 * @param objectPropertyExpression
	 */
	public IndexedObjectProperty(
			ElkObjectProperty objectPropertyExpression) {
		this.objectProperty = objectPropertyExpression;
		this.subObjectProperties = new ArrayList<IndexedObjectProperty>();
		this.negExistentialsByClassExpression = new HashMultimap<IndexedClassExpression,
														IndexedClassExpression>(1);
	}

	/**
	 * Represent the object's ElkObjectProperty as a string. This
	 * implementation reflects the fact that we generally consider only one
	 * IndexedObjectProperty for each ElkObjectPropertyExpression.
	 * 
	 * @return string representation
	 */
	public String toString() {
		return "[" + objectProperty.toString() + "]";
	}

	/**
	 * Return the set of all IndexedObjectProperty objects that are inferred to
	 * be subproperties of this object, based on the told property hierarchy.
	 * 
	 * @return Set of subproperties
	 */
	public Set<IndexedObjectProperty> getSubRelation() {
		if (inferredSubObjectProperties == null) {
			computeSubRoles();
		}
		return inferredSubObjectProperties;
	}

	/**
	 * Compute the subproperties of this object and store them in the internal
	 * cache.
	 */
	public Multimap<IndexedClassExpression, IndexedClassExpression> getPropagations() {
		return this.negExistentialsByClassExpression;
	}

	protected void computeSubRoles() {
		inferredSubObjectProperties = new ArrayHashSet<IndexedObjectProperty>();
		ArrayDeque<IndexedObjectProperty> queue = new ArrayDeque<IndexedObjectProperty>();
		inferredSubObjectProperties.add(this);
		queue.addLast(this);
		while (!queue.isEmpty()) {
			IndexedObjectProperty indexedObjectProperty = queue.removeLast();
			for (IndexedObjectProperty r : indexedObjectProperty.subObjectProperties) {
				if (inferredSubObjectProperties.add(r)) {
					queue.addLast(r);
				}
			}
		}
	}

	private static int lastHashCode = 0;
	private final int hashCode_ = ++lastHashCode;

	/**
	 * Get an integer hash code to be used for this object. Care will be taken
	 * that there is never more than one IndexedClassExpression per
	 * ElkObjectProperty, so it is viable to use a running number as a hash
	 * here.
	 * 
	 * @return integer hash code
	 */
	@Override
	public final int hashCode() {
		return hashCode_;
	}
}