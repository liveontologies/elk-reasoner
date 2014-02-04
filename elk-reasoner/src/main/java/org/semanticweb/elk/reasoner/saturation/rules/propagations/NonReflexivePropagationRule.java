package org.semanticweb.elk.reasoner.saturation.rules.propagations;

import java.util.Collection;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.util.collections.Multimap;

/**
 * A {@link NonReflexivePropagationRule} producing {@link Subsumer}s in the
 * source {@link Context}s of relevant non-reflexive {@link BackwardLink}s
 * stored in the {@link Context} with which this rule applies.
 * 
 * @see Context#getBackwardLinksByObjectProperty()
 * @see ReflexivePropagationRule
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class NonReflexivePropagationRule extends AbstractPropagationRule {

	private static final String NAME_ = "Reflexive Propagation";

	private static final NonReflexivePropagationRule INSTANCE_ = new NonReflexivePropagationRule();

	public static final NonReflexivePropagationRule getInstance() {
		return INSTANCE_;
	}

	@Override
	public String getName() {
		return NAME_;
	}

	@Override
	public void apply(Propagation premise, Context context,
			ConclusionProducer producer) {
		final Multimap<IndexedPropertyChain, Context> backLinks = context
				.getBackwardLinksByObjectProperty();
		Collection<Context> targets = backLinks.get(premise.getRelation());
		IndexedClassExpression carry = premise.getCarry();
		for (Context target : targets) {
			producer.produce(target, new ComposedSubsumer(carry));
		}
	}

	@Override
	public void accept(PropagationRuleVisitor visitor, Propagation premise,
			Context context, ConclusionProducer producer) {
		visitor.visit(this, premise, context, producer);
	}

}
