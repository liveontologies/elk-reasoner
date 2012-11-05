/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.conclusions;

import java.util.Collection;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.BackwardLinkRules;
import org.semanticweb.elk.util.collections.HashSetMultimap;
import org.semanticweb.elk.util.collections.Multimap;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class Propagation implements Conclusion {

	// logger for this class
	//private static final Logger LOGGER_ = Logger.getLogger(Propagation.class);	
	
	private final IndexedPropertyChain relation_;
	
	private final IndexedClassExpression carry_;
	
	public Propagation(final IndexedPropertyChain relation, final IndexedClassExpression carry) {
		relation_ = relation;
		carry_ = carry;
	}
	
	@Override
	public void deapply(SaturationState state, Context context) {
		apply(state, context);
	}

	@Override
	public String toString() {
		return "Propagation "+ relation_ + "->" + carry_;
	}

	@Override
	public void apply(SaturationState state, Context context) {
		// propagate over all backward links
		final Multimap<IndexedPropertyChain, Context> backLinks = context.getBackwardLinksByObjectProperty();
		
		Collection<Context> targets = backLinks.get(relation_);

		for (Context target : targets) {
			state.produce(target,
					new NegativeSuperClassExpression(carry_));
		}
	}

	@Override
	public <R> R accept(ConclusionVisitor<R> visitor, Context context) {
		return visitor.visit(this, context);
	}

	public boolean addToContextBackwardLinkRule(Context context) {
		return context
				.getBackwardLinkRulesChain()
				.getCreate(ThisBackwardLinkRule.MATCHER_, ThisBackwardLinkRule.FACTORY_)
				.addPropagationByObjectProperty(relation_, carry_);
	}
	
	public boolean removeFromContextBackwardLinkRule(Context context) {
		ThisBackwardLinkRule rule = context
				.getBackwardLinkRulesChain()
				.find(ThisBackwardLinkRule.MATCHER_);
		
		return rule != null ? rule.removePropagationByObjectProperty(relation_, carry_) : false;
	}	

	public boolean containsBackwardLinkRule(Context context) {
		ThisBackwardLinkRule rule = context.getBackwardLinkRulesChain().find(
				ThisBackwardLinkRule.MATCHER_);
		
		return rule != null ? rule.containsPropagationByObjectProperty(relation_, carry_) : false;
	}	
	
	/**
	 * 
	 * 
	 */
	private static class ThisBackwardLinkRule extends BackwardLinkRules {

		private final Multimap<IndexedPropertyChain, IndexedClassExpression> propagationsByObjectProperty_;

		ThisBackwardLinkRule(BackwardLinkRules tail) {
			super(tail);
			this.propagationsByObjectProperty_ = new HashSetMultimap<IndexedPropertyChain, IndexedClassExpression>(1);
		}

		private boolean addPropagationByObjectProperty(
				IndexedPropertyChain propRelation,
				IndexedClassExpression conclusion) {
			return propagationsByObjectProperty_.add(propRelation, conclusion);
		}
		
		private boolean removePropagationByObjectProperty(
				IndexedPropertyChain propRelation,
				IndexedClassExpression conclusion) {
			return propagationsByObjectProperty_.remove(propRelation, conclusion);
		}		
		
		private boolean containsPropagationByObjectProperty(
				IndexedPropertyChain propRelation,
				IndexedClassExpression conclusion) {
			return propagationsByObjectProperty_.contains(propRelation, conclusion);
		}		

		@Override
		public void apply(SaturationState state, BackwardLink link) {
			/*RuleStatistics stats = ruleEngine.getRulesTimer();

			stats.timeObjectSomeValuesFromBackwardLinkRule -= CachedTimeThread.currentTimeMillis;
			stats.countObjectSomeValuesFromBackwardLinkRule++;*/

			try {
				for (IndexedClassExpression carry : propagationsByObjectProperty_
						.get(link.getRelation()))
					state.produce(link.getSource(),
							new NegativeSuperClassExpression(carry));
			} finally {
				//stats.timeObjectSomeValuesFromBackwardLinkRule += CachedTimeThread.currentTimeMillis;
			}
		}

		private static Matcher<BackwardLinkRules, ThisBackwardLinkRule> MATCHER_ = new SimpleTypeBasedMatcher<BackwardLinkRules, ThisBackwardLinkRule>(
				ThisBackwardLinkRule.class);

		private static ReferenceFactory<BackwardLinkRules, ThisBackwardLinkRule> FACTORY_ = new ReferenceFactory<BackwardLinkRules, ThisBackwardLinkRule>() {

			@Override
			public ThisBackwardLinkRule create(BackwardLinkRules tail) {
				return new ThisBackwardLinkRule(tail);
			}
		};
	}	
}