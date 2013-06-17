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
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ChainableRule;
import org.semanticweb.elk.reasoner.saturation.rules.DatatypeRule;

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
	 */
	public void add(IndexedObject newObject);

	/**
	 * Removes the given {@link IndexedObject} from this {@link OntologyIndex}
	 * 
	 * @param oldObject
	 *            the object to be removed
	 * 
	 * @throws ElkUnexpectedIndexingException
	 *             if the given object does not occur in this
	 *             {@link OntologyIndex}
	 */
	public void remove(IndexedObject oldObject)
			throws ElkUnexpectedIndexingException;

	/**
	 * Add the given {@link ElkClass} to the signature of this
	 * {@link OntologyIndex}
	 * 
	 * @param newClass
	 *            the {@link ElkClass} to be added
	 */
	public void addClass(ElkClass newClass);

	/**
	 * Removes the given {@link ElkClass} from the signature of this
	 * {@link OntologyIndex}
	 * 
	 * @param newClass
	 *            the {@link ElkClass} to be removed
	 */
	public void removeClass(ElkClass oldClass)
			throws ElkUnexpectedIndexingException;
	
	/**
	 * Add the given {@link ElkNamedIndividual} to the signature of this
	 * {@link OntologyIndex}
	 * 
	 * @param newClass
	 *            the {@link ElkNamedIndividual} to be added
	 */
	public void addNamedIndividual(ElkNamedIndividual newIndividual);

	/**
	 * Removes the given {@link ElkNamedIndividual} from the signature of this
	 * {@link OntologyIndex}
	 * 
	 * @param newClass
	 *            the {@link ElkNamedIndividual} to be removed
	 */
	public void removeNamedIndividual(ElkNamedIndividual oldIndividual)
			throws ElkUnexpectedIndexingException;

	/**
	 * Assert reflexivity of the given {@link IndexedObjectProperty}
	 * 
	 * @param property
	 *            the {@link IndexedObjectProperty} which should be asserted
	 *            reflexive
	 */
	public void addReflexiveProperty(IndexedObjectProperty property);

	/**
	 * Retracts reflexivity of the given {@link IndexedObjectProperty}
	 * 
	 * @param property
	 *            the {@link IndexedObjectProperty} which should not be
	 *            reflexive anymore
	 */
	public void removeReflexiveProperty(IndexedObjectProperty property)
			throws ElkUnexpectedIndexingException;

	/**
	 * Adds a new context initialization for this {@link OntologyIndex}.
	 * 
	 * @param newRule
	 *            the context initialization rule to be added
	 * @see OntologyIndex#getContextInitRuleHead()
	 */
	public void addContextInitRule(ChainableRule<Context> newRule);

	/**
	 * Removes an existing context initialization for this {@link OntologyIndex}
	 * 
	 * @param oldRule
	 *            the context initialization rule to be removed
	 * @throws ElkUnexpectedIndexingException
	 *             if the given rule was not registered with this
	 *             {@link OntologyIndex}
	 * 
	 * @see OntologyIndex#getContextInitRuleHead()
	 */
	public void removeContextInitRule(ChainableRule<Context> oldRule)
			throws ElkUnexpectedIndexingException;

	/**
	 * Adds a new context rule for the given {@link IndexedClassExpression}
	 * 
	 * @param target
	 *            the {@link IndexedClassExpression} for which to add the rule
	 * @param newRule
	 *            the context rule to be added
	 */
	public void add(IndexedClassExpression target,
			ChainableRule<Context> newRule);

	/**
	 * Removes an existing context rule for the given
	 * {@link IndexedClassExpression}
	 * 
	 * @param target
	 *            the {@link IndexedClassExpression} for which to remove the
	 *            rule
	 * @param oldRule
	 *            the context rule to be removed
	 * @throws ElkUnexpectedIndexingException
	 *             if the given rule was not registered with this
	 *             {@link IndexedClassExpression}
	 */
	public void remove(IndexedClassExpression target,
			ChainableRule<Context> oldRule)
			throws ElkUnexpectedIndexingException;
	
	/**
	 * Adds a new datatype rule for the given {@link IndexedDataProperty}
	 * 
	 * @param target
	 *            the {@link IndexedDataProperty} for which to add the rule
	 * @param newRule
	 *            the datatype rule to be added
	 */
	public void add(IndexedDataProperty target, DatatypeRule<Context> newRule);
	
	/**
	 * Removes an existing datatype rule for the given
	 * {@link IndexedDataProperty}
	 * 
	 * @param target
	 *            the {@link IndexedDataProperty} for which to remove the
	 *            rule
	 * @param oldRule
	 *            the context rule to be removed
	 * @throws ElkUnexpectedIndexingException
	 *             if the given rule was not registered with this
	 *             {@link IndexedDataProperty}
	 */
	public void remove(IndexedDataProperty target, DatatypeRule<Context> oldRule)
			throws ElkUnexpectedIndexingException;

}
