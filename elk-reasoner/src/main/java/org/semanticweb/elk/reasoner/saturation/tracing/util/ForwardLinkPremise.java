/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.util;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
class ForwardLinkPremise implements ForwardLink {

	private final Context target_;

	private final IndexedPropertyChain relation_;

	ForwardLinkPremise(Context target, IndexedPropertyChain relation) {
		this.relation_ = relation;
		this.target_ = target;
	}

	@Override
	public IndexedPropertyChain getRelation() {
		return relation_;
	}

	@Override
	public Context getTarget() {
		return target_;
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
	public boolean addToContextBackwardLinkRule(Context context) {
		//no-op
		return false;
	}

	@Override
	public boolean removeFromContextBackwardLinkRule(Context context) {
		//no-op
		return false;
	}

	@Override
	public boolean containsBackwardLinkRule(Context context) {
		//no-op
		return false;
	}

	@Override
	public void apply(BasicSaturationStateWriter writer, Context context) {
		//no-op
	}

	

}
