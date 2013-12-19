/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.util;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.PositiveSubsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.CompositionRuleApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.DecompositionRuleApplicationVisitor;

/**
 * Used only to create temporary subsumers when returning indexed class expressions as premises of inferences. 
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
class SubsumerPremise implements PositiveSubsumer {

	private final IndexedClassExpression ice_;
	
	/**
	 * 
	 */
	public SubsumerPremise(IndexedClassExpression ice) {
		ice_ = ice;
	}

	@Override
	public <R, C> R accept(ConclusionVisitor<R, C> visitor, C parameter) {
		return visitor.visit(this, parameter);
	}

	@Override
	public Context getSourceContext(Context contextWhereStored) {
		return contextWhereStored;
	}

	@Override
	public IndexedClassExpression getExpression() {
		return ice_;
	}

	@Override
	public void apply(BasicSaturationStateWriter writer, Context context,
			CompositionRuleApplicationVisitor ruleAppVisitor,
			DecompositionRuleApplicationVisitor decompVisitor) {
		//no-op
	}
	
	@Override
	public String toString() {
		return ice_.toString();
	}

}
