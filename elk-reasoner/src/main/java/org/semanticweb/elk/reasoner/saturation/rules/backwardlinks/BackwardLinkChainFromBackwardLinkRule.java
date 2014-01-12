package org.semanticweb.elk.reasoner.saturation.rules.backwardlinks;

import java.util.Collection;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.util.collections.HashSetMultimap;
import org.semanticweb.elk.util.collections.LazySetIntersection;
import org.semanticweb.elk.util.collections.Multimap;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link LinkableBackwardLinkRule} applied when processing a
 * {@link BackwardLink} producing {@link BackwardLink}s resulted by composing
 * the processed {@link BackwardLink} with the {@link ForwardLink}s contained in
 * the {@link Context} using property chain axioms
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class BackwardLinkChainFromBackwardLinkRule extends
		AbstractLinkableBackwardLinkRule {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(BackwardLinkChainFromBackwardLinkRule.class);

	private static final String NAME_ = "BackwardLink ForwardLink Composition";

	/**
	 * the record that stores all {@link ForwardLink}s produced in the
	 * {@link Context} in which this rule is saved; it stores every
	 * {@link ForwardLink} by indexing its target by its property
	 */
	private final Multimap<IndexedPropertyChain, Context> forwardLinksByObjectProperty_;

	private BackwardLinkChainFromBackwardLinkRule(LinkableBackwardLinkRule tail) {
		super(tail);
		this.forwardLinksByObjectProperty_ = new HashSetMultimap<IndexedPropertyChain, Context>(
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
		return rule.forwardLinksByObjectProperty_.add(link.getRelation(),
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
				.remove(link.getRelation(), link.getTarget());
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
				.contains(link.getRelation(), link.getTarget());
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
			ConclusionProducer producer) {

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
					producer.produce(forwardTarget, new BackwardLink(source,
							composition));
		}

	}

	@Override
	public void accept(LinkedBackwardLinkRuleVisitor visitor,
			BackwardLink premise, Context context, ConclusionProducer producer) {
		visitor.visit(this, premise, context, producer);
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