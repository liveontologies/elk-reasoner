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
package org.semanticweb.elk.reasoner.indexing.model;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObject;
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
public interface ModifiableOntologyIndex
		extends OntologyIndex, ModifiableIndexedObjectCache {

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
	boolean addReflexiveProperty(IndexedObjectProperty property,
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
	boolean removeReflexiveProperty(IndexedObjectProperty property,
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
	boolean addContextInitRule(ChainableContextInitRule newRule);

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
	boolean removeContextInitRule(ChainableContextInitRule oldRule);

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
	boolean add(ModifiableIndexedClassExpression target,
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
	boolean remove(ModifiableIndexedClassExpression target,
			ChainableSubsumerRule oldRule);

	/**
	 * Tries to set the given {@link IndexedClassExpression} as definition for
	 * the given target {@link ModifiableIndexedClass}. There can be at most one
	 * definition set for each {@link ModifiableIndexedClass}.
	 * 
	 * @param target
	 *            the {@link ModifiableIndexedClass} for which to add a new
	 *            definition
	 * @param definition
	 *            the {@link ModifiableIndexedClassExpression} to be added as
	 *            the definition
	 * @param reason
	 *            the {@link ElkAxiom} from which the added definition
	 *            originates
	 * @return {@code true} if the definition was set and {@code false} if this
	 *         operation was not successful
	 */
	boolean tryAddDefinition(ModifiableIndexedClass target,
			ModifiableIndexedClassExpression definition, ElkAxiom reason);

	/**
	 * Tries to remove the given {@link IndexedClassExpression} from the
	 * definition of the given target {@link ModifiableIndexedClass}. The
	 * definition is removed if it is set for exactly the given
	 * {@link IndexedClassExpression}
	 * 
	 * @param target
	 *            the {@link ModifiableIndexedClass} for which to remove the
	 *            definition
	 * @param definition
	 *            the {@link ModifiableIndexedClassExpression} that was defined
	 *            for the given {@link ModifiableIndexedClass} and should be now
	 *            removed
	 * @param reason
	 *            the {@link ElkAxiom} from which the removed definition
	 *            originates
	 * @return {@code true} if the definition was removed and {@code false} if
	 *         this operation was not successful
	 */
	boolean tryRemoveDefinition(ModifiableIndexedClass target,
			ModifiableIndexedClassExpression definition, ElkAxiom reason);

	/**
	 * Registers a given {@link IndexingUnsupportedListener} with this
	 * {@link ModifiableOntologyIndex}
	 * 
	 * @param listener
	 * @return {@code true} if the operation was successful and {@code false}
	 *         otherwise; if {@code false} is return, the listener was not
	 *         registered
	 */
	boolean addIndexingUnsupportedListener(
			IndexingUnsupportedListener listener);

	/**
	 * Removes a given {@link IndexingUnsupportedListener} from this
	 * {@link ModifiableOntologyIndex}
	 * 
	 * @param listener
	 * @return {@code true} if the operation was successful and {@code false}
	 *         otherwise; if {@code false} is return, the listener was not
	 *         removed
	 */
	boolean removeIndexingUnsupportedListener(
			IndexingUnsupportedListener listener);

	/**
	 * Calls
	 * {@link IndexingUnsupportedListener#indexingUnsupported(ModifiableIndexedObject, OccurrenceIncrement)}
	 * on each registered listener with the provided arguments.
	 * 
	 * @param indexedObject
	 * @param increment
	 */
	void fireIndexingUnsupported(ModifiableIndexedObject indexedObject,
			OccurrenceIncrement increment);

	/**
	 * Calls {@link IndexingUnsupportedListener#indexingUnsupported(ElkObject)}
	 * on each registered listener with the provided arguments.
	 * 
	 * @param elkObject
	 */
	void fireIndexingUnsupported(ElkObject elkObject);

	public interface IndexingUnsupportedListener {

		void indexingUnsupported(ModifiableIndexedObject indexedObject,
				OccurrenceIncrement increment);

		void indexingUnsupported(ElkObject elkObject);

	}

}
