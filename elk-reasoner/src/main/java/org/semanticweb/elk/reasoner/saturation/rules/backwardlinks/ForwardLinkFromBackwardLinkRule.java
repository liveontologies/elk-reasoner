package org.semanticweb.elk.reasoner.saturation.rules.backwardlinks;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;

/**
 * A {@link BackwardLinkRule} applied when processing {@link BackwardLink}
 * producing the corresponding {@link ForwardLink} if the relation of this
 * {@link BackwardLink} can be composed with other {@link IndexedPropertyChain}s
 * 
 * @see {@link BackwardLink#getRelation()}
 * 
 * @author "Yevgeny Kazakov"
 */
public class ForwardLinkFromBackwardLinkRule extends AbstractBackwardLinkRule {

	private static final String NAME_ = "ForwardLink from BackwardLink";

	private static final ForwardLinkFromBackwardLinkRule INSTANCE_ = new ForwardLinkFromBackwardLinkRule();

	public static ForwardLinkFromBackwardLinkRule getInstance() {
		return INSTANCE_;
	}

	@Override
	public String getName() {
		return NAME_;
	}

	@Override
	public void apply(BackwardLink premise, Context context,
			ConclusionProducer producer) {
		IndexedPropertyChain premiseRelation = premise.getRelation();
		/*
		 * convert backward link to a forward link if it can potentially be
		 * composed
		 */
		if (!premiseRelation.getSaturated().getCompositionsByLeftSubProperty()
				.isEmpty()) {
			producer.produce(premise.getSource(), new ForwardLink(
					premiseRelation, context));
		}

	}

	@Override
	public void accept(BackwardLinkRuleVisitor visitor, BackwardLink premise,
			Context context, ConclusionProducer producer) {
		visitor.visit(this, premise, context, producer);
	}

}