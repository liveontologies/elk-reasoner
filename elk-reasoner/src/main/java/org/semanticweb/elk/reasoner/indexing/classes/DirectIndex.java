/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.indexing.classes;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.predefined.PredefinedElkEntityFactory;
import org.semanticweb.elk.reasoner.completeness.Feature;
import org.semanticweb.elk.reasoner.completeness.OccurrenceListener;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedOwlNothing;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedOwlThing;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.model.OccurrenceIncrement;
import org.semanticweb.elk.reasoner.indexing.model.OntologyIndex;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.ChainableContextInitRule;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.LinkedContextInitRule;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.RootContextInitializationRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ChainableSubsumerRule;
import org.semanticweb.elk.util.collections.chains.AbstractChain;
import org.semanticweb.elk.util.collections.chains.Chain;

/**
 * An implementation of {@link ModifiableOntologyIndex}
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public class DirectIndex extends ModifiableIndexedObjectCacheImpl
		implements ModifiableOntologyIndex {

	private ChainableContextInitRule contextInitRules_;

	private final List<OntologyIndex.ChangeListener> listeners_;

	private List<OccurrenceListener> occurrenceListeners_ = new ArrayList<OccurrenceListener>();

	public DirectIndex(final PredefinedElkEntityFactory elkFactory) {
		super(elkFactory);
		this.listeners_ = new ArrayList<OntologyIndex.ChangeListener>();
		// the context root initialization rule is always registered
		RootContextInitializationRule.addRuleFor(this);
		// owl:Thing and owl:Nothing always occur
		OccurrenceIncrement addition = OccurrenceIncrement
				.getNeutralIncrement(1);
		getOwlThing().updateOccurrenceNumbers(this, addition);
		getOwlNothing().updateOccurrenceNumbers(this, addition);
		getOwlTopObjectProperty().updateOccurrenceNumbers(this, addition);
		getOwlBottomObjectProperty().updateOccurrenceNumbers(this, addition);
		// register listeners for occurrences
		getOwlThing().addListener(new CachedIndexedOwlThing.ChangeListener() {

			@Override
			public void negativeOccurrenceAppeared() {
				for (int i = 0; i < listeners_.size(); i++) {
					listeners_.get(i).negativeOwlThingAppeared();
				}
			}

			@Override
			public void negativeOccurrenceDisappeared() {
				for (int i = 0; i < listeners_.size(); i++) {
					listeners_.get(i).negativeOwlThingDisappeared();
				}

			}

		});
		getOwlNothing()
				.addListener(new CachedIndexedOwlNothing.ChangeListener() {

					@Override
					public void positiveOccurrenceAppeared() {
						for (int i = 0; i < listeners_.size(); i++) {
							listeners_.get(i).positiveOwlNothingAppeared();
						}
					}

					@Override
					public void positiveOccurrenceDisappeared() {
						for (int i = 0; i < listeners_.size(); i++) {
							listeners_.get(i).positiveOwlNothingDisappeared();
						}

					}
				});
	}

	/* read-only methods required by the interface */

	@Override
	public final LinkedContextInitRule getContextInitRuleHead() {
		return contextInitRules_;
	}

	/* read-write methods required by the interface */

	@Override
	public boolean addContextInitRule(ChainableContextInitRule newRule) {
		return newRule.addTo(getContextInitRuleChain());
	}

	@Override
	public boolean removeContextInitRule(ChainableContextInitRule oldRule) {
		return oldRule.removeFrom(getContextInitRuleChain());
	}

	@Override
	public boolean add(ModifiableIndexedClassExpression target,
			ChainableSubsumerRule rule) {
		return rule.addTo(target.getCompositionRuleChain());
	}

	@Override
	public boolean remove(ModifiableIndexedClassExpression target,
			ChainableSubsumerRule rule) {
		return rule.removeFrom(target.getCompositionRuleChain());
	}

	@Override
	public final boolean hasNegativeOwlThing() {
		return getOwlThing().occursNegatively();
	}

	@Override
	public final boolean hasPositiveOwlNothing() {
		return getOwlNothing().occursPositively();
	}

	@Override
	public boolean tryAddDefinition(ModifiableIndexedClass target,
			ModifiableIndexedClassExpression definition, ElkAxiom reason) {
		return target.setDefinition(definition, reason);
	}

	@Override
	public boolean tryRemoveDefinition(ModifiableIndexedClass target,
			ModifiableIndexedClassExpression definition, ElkAxiom reason) {
		if (target.getDefinition() != definition
				|| !target.getDefinitionReason().equals(reason))
			// it was not defined by this definition
			return false;
		// else
		target.removeDefinition();
		return true;
	}

	/* class-specific methods */

	/**
	 * @return a {@link Chain} view of context initialization rules assigned to
	 *         this {@link OntologyIndex}; it can be used for inserting new
	 *         rules or deleting existing ones
	 */
	public Chain<ChainableContextInitRule> getContextInitRuleChain() {
		return new AbstractChain<ChainableContextInitRule>() {

			@Override
			public ChainableContextInitRule next() {
				return contextInitRules_;
			}

			@Override
			public void setNext(ChainableContextInitRule tail) {
				contextInitRules_ = tail;
				for (int i = 0; i < listeners_.size(); i++) {
					listeners_.get(i).contextInitRuleHeadSet(tail);
				}
			}
		};
	}

	@Override
	public boolean addListener(OntologyIndex.ChangeListener listener) {
		if (!super.addListener(listener)) {
			return false;
		}
		// else
		if (!listeners_.add(listener)) {
			// revert
			super.removeListener(listener);
			return false;
		}
		// else
		return true;
	}

	@Override
	public boolean removeListener(OntologyIndex.ChangeListener listener) {
		if (!super.removeListener(listener)) {
			return false;
		}
		// else
		if (!listeners_.remove(listener)) {
			// revert
			super.addListener(listener);
			return false;
		}
		// else
		return true;
	}

	@Override
	public void addOccurrenceListener(OccurrenceListener listener) {
		occurrenceListeners_.add(listener);
	}

	@Override
	public void removeOccurrenceListener(OccurrenceListener listener) {
		occurrenceListeners_.remove(listener);
	}
	
	@Override
	public void occurrenceChanged(Feature occurrence, int increment) {
		for (OccurrenceListener listener : occurrenceListeners_) {
			listener.occurrenceChanged(occurrence, increment);
		}
	}

}
