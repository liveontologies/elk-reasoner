/*
 * #%L
 * elk-reasoner
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
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedObjectSomeValuesFromVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.NegativeSuperClassExpression;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.properties.SaturatedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.rules.BackwardLinkRules;
import org.semanticweb.elk.reasoner.saturation.rules.ContextRules;
import org.semanticweb.elk.reasoner.saturation.rules.RuleEngine;
import org.semanticweb.elk.util.collections.HashSetMultimap;
import org.semanticweb.elk.util.collections.LazySetIntersection;
import org.semanticweb.elk.util.collections.Multimap;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;
import org.semanticweb.elk.util.logging.CachedTimeThread;

/**
 * Represents all occurrences of an ElkObjectSomeValuesFrom in an ontology.
 * 
 * @author Frantisek Simancik
 * 
 */
public class IndexedObjectSomeValuesFrom extends IndexedClassExpression {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(IndexedObjectSomeValuesFrom.class);

	protected final IndexedObjectProperty property;

	protected final IndexedClassExpression filler;

	IndexedObjectSomeValuesFrom(IndexedObjectProperty indexedObjectProperty,
			IndexedClassExpression filler) {
		this.property = indexedObjectProperty;
		this.filler = filler;
	}

	/**
	 * @return The indexed object property comprising this ObjectSomeValuesFrom.
	 */
	public IndexedObjectProperty getRelation() {
		return property;
	}

	/**
	 * @return The indexed class expression comprising this
	 *         ObjectSomeValuesFrom.
	 */
	public IndexedClassExpression getFiller() {
		return filler;
	}

	public <O> O accept(IndexedObjectSomeValuesFromVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(IndexedClassExpressionVisitor<O> visitor) {
		return accept((IndexedObjectSomeValuesFromVisitor<O>) visitor);
	}

	@Override
	protected void updateOccurrenceNumbers(final IndexUpdater indexUpdater, final int increment,
			final int positiveIncrement, final int negativeIncrement) {

		if (negativeOccurrenceNo == 0 && negativeIncrement > 0) {
			// first negative occurrence of this expression
			registerCompositionRule(indexUpdater);
		}

		if (positiveOccurrenceNo == 0 && positiveIncrement > 0) {
			// first positive occurrence of this expression
			//filler.addPosPropertyInExistential(property);
			indexUpdater.add(filler, new ThisPositiveIndexedObjectSomeValuesFrom());
		}

		positiveOccurrenceNo += positiveIncrement;
		negativeOccurrenceNo += negativeIncrement;

		if (negativeOccurrenceNo == 0 && negativeIncrement < 0) {
			// no negative occurrences of this expression left
			deregisterCompositionRule(indexUpdater);
		}

		if (positiveOccurrenceNo == 0 && positiveIncrement < 0) {
			// no positive occurrences of this expression left
			indexUpdater.remove(filler, new ThisPositiveIndexedObjectSomeValuesFrom());
			//filler.removePosPropertyInExistential(property);
		}
	}

	public void registerCompositionRule(final IndexUpdater indexUpdater) {
		indexUpdater.add(filler, this);
		
		/*filler.getChainCompositionRules()
				.getCreate(ThisCompositionRule.MATCHER_,
						ThisCompositionRule.FACTORY_).addNegExistential(this);*/
	}

	public void deregisterCompositionRule(final IndexUpdater indexUpdater) {
		indexUpdater.remove(filler, this);
		/*Chain<ContextRules> rules = filler.getChainCompositionRules();
		ThisCompositionRule rule = rules.find(ThisCompositionRule.MATCHER_);
		if (rule != null) {
			rule.removeNegExistential(this);
			if (rule.isEmpty())
				rules.remove(ThisCompositionRule.MATCHER_);
		} else {
			// TODO: throw/log something, this should never happen
		}*/
	}

	@Override
	public String toString() {
		return "ObjectSomeValuesFrom(" + this.property + ' ' + this.filler
				+ ')';
	}

	@Override
	public void applyDecompositionRule(RuleEngine ruleEngine, Context context) {
		RuleStatistics stats = ruleEngine.getRulesTimer();

		stats.timeObjectSomeValuesFromDecompositionRule -= CachedTimeThread.currentTimeMillis;
		stats.countObjectSomeValuesFromDecompositionRule++;

		try {
			ruleEngine.produce(ruleEngine.getCreateContext(filler),
					new BackwardLink(context, property));
		} finally {
			stats.timeObjectSomeValuesFromDecompositionRule += CachedTimeThread.currentTimeMillis;
		}
	}
	
	/*
	 * The next two methods treat this object as a negative ObjectSomeValuesFrom
	 */
	@Override
	public boolean add(IndexedClassExpression target) {
		return filler
				.getChainCompositionRules()
				.getCreate(ThisCompositionRule.MATCHER_,
						ThisCompositionRule.FACTORY_).addNegExistential(this);
	}

	@Override
	public boolean remove(IndexedClassExpression target) {
		boolean changed = false;		
		Chain<ContextRules> rules = filler.getChainCompositionRules();
		ThisCompositionRule rule = rules.find(ThisCompositionRule.MATCHER_);
		
		if (rule != null) {
			changed |= rule.removeNegExistential(this);
			if (rule.isEmpty()) {
				rules.remove(ThisCompositionRule.MATCHER_);
				changed = true;
			}
		} else {
			// TODO: throw/log something, this should never happen
		}
		
		return changed;
	}



	private static class ThisCompositionRule extends ContextRules {

		private final Collection<IndexedObjectSomeValuesFrom> negExistentials_;

		ThisCompositionRule(ContextRules tail) {
			super(tail);
			this.negExistentials_ = new ArrayList<IndexedObjectSomeValuesFrom>(
					1);
		}

		private boolean addNegExistential(IndexedObjectSomeValuesFrom existential) {
			return negExistentials_.add(existential);
		}

		private boolean removeNegExistential(
				IndexedObjectSomeValuesFrom existential) {
			return negExistentials_.remove(existential);
		}

		/**
		 * @return {@code true} if this rule never does anything
		 */
		private boolean isEmpty() {
			return negExistentials_.isEmpty();
		}

		@Override
		public void apply(RuleEngine ruleEngine, Context context) {

			RuleStatistics stats = ruleEngine.getRulesTimer();

			stats.timeObjectSomeValuesFromCompositionRule -= CachedTimeThread.currentTimeMillis;
			stats.countObjectSomeValuesFromCompositionRule++;

			try {

				final Set<IndexedPropertyChain> candidatePropagationProperties = context
						.getRoot().getPosPropertiesInExistentials();

				if (candidatePropagationProperties == null)
					return;

				for (IndexedObjectSomeValuesFrom e : negExistentials_) {
					IndexedPropertyChain relation = e.getRelation();
					/*
					 * creating propagations for relevant sub-properties of the
					 * relation
					 */
					for (IndexedPropertyChain property : new LazySetIntersection<IndexedPropertyChain>(
							candidatePropagationProperties, relation
									.getSaturated().getSubProperties())) {
						addPropagation(ruleEngine, property, e, context);
					}

					/*
					 * creating propagations for relevant sub-compositions of
					 * the relation
					 */
					for (IndexedPropertyChain property : relation
							.getSaturated().getSubCompositions()) {
						SaturatedPropertyChain propertySaturation = property
								.getSaturated();
						if (!new LazySetIntersection<IndexedPropertyChain>(
								candidatePropagationProperties,
								propertySaturation.getRightSubProperties())
								.isEmpty()) {
							/*
							 * create propagations for told super-properties of
							 * the chain instead of the chain itself if the
							 * optimization is on. otherwise a composed backward
							 * link will be created for a super-property while
							 * the propagation for the chain, so we can lose the
							 * entailment.
							 */
							if (SaturatedPropertyChain.REPLACE_CHAINS_BY_TOLD_SUPER_PROPERTIES
									&& property.getRightChains() == null) {
								for (IndexedPropertyChain superChain : property
										.getToldSuperProperties()) {
									addPropagation(ruleEngine, superChain, e,
											context);
								}
							} else {
								addPropagation(ruleEngine, property, e, context);
							}
						}
					}

					// propagating to the this context if relation is reflexive
					if (relation.getSaturated().isReflexive())
						ruleEngine.produce(context,
								new NegativeSuperClassExpression(e));
				}
			} finally {
				stats.timeObjectSomeValuesFromCompositionRule += CachedTimeThread.currentTimeMillis;
			}
		}

		private static void addPropagation(RuleEngine ruleEngine,
				IndexedPropertyChain propRelation,
				IndexedClassExpression carry, Context context) {

			if (LOGGER_.isTraceEnabled())
				LOGGER_.trace(context.getRoot() + ": new propagation "
						+ propRelation + "->" + carry);

			if (context
					.getBackwardLinkRulesChain()
					.getCreate(ThisBackwardLinkRule.MATCHER_,
							ThisBackwardLinkRule.FACTORY_)
					.addPropagationByObjectProperty(propRelation, carry)) {
				// propagate over all backward links
				final Multimap<IndexedPropertyChain, Context> backLinks = context
						.getBackwardLinksByObjectProperty();

				Collection<Context> targets = backLinks.get(propRelation);

				for (Context target : targets)
					ruleEngine.produce(target,
							new NegativeSuperClassExpression(carry));
			}

		}

		private static Matcher<ContextRules, ThisCompositionRule> MATCHER_ = new SimpleTypeBasedMatcher<ContextRules, ThisCompositionRule>(
				ThisCompositionRule.class);

		private static ReferenceFactory<ContextRules, ThisCompositionRule> FACTORY_ = new ReferenceFactory<ContextRules, ThisCompositionRule>() {
			@Override
			public ThisCompositionRule create(ContextRules tail) {
				return new ThisCompositionRule(tail);
			}
		};

	}

	private static class ThisBackwardLinkRule extends BackwardLinkRules {

		private final Multimap<IndexedPropertyChain, IndexedClassExpression> propagationsByObjectProperty_;

		ThisBackwardLinkRule(BackwardLinkRules tail) {
			super(tail);
			this.propagationsByObjectProperty_ = new HashSetMultimap<IndexedPropertyChain, IndexedClassExpression>(
					1);
		}

		private boolean addPropagationByObjectProperty(
				IndexedPropertyChain propRelation,
				IndexedClassExpression conclusion) {
			return propagationsByObjectProperty_.add(propRelation, conclusion);
		}

		@Override
		public void apply(RuleEngine ruleEngine, BackwardLink link) {
			RuleStatistics stats = ruleEngine.getRulesTimer();

			stats.timeObjectSomeValuesFromBackwardLinkRule -= CachedTimeThread.currentTimeMillis;
			stats.countObjectSomeValuesFromBackwardLinkRule++;

			try {
				for (IndexedClassExpression carry : propagationsByObjectProperty_
						.get(link.getReltaion()))
					ruleEngine.produce(link.getSource(),
							new NegativeSuperClassExpression(carry));
			} finally {
				stats.timeObjectSomeValuesFromBackwardLinkRule += CachedTimeThread.currentTimeMillis;
			}
		}

		private static Matcher<BackwardLinkRules, ThisBackwardLinkRule> MATCHER_ = new SimpleTypeBasedMatcher<BackwardLinkRules, ThisBackwardLinkRule>(
				ThisBackwardLinkRule.class);

		private static ReferenceFactory<BackwardLinkRules, ThisBackwardLinkRule> FACTORY_ = new ReferenceFactory<BackwardLinkRules, ThisBackwardLinkRule>() {

			@Override
			public ThisBackwardLinkRule create(BackwardLinkRules tail) {
				return new ThisBackwardLinkRule(tail);
			}
		};
	}
	
	/**
	 * Records a positive occurrence of this SomeValuesFrom expression.
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	private class ThisPositiveIndexedObjectSomeValuesFrom implements IndexChange {

		@Override
		public boolean add(IndexedClassExpression target) {
			return filler.addPosPropertyInExistential(property);
		}

		@Override
		public boolean remove(IndexedClassExpression target) {	
			return filler.removePosPropertyInExistential(property);
		}
	}
	
	
/*	final IndexChange thisPositiveChange_ = new ThisPositiveIndexedObjectSomeValuesFrom();*/
}