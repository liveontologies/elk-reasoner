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
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
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
public interface ModifiableOntologyIndex extends OntologyIndex {

	/**
	 * @return the {@link IndexedObjectCache} associated with this index; this
	 *         is where all {@link IndexedObject}s of this {@link OntologyIndex}
	 *         are stored
	 */
	IndexedObjectCache getIndexedObjectCache();

	/**
	 * Adds the given {@link IndexedObject} to this {@link OntologyIndex}
	 * 
	 * @param newObject
	 *            the object to be added
	 * @return {@code true} if the operation is successful and {@code false} if
	 *         not; if {@code false} is return, the index remains unchanged
	 */
	public boolean add(IndexedObject newObject);

	/**
	 * Removes the given {@link IndexedObject} from this {@link OntologyIndex}
	 * 
	 * @param oldObject
	 *            the object to be removed
	 * 
	 * @return {@code true} if the operation is successful and {@code false} if
	 *         not; if {@code false} is return, the index remains unchanged
	 */
	public boolean remove(IndexedObject oldObject);

	/**
	 * Add the given {@link ElkClass} to the signature of this
	 * {@link OntologyIndex}
	 * 
	 * @param newClass
	 *            the {@link ElkClass} to be added
	 * @return {@code true} if the operation is successful and {@code false} if
	 *         not; if {@code false} is return, the index remains unchanged
	 */
	public boolean addClass(ElkClass newClass);

	/**
	 * Removes the given {@link ElkClass} from the signature of this
	 * {@link OntologyIndex}
	 * 
	 * @param oldClass
	 *            the {@link ElkClass} to be removed
	 * @return {@code true} if the operation is successful and {@code false} if
	 *         not; if {@code false} is return, the index remains unchanged
	 */
	public boolean removeClass(ElkClass oldClass);

	/**
	 * Add the given {@link ElkNamedIndividual} to the signature of this
	 * {@link OntologyIndex}
	 * 
	 * @param newIndividual
	 *            the {@link ElkNamedIndividual} to be added
	 * @return {@code true} if the operation is successful and {@code false} if
	 *         not; if {@code false} is return, the index remains unchanged
	 */
	public boolean addNamedIndividual(ElkNamedIndividual newIndividual);

	/**
	 * Removes the given {@link ElkNamedIndividual} from the signature of this
	 * {@link OntologyIndex}
	 * 
	 * @param oldIndividual
	 *            the {@link ElkNamedIndividual} to be removed
	 * @return {@code true} if the operation is successful and {@code false} if
	 *         not; if {@code false} is return, the index remains unchanged
	 */
	public boolean removeNamedIndividual(ElkNamedIndividual oldIndividual);

	/**
	 * Assert reflexivity of the given {@link IndexedObjectProperty}
	 * 
	 * @param property
	 *            the {@link IndexedObjectProperty} which should be asserted
	 *            reflexive
	 * @return {@code true} if the operation is successful and {@code false} if
	 *         not; if {@code false} is return, the index remains unchanged
	 */
	public boolean addReflexiveProperty(IndexedObjectProperty property);

	/**
	 * Retracts reflexivity of the given {@link IndexedObjectProperty}
	 * 
	 * @param property
	 *            the {@link IndexedObjectProperty} which should not be
	 *            reflexive anymore
	 * @return {@code true} if the operation is successful and {@code false} if
	 *         not; if {@code false} is return, the index remains unchanged
	 */
	public boolean removeReflexiveProperty(IndexedObjectProperty property);

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
	public boolean add(IndexedClassExpression target,
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
	public boolean remove(IndexedClassExpression target,
			ChainableSubsumerRule oldRule);

}
