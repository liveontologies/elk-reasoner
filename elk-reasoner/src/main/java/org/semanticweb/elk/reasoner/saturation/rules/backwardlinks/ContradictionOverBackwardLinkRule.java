package org.semanticweb.elk.reasoner.saturation.rules.backwardlinks;

import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link LinkableBackwardLinkRule} applied when processing
 * {@link BackwardLink} that produces {@link Contradiction} in the context
 * linked by this {@link BackwardLink}
 */
public class ContradictionOverBackwardLinkRule extends
		AbstractLinkableBackwardLinkRule {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ContradictionOverBackwardLinkRule.class);

	private static final String NAME_ = "Backward Link Contradiction Propagation";

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
	public String getName() {
		return NAME_;
	}

	@Override
	public void apply(BackwardLink premise, Context contex,
			ConclusionProducer producer) {
		LOGGER_.trace("Applying {} to {}", NAME_, premise);
		producer.produce(premise.getSource(), Contradiction.getInstance());
	}

	@Override
	public void accept(LinkedBackwardLinkRuleVisitor visitor,
			BackwardLink premise, Context context, ConclusionProducer producer) {
		visitor.visit(this, premise, context, producer);
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