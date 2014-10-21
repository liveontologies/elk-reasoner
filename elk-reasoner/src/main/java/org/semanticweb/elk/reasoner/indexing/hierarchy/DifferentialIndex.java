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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.reasoner.saturation.rules.Rule;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.ChainableContextInitRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ChainableSubsumerRule;
import org.semanticweb.elk.util.collections.ArrayHashMap;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.chains.AbstractChain;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class DifferentialIndex extends DirectIndex {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(DifferentialIndex.class);

	/**
	 * if {@code true} all changes will be applied incrementally; otherwise
	 * changes are applied directly
	 */
	boolean incrementalMode = false;

	/**
	 * the {@link ElkClass} added during the last incremental session
	 */
	private Set<ElkClass> addedClasses_;

	/**
	 * the {@link ElkClass} removed during the last incremental session
	 */
	private Set<ElkClass> removedClasses_;

	/**
	 * the {@link ElkNamedIndividual} added during the last incremental session
	 */
	private Set<ElkNamedIndividual> addedIndividuals_;

	/**
	 * the {@link ElkNamedIndividual} removed during the last incremental
	 * session
	 */
	private Set<ElkNamedIndividual> removedIndividuals_;

	/**
	 * Objects that should be deleted
	 */
	private IndexedObjectCache todoDeletions_;

	/**
	 * The added and removed initialization {@link Rule}s
	 */
	private ChainableContextInitRule addedContextInitRules_,
			removedContextInitRules_;

	/**
	 * The maps of added and removed {@link Rule}s for index class expressions;
	 */
	private Map<IndexedClassExpression, ChainableSubsumerRule> addedContextRuleHeadByClassExpressions_,
			removedContextRuleHeadByClassExpressions_;

	public DifferentialIndex(IndexedObjectCache objectCache) {
		super(objectCache);
		init();
	}

	/**
	 * Initializes all datastructures
	 */
	void init() {
		initClassSignatureChanges();
		initIndividualSignatureChanges();
		initAdditions();
		initDeletions();
	}

	public void initClassSignatureChanges() {
		this.addedClasses_ = new ArrayHashSet<ElkClass>(32);
		this.removedClasses_ = new ArrayHashSet<ElkClass>(32);
	}

	public void initIndividualSignatureChanges() {
		this.addedIndividuals_ = new ArrayHashSet<ElkNamedIndividual>(32);
		this.removedIndividuals_ = new ArrayHashSet<ElkNamedIndividual>(32);
	}

	public void initAdditions() {
		this.addedContextInitRules_ = null;
		this.addedContextRuleHeadByClassExpressions_ = new ArrayHashMap<IndexedClassExpression, ChainableSubsumerRule>(
				32);
	}

	public void initDeletions() {
		this.removedContextInitRules_ = null;
		this.todoDeletions_ = new IndexedObjectCache();
		this.removedContextRuleHeadByClassExpressions_ = new ArrayHashMap<IndexedClassExpression, ChainableSubsumerRule>(
				32);
	}

	/* read-only methods */

	// nothing so far

	/* read-write methods */

	@Override
	public boolean addClass(ElkClass newClass) {
		if (!incrementalMode) {
			return super.addClass(newClass);
		}
		// else incrementalMode
		if (!removedClasses_.remove(newClass))
			addedClasses_.add(newClass);
		return true;
	}

	@Override
	public boolean removeClass(ElkClass oldClass) {
		if (!incrementalMode) {
			return super.removeClass(oldClass);
		}
		// else incrementalMode
		if (!addedClasses_.remove(oldClass))
			removedClasses_.add(oldClass);
		return true;
	}

	@Override
	public boolean addNamedIndividual(ElkNamedIndividual newIndividual) {
		if (!incrementalMode) {
			return super.addNamedIndividual(newIndividual);
		}
		// else incrementalMode
		if (!removedIndividuals_.remove(newIndividual))
			addedIndividuals_.add(newIndividual);
		return true;
	}

	@Override
	public boolean removeNamedIndividual(ElkNamedIndividual oldIndividual) {
		if (!incrementalMode) {
			return super.removeNamedIndividual(oldIndividual);
		}
		// else incrementalMode
		if (!addedIndividuals_.remove(oldIndividual))
			removedIndividuals_.add(oldIndividual);
		return true;
	}

	@Override
	public boolean add(IndexedClassExpression target,
			ChainableSubsumerRule newRule) {
		if (!incrementalMode) {
			return super.add(target, newRule);
		}
		// else incrementalMode
		if (newRule.removeFrom(getRemovedContextRuleChain(target))) {
			if (newRule.addTo(target.getCompositionRuleChain()))
				return true;
			// else revert
			newRule.addTo(getRemovedContextRuleChain(target));
		}
		// if above fails
		return newRule.addTo(getAddedContextRuleChain(target));
	}

	@Override
	public boolean remove(IndexedClassExpression target,
			ChainableSubsumerRule oldRule) {
		if (!incrementalMode) {
			return super.remove(target, oldRule);
		}
		// else incrementalMode
		if (oldRule.removeFrom(getAddedContextRuleChain(target)))
			return true;
		// else
		if (oldRule.addTo(getRemovedContextRuleChain(target))) {
			if (oldRule.removeFrom(target.getCompositionRuleChain()))
				return true;
			// else revert
			oldRule.removeFrom(getRemovedContextRuleChain(target));
		}
		return false;
	}

	@Override
	public boolean addContextInitRule(ChainableContextInitRule newRule) {
		if (!incrementalMode) {
			return super.addContextInitRule(newRule);
		}
		// else incrementalMode
		if (newRule.removeFrom(getRemovedContextInitRuleChain())) {
			if (newRule.addTo(getContextInitRuleChain()))
				return true;
			// else revert
			newRule.addTo(getRemovedContextInitRuleChain());
		}
		// if above fails
		return newRule.addTo(getAddedContextInitRuleChain());
	}

	@Override
	public boolean removeContextInitRule(ChainableContextInitRule oldRule) {
		if (!incrementalMode) {
			return super.removeContextInitRule(oldRule);
		}
		// else incrementalMode
		if (oldRule.removeFrom(getAddedContextInitRuleChain()))
			return true;
		// else
		if (oldRule.addTo(getRemovedContextInitRuleChain())) {
			if (oldRule.removeFrom(getContextInitRuleChain()))
				return true;
			// else revert
			oldRule.removeFrom(getRemovedContextInitRuleChain());
		}
		return false;
	}

	@Override
	public boolean add(IndexedObject newObject) {
		if (!incrementalMode) {
			return super.add(newObject);
		}
		// else incrementalMode
		LOGGER_.trace("{}: to add", newObject);
		if (newObject.accept(todoDeletions_.deletor))
			return true;
		// else
		return newObject.accept(objectCache.inserter);
	}

	@Override
	public boolean remove(IndexedObject oldObject) {
		if (!incrementalMode) {
			return super.remove(oldObject);
		}
		// else incrementalMode
		LOGGER_.trace("{}: to remove", oldObject);
		return oldObject.accept(todoDeletions_.inserter);
	}

	/* incremental-specific methods */

	/**
	 * @return the context initialization rules added during the last
	 *         incremental session
	 */
	public ChainableContextInitRule getAddedContextInitRules() {
		return addedContextInitRules_;
	}

	/**
	 * @return the context initialization rules removed during the last
	 *         incremental session
	 */
	public ChainableContextInitRule getRemovedContextInitRules() {
		return removedContextInitRules_;
	}

	/**
	 * @return the map from indexed class expressions to the corresponding
	 *         objects containing index additions for these class expressions
	 */
	public Map<IndexedClassExpression, ChainableSubsumerRule> getAddedContextRulesByClassExpressions() {
		return this.addedContextRuleHeadByClassExpressions_;
	}

	/**
	 * @return the map from indexed class expressions to the corresponding
	 *         objects containing index deletions for these class expressions
	 */
	public Map<IndexedClassExpression, ChainableSubsumerRule> getRemovedContextRulesByClassExpressions() {
		return this.removedContextRuleHeadByClassExpressions_;
	}

	/**
	 * @return the {@link ElkClass} added during the last incremental session
	 */
	public Collection<ElkClass> getAddedClasses() {
		return this.addedClasses_;
	}

	/**
	 * @return the collection of named individuals added to the signature
	 */
	public Collection<ElkNamedIndividual> getAddedIndividuals() {
		return this.addedIndividuals_;
	}

	/**
	 * @return the {@link IndexedClassExpression}s removed during the last
	 *         incremental session
	 */
	public Collection<IndexedClassExpression> getRemovedClassExpressions() {
		return todoDeletions_.indexedClassExpressionLookup;
	}

	/**
	 * Removes the deleted rules from this {@link DifferentialIndex}; these
	 * rules should be already applied in the main index during their
	 * registration
	 */
	public void clearDeletedRules() {
		objectCache.subtract(todoDeletions_);
		initDeletions();
	}

	/**
	 * Commits the added rules to the main index and removes them from this
	 * {@link DifferentialIndex}.
	 */
	public void commitAddedRules() {
		// commit changes in the context initialization rules
		ChainableContextInitRule nextContextInitRule;
		Chain<ChainableContextInitRule> contextInitRuleChain;

		nextContextInitRule = addedContextInitRules_;
		contextInitRuleChain = getContextInitRuleChain();
		while (nextContextInitRule != null) {
			nextContextInitRule.addTo(contextInitRuleChain);
			nextContextInitRule = nextContextInitRule.next();
		}

		// commit changes in rules for IndexedClassExpression
		ChainableSubsumerRule nextClassExpressionRule;
		Chain<ChainableSubsumerRule> classExpressionRuleChain;
		for (IndexedClassExpression target : addedContextRuleHeadByClassExpressions_
				.keySet()) {
			LOGGER_.trace("Committing context rule additions for {}", target);

			nextClassExpressionRule = addedContextRuleHeadByClassExpressions_
					.get(target);
			classExpressionRuleChain = target.getCompositionRuleChain();
			while (nextClassExpressionRule != null) {
				nextClassExpressionRule.addTo(classExpressionRuleChain);
				nextClassExpressionRule = nextClassExpressionRule.next();
			}
		}
		initAdditions();
	}

	/**
	 * @return {@code true} if there are no uncommitted changes in this
	 *         {@link DifferentialIndex}
	 */
	public boolean isEmpty() {
		return addedContextInitRules_ == null
				&& removedContextInitRules_ == null
				&& (addedContextRuleHeadByClassExpressions_ == null || addedContextRuleHeadByClassExpressions_
						.isEmpty())
				&& (removedContextRuleHeadByClassExpressions_ == null || removedContextRuleHeadByClassExpressions_
						.isEmpty());
	}

	/**
	 * Sets the incremental mode for this {@code DifferentialIndex}.
	 * 
	 * @param mode
	 *            if {@code true}, deletions and additions to this indexed are
	 *            stored separately; if {@code false} all changes are
	 *            immediately applied to the index.
	 */
	public void setIncrementalMode(boolean mode) {
		if (this.incrementalMode == mode)
			// already set
			return;
		this.incrementalMode = mode;
		if (!mode) {
			clearDeletedRules();
			commitAddedRules();
			initClassSignatureChanges();
			initIndividualSignatureChanges();
		}
	}

	/**
	 * @return the current value of the incremental mode for this
	 *         {@code DifferentialIndex}
	 * @see #setIncrementalMode(boolean)
	 */
	public boolean isIncrementalMode() {
		return incrementalMode;
	}

	/**
	 * @return the chain of added context initialization rules suitable for
	 *         modifications (addition or deletions) of rules
	 */
	private Chain<ChainableContextInitRule> getAddedContextInitRuleChain() {
		return new AbstractChain<ChainableContextInitRule>() {

			@Override
			public ChainableContextInitRule next() {
				return addedContextInitRules_;
			}

			@Override
			public void setNext(ChainableContextInitRule tail) {
				addedContextInitRules_ = tail;
			}
		};
	}

	/**
	 * @return the chain of removed context initialization rules suitable for
	 *         modifications (addition or deletions) of rules
	 */
	private Chain<ChainableContextInitRule> getRemovedContextInitRuleChain() {
		return new AbstractChain<ChainableContextInitRule>() {

			@Override
			public ChainableContextInitRule next() {
				return removedContextInitRules_;
			}

			@Override
			public void setNext(ChainableContextInitRule tail) {
				removedContextInitRules_ = tail;
			}
		};
	}

	/**
	 * @param target
	 *            the {@link IndexedClassExpression} for which to return the
	 *            chain of added context rules
	 * 
	 * @return the chain of added context rules for the given
	 *         {@link IndexedClassExpression} that is suitable for modifications
	 *         (addition or deletions) of rules
	 */
	private Chain<ChainableSubsumerRule> getAddedContextRuleChain(
			final IndexedClassExpression target) {
		return AbstractChain.getMapBackedChain(
				addedContextRuleHeadByClassExpressions_, target);
	}

	/**
	 * @param target
	 *            the {@link IndexedClassExpression} for which to return the
	 *            chain of removed context rules
	 * 
	 * @return the chain of removed context rules for the given
	 *         {@link IndexedClassExpression} that is suitable for modifications
	 *         (addition or deletions) of rules
	 */
	private Chain<ChainableSubsumerRule> getRemovedContextRuleChain(
			final IndexedClassExpression target) {
		return AbstractChain.getMapBackedChain(
				removedContextRuleHeadByClassExpressions_, target);
	}
}
