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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ChainableRule;
import org.semanticweb.elk.reasoner.saturation.rules.Rule;
import org.semanticweb.elk.util.collections.ArrayHashMap;
import org.semanticweb.elk.util.collections.chains.AbstractChain;
import org.semanticweb.elk.util.collections.chains.Chain;

/**
 * An object representing incremental changes in the index. The changes are
 * stored in two maps: additions and deletions. The map for additions assigns to
 * every {@link IndexedClassExpression} new rules to be added to the index;
 * likewise, the map of deletions assigns to every
 * {@link IndexedClassExpression} a new rules to be removed from the index.
 * 
 * @author "Yevgeny Kazakov"
 * @author Pavel Klinov
 * 
 */
public class DifferentialIndex {

	private static final Logger LOGGER_ = Logger
			.getLogger(DifferentialIndex.class);

	/**
	 * The list of {@link ElkClass}es to be added to the signature of the
	 * ontology; the new signature is obtained by adding {@link #addedClasses}
	 * and removing {@link #removedClasses} in this particular order; these sets
	 * are not necessarily disjoint.
	 */
	final List<ElkClass> addedClasses;

	/**
	 * The list of {@link ElkClass}es to be removed from the signature of the
	 * ontology; the new signature is obtained by adding {@link #addedClasses}
	 * and removing {@link #removedClasses} in this particular order; these sets
	 * are not necessarily disjoint.
	 */
	final List<ElkClass> removedClasses;

	final List<ElkNamedIndividual> addedIndividuals;

	final List<ElkNamedIndividual> removedIndividuals;

	private final OntologyIndex mainIndex_;

	/**
	 * The added initialization {@link Rule}s
	 */
	private ChainableRule<Context> addedContextInitRules_ = null;

	/**
	 * The removed initialization {@link Rule}s
	 */
	private ChainableRule<Context> removedContextInitRules_ = null;

	/**
	 * The map of added {@link Rule}s for index class expressions;
	 */
	private final Map<IndexedClassExpression, ChainableRule<Context>> addedContextRuleHeadByClassExpressions_;

	/**
	 * The map of removed {@link Rule}s for index class expressions;
	 */
	private final Map<IndexedClassExpression, ChainableRule<Context>> removedContextRuleHeadByClassExpressions_;

	private final ElkAxiomIndexerVisitor axiomInserter_;

	private final ElkAxiomIndexerVisitor axiomDeleter_;

	public DifferentialIndex(OntologyIndex mainIndex,
			IndexedObjectCache objectCache, IndexedClass owlNothing) {
		this.addedClasses = new ArrayList<ElkClass>(127);
		this.removedClasses = new ArrayList<ElkClass>(127);
		this.addedIndividuals = new ArrayList<ElkNamedIndividual>();
		this.removedIndividuals = new ArrayList<ElkNamedIndividual>();
		this.addedContextRuleHeadByClassExpressions_ = new ArrayHashMap<IndexedClassExpression, ChainableRule<Context>>(
				127);
		this.removedContextRuleHeadByClassExpressions_ = new ArrayHashMap<IndexedClassExpression, ChainableRule<Context>>(
				127);
		this.axiomInserter_ = new ElkAxiomIndexerVisitor(objectCache,
				owlNothing, new IncrementalIndexUpdater(this), true);
		this.axiomDeleter_ = new ElkAxiomIndexerVisitor(objectCache,
				owlNothing, new IncrementalIndexUpdater(this), false);
		this.mainIndex_ = mainIndex;
	}

	public ChainableRule<Context> getAddedContextInitRules() {
		return addedContextInitRules_;
	}

	public ChainableRule<Context> getRemovedContextInitRules() {
		return removedContextInitRules_;
	}

	/**
	 * @return the map from indexed class expressions to the corresponding
	 *         objects containing index additions for these class expressions
	 * 
	 */
	public Map<IndexedClassExpression, ChainableRule<Context>> getAddedContextRulesByClassExpressions() {
		return this.addedContextRuleHeadByClassExpressions_;
	}

	/**
	 * @return the map from indexed class expressions to the corresponding
	 *         objects containing index deletions for these class expressions
	 */
	public Map<IndexedClassExpression, ChainableRule<Context>> getRemovedContextRulesByClassExpressions() {
		return this.removedContextRuleHeadByClassExpressions_;
	}

	/**
	 * @return the list of ELK classes to be added to the signature of the
	 *         ontology
	 */
	public List<ElkClass> getAddedClasses() {
		return this.addedClasses;
	}

	/**
	 * @return the list of ELK classes to be removed from the signature of the
	 *         ontology
	 */
	public List<ElkClass> getRemovedClasses() {
		return this.removedClasses;
	}

	public List<ElkNamedIndividual> getAddedIndividuals() {
		return this.addedIndividuals;
	}

	public List<ElkNamedIndividual> getRemovedIndividuals() {
		return this.removedIndividuals;
	}

	/**
	 * Commits the changes to the indexed objects and clears all changes.
	 */
	public void commit() {
		// commit changes in the context initialization rules
		ChainableRule<Context> nextRule;
		Chain<ChainableRule<Context>> chain;

		nextRule = removedContextInitRules_;
		chain = mainIndex_.getContextInitRuleChain();
		while (nextRule != null) {
			nextRule.removeFrom(chain);
			nextRule = nextRule.next();
		}
		removedContextInitRules_ = null;

		nextRule = addedContextInitRules_;
		chain = mainIndex_.getContextInitRuleChain();
		while (nextRule != null) {
			nextRule.addTo(chain);
			nextRule = nextRule.next();
		}
		addedContextInitRules_ = null;

		// commit context rule deletions and additions
		for (IndexedClassExpression target : removedContextRuleHeadByClassExpressions_
				.keySet()) {
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace("Committing context rule deletions for " + target);
			}

			nextRule = removedContextRuleHeadByClassExpressions_.get(target);
			chain = target.getCompositionRuleChain();
			while (nextRule != null) {
				nextRule.removeFrom(chain);
				nextRule = nextRule.next();
			}
		}

		removedContextRuleHeadByClassExpressions_.clear();

		for (IndexedClassExpression target : addedContextRuleHeadByClassExpressions_
				.keySet()) {
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace("Committing context rule additions for " + target);
			}
			nextRule = addedContextRuleHeadByClassExpressions_.get(target);
			chain = target.getCompositionRuleChain();
			while (nextRule != null) {
				nextRule.addTo(chain);
				nextRule = nextRule.next();
			}
		}
		addedContextRuleHeadByClassExpressions_.clear();

	}

	public void clearSignatureChanges() {
		addedClasses.clear();
		removedClasses.clear();
		addedIndividuals.clear();
		removedIndividuals.clear();
	}

	public boolean isEmpty() {
		return addedContextInitRules_ == null
				&& removedContextInitRules_ == null
				&& addedContextRuleHeadByClassExpressions_.isEmpty()
				&& removedContextRuleHeadByClassExpressions_.isEmpty();
	}

	public ElkAxiomProcessor getAxiomInserter() {
		return axiomInserter_;
	}

	public ElkAxiomProcessor getAxiomDeleter() {
		return axiomDeleter_;
	}

	private Chain<ChainableRule<Context>> getAddedContextInitRuleChain() {
		return new AbstractChain<ChainableRule<Context>>() {

			@Override
			public ChainableRule<Context> next() {
				return addedContextInitRules_;
			}

			@Override
			public void setNext(ChainableRule<Context> tail) {
				addedContextInitRules_ = tail;
			}
		};
	}

	private Chain<ChainableRule<Context>> getRemovedContextInitRuleChain() {
		return new AbstractChain<ChainableRule<Context>>() {

			@Override
			public ChainableRule<Context> next() {
				return removedContextInitRules_;
			}

			@Override
			public void setNext(ChainableRule<Context> tail) {
				removedContextInitRules_ = tail;
			}
		};
	}

	private Chain<ChainableRule<Context>> getAddedContextRuleChain(
			final IndexedClassExpression target) {
		return AbstractChain.getMapBackedChain(
				addedContextRuleHeadByClassExpressions_, target);
	}

	private Chain<ChainableRule<Context>> getRemovedContextRuleChain(
			final IndexedClassExpression target) {
		return AbstractChain.getMapBackedChain(
				removedContextRuleHeadByClassExpressions_, target);
	}

	boolean registerAddedContextInitRule(ChainableRule<Context> rule) {
		return rule.addTo(getAddedContextInitRuleChain());
	}

	boolean registerRemovedContextInitRule(ChainableRule<Context> rule) {
		return rule.addTo(getRemovedContextInitRuleChain());
	}

	/**
	 * Get the object assigned to the given indexed class expression for storing
	 * index additions, or assign a new one if no such object has been assigned.
	 * 
	 * @param target
	 *            the indexed class expressions for which to find the changes
	 *            additions object
	 * @return the object which contains all index additions for the given
	 *         indexed class expression
	 */
	boolean registerAddedContextRule(IndexedClassExpression target,
			ChainableRule<Context> rule) {
		return rule.addTo(getAddedContextRuleChain(target));
	}

	/**
	 * Get the object assigned to the given indexed class expression for storing
	 * index deletions, or assign a new one if no such object has been assigned.
	 * 
	 * @param target
	 *            the indexed class expressions for which to find the changes
	 *            deletions object
	 * @return the object which contains all index deletions for the given
	 *         indexed class expression
	 */
	boolean registerRemovedContextRule(IndexedClassExpression target,
			ChainableRule<Context> rule) {
		return rule.addTo(getRemovedContextRuleChain(target));
	}
}
