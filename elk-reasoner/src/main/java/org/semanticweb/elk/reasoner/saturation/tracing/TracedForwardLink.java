/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.Inference;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TracedForwardLink extends TracedConclusion<ForwardLink> implements
		ForwardLink {

	public TracedForwardLink(Inference inf, ForwardLink cnl) {
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
	public IndexedPropertyChain getRelation() {
		return conclusion.getRelation();
	}

	@Override
	public Context getTarget() {
		return conclusion.getTarget();
	}

	@Override
	public boolean addToContextBackwardLinkRule(Context context) {
		return conclusion.addToContextBackwardLinkRule(context);
	}

	@Override
	public boolean removeFromContextBackwardLinkRule(Context context) {
		return conclusion.removeFromContextBackwardLinkRule(context);
	}

	@Override
	public boolean containsBackwardLinkRule(Context context) {
		return conclusion.containsBackwardLinkRule(context);
	}

	@Override
	public void apply(BasicSaturationStateWriter writer, Context context) {
		conclusion.apply(writer, context);
	}

}
