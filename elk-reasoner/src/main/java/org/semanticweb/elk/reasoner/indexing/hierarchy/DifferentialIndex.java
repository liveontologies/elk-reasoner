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
import java.util.Collection;
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
	 * The list of {@link ElkClass}es to be removed from
	 * the signature of the ontology; 
	 */
	private final List<ElkClass> removedClasses_;

	private final List<ElkNamedIndividual> removedIndividuals_;

	private final OntologyIndex mainIndex_;

	/**
	 * object that should be deleted
	 */
	private final IndexedObjectCache todoDeletions_;

	/**
	 * The added and removed initialization {@link Rule}s
	 */
	private ChainableRule<Context> addedContextInitRules_ = null,
			removedContextInitRules_ = null;

	/**
	 * The maps of added and removed {@link Rule}s for index class expressions;
	 */
	private final Map<IndexedClassExpression, ChainableRule<Context>> addedContextRuleHeadByClassExpressions_,
			removedContextRuleHeadByClassExpressions_;

	private final ElkAxiomProcessor axiomInserter_, axiomDeleter_;

	public DifferentialIndex(OntologyIndex mainIndex,
			IndexedObjectCache objectCache, IndexedClass owlNothing) {
		this.removedClasses_ = new ArrayList<ElkClass>(127);
		this.removedIndividuals_ = new ArrayList<ElkNamedIndividual>();
		this.todoDeletions_ = new IndexedObjectCache();
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
	 * @return the list of ELK classes to be removed from the signature of the
	 *         ontology
	 */
	public List<ElkClass> getRemovedClasses() {
		return this.removedClasses_;
	}
	
	public Collection<IndexedClassExpression> getRemovedClassExpressions() {
		return todoDeletions_.indexedClassExpressionLookup;
	}

	public List<ElkNamedIndividual> getRemovedIndividuals() {
		return this.removedIndividuals_;
	}

	/**
	 * Removes the deleted rules from this {@link DifferentialIndex}; these
	 * rules should be already applied in the main index during their
	 * registration
	 */
	public void clearDeletedRules() {
		removedContextInitRules_ = null;
		removedContextRuleHeadByClassExpressions_.clear();
		mainIndex_.getIndexedObjectCache().subtract(todoDeletions_);
		todoDeletions_.clear();
	}

	/**
	 * Commits the added rules to the main index and removes them from this
	 * {@link DifferentialIndex}.
	 */
	public void commitAddedRules() {
		// commit changes in the context initialization rules
		ChainableRule<Context> nextRule;
		Chain<ChainableRule<Context>> chain;

		nextRule = addedContextInitRules_;
		chain = mainIndex_.getContextInitRuleChain();
		while (nextRule != null) {
			nextRule.addTo(chain);
			nextRule = nextRule.next();
		}
		addedContextInitRules_ = null;

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
		removedClasses_.clear();
		removedIndividuals_.clear();
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

	void removeClass(ElkClass oldClass) {
		removedClasses_.add(oldClass);
	}

	void addIndexedObject(IndexedObject iobj) {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("Adding: " + iobj + " (hash: " + iobj.hashCode()
					+ ")");
		if (!iobj.accept(todoDeletions_.deletor))
			iobj.accept(mainIndex_.getIndexedObjectCache().inserter);
	}

	void removeIndexedObject(IndexedObject iobj) {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("To remove: " + iobj + " (hash: " + iobj.hashCode()
					+ ")");
		iobj.accept(todoDeletions_.inserter);
	}

	void removeNamedIndividual(ElkNamedIndividual newIndividual) {
		removedIndividuals_.add(newIndividual);
	}

	void registerAddedContextInitRule(ChainableRule<Context> rule) {
		rule.addTo(getAddedContextInitRuleChain());
	}

	void registerRemovedContextInitRule(ChainableRule<Context> rule) {
		if (!rule.removeFrom(getAddedContextInitRuleChain())) {
			rule.addTo(getRemovedContextInitRuleChain());
			if (!rule.removeFrom(mainIndex_.getContextInitRuleChain()))
				throw new ElkUnexpectedIndexingException(
						"Cannot remove context initialization rule "
								+ rule.getName());
		}
	}

	void registerAddedContextRule(IndexedClassExpression target,
			ChainableRule<Context> rule) {
		rule.addTo(getAddedContextRuleChain(target));
	}

	void registerRemovedContextRule(IndexedClassExpression target,
			ChainableRule<Context> rule) {
		if (!rule.removeFrom(getAddedContextRuleChain(target))) {
			rule.addTo(getRemovedContextRuleChain(target));
			if (!rule.removeFrom(target.getCompositionRuleChain()))
				throw new ElkUnexpectedIndexingException(
						"Cannot remove context rule " + rule.getName()
								+ " for " + target);
		}
	}
}
