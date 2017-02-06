package org.semanticweb.elk.reasoner.saturation.rules.backwardlinks;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.model.IndexedComplexPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ClassInferenceProducer;
import org.semanticweb.elk.util.collections.HashSetMultimap;
import org.semanticweb.elk.util.collections.LazySetIntersection;
import org.semanticweb.elk.util.collections.Multimap;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;

/**
 * A {@link LinkableBackwardLinkRule} applied when processing a
 * {@link BackwardLink} producing {@link BackwardLink}s or {@link ForwardLink}s
 * resulted by composing the processed {@link BackwardLink} with the
 * {@link ForwardLink}s contained in the {@link ContextPremises} using property
 * chain axioms.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class BackwardLinkChainFromBackwardLinkRule
		extends AbstractLinkableBackwardLinkRule {

	// logger for events
	/*
	 * private static final Logger LOGGER_ = LoggerFactory
	 * .getLogger(BackwardLinkChainFromBackwardLinkRule.class);
	 */

	public static final String NAME = "BackwardLink ForwardLink Composition";

	/**
	 * the record that stores all {@link ForwardLink}s produced in the
	 * {@link ContextPremises} in which this rule is saved; it stores every
	 * {@link ForwardLink} by indexing its target by its property
	 */
	private final Multimap<IndexedPropertyChain, IndexedContextRoot> forwardLinksByObjectProperty_;

	private BackwardLinkChainFromBackwardLinkRule(
			LinkableBackwardLinkRule tail) {
		super(tail);
		this.forwardLinksByObjectProperty_ = new HashSetMultimap<IndexedPropertyChain, IndexedContextRoot>(
				3);
	}

	/**
	 * Adds a {@link BackwardLinkChainFromBackwardLinkRule} inferences for the
	 * given {@link ForwardLink} in the given {@link Context}
	 * 
	 * @param link
	 * @param context
	 * @return {@code true} if the {@link BackwardLinkChainFromBackwardLinkRule}
	 *         stored in the {@link Context} is modified as a result of this
	 *         operation
	 */
	public static boolean addRuleFor(ForwardLink link, Context context) {
		BackwardLinkChainFromBackwardLinkRule rule = context
				.getBackwardLinkRuleChain().getCreate(MATCHER_, FACTORY_);
		return rule.forwardLinksByObjectProperty_.add(link.getChain(),
				link.getTarget());
	}

	/**
	 * Removes a {@link BackwardLinkChainFromBackwardLinkRule} inferences for
	 * the given {@link ForwardLink} from the given {@link Context}
	 * 
	 * @param link
	 * @param context
	 * @return {@code true} if the {@link BackwardLinkChainFromBackwardLinkRule}
	 *         stored in the {@link Context} is modified as a result of this
	 *         operation
	 */
	public static boolean removeRuleFor(ForwardLink link, Context context) {
		BackwardLinkChainFromBackwardLinkRule rule = context
				.getBackwardLinkRuleChain().find(MATCHER_);
		return rule == null ? false : rule.forwardLinksByObjectProperty_
				.remove(link.getChain(), link.getTarget());
	}

	/**
	 * Tests if a {@link BackwardLinkChainFromBackwardLinkRule} inferences for
	 * the given {@link ForwardLink} is present in the given {@link Context}
	 * 
	 * @param link
	 * @param context
	 * @return {@code true} if the inference is present
	 */
	public static boolean containsRuleFor(ForwardLink link, Context context) {
		BackwardLinkChainFromBackwardLinkRule rule = context
				.getBackwardLinkRuleChain().find(MATCHER_);
		return rule == null ? false : rule.forwardLinksByObjectProperty_
				.contains(link.getChain(), link.getTarget());
	}

	@Deprecated
	public Multimap<IndexedPropertyChain, IndexedContextRoot> getForwardLinksByObjectProperty() {
		return forwardLinksByObjectProperty_;
	}

	@Override
	public String toString() {
		return NAME;
	}

	private void apply(
			Multimap<IndexedPropertyChain, IndexedComplexPropertyChain> compsByForwardRelations,
			BackwardLink link, ContextPremises premises,
			ClassInferenceProducer producer) {
		/* compose the link with all forward links */
		if (compsByForwardRelations == null)
			return;

		for (IndexedPropertyChain forwardRelation : new LazySetIntersection<IndexedPropertyChain>(
				compsByForwardRelations.keySet(), forwardLinksByObjectProperty_.keySet())) {

			Collection<IndexedComplexPropertyChain> compositions = compsByForwardRelations
					.get(forwardRelation);
			Collection<IndexedContextRoot> forwardTargets = forwardLinksByObjectProperty_
					.get(forwardRelation);

			for (IndexedComplexPropertyChain composition : compositions)
				for (IndexedContextRoot forwardTarget : forwardTargets)
					IndexedObjectSomeValuesFrom.Helper.produceComposedLink(
							producer, link.getTraceRoot(), link.getRelation(),
							premises.getRoot(), forwardRelation, forwardTarget,
							composition);
		}
	}
	
	@Override
	public void apply(BackwardLink link, ContextPremises premises,
			ClassInferenceProducer producer) {
		apply(link.getRelation().getSaturated()
				.getNonRedundantCompositionsByRightSubProperty(), link,
				premises, producer);
	}
	
	@Override
	public void applyTracing(BackwardLink link, ContextPremises premises,
			ClassInferenceProducer producer) {
		apply(link.getRelation().getSaturated()
				.getNonRedundantCompositionsByRightSubProperty(), link,
				premises, producer);
		apply(link.getRelation().getSaturated()
				.getRedundantCompositionsByRightSubProperty(), link, premises,
				producer);
	}

	@Override
	public boolean isTracingRule() {
		return true;
	}

	@Override
	public void accept(LinkedBackwardLinkRuleVisitor<?> visitor,
			BackwardLink premise, ContextPremises premises,
			ClassInferenceProducer producer) {
		visitor.visit(this, premise, premises, producer);
	}

	static Matcher<LinkableBackwardLinkRule, BackwardLinkChainFromBackwardLinkRule> MATCHER_ = new SimpleTypeBasedMatcher<LinkableBackwardLinkRule, BackwardLinkChainFromBackwardLinkRule>(
			BackwardLinkChainFromBackwardLinkRule.class);

	/**
	 * The factory used for appending a new instance of this rule to a
	 * {@link LinkableBackwardLinkRule} chain
	 */
	static ReferenceFactory<LinkableBackwardLinkRule, BackwardLinkChainFromBackwardLinkRule> FACTORY_ = new ReferenceFactory<LinkableBackwardLinkRule, BackwardLinkChainFromBackwardLinkRule>() {
		@Override
		public BackwardLinkChainFromBackwardLinkRule create(
				LinkableBackwardLinkRule tail) {
			return new BackwardLinkChainFromBackwardLinkRule(tail);
		}
	};

}
