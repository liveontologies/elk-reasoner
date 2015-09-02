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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedObject;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkUnexpectedIndexingException;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClass;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedEntityVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.Rule;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.ChainableContextInitRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ChainableSubsumerRule;
import org.semanticweb.elk.util.collections.ArrayHashMap;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.Operations;
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
	 * added and removed {@link ElkClass}es during the last incremental session
	 */
	private Set<ElkClass> addedClasses_, removedClasses_;

	/**
	 * added and removed {@link ElkNamedIndividual}s during the last incremental
	 * session
	 */
	private Set<ElkNamedIndividual> addedIndividuals_, removedIndividuals_;

	/**
	 * added and removed {@link ElkObjectProperty}ies during the last
	 * incremental session
	 */
	private Set<ElkObjectProperty> addedObjectProperties_,
			removedObjectProperties_;

	private final IndexedEntityVisitor<Void> entityInsertionListener_ = new EntityInsertionListener(),
			entityDeletionListener_ = new EntityDeletionListener();

	/**
	 * Pending deletions of {@link CachedIndexedObject}s
	 */
	private Set<CachedIndexedObject<?>> todoDeletions_;

	/**
	 * The added and removed initialization {@link Rule}s
	 */
	private ChainableContextInitRule addedContextInitRules_,
			removedContextInitRules_;

	/**
	 * The maps of added and removed {@link Rule}s for index class expressions;
	 */
	private Map<ModifiableIndexedClassExpression, ChainableSubsumerRule> addedContextRuleHeadByClassExpressions_,
			removedContextRuleHeadByClassExpressions_;

	private Map<ModifiableIndexedClass, ModifiableIndexedClassExpression> addedDefinitions_,
			removedDefinitions_;

	private Map<ModifiableIndexedClass, ElkAxiom> addedDefinitionReasons_,
			removedDefinitionReasons_;

	public DifferentialIndex() {
		init();
	}

	/**
	 * Initializes all data structures
	 */
	void init() {
		initClassChanges();
		initIndividualChanges();
		initObjectPropertyChanges();
		initAdditions();
		initDeletions();
	}

	public void initClassChanges() {
		this.addedClasses_ = new ArrayHashSet<ElkClass>(32);
		this.removedClasses_ = new ArrayHashSet<ElkClass>(32);
	}

	public void initIndividualChanges() {
		this.addedIndividuals_ = new ArrayHashSet<ElkNamedIndividual>(32);
		this.removedIndividuals_ = new ArrayHashSet<ElkNamedIndividual>(32);
	}

	public void initObjectPropertyChanges() {
		this.addedObjectProperties_ = new ArrayHashSet<ElkObjectProperty>(32);
		this.removedObjectProperties_ = new ArrayHashSet<ElkObjectProperty>(32);
	}

	public void initAdditions() {
		this.addedContextInitRules_ = null;
		this.addedContextRuleHeadByClassExpressions_ = new ArrayHashMap<ModifiableIndexedClassExpression, ChainableSubsumerRule>(
				32);
		this.addedDefinitions_ = new ArrayHashMap<ModifiableIndexedClass, ModifiableIndexedClassExpression>(
				32);
		this.addedDefinitionReasons_ = new ArrayHashMap<ModifiableIndexedClass, ElkAxiom>(
				32);
	}

	public void initDeletions() {
		this.removedContextInitRules_ = null;
		this.todoDeletions_ = new ArrayHashSet<CachedIndexedObject<?>>(1024);
		this.removedContextRuleHeadByClassExpressions_ = new ArrayHashMap<ModifiableIndexedClassExpression, ChainableSubsumerRule>(
				32);
		this.removedDefinitions_ = new ArrayHashMap<ModifiableIndexedClass, ModifiableIndexedClassExpression>(
				32);
		this.removedDefinitionReasons_ = new ArrayHashMap<ModifiableIndexedClass, ElkAxiom>(
				32);
	}

	/* read-only methods */

	// nothing so far

	/* read-write methods */

	@Override
	public void add(CachedIndexedObject<?> input) {
		if (!incrementalMode) {
			super.add(input);
			return;
		}
		// else incrementalMode
		LOGGER_.trace("{}: to add", input);
		if (input instanceof IndexedEntity)
			((IndexedEntity) input).accept(entityInsertionListener_);
		if (todoDeletions_.remove(input))
			return;
		// else
		super.add(input);
	}

	@Override
	public void remove(CachedIndexedObject<?> input) {
		if (!incrementalMode) {
			super.remove(input);
			return;
		}
		// else incrementalMode
		LOGGER_.trace("{}: to remove", input);
		if (input instanceof IndexedEntity)
			((IndexedEntity) input).accept(entityDeletionListener_);
		todoDeletions_.add(input);
	}

	private class EntityInsertionListener implements IndexedEntityVisitor<Void> {

		@Override
		public Void visit(IndexedClass element) {
			ElkClass entity = element.getElkEntity();
			if (!removedClasses_.remove(entity))
				addedClasses_.add(entity);
			return null;
		}

		@Override
		public Void visit(IndexedIndividual element) {
			ElkNamedIndividual entity = element.getElkEntity();
			if (!removedIndividuals_.remove(entity))
				addedIndividuals_.add(entity);
			return null;
		}

		@Override
		public Void visit(IndexedObjectProperty element) {
			ElkObjectProperty entity = element.getElkEntity();
			if (!removedObjectProperties_.remove(entity))
				addedObjectProperties_.add(entity);
			return null;
		}

	}

	private class EntityDeletionListener implements IndexedEntityVisitor<Void> {

		@Override
		public Void visit(IndexedClass element) {
			ElkClass entity = element.getElkEntity();
			if (!addedClasses_.remove(entity))
				removedClasses_.add(entity);
			return null;
		}

		@Override
		public Void visit(IndexedIndividual element) {
			ElkNamedIndividual entity = element.getElkEntity();
			if (!addedIndividuals_.remove(entity))
				removedIndividuals_.add(entity);
			return null;
		}

		@Override
		public Void visit(IndexedObjectProperty element) {
			ElkObjectProperty entity = element.getElkEntity();
			if (!addedObjectProperties_.remove(entity))
				removedObjectProperties_.add(entity);
			return null;
		}
	}

	@Override
	public boolean add(ModifiableIndexedClassExpression target,
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
	public boolean remove(ModifiableIndexedClassExpression target,
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
	public boolean tryAddDefinition(ModifiableIndexedClass target,
			ModifiableIndexedClassExpression definition, ElkAxiom reason) {
		if (!incrementalMode)
			return super.tryAddDefinition(target, definition, reason);
		// for incremental mode:
		IndexedClassExpression removedDefintion = removedDefinitions_
				.get(target);
		ElkAxiom removedDefinitionReason = removedDefinitionReasons_
				.get(target);
		if (target.getDefinition() != removedDefintion
				|| addedDefinitions_.get(target) != null) {
			// the existing definition was not removed or some definition has
			// been added
			return false;
		}
		// else removing
		if (removedDefintion == definition
				&& removedDefinitionReason.equals(reason)) {
			removedDefinitions_.remove(target);
			removedDefinitionReasons_.remove(target);
			target.setDefinition(definition, reason);
		} else {
			addedDefinitions_.put(target, definition);
			addedDefinitionReasons_.put(target, reason);
		}
		return true;
	}

	@Override
	public boolean tryRemoveDefinition(ModifiableIndexedClass target,
			ModifiableIndexedClassExpression definition, ElkAxiom reason) {
		if (!incrementalMode)
			return super.tryRemoveDefinition(target, definition, reason);
		// for incremental mode:
		IndexedClassExpression addedDefinition = addedDefinitions_.get(target);
		ElkAxiom addedDefinitionReason = addedDefinitionReasons_.get(target);
		if (addedDefinition == definition
				&& addedDefinitionReason.equals(reason)) {
			addedDefinitions_.remove(target);
			addedDefinitionReasons_.remove(target);
			return true;
		}
		// else
		if (addedDefinition != null || target.getDefinition() != definition
				|| !target.getDefinitionReason().equals(reason))
			return false;
		// else
		target.removeDefinition();
		removedDefinitions_.put(target, definition);
		removedDefinitionReasons_.put(target, reason);
		return true;
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
	public Map<? extends IndexedClassExpression, ChainableSubsumerRule> getAddedContextRulesByClassExpressions() {
		return this.addedContextRuleHeadByClassExpressions_;
	}

	/**
	 * @return the map from indexed class expressions to the corresponding
	 *         objects containing index deletions for these class expressions
	 */
	public Map<? extends IndexedClassExpression, ChainableSubsumerRule> getRemovedContextRulesByClassExpressions() {
		return this.removedContextRuleHeadByClassExpressions_;
	}

	/**
	 * @return the added definitions for {@link IndexedClass}es
	 */
	public Map<? extends IndexedClass, ? extends IndexedClassExpression> getAddedDefinitions() {
		return this.addedDefinitions_;
	}

	/**
	 * @return the {@link ElkAxiom}s from which the added definitions for the
	 *         corresponding {@link IndexedClass}es originate
	 */
	public Map<? extends IndexedClass, ? extends ElkAxiom> getAddedDefinitionReasons() {
		return this.addedDefinitionReasons_;
	}

	/**
	 * @return the removed definitions for {@link IndexedClass}es
	 */
	public Map<? extends IndexedClass, ? extends IndexedClassExpression> getRemovedDefinitions() {
		return this.removedDefinitions_;
	}

	/**
	 * @return the {@link ElkAxiom}s from which the removed definitions for the
	 *         corresponding {@link IndexedClass}es originate
	 */
	public Map<? extends IndexedClass, ? extends ElkAxiom> getRemovedDefinitionReasons() {
		return this.removedDefinitionReasons_;
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
	public Iterable<? extends IndexedClassExpression> getRemovedClassExpressions() {
		return Operations.filter(todoDeletions_, IndexedClassExpression.class);
	}

	/**
	 * Removes the deleted rules from this {@link DifferentialIndex}; these
	 * rules should be already applied in the main index during their
	 * registration
	 */
	public void clearDeletedRules() {
		for (CachedIndexedObject<?> deletion : todoDeletions_) {
			LOGGER_.trace("{}: comitting removal", deletion);
			super.remove(deletion);
		}
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
		for (ModifiableIndexedClassExpression target : addedContextRuleHeadByClassExpressions_
				.keySet()) {
			LOGGER_.trace("{}: committing context rule additions", target);

			nextClassExpressionRule = addedContextRuleHeadByClassExpressions_
					.get(target);
			classExpressionRuleChain = target.getCompositionRuleChain();
			while (nextClassExpressionRule != null) {
				nextClassExpressionRule.addTo(classExpressionRuleChain);
				nextClassExpressionRule = nextClassExpressionRule.next();
			}
		}
		for (ModifiableIndexedClass target : addedDefinitions_.keySet()) {
			ModifiableIndexedClassExpression definition = addedDefinitions_
					.get(target);
			ElkAxiom reason = addedDefinitionReasons_.get(target);
			LOGGER_.trace("{}: committing definition addition {}", target,
					definition);
			if (!target.setDefinition(definition, reason))
				throw new ElkUnexpectedIndexingException(target);
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
			initClassChanges();
			initIndividualChanges();
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
			final ModifiableIndexedClassExpression target) {
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
			final ModifiableIndexedClassExpression target) {
		return AbstractChain.getMapBackedChain(
				removedContextRuleHeadByClassExpressions_, target);
	}
}
