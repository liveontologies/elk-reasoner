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
package org.semanticweb.elk.reasoner.saturation.classes;

import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.rulesystem.AbstractContext;
import org.semanticweb.elk.reasoner.saturation.rulesystem.Queueable;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.HashSetMultimap;
import org.semanticweb.elk.util.collections.Multimap;

/**
 * Context implementation that is used for EL reasoning. It provides data
 * structures for storing and retrieving various types of derived expressions,
 * including computed subsumptions between class expressions.
 * 
 * @author Markus Kroetzsch
 * @author Frantisek Simancik
 * 
 */
public class ContextElClassSaturation extends AbstractContext implements
		ContextClassSaturation {

	public final Set<IndexedClassExpression> superClassExpressions;

	public Multimap<IndexedPropertyChain, ContextElClassSaturation> backwardLinksByObjectProperty;

	public Multimap<IndexedPropertyChain, ContextElClassSaturation> forwardLinksByObjectProperty;

	protected Multimap<IndexedPropertyChain, Queueable<? extends ContextElClassSaturation>> propagationsByObjectProperty;

	/**
	 * A context is saturated if all superclass expressions of the root
	 * expression have been computed.
	 */
	protected volatile boolean isSaturated = false;

	/**
	 * False if owl:Nothing is stored as a superclass in this context.
	 */
	protected volatile boolean isSatisfiable = true;

	/**
	 * If set to true, then composition rules will be applied to derive all
	 * incoming links. This is usually needed only when at least one propagation
	 * has been derived at this object.
	 */
	protected boolean composeBackwardLinks = false;

	/**
	 * If set to true, then propagations will be derived in this context. This
	 * is needed only when there is at least one backward link.
	 */
	protected boolean derivePropagations = false;

	public ContextElClassSaturation(IndexedClassExpression root) {
		super(root);
		this.superClassExpressions = new ArrayHashSet<IndexedClassExpression>(
				13);
	}

	/**
	 * Get the set of super class expressions that have been derived for this
	 * context so far.
	 * 
	 * @return the set of derived indexed class expressions
	 */
	public Set<IndexedClassExpression> getSuperClassExpressions() {
		return superClassExpressions;
	}

	/**
	 * Return true if this context is set to isSatisfiable. A value of true
	 * means that owl:Nothing was stored as a superclass in this context.
	 * 
	 * @return
	 */
	public boolean isSatisfiable() {
		return isSatisfiable;
	}

	/**
	 * Set the satisfiability of this context. A value of true means that
	 * owl:Nothing was stored as a superclass in this context.
	 * 
	 * @param satisfiable
	 */
	public void setSatisfiable(boolean satisfiable) {
		isSatisfiable = satisfiable;
	}

	/**
	 * If set to true, then composition rules will be applied to derive all
	 * incoming links. This is usually needed only when at least one propagation
	 * has been derived at this object.
	 */
	public boolean getDeriveBackwardLinks() {
		return composeBackwardLinks;
	}

	public void setDeriveBackwardLinks(boolean deriveBackwardLinks) {
		this.composeBackwardLinks = deriveBackwardLinks;
	}

	public Multimap<IndexedPropertyChain, ContextElClassSaturation> getBackwardLinksByObjectProperty() {
		return backwardLinksByObjectProperty;
	}

	/**
	 * Initialize the set of propagations with the empty set
	 */
	public void initBackwardLinksByProperty() {
		backwardLinksByObjectProperty = new HashSetMultimap<IndexedPropertyChain, ContextElClassSaturation>();
	}

	public Multimap<IndexedPropertyChain, ContextElClassSaturation> getForwardLinksByObjectProperty() {
		return forwardLinksByObjectProperty;
	}

	public void initForwardLinksByProperty() {
		forwardLinksByObjectProperty = new HashSetMultimap<IndexedPropertyChain, ContextElClassSaturation>();
	}

	/**
	 * If set to true, then propagations will be derived in this context. This
	 * is needed only when there is at least one backward link.
	 */
	public boolean getDerivePropagations() {
		return derivePropagations;
	}

	public void setDerivePropagations(boolean derivePropagations) {
		this.derivePropagations = derivePropagations;
	}

	public Multimap<IndexedPropertyChain, Queueable<? extends ContextElClassSaturation>> getPropagationsByObjectProperty() {
		return propagationsByObjectProperty;
	}

	/**
	 * Initialize the set of propagations with the empty set
	 */
	public void initPropagationsByProperty() {
		propagationsByObjectProperty = new HashSetMultimap<IndexedPropertyChain, Queueable<? extends ContextElClassSaturation>>();
	}

	/**
	 * Mark context as saturated. A context is saturated if all superclass
	 * expressions of the root expression have been computed.
	 */
	public void setSaturated() {
		isSaturated = true;
	}

	/**
	 * Return if context is saturated. A context is saturated if all superclass
	 * expressions of the root expression have been computed. This needs to be
	 * set explicitly by some processor.
	 * 
	 * @return <tt>true</tt> if this context is saturated and <tt>false</tt>
	 *         otherwise
	 */
	public boolean isSaturated() {
		return isSaturated;
	}

}
