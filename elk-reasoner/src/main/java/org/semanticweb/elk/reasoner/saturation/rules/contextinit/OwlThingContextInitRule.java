package org.semanticweb.elk.reasoner.saturation.rules.contextinit;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.saturation.conclusions.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ChainableContextInitRule} that produces {@link Subsumer}
 * {@code owl:Thing} in a context. It should be applied only if
 * {@code owl:Thing} occurs negatively in the ontology.
 */
public class OwlThingContextInitRule extends AbstractChainableContextInitRule {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(OwlThingContextInitRule.class);

	private static final String NAME_ = "owl:Thing Introduction";

	private IndexedClassExpression owlThing_;

	private OwlThingContextInitRule(ChainableContextInitRule tail) {
		super(tail);
	}

	private OwlThingContextInitRule(IndexedClassExpression owlThing) {
		super(null);
		this.owlThing_ = owlThing;
	}

	/**
	 * Add an {@link OwlThingContextInitRule} to the given
	 * {@link ModifiableOntologyIndex}
	 * 
	 * @param owlThing
	 * @param index
	 */
	public static void addRuleFor(IndexedClass owlThing,
			ModifiableOntologyIndex index) {
		index.addContextInitRule(new OwlThingContextInitRule(owlThing));
	}

	/**
	 * Removes an {@link OwlThingContextInitRule} from the given
	 * {@link ModifiableOntologyIndex}
	 * 
	 * @param owlThing
	 * @param index
	 */
	public static void removeRuleFor(IndexedClass owlThing,
			ModifiableOntologyIndex index) {
		index.removeContextInitRule(new OwlThingContextInitRule(owlThing));
	}

	@Override
	public String getName() {
		return NAME_;
	}

	@Override
	public void apply(Void premise, Context context, ConclusionProducer producer) {
		LOGGER_.trace("Applying {} to {}", NAME_, context);

		producer.produce(context, new DecomposedSubsumer(owlThing_));
	}

	@Override
	public boolean addTo(Chain<ChainableContextInitRule> ruleChain) {
		OwlThingContextInitRule rule = ruleChain.getCreate(MATCHER_, FACTORY_);
		if (rule.owlThing_ == null) {
			// new rule created
			rule.owlThing_ = owlThing_;
			return true;
		}
		// owl:Thing was already registered
		return false;
	}

	@Override
	public boolean removeFrom(Chain<ChainableContextInitRule> ruleChain) {
		return ruleChain.remove(MATCHER_) != null;
	}

	@Override
	public void accept(LinkedContextInitRuleVisitor visitor, Context context,
			ConclusionProducer producer) {
		visitor.visit(this, context, producer);
	}

	private static final Matcher<ChainableContextInitRule, OwlThingContextInitRule> MATCHER_ = new SimpleTypeBasedMatcher<ChainableContextInitRule, OwlThingContextInitRule>(
			OwlThingContextInitRule.class);

	private static final ReferenceFactory<ChainableContextInitRule, OwlThingContextInitRule> FACTORY_ = new ReferenceFactory<ChainableContextInitRule, OwlThingContextInitRule>() {
		@Override
		public OwlThingContextInitRule create(ChainableContextInitRule tail) {
			return new OwlThingContextInitRule(tail);
		}
	};

}