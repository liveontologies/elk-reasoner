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

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ModifiableLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationVisitor;
import org.semanticweb.elk.util.collections.Multimap;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ModifiableLinkImpl;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 */
public class Contradiction extends AbstractConclusion {

	private static final Logger LOGGER_ = Logger.getLogger(Contradiction.class);

	private static Contradiction INSTANCE_ = new Contradiction();

	public static Contradiction getInstance() {
		return INSTANCE_;
	}

	private Contradiction() {
		// do not allow creation of instances outside of this class
	}

	@Override
	public void deapply(BasicSaturationStateWriter engine, Context context) {
		propagateThroughBackwardLinks(engine, context);
		context.getBackwardLinkRuleChain().remove(
				ContradictionBackwardLinkRule.MATCHER_);
	}

	@Override
	public void apply(BasicSaturationStateWriter engine, Context context) {
		propagateThroughBackwardLinks(engine, context);
		// register the backward link rule for propagation of bottom
		context.getBackwardLinkRuleChain().getCreate(
				ContradictionBackwardLinkRule.MATCHER_,
				ContradictionBackwardLinkRule.FACTORY_);
	}

	private void propagateThroughBackwardLinks(BasicSaturationStateWriter engine,
			Context context) {

		final Multimap<IndexedPropertyChain, Context> backLinks = context
				.getBackwardLinksByObjectProperty();

		for (IndexedPropertyChain propRelation : backLinks.keySet()) {

			Collection<Context> targets = backLinks.get(propRelation);

			for (Context target : targets) {
				engine.produce(target, Contradiction.getInstance());
			}
		}
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
	 * A backward link rule to propagate contradiction through any new backward
	 * links
	 */
	public static class ContradictionBackwardLinkRule extends
			ModifiableLinkImpl<ModifiableLinkRule<BackwardLink>> implements
			ModifiableLinkRule<BackwardLink> {

		private static final String NAME = "Contradiction Existential Propagation";

		ContradictionBackwardLinkRule(ModifiableLinkRule<BackwardLink> tail) {
			super(tail);
		}

		@Override
		public String getName() {
			return NAME;
		}

		@Override
		public void apply(BasicSaturationStateWriter engine, BackwardLink link) {
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace("Applying " + NAME + " to " + link);
			}
			engine.produce(link.getSource(), Contradiction.getInstance());
		}

		@Override
		public void accept(RuleApplicationVisitor visitor,
				BasicSaturationStateWriter writer, BackwardLink backwardLink) {
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
