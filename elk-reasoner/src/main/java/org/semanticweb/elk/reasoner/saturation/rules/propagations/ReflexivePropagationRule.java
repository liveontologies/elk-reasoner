package org.semanticweb.elk.reasoner.saturation.rules.propagations;

import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;

/**
 * A {@link PropagationRule} producing {@link Subsumer}s in the {@link Context}s
 * in which the rule applies propagated over reflexive backward links stored in
 * this {@link Context}
 * 
 * @see Context#getLocalReflexiveObjectProperties()
 * @see NonReflexivePropagationRule
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class ReflexivePropagationRule extends AbstractPropagationRule {

	private static final String NAME_ = "Reflexive Propagation";

	private static final ReflexivePropagationRule INSTANCE_ = new ReflexivePropagationRule();

	public static final ReflexivePropagationRule getInstance() {
		return INSTANCE_;
	}

	@Override
	public String getName() {
		return NAME_;
	}

	@Override
	public void apply(Propagation premise, Context context,
			ConclusionProducer producer) {
		final Set<IndexedPropertyChain> reflexive = context
				.getLocalReflexiveObjectProperties();
		if (reflexive.contains(premise.getRelation()))
			producer.produce(context, new ComposedSubsumer(premise.getCarry()));
	}

	@Override
	public void accept(PropagationRuleVisitor visitor, Propagation premise,
			Context context, ConclusionProducer producer) {
		visitor.visit(this, premise, context, producer);
	}

}
