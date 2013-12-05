/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.NegativeSubsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.CompositionRuleApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.DecompositionRuleApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.Inference;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TracedNegativeSubsumer extends TracedConclusion<NegativeSubsumer> implements
		NegativeSubsumer {

	TracedNegativeSubsumer(Inference inf, NegativeSubsumer cnl) {
		super(inf, cnl);
	}

	@Override
	public <R, C> R accept(ConclusionVisitor<R, C> visitor, C context) {
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
			CompositionRuleApplicationVisitor ruleAppVisitor) {
		conclusion.apply(writer, context, ruleAppVisitor);
	}

	@Override
	public void applyDecompositionRules(Context context,
			DecompositionRuleApplicationVisitor decompVisitor) {
		conclusion.applyDecompositionRules(context, decompVisitor);
	}

}
