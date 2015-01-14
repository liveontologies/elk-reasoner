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

package org.semanticweb.elk.reasoner.indexing.hierarchy;

import java.util.Collection;
import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;

/**
 * Represents occurrences of an {@link ElkObjectProperty} in an ontology.
 * 
 * @author Frantisek Simancik
 * @author Markus Kroetzsch
 */

public interface IndexedObjectProperty extends IndexedPropertyChain,
		IndexedEntity {

	/**
	 * @return The {@link ElkObjectProperty} represented by this
	 *         {@link IndexedObjectProperty}.
	 */
	@Override
	public ElkObjectProperty getElkEntity();

	/**
	 * @return The representations of all {@link ElkSubObjectPropertyExpression}
	 *         s occurring in {@link ElkSubObjectPropertyOfAxiom}, where the
	 *         super property {@link ElkObjectProperty} is represented by this
	 *         {@link IndexedObjectProperty}
	 * 
	 * @see {@link ElkSubObjectPropertyOfAxiom#getSubObjectPropertyExpression()}
	 * @see {@link ElkSubObjectPropertyOfAxiom#getSuperObjectPropertyExpression()}
	 */
	public List<IndexedPropertyChain> getToldSubProperties();

	/**
	 * @return All {@link IndexedBinaryPropertyChain}s in which this
	 *         {@link IndexedObjectProperty} is a left property
	 * 
	 * @see {@link IndexedBinaryPropertyChain#getLeftProperty()}
	 */
	public Collection<IndexedBinaryPropertyChain> getLeftChains();

	/**
	 * @return {@code true} if this object property occurs in a
	 *         {@link IndexedReflexiveObjectPropertyAxiom}.
	 * 
	 * @see {@link IndexedReflexiveObjectPropertyAxiom#getProperty()}
	 */
	public boolean isToldReflexive();

}