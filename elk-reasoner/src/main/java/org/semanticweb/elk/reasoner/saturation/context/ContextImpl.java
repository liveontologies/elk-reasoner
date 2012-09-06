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
package org.semanticweb.elk.reasoner.saturation.context;

import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointnessAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.rules.BackwardLinkRules;
import org.semanticweb.elk.reasoner.indexing.rules.Chain;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.HashSetMultimap;
import org.semanticweb.elk.util.collections.Multimap;
import org.semanticweb.elk.util.collections.Operations;

/**
 * Context implementation that is used for EL reasoning. It provides data
 * structures for storing and retrieving various types of derived expressions,
 * including computed subsumptions between class expressions.
 * 
 * @author Markus Kroetzsch
 * @author Frantisek Simancik
 * 
 */
public class ContextImpl extends AbstractContext implements Context {

	final Set<IndexedClassExpression> superClassExpressions;

	Multimap<IndexedPropertyChain, Context> backwardLinksByObjectProperty;

	Multimap<IndexedPropertyChain, Context> forwardLinksByObjectProperty;

	Set<IndexedDisjointnessAxiom> disjointnessAxioms;

	/**
	 * A context is saturated if all superclass expressions of the root
	 * expression have been computed.
	 */
	protected volatile boolean isSaturated = false;

	/**
	 * False if owl:Nothing is stored as a superclass in this context.
	 */
	protected volatile boolean isSatisfiable = true;

	public ContextImpl(IndexedClassExpression root) {
		super(root);
		this.superClassExpressions = new ArrayHashSet<IndexedClassExpression>(
				13);
	}

	@Override
	public Set<IndexedClassExpression> getSuperClassExpressions() {
		return superClassExpressions;
	}

	@Override
	public boolean isSatisfiable() {
		return isSatisfiable;
	}

	@Override
	public void setSatisfiable(boolean satisfiable) {
		isSatisfiable = satisfiable;
	}

	public Multimap<IndexedPropertyChain, Context> getBackwardLinksByObjectProperty() {
		if (backwardLinksByObjectProperty == null)
			return Operations.emptyMultimap();
		return backwardLinksByObjectProperty;
	}

	public Multimap<IndexedPropertyChain, Context> getForwardLinksByObjectProperty() {
		if (forwardLinksByObjectProperty == null)
			return Operations.emptyMultimap();
		return forwardLinksByObjectProperty;
	}

	public boolean addDisjointessAxiom(
			IndexedDisjointnessAxiom disjointnessAxiom) {
		if (disjointnessAxioms == null)
			disjointnessAxioms = new ArrayHashSet<IndexedDisjointnessAxiom>();

		return disjointnessAxioms.add(disjointnessAxiom);
	}

	/**
	 * Mark context as saturated. A context is saturated if all superclass
	 * expressions of the root expression have been computed.
	 */
	@Override
	public void setSaturated() {
		isSaturated = true;
	}

	/**
	 * Return if context is saturated. A context is saturated if all superclass
	 * expressions of the root expression have been computed. This needs to be
	 * set explicitly by some processor.
	 * 
	 * @return {@code true} if this context is saturated and {@code false}
	 *         otherwise
	 */
	@Override
	public boolean isSaturated() {
		return isSaturated;
	}

	@Override
	public boolean addBackwardLinkByObjectProperty(
			IndexedPropertyChain relation, Context target) {
		if (backwardLinksByObjectProperty == null)
			backwardLinksByObjectProperty = new HashSetMultimap<IndexedPropertyChain, Context>();
		return backwardLinksByObjectProperty.add(relation, target);
	}

	@Override
	public boolean addForwardLinkByObjectProperty(
			IndexedPropertyChain relation, Context target) {
		if (forwardLinksByObjectProperty == null)
			forwardLinksByObjectProperty = new HashSetMultimap<IndexedPropertyChain, Context>();
		return forwardLinksByObjectProperty.add(relation, target);
	}

	@Override
	public boolean addSuperClassExpression(IndexedClassExpression expression) {
		return superClassExpressions.add(expression);
	}

	@Override
	public Chain<BackwardLinkRules> getBackwardLinkRules() {
		return this;
	}

}
