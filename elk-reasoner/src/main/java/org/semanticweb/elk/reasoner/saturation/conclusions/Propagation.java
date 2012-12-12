/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.conclusions;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
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

import java.util.Collection;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationState.Writer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ModifiableLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationVisitor;
import org.semanticweb.elk.util.collections.HashSetMultimap;
import org.semanticweb.elk.util.collections.Multimap;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ModifiableLinkImpl;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class Propagation extends AbstractConclusion {

	// logger for this class
	// private static final Logger LOGGER_ =
	// Logger.getLogger(Propagation.class);

	private final IndexedPropertyChain relation_;

	private final IndexedClassExpression carry_;

	public Propagation(final IndexedPropertyChain relation,
			final IndexedClassExpression carry) {
		relation_ = relation;
		carry_ = carry;
	}

	@Override
	public String toString() {
		return "Propagation " + relation_ + "->" + carry_;
	}

	@Override
	public void apply(SaturationState.Writer engine, Context context) {
		// propagate over all backward links
		final Multimap<IndexedPropertyChain, Context> backLinks = context
				.getBackwardLinksByObjectProperty();

		Collection<Context> targets = backLinks.get(relation_);

		for (Context target : targets) {
			engine.produce(target, new NegativeSubsumer(carry_));
		}
	}

	@Override
	public <R> R accept(ConclusionVisitor<R> visitor, Context context) {
		return visitor.visit(this, context);
	}

	public boolean addToContextBackwardLinkRule(Context context) {
		return context
				.getBackwardLinkRuleChain()
				.getCreate(ThisBackwardLinkRule.MATCHER_,
						ThisBackwardLinkRule.FACTORY_)
				.addPropagationByObjectProperty(relation_, carry_);
	}

	public boolean removeFromContextBackwardLinkRule(Context context) {
		ThisBackwardLinkRule rule = context.getBackwardLinkRuleChain().find(
				ThisBackwardLinkRule.MATCHER_);

		return rule != null ? rule.removePropagationByObjectProperty(relation_,
				carry_) : false;
	}

	public boolean containsBackwardLinkRule(Context context) {
		ThisBackwardLinkRule rule = context.getBackwardLinkRuleChain().find(
				ThisBackwardLinkRule.MATCHER_);

		return rule != null ? rule.containsPropagationByObjectProperty(
				relation_, carry_) : false;
	}

	/**
	 * 
	 * 
	 */
	public static class ThisBackwardLinkRule extends
			ModifiableLinkImpl<ModifiableLinkRule<BackwardLink>> implements
			ModifiableLinkRule<BackwardLink> {

		private final Multimap<IndexedPropertyChain, IndexedClassExpression> propagationsByObjectProperty_;

		ThisBackwardLinkRule(ModifiableLinkRule<BackwardLink> tail) {
			super(tail);
			this.propagationsByObjectProperty_ = new HashSetMultimap<IndexedPropertyChain, IndexedClassExpression>(
					1);
		}

		private boolean addPropagationByObjectProperty(
				IndexedPropertyChain propRelation,
				IndexedClassExpression conclusion) {
			return propagationsByObjectProperty_.add(propRelation, conclusion);
		}

		private boolean removePropagationByObjectProperty(
				IndexedPropertyChain propRelation,
				IndexedClassExpression conclusion) {
			return propagationsByObjectProperty_.remove(propRelation,
					conclusion);
		}

		private boolean containsPropagationByObjectProperty(
				IndexedPropertyChain propRelation,
				IndexedClassExpression conclusion) {
			return propagationsByObjectProperty_.contains(propRelation,
					conclusion);
		}

		@Override
		public void apply(SaturationState.Writer engine, BackwardLink link) {
			/*
			 * RuleStatistics stats = ruleEngine.getRulesTimer();
			 * 
			 * stats.timeObjectSomeValuesFromBackwardLinkRule -=
			 * CachedTimeThread.currentTimeMillis;
			 * stats.countObjectSomeValuesFromBackwardLinkRule++;
			 */

			try {
				for (IndexedClassExpression carry : propagationsByObjectProperty_
						.get(link.getRelation()))
					engine.produce(link.getSource(),
							new NegativeSubsumer(carry));
			} finally {
				// stats.timeObjectSomeValuesFromBackwardLinkRule +=
				// CachedTimeThread.currentTimeMillis;
			}
		}

		private static Matcher<ModifiableLinkRule<BackwardLink>, ThisBackwardLinkRule> MATCHER_ = new SimpleTypeBasedMatcher<ModifiableLinkRule<BackwardLink>, ThisBackwardLinkRule>(
				ThisBackwardLinkRule.class);

		private static ReferenceFactory<ModifiableLinkRule<BackwardLink>, ThisBackwardLinkRule> FACTORY_ = new ReferenceFactory<ModifiableLinkRule<BackwardLink>, ThisBackwardLinkRule>() {

			@Override
			public ThisBackwardLinkRule create(
					ModifiableLinkRule<BackwardLink> tail) {
				return new ThisBackwardLinkRule(tail);
			}
		};
		
		@Override
		public void accept(RuleApplicationVisitor visitor, Writer writer,
				BackwardLink backwardLink) {
			visitor.visit(this, writer, backwardLink);
		}
	}
}