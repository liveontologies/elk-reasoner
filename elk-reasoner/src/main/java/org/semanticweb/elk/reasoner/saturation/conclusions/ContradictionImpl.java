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
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.CompositionRuleApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.ModifiableLinkRule;
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
public class ContradictionImpl extends AbstractConclusion implements Contradiction {

	private static final Logger LOGGER_ = LoggerFactory.getLogger(Contradiction.class);

	private static Contradiction INSTANCE_ = new ContradictionImpl();

	public static Contradiction getInstance() {
		return INSTANCE_;
	}

	private ContradictionImpl() {
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
				engine.produce(target, ContradictionImpl.getInstance());
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
			ModifiableLinkImpl<ModifiableLinkRule<BackwardLink, Context>> implements
			ModifiableLinkRule<BackwardLink, Context> {

		private static final String NAME = "Contradiction Existential Propagation";

		ContradictionBackwardLinkRule(ModifiableLinkRule<BackwardLink, Context> tail) {
			super(tail);
		}

		@Override
		public String getName() {
			return NAME;
		}

		@Override
		public void apply(BasicSaturationStateWriter engine, BackwardLink link, Context context) {
			LOGGER_.trace("Applying {} to {}", NAME, link);
			
			engine.produce(link.getSource(), ContradictionImpl.getInstance());
		}

		@Override
		public void accept(CompositionRuleApplicationVisitor visitor,
				BasicSaturationStateWriter writer, BackwardLink backwardLink, Context context) {
			visitor.visit(this, writer, backwardLink, context);
		}

		private static final Matcher<ModifiableLinkRule<BackwardLink, Context>, ContradictionBackwardLinkRule> MATCHER_ = new SimpleTypeBasedMatcher<ModifiableLinkRule<BackwardLink, Context>, ContradictionBackwardLinkRule>(
				ContradictionBackwardLinkRule.class);

		private static final ReferenceFactory<ModifiableLinkRule<BackwardLink, Context>, ContradictionBackwardLinkRule> FACTORY_ = new ReferenceFactory<ModifiableLinkRule<BackwardLink, Context>, ContradictionBackwardLinkRule>() {
			@Override
			public ContradictionBackwardLinkRule create(
					ModifiableLinkRule<BackwardLink, Context> tail) {
				return new ContradictionBackwardLinkRule(tail);
			}
		};
	}
}
