package org.semanticweb.elk.reasoner.saturation.rules.contradiction;

import java.util.Collection;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.util.collections.Multimap;

/**
 * A {@link ContradictionRule} applied when processing {@link Contradiction}
 * producing {@link Contradiction} in all contexts linked by
 * {@link BackwardLink}s in a {@code Context}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class ContradictionPropagationRule extends AbstractContradictionRule {

	private static final ContradictionPropagationRule INSTANCE_ = new ContradictionPropagationRule();

	private static final String NAME_ = "Contradiction Propagation over Backward Links";

	private ContradictionPropagationRule() {
		// do not allow creation of instances outside of this class
	}

	public static ContradictionPropagationRule getInstance() {
		return INSTANCE_;
	}

	@Override
	public String getName() {
		return NAME_;
	}

	@Override
	public void apply(Contradiction premise, Context context,
			ConclusionProducer producer) {
		final Multimap<IndexedPropertyChain, Context> backLinks = context
				.getBackwardLinksByObjectProperty();

		for (IndexedPropertyChain propRelation : backLinks.keySet()) {

			Collection<Context> targets = backLinks.get(propRelation);

			for (Context target : targets) {
				producer.produce(target, premise);
			}
		}
	}

	@Override
	public void accept(ContradictionRuleVisitor visitor, Contradiction premise,
			Context context, ConclusionProducer producer) {
		visitor.visit(this, premise, context, producer);
	}

}