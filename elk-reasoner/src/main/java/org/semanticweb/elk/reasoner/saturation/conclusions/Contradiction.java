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
import org.semanticweb.elk.util.collections.Multimap;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ModifiableLinkImpl;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 */
public class Contradiction extends AbstractConclusion {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(Contradiction.class);

	/**
	 * we use just one instance of this class
	 */
	private static Contradiction INSTANCE_ = new Contradiction();

	/**
	 * one instance of composition rule is sufficient
	 */
	private static ContradictionPropagationRule THIS_COMPOSITION_RULE_ = new ContradictionPropagationRule();

	public static Contradiction getInstance() {
		return INSTANCE_;
	}

	private Contradiction() {
		// do not allow creation of instances outside of this class
	}

	@SuppressWarnings("static-method")
	public void removeFrom(Context context) {
		context.getBackwardLinkRuleChain().remove(
				ContradictionBackwardLinkRule.MATCHER_);
	}

	@SuppressWarnings("static-method")
	public void addTo(Context context) {
		// register the backward link rule for propagation of bottom
		context.getBackwardLinkRuleChain().getCreate(
				ContradictionBackwardLinkRule.MATCHER_,
				ContradictionBackwardLinkRule.FACTORY_);
	}

	@Override
	public void accept(CompositionRuleVisitor ruleAppVisitor,
			SaturationStateWriter writer, Context context) {
		ruleAppVisitor.visit(THIS_COMPOSITION_RULE_, writer, context);
	}

	@Override
	public <R> R accept(ConclusionVisitor<R> visitor, Context context) {
		return visitor.visit(this, context);
	}

	@Override
	public String toString() {
		return "Contradiction";
	}

	/**
	 * The composition rule applied when processing {@link Contradiction}
	 * producing {@link Contradiction} in all contexts linked by
	 * {@link BackwardLink}s in a {@code Context}
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	public static class ContradictionPropagationRule implements Rule<Context> {

		private static final String NAME_ = "Contradiction Propagation over Backward Links";

		@Override
		public String getName() {
			return NAME_;
		}

		@Override
		public void apply(SaturationStateWriter writer, Context context) {
			final Multimap<IndexedPropertyChain, Context> backLinks = context
					.getBackwardLinksByObjectProperty();

			for (IndexedPropertyChain propRelation : backLinks.keySet()) {

				Collection<Context> targets = backLinks.get(propRelation);

				for (Context target : targets) {
					writer.produce(target, Contradiction.getInstance());
				}
			}
		}

	}

	/**
	 * The composition rule applied when processing {@link BackwardLink} that
	 * produces {@link Contradiction} in the context linked by this
	 * {@link BackwardLink}
	 */
	public static class ContradictionBackwardLinkRule extends
			ModifiableLinkImpl<ModifiableLinkRule<BackwardLink>> implements
			ModifiableLinkRule<BackwardLink> {

		private static final String NAME_ = "Backward Link Contradiction Propagation";

		ContradictionBackwardLinkRule(ModifiableLinkRule<BackwardLink> tail) {
			super(tail);
		}

		@Override
		public String getName() {
			return NAME_;
		}

		@Override
		public void apply(SaturationStateWriter engine, BackwardLink link) {
			LOGGER_.trace("Applying {} to {}", NAME_, link);

			engine.produce(link.getSource(), Contradiction.getInstance());
		}

		@Override
		public void accept(CompositionRuleVisitor visitor,
				SaturationStateWriter writer, BackwardLink backwardLink) {
			visitor.visit(this, writer, backwardLink);
		}

		private static final Matcher<ModifiableLinkRule<BackwardLink>, ContradictionBackwardLinkRule> MATCHER_ = new SimpleTypeBasedMatcher<ModifiableLinkRule<BackwardLink>, ContradictionBackwardLinkRule>(
				ContradictionBackwardLinkRule.class);

		private static final ReferenceFactory<ModifiableLinkRule<BackwardLink>, ContradictionBackwardLinkRule> FACTORY_ = new ReferenceFactory<ModifiableLinkRule<BackwardLink>, ContradictionBackwardLinkRule>() {
			@Override
			public ContradictionBackwardLinkRule create(
					ModifiableLinkRule<BackwardLink> tail) {
				return new ContradictionBackwardLinkRule(tail);
			}
		};
	}

}
