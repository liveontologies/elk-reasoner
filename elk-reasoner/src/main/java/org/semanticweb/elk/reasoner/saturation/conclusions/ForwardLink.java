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
package org.semanticweb.elk.reasoner.saturation.conclusions;

import java.util.Collection;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.BackwardLinkRules;
import org.semanticweb.elk.reasoner.saturation.rules.RuleEngine;
import org.semanticweb.elk.util.collections.HashSetMultimap;
import org.semanticweb.elk.util.collections.LazySetIntersection;
import org.semanticweb.elk.util.collections.Multimap;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;
import org.semanticweb.elk.util.logging.CachedTimeThread;

/**
 * A {@link Conclusion} representing derived existential restrictions from this
 * source {@link Context} to a target {@link Context}. Intuitively, if a
 * subclass axiom {@code SubClassOf(:A ObjectSomeValuesFrom(:r :B))} is derived
 * by inference rules, then a {@link ForwardLink} with the relation {@code :r}
 * and the target {@code :B} can be produced for the source context with root
 * {@code :A}.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public class ForwardLink implements Conclusion {

	/**
	 * the {@link IndexedPropertyChain} in the existential restriction
	 * corresponding to this {@link ForwardLink}
	 */
	private final IndexedPropertyChain relation_;

	/**
	 * the {@link Context}, which root is the filler of the existential
	 * restriction corresponding to this {@link ForwardLink}
	 */
	private final Context target_;

	public ForwardLink(IndexedPropertyChain relation, Context target) {
		this.relation_ = relation;
		this.target_ = target;
	}

	@Override
	public void apply(RuleEngine ruleEngine, Context context) {

		ConclusionsCounter statistics = ruleEngine.getConclusionsCounter();
		statistics.forwLinkInfNo++;

		if (!context
				.getBackwardLinkRulesChain()
				.getCreate(ThisBackwardLinkRule.MATCHER_,
						ThisBackwardLinkRule.FACTORY_).addForwardLink(this))
			return;

		statistics.forwLinkNo++;

		/* compose the link with all backward links */
		final Multimap<IndexedPropertyChain, IndexedPropertyChain> comps = relation_
				.getSaturated().getCompositionsByLeftSubProperty();
		final Multimap<IndexedPropertyChain, Context> backLinks = context
				.getBackwardLinksByObjectProperty();

		for (IndexedPropertyChain backwardRelation : new LazySetIntersection<IndexedPropertyChain>(
				comps.keySet(), backLinks.keySet())) {

			Collection<IndexedPropertyChain> compositions = comps
					.get(backwardRelation);
			Collection<Context> sources = backLinks.get(backwardRelation);

			for (IndexedPropertyChain composition : compositions)
				for (Context source : sources) {
					ruleEngine.produce(target_, new BackwardLink(source,
							composition));
				}
		}
	}

	/**
	 * A type of {@link BackwardLinkRules} created for {@link ForwardLink}s and
	 * stored in the {@link Context} where it is produced. There can be at most
	 * one rule of this type stored in every {@link Context}. The rule
	 * essentially indexes all {@link ForwardLink}s produced in this
	 * {@link Context} and applies inferences with every produced
	 * {@link BackwardLink} in this {@link Context}, such as computing implied
	 * role chains.
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	private static class ThisBackwardLinkRule extends BackwardLinkRules {

		/**
		 * the record that stores all {@link ForwardLink}s produced in the
		 * {@link Context} in which this rule is saved; it stores every
		 * {@link ForwardLink} by indexing its target by its property
		 */
		private final Multimap<IndexedPropertyChain, Context> forwardLinksByObjectProperty_;

		ThisBackwardLinkRule(BackwardLinkRules tail) {
			super(tail);
			this.forwardLinksByObjectProperty_ = new HashSetMultimap<IndexedPropertyChain, Context>(
					3);
		}

		/**
		 * Updates this rule with the input {@link ForwardLink}.
		 * 
		 * @param link
		 *            a {@link ForwardLink} that should be taken into account in
		 *            this rule
		 * @return {@code true} if the rule has changed as a result of this
		 *         operation
		 */
		private boolean addForwardLink(ForwardLink link) {
			return forwardLinksByObjectProperty_.add(link.relation_,
					link.target_);
		}

		@Override
		public void apply(RuleEngine ruleEngine, BackwardLink link) {

			RuleStatistics timer = ruleEngine.getRulesTimer();

			timer.timeForwardLinkBackwardLinkRule -= CachedTimeThread
					.currentTimeMillis();

			timer.countForwardLinkBackwardLinkRule++;

			try {

				/* compose the link with all forward links */
				final Multimap<IndexedPropertyChain, IndexedPropertyChain> comps = link
						.getReltaion().getSaturated()
						.getCompositionsByRightSubProperty();
				if (comps == null)
					return;

				Context source = link.getSource();

				for (IndexedPropertyChain forwardRelation : new LazySetIntersection<IndexedPropertyChain>(
						comps.keySet(), forwardLinksByObjectProperty_.keySet())) {

					Collection<IndexedPropertyChain> compositions = comps
							.get(forwardRelation);
					Collection<Context> forwardTargets = forwardLinksByObjectProperty_
							.get(forwardRelation);

					for (IndexedPropertyChain composition : compositions)
						for (Context forwardTarget : forwardTargets)
							ruleEngine.produce(forwardTarget, new BackwardLink(
									source, composition));
				}

			} finally {
				timer.timeForwardLinkBackwardLinkRule += CachedTimeThread
						.currentTimeMillis();
			}
		}

		private static Matcher<BackwardLinkRules, ThisBackwardLinkRule> MATCHER_ = new SimpleTypeBasedMatcher<BackwardLinkRules, ThisBackwardLinkRule>(
				ThisBackwardLinkRule.class);

		/**
		 * The factory used for appending a new instance of this rule to a
		 * {@link BackwardLinkRules} chain
		 */
		private static ReferenceFactory<BackwardLinkRules, ThisBackwardLinkRule> FACTORY_ = new ReferenceFactory<BackwardLinkRules, ThisBackwardLinkRule>() {
			@Override
			public ThisBackwardLinkRule create(BackwardLinkRules tail) {
				return new ThisBackwardLinkRule(tail);
			}
		};

	}
}