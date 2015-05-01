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

import java.util.ArrayList;
import java.util.Collection;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
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
	 * @see ElkSubObjectPropertyOfAxiom#getSubObjectPropertyExpression()
	 * @see ElkSubObjectPropertyOfAxiom#getSuperObjectPropertyExpression()
	 * @see IndexedPropertyChain#getToldSuperProperties()
	 */
	public ArrayList<IndexedPropertyChain> getToldSubChains();

	/**
	 * @return The {@link ElkAxiom}s responsible for the respective told sub
	 *         properties returned by {@link #getToldSubChains()}
	 * 
	 * @see IndexedPropertyChain#getToldSuperPropertiesReasons()
	 */
	public ArrayList<ElkAxiom> getToldSubChainsReasons();

	/**
	 * @return All {@link IndexedComplexPropertyChain}s in which this
	 *         {@link IndexedObjectProperty} is a left property
	 * 
	 * @see {@link IndexedComplexPropertyChain#getFirstProperty()}
	 */
	public Collection<IndexedComplexPropertyChain> getLeftChains();

}