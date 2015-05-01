/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
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
package org.semanticweb.elk.reasoner.indexing.modifiable;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.caching.ModifiableIndexedObjectCache;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObject;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.OntologyIndex;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.ChainableContextInitRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ChainableSubsumerRule;

/**
 * Common methods for constructing an {@link OntologyIndex}. They allow addition
 * and removal of {@link IndexedObject}s, as well as assigning and removing
 * rules for such expressions.
 * 
 * @see OntologyIndex
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface ModifiableOntologyIndex extends OntologyIndex,
		ModifiableIndexedObjectCache {

	/**
	 * Assert reflexivity of the given {@link IndexedObjectProperty}
	 * 
	 * @param property
	 *            the {@link IndexedObjectProperty} which should be asserted
	 *            reflexive
	 * @param reason
	 *            the {@link ElkAxiom} that is responsible for reflexivity of
	 *            this property
	 * @return {@code true} if the operation is successful and {@code false} if
	 *         not; if {@code false} is return, the index remains unchanged
	 */
	public boolean addReflexiveProperty(IndexedObjectProperty property,
			ElkAxiom reason);

	/**
	 * Retracts reflexivity of the given {@link IndexedObjectProperty}
	 * 
	 * @param property
	 *            the {@link IndexedObjectProperty} which should not be
	 *            reflexive anymore
	 * @param reason
	 *            the {@link ElkAxiom} that is responsible for reflexivity of
	 *            this property
	 * @return {@code true} if the operation is successful and {@code false} if
	 *         not; if {@code false} is return, the index remains unchanged
	 */
	public boolean removeReflexiveProperty(IndexedObjectProperty property,
			ElkAxiom reason);

	/**
	 * Adds a new context initialization for this {@link OntologyIndex}.
	 * 
	 * @param newRule
	 *            the context initialization rule to be added
	 * @return {@code true} if the operation is successful and {@code false} if
	 *         not; if {@code false} is return, the index remains unchanged
	 * 
	 * @see OntologyIndex#getContextInitRuleHead()
	 */
	public boolean addContextInitRule(ChainableContextInitRule newRule);

	/**
	 * Removes an existing context initialization for this {@link OntologyIndex}
	 * 
	 * @param oldRule
	 *            the context initialization rule to be removed
	 * @return {@code true} if the operation is successful and {@code false} if
	 *         not; if {@code false} is return, the index remains unchanged
	 * 
	 * @see OntologyIndex#getContextInitRuleHead()
	 */
	public boolean removeContextInitRule(ChainableContextInitRule oldRule);

	/**
	 * Adds a new context rule for the given {@link IndexedClassExpression}
	 * 
	 * @param target
	 *            the {@link IndexedClassExpression} for which to add the rule
	 * @param newRule
	 *            the context rule to be added
	 * @return {@code true} if the operation is successful and {@code false} if
	 *         not; if {@code false} is return, the index remains unchanged
	 */
	public boolean add(ModifiableIndexedClassExpression target,
			ChainableSubsumerRule newRule);

	/**
	 * Removes an existing context rule for the given
	 * {@link IndexedClassExpression}
	 * 
	 * @param target
	 *            the {@link IndexedClassExpression} for which to remove the
	 *            rule
	 * @param oldRule
	 *            the context rule to be removed
	 * @return {@code true} if the operation is successful and {@code false} if
	 *         not; if {@code false} is return, the index remains unchanged
	 */
	public boolean remove(ModifiableIndexedClassExpression target,
			ChainableSubsumerRule oldRule);

	/**
	 * Tries to change the number of negative occurrences of {@code owl:Thing}
	 * in the ontology represented by this {@link ModifiableOntologyIndex}.
	 * 
	 * @param increment
	 *            how many occurrences should be added (if positive) or removed
	 *            (if negative)
	 * 
	 * @return {@code true} if the operation has been successful or
	 *         {@code false} otherwise. If {@code false} is return, this
	 *         {@link ModifiableOntologyIndex} should not logically change,
	 *         i.e., it should correspond to the same logical representation of
	 *         the ontology as before the operation
	 * 
	 * @see #hasNegativeOwlThing()
	 */
	public boolean updateNegativeOwlThingOccurrenceNo(int increment);

	/**
	 * Tries to change the number of positive occurrences of {@code owl:Nothing}
	 * in the ontology represented by this {@link ModifiableOntologyIndex}.
	 * 
	 * @param increment
	 *            how many occurrences should be added (if positive) or removed
	 *            (if negative)
	 * 
	 * @return {@code true} if the operation has been successful or
	 *         {@code false} otherwise. If {@code false} is return, this
	 *         {@link ModifiableOntologyIndex} should not logically change,
	 *         i.e., it should correspond to the same logical representation of
	 *         the ontology as before the operation
	 * 
	 * @see #hasPositivelyOwlNothing()
	 */
	public boolean updatePositiveOwlNothingOccurrenceNo(int increment);
}
