package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.CompositionRuleApplicationVisitor;

/**
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TracedBackwardLink extends TracedConclusion<BackwardLink> implements BackwardLink {

	public TracedBackwardLink(Inference inf, BackwardLink link) {
		super(inf, link);
	}

	@Override
	public <R> R accept(ConclusionVisitor<R> visitor, Context context) {
		return visitor.visit(this, context);
	}

	@Override
	public Context getSourceContext(Context contextWhereStored) {
		return conclusion.getSourceContext(contextWhereStored);
	}

	@Override
	public IndexedPropertyChain getRelation() {
		return conclusion.getRelation();
	}

	@Override
	public Context getSource() {
		return conclusion.getSource();
	}

	@Override
	public void apply(BasicSaturationStateWriter writer, Context context,
			CompositionRuleApplicationVisitor ruleAppVisitor) {
		conclusion.apply(writer, context, ruleAppVisitor);
	}

}
