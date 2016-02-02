package org.semanticweb.elk.reasoner.saturation.rules.backwardlinks;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Contradiction;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.inferences.ContradictionPropagated;
import org.semanticweb.elk.reasoner.saturation.rules.ClassInferenceProducer;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;

/**
 * A {@link LinkableBackwardLinkRule} applied when processing
 * {@link BackwardLink} that produces {@link Contradiction} in the context
 * linked by this {@link BackwardLink}.
 */
public class ContradictionOverBackwardLinkRule extends
		AbstractLinkableBackwardLinkRule {

	public static final String NAME = "Backward Link Contradiction Propagation";

	private ContradictionOverBackwardLinkRule(LinkableBackwardLinkRule tail) {
		super(tail);
	}

	/**
	 * Add a {@link ContradictionOverBackwardLinkRule} to the
	 * {@link BackwardLinkRule}s of the given {@link Context}
	 * 
	 * @param context
	 */
	public static void addTo(Context context) {
		context.getBackwardLinkRuleChain().getCreate(MATCHER_, FACTORY_);
	}

	/**
	 * Remove the {@link ContradictionOverBackwardLinkRule} from the
	 * {@link BackwardLinkRule}s of the given {@link Context}
	 * 
	 * @param context
	 */
	public static void removeFrom(Context context) {
		context.getBackwardLinkRuleChain().remove(MATCHER_);
	}

	@Override
	public String toString() {
		return NAME;
	}

	@Override
	public void apply(BackwardLink premise, ContextPremises premises,
			ClassInferenceProducer producer) {
		producer.produce(new ContradictionPropagated(premise));
	}

	@Override
	public boolean isTracing() {
		return true;
	}

	@Override
	public void accept(LinkedBackwardLinkRuleVisitor<?> visitor,
			BackwardLink premise, ContextPremises premises,
			ClassInferenceProducer producer) {
		visitor.visit(this, premise, premises, producer);
	}

	static final Matcher<LinkableBackwardLinkRule, ContradictionOverBackwardLinkRule> MATCHER_ = new SimpleTypeBasedMatcher<LinkableBackwardLinkRule, ContradictionOverBackwardLinkRule>(
			ContradictionOverBackwardLinkRule.class);

	static final ReferenceFactory<LinkableBackwardLinkRule, ContradictionOverBackwardLinkRule> FACTORY_ = new ReferenceFactory<LinkableBackwardLinkRule, ContradictionOverBackwardLinkRule>() {
		@Override
		public ContradictionOverBackwardLinkRule create(
				LinkableBackwardLinkRule tail) {
			return new ContradictionOverBackwardLinkRule(tail);
		}
	};
}