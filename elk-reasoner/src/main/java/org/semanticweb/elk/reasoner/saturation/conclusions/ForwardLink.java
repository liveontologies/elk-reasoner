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

/**
 * @author Frantisek Simancik
 * 
 */
public class ForwardLink implements Conclusion {

	private final IndexedPropertyChain relation_;

	private final Context target_;

	public ForwardLink(IndexedPropertyChain relation, Context target) {
		this.relation_ = relation;
		this.target_ = target;
	}

	@Override
	public void apply(RuleEngine ruleEngine, Context context) {

		RuleStatistics statistics = ruleEngine.getRuleStatistics();
		statistics.forwLinkInfNo++;

		if (!context
				.getBackwardLinkRulesChain()
				.getCreate(ThisBackwardLinkRule.MATCHER_,
						ThisBackwardLinkRule.FACTORY_)
				.addForwardLinkByObjectProperty(relation_, target_))
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
			Collection<Context> backwardTargets = backLinks
					.get(backwardRelation);

			for (IndexedPropertyChain composition : compositions)
				for (Context backwardTarget : backwardTargets) {
					
					//System.err.println("B: Backward link composed at " + context.getRoot()  + "\n" + target_.getRoot() + " -> " + composition + " -> " + backwardTarget.getRoot());
					
					ruleEngine.produce(target_, new BackwardLink(composition,
							backwardTarget));
				}
		}
	}

	private static class ThisBackwardLinkRule extends BackwardLinkRules {

		private final Multimap<IndexedPropertyChain, Context> forwardLinksByObjectProperty_;

		ThisBackwardLinkRule(BackwardLinkRules tail) {
			super(tail);
			this.forwardLinksByObjectProperty_ = new HashSetMultimap<IndexedPropertyChain, Context>();
		}

		private boolean addForwardLinkByObjectProperty(
				IndexedPropertyChain propRelation, Context target) {
			return forwardLinksByObjectProperty_.add(propRelation, target);
		}

		@Override
		public void apply(RuleEngine ruleEngine, BackwardLink link) {
			/* compose the link with all forward links */
			final Multimap<IndexedPropertyChain, IndexedPropertyChain> comps = link
					.getRelation().getSaturated()
					.getCompositionsByRightSubProperty();
			if (comps == null)
				return;

			Context target = link.getTarget();

			for (IndexedPropertyChain forwardRelation : new LazySetIntersection<IndexedPropertyChain>(
					comps.keySet(), forwardLinksByObjectProperty_.keySet())) {

				Collection<IndexedPropertyChain> compositions = comps
						.get(forwardRelation);
				Collection<Context> forwardTargets = forwardLinksByObjectProperty_
						.get(forwardRelation);

				for (IndexedPropertyChain composition : compositions)
					for (Context forwardTarget : forwardTargets) {
						
						//System.err.println("F: Backward link composed \n" + forwardTarget.getRoot() + " -> " + composition + " -> " + target.getRoot() + "\n" +
						//composition.getToldSuperProperties());						
						
						ruleEngine.produce(forwardTarget, new BackwardLink(
								composition, target));
					}
			}
		}

		private static Matcher<BackwardLinkRules, ThisBackwardLinkRule> MATCHER_ = new SimpleTypeBasedMatcher<BackwardLinkRules, ThisBackwardLinkRule>(ThisBackwardLinkRule.class);
		
		private static ReferenceFactory<BackwardLinkRules, ThisBackwardLinkRule> FACTORY_ = new ReferenceFactory<BackwardLinkRules, ThisBackwardLinkRule>() {
			@Override
			public ThisBackwardLinkRule create(BackwardLinkRules tail) {
				return new ThisBackwardLinkRule(tail);
			}
		};

	}

}
