package org.semanticweb.elk.reasoner.saturation.rules.backwardlinks;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.util.collections.HashSetMultimap;
import org.semanticweb.elk.util.collections.Multimap;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link LinkableBackwardLinkRule} producing {@link Subsumer}s when
 * processing {@link BackwardLink}s that are propagated over them using
 * {@link Propagation}s contained in the {@link Context}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class SubsumerBackwardLinkRule extends AbstractLinkableBackwardLinkRule {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(SubsumerBackwardLinkRule.class);

	private static final String NAME_ = "Propagation Over BackwardLink";

	private final Multimap<IndexedPropertyChain, IndexedClassExpression> propagationsByObjectProperty_;

	private SubsumerBackwardLinkRule(LinkableBackwardLinkRule tail) {
		super(tail);
		this.propagationsByObjectProperty_ = new HashSetMultimap<IndexedPropertyChain, IndexedClassExpression>(
				1);
	}

	public static boolean addRuleFor(Propagation propagation, Context context) {
		SubsumerBackwardLinkRule rule = context.getBackwardLinkRuleChain()
				.getCreate(MATCHER_, FACTORY_);
		return rule.propagationsByObjectProperty_.add(
				propagation.getRelation(), propagation.getCarry());
	}

	public static boolean removeRuleFor(Propagation propagation, Context context) {
		SubsumerBackwardLinkRule rule = context.getBackwardLinkRuleChain()
				.find(MATCHER_);
		return rule == null ? false : rule.propagationsByObjectProperty_
				.remove(propagation.getRelation(), propagation.getCarry());
	}

	public static boolean containsRuleFor(Propagation propagation,
			Context context) {
		SubsumerBackwardLinkRule rule = context.getBackwardLinkRuleChain()
				.find(MATCHER_);
		return rule == null ? false : rule.propagationsByObjectProperty_
				.contains(propagation.getRelation(), propagation.getCarry());
	}

	// TODO: hide this method
	public Multimap<IndexedPropertyChain, IndexedClassExpression> getPropagationsByObjectProperty() {
		return propagationsByObjectProperty_;
	}

	@Override
	public String getName() {
		return NAME_;
	}

	@Override
	public void apply(BackwardLink premise, Context context,
			ConclusionProducer producer) {
		LOGGER_.trace("Applying {} to {}", NAME_, premise);

		for (IndexedClassExpression carry : propagationsByObjectProperty_
				.get(premise.getRelation()))
			producer.produce(premise.getSource(), new ComposedSubsumer(carry));
	}

	boolean addPropagationByObjectProperty(IndexedPropertyChain propRelation,
			IndexedClassExpression conclusion) {
		return propagationsByObjectProperty_.add(propRelation, conclusion);
	}

	boolean removePropagationByObjectProperty(
			IndexedPropertyChain propRelation, IndexedClassExpression conclusion) {
		return propagationsByObjectProperty_.remove(propRelation, conclusion);
	}

	boolean containsPropagationByObjectProperty(
			IndexedPropertyChain propRelation, IndexedClassExpression conclusion) {
		return propagationsByObjectProperty_.contains(propRelation, conclusion);
	}

	static Matcher<LinkableBackwardLinkRule, SubsumerBackwardLinkRule> MATCHER_ = new SimpleTypeBasedMatcher<LinkableBackwardLinkRule, SubsumerBackwardLinkRule>(
			SubsumerBackwardLinkRule.class);

	static ReferenceFactory<LinkableBackwardLinkRule, SubsumerBackwardLinkRule> FACTORY_ = new ReferenceFactory<LinkableBackwardLinkRule, SubsumerBackwardLinkRule>() {

		@Override
		public SubsumerBackwardLinkRule create(LinkableBackwardLinkRule tail) {
			return new SubsumerBackwardLinkRule(tail);
		}
	};

	@Override
	public void accept(LinkedBackwardLinkRuleVisitor visitor,
			BackwardLink premise, Context context, ConclusionProducer producer) {
		visitor.visit(this, premise, context, producer);
	}

}