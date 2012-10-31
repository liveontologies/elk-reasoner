/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.conclusions;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.BackwardLinkRules;
import org.semanticweb.elk.util.collections.Multimap;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class Bottom implements Conclusion {

	protected static final Logger LOGGER_ = Logger.getLogger(Bottom.class);
	
	@Override
	public void deapply(SaturationState state, Context context) {
		context.setConsistent(true);
		propagateThroughBackwardLinks(state, context);
		context.getBackwardLinkRulesChain().remove(
				BottomBackwardLinkRule.MATCHER_);		
	}

	@Override
	public void apply(SaturationState state, Context context) {
		context.setConsistent(false);
		propagateThroughBackwardLinks(state, context);
		// register the backward link rule for propagation of bottom
		context.getBackwardLinkRulesChain().getCreate(
						BottomBackwardLinkRule.MATCHER_,
						BottomBackwardLinkRule.FACTORY_);		
	}
	
	private void propagateThroughBackwardLinks(SaturationState state, Context context) {
		
		final Multimap<IndexedPropertyChain, Context> backLinks = context
				.getBackwardLinksByObjectProperty();

		for (IndexedPropertyChain propRelation : backLinks.keySet()) {

			Collection<Context> targets = backLinks.get(propRelation);

			for (Context target : targets) {
				//the reason we propagate a positive SCE, not the Bot directly,
				//is because we want the SCE to appear in the list of superclasses
				state.produce(target, new PositiveSuperClassExpression(state.getOwlNothing()));
			}
		}
		
		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace("Registering the Bot backward link rule for " + context.getRoot());
		}

				
	}

	@Override
	public <R> R accept(ConclusionVisitor<R> visitor, Context context) {
		return visitor.visit(this, context);
	}
	

	@Override
	public String toString() {
		return "owl:Nothing";
	}


	/**
	 * A backward link rule to propagate bottom through any new backward links
	 */
	private static class BottomBackwardLinkRule extends BackwardLinkRules {

		BottomBackwardLinkRule(BackwardLinkRules tail) {
			super(tail);
		}

		@Override
		public void apply(SaturationState state, BackwardLink link) {
			/*RuleStatistics stats = ruleEngine.getRulesTimer();

			stats.timeClassBottomBackwardLinkRule -= CachedTimeThread.currentTimeMillis;
			stats.countClassBottomBackwardLinkRule++;*/

			try {
				state.produce(
						link.getSource(),
						new PositiveSuperClassExpression(state.getOwlNothing()));
			} finally {
				//stats.timeClassBottomBackwardLinkRule += CachedTimeThread.currentTimeMillis;
			}
		}

		private static Matcher<BackwardLinkRules, BottomBackwardLinkRule> MATCHER_ = new SimpleTypeBasedMatcher<BackwardLinkRules, BottomBackwardLinkRule>(
				BottomBackwardLinkRule.class);

		private static ReferenceFactory<BackwardLinkRules, BottomBackwardLinkRule> FACTORY_ = new ReferenceFactory<BackwardLinkRules, BottomBackwardLinkRule>() {
			@Override
			public BottomBackwardLinkRule create(BackwardLinkRules tail) {
				return new BottomBackwardLinkRule(tail);
			}
		};
	}	
}