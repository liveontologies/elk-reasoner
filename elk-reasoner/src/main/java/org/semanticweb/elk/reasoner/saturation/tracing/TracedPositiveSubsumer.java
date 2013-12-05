/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.PositiveSubsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.CompositionRuleApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.DecompositionRuleApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.Inference;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TracedPositiveSubsumer extends TracedConclusion<PositiveSubsumer> implements
		PositiveSubsumer {

	TracedPositiveSubsumer(Inference inf, PositiveSubsumer cnl) {
		super(inf, cnl);
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
	public IndexedClassExpression getExpression() {
		return conclusion.getExpression();
	}

	@Override
	public void apply(BasicSaturationStateWriter writer, Context context,
			CompositionRuleApplicationVisitor ruleAppVisitor,
			DecompositionRuleApplicationVisitor decompVisitor) {
		conclusion.apply(writer, context, ruleAppVisitor, decompVisitor);
	}

}
