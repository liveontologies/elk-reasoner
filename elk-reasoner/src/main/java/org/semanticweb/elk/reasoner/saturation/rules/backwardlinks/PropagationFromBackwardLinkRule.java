package org.semanticweb.elk.reasoner.saturation.rules.backwardlinks;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;

/**
 * A {@link BackwardLinkRule} applied when processing {@link BackwardLink}
 * producing new {@link Propagation}s that can be used with this
 * {@link BackwardLink}
 * 
 * @author "Yevgeny Kazakov"
 */
public class PropagationFromBackwardLinkRule extends AbstractBackwardLinkRule {

	private static final String NAME_ = "Propagations For BackwardLink";

	private static final PropagationFromBackwardLinkRule INSTANCE_ = new PropagationFromBackwardLinkRule();

	public static PropagationFromBackwardLinkRule getInstance() {
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
		// if this is the first/last backward link for this relation,
		// generate new propagations for this relation
		if (context.getBackwardLinksByObjectProperty().get(premiseRelation)
				.size() == 1) {
			IndexedObjectSomeValuesFrom.generatePropagations(premiseRelation,
					context, producer);
		}
	}

	@Override
	public void accept(BackwardLinkRuleVisitor visitor, BackwardLink premise,
			Context context, ConclusionProducer producer) {
		visitor.visit(this, premise, context, producer);
	}

}