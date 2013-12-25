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
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.CompositionRuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.ModifiableLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.Rule;
import org.semanticweb.elk.util.collections.HashSetMultimap;
import org.semanticweb.elk.util.collections.LazySetIntersection;
import org.semanticweb.elk.util.collections.Multimap;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ModifiableLinkImpl;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class ForwardLink extends AbstractConclusion {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ForwardLink.class);

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

	private final BackwardLinkCompositionRule thisCompositionRule_ = new BackwardLinkCompositionRule();

	public ForwardLink(IndexedPropertyChain relation, Context target) {
		this.relation_ = relation;
		this.target_ = target;
	}

	public IndexedPropertyChain getRelation() {
		return relation_;
	}

	public Context getTarget() {
		return target_;
	}

	@Override
	public void accept(CompositionRuleVisitor ruleAppVisitor,
			SaturationStateWriter writer, Context context) {
		ruleAppVisitor.visit(thisCompositionRule_, this, context, writer);
	}

	@Override
	public <R> R accept(ConclusionVisitor<R> visitor, Context context) {
		return visitor.visit(this, context);
	}

	public boolean addToContextBackwardLinkRule(Context context) {
		return context
				.getBackwardLinkRuleChain()
				.getCreate(ThisBackwardLinkRule.MATCHER_,
						ThisBackwardLinkRule.FACTORY_).addForwardLink(this);
	}

	public boolean removeFromContextBackwardLinkRule(Context context) {
		ThisBackwardLinkRule rule = context.getBackwardLinkRuleChain().find(
				ThisBackwardLinkRule.MATCHER_);

		return rule != null ? rule.removeForwardLink(this) : false;
	}

	public boolean containsBackwardLinkRule(Context context) {
		ThisBackwardLinkRule rule = context.getBackwardLinkRuleChain().find(
				ThisBackwardLinkRule.MATCHER_);

		return rule != null ? rule.containsForwardLink(this) : false;
	}

	@Override
	public String toString() {
		return relation_ + "->" + target_.getRoot();
	}

	/**
	 * The composition rule applied when processing this {@link ForwardLink}
	 * producing {@link BackwardLink}s resulted by composition of this
	 * {@link ForwardLink} with existing {@link BackwardLink}s using property
	 * chain axioms
	 * 
	 * @author "Yevgeny Kazakov"
	 */
	public class BackwardLinkCompositionRule implements Rule<ForwardLink> {

		private static final String NAME_ = "ForwardLink BackwardLink Composition";

		@Override
		public String getName() {
			return NAME_;
		}

		@Override
		public void apply(ForwardLink premise, Context context,
				SaturationStateWriter writer) {
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
						writer.produce(target_, new BackwardLink(source,
								composition));
					}
			}
		}

	}

	/**
	 * The composition rule applied when processing a {@link BackwardLink}
	 * producing {@link BackwardLink}s resulted by composing the processed
	 * {@link BackwardLink} with the {@link ForwardLink}s contained in the
	 * {@link Context} using property chain axioms
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	public static class ThisBackwardLinkRule extends
			ModifiableLinkImpl<ModifiableLinkRule<BackwardLink>> implements
			ModifiableLinkRule<BackwardLink> {

		private static final String NAME_ = "BackwardLink ForwardLink Composition";

		/**
		 * the record that stores all {@link ForwardLink}s produced in the
		 * {@link Context} in which this rule is saved; it stores every
		 * {@link ForwardLink} by indexing its target by its property
		 */
		private final Multimap<IndexedPropertyChain, Context> forwardLinksByObjectProperty_;

		ThisBackwardLinkRule(ModifiableLinkRule<BackwardLink> tail) {
			super(tail);
			this.forwardLinksByObjectProperty_ = new HashSetMultimap<IndexedPropertyChain, Context>(
					3);
		}

		// TODO: hide this method
		public Multimap<IndexedPropertyChain, Context> getForwardLinksByObjectProperty() {
			return forwardLinksByObjectProperty_;
		}

		@Override
		public String getName() {
			return NAME_;
		}

		@Override
		public void apply(BackwardLink link, Context context,
				SaturationStateWriter engine) {

			LOGGER_.trace("Applying {} to {}", NAME_, link);

			/* compose the link with all forward links */
			final Multimap<IndexedPropertyChain, IndexedPropertyChain> comps = link
					.getRelation().getSaturated()
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
						engine.produce(forwardTarget, new BackwardLink(source,
								composition));
			}

		}

		@Override
		public void accept(CompositionRuleVisitor visitor,
				BackwardLink premise, Context context,
				SaturationStateWriter writer) {
			visitor.visit(this, premise, context, writer);
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

		private boolean removeForwardLink(ForwardLink link) {
			return forwardLinksByObjectProperty_.remove(link.relation_,
					link.target_);
		}

		private boolean containsForwardLink(ForwardLink link) {
			return forwardLinksByObjectProperty_.contains(link.relation_,
					link.target_);
		}

		private static Matcher<ModifiableLinkRule<BackwardLink>, ThisBackwardLinkRule> MATCHER_ = new SimpleTypeBasedMatcher<ModifiableLinkRule<BackwardLink>, ThisBackwardLinkRule>(
				ThisBackwardLinkRule.class);

		/**
		 * The factory used for appending a new instance of this rule to a
		 * {@link ModifiableLinkRule<BackwardLink>} chain
		 */
		private static ReferenceFactory<ModifiableLinkRule<BackwardLink>, ThisBackwardLinkRule> FACTORY_ = new ReferenceFactory<ModifiableLinkRule<BackwardLink>, ThisBackwardLinkRule>() {
			@Override
			public ThisBackwardLinkRule create(
					ModifiableLinkRule<BackwardLink> tail) {
				return new ThisBackwardLinkRule(tail);
			}
		};

	}
}
