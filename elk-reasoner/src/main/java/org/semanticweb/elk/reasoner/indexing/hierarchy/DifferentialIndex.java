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
	 * the {@link ElkNamedIndividual} added during the last incremental session
	 */
	private Set<ElkNamedIndividual> addedIndividuals_;

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
	private void init() {
		initClassSignatureChanges();
		initIndividualSignatureChanges();
		initAdditions();
		initDeletions();
	}

	public void initClassSignatureChanges() {
		this.addedClasses_ = new ArrayHashSet<ElkClass>(32);
	}

	public void initIndividualSignatureChanges() {
		this.addedIndividuals_ = new ArrayHashSet<ElkNamedIndividual>(32);
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
	public void addClass(ElkClass newClass) {
		if (incrementalMode) {
			addedClasses_.add(newClass);
		} else {
			super.addClass(newClass);
		}

	}

	@Override
	public void removeClass(ElkClass oldClass) {
		if (incrementalMode) {
			addedClasses_.remove(oldClass);
		} else {
			super.removeClass(oldClass);
		}
	}

	@Override
	public void addNamedIndividual(ElkNamedIndividual newIndividual) {
		if (incrementalMode) {
			addedIndividuals_.add(newIndividual);
		} else {
			super.addNamedIndividual(newIndividual);
		}
	}

	@Override
	public void removeNamedIndividual(ElkNamedIndividual oldIndividual) {
		if (incrementalMode) {
			addedIndividuals_.remove(oldIndividual);
		} else {
			super.removeNamedIndividual(oldIndividual);
		}
	}

	@Override
	public void add(IndexedClassExpression target, ChainableSubsumerRule newRule) {
		if (incrementalMode) {
			if (newRule.removeFrom(getRemovedContextRuleChain(target))) {
				newRule.addTo(target.getCompositionRuleChain());
			} else
				newRule.addTo(getAddedContextRuleChain(target));
		} else {
			super.add(target, newRule);
		}
	}

	@Override
	public void remove(IndexedClassExpression target,
			ChainableSubsumerRule oldRule) {
		if (incrementalMode) {
			if (!oldRule.removeFrom(getAddedContextRuleChain(target))) {
				oldRule.addTo(getRemovedContextRuleChain(target));
				if (!oldRule.removeFrom(target.getCompositionRuleChain()))
					throw new ElkUnexpectedIndexingException(
							"Cannot remove context rule " + oldRule.getName()
									+ " for " + target);
			}
		} else {
			super.remove(target, oldRule);
		}
	}

	@Override
	public void addContextInitRule(ChainableContextInitRule newRule) {
		if (incrementalMode) {
			if (newRule.removeFrom(getRemovedContextInitRuleChain())) {
				newRule.addTo(getContextInitRuleChain());
			} else
				newRule.addTo(getAddedContextInitRuleChain());
		} else {
			super.addContextInitRule(newRule);
		}

	}

	@Override
	public void removeContextInitRule(ChainableContextInitRule oldRule) {
		if (incrementalMode) {
			if (!oldRule.removeFrom(getAddedContextInitRuleChain())) {
				oldRule.addTo(getRemovedContextInitRuleChain());
				if (!oldRule.removeFrom(getContextInitRuleChain()))
					throw new ElkUnexpectedIndexingException(
							"Cannot remove context initialization rule "
									+ oldRule.getName());
			}
		} else {
			super.removeContextInitRule(oldRule);
		}
	}

	@Override
	public void add(IndexedObject newObject) {
		if (incrementalMode) {
			addIndexedObject(newObject);
		} else {
			super.add(newObject);
		}
	}

	@Override
	public void remove(IndexedObject oldObject) {
		if (incrementalMode) {
			LOGGER_.trace("To remove: {}", oldObject);
			oldObject.accept(todoDeletions_.inserter);
		} else {
			super.remove(oldObject);
		}
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

	void addIndexedObject(IndexedObject iobj) {
		LOGGER_.trace("Adding: {}", iobj);

		if (!iobj.accept(todoDeletions_.deletor))
			iobj.accept(objectCache.inserter);

	}
}
