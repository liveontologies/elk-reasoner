/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.util;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.AbstractConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class PropagationPremise extends AbstractConclusion implements Propagation {

	private final IndexedPropertyChain propagationRelation_;
	
	private final IndexedObjectSomeValuesFrom carry_;
	/**
	 * 
	 */
	public PropagationPremise(IndexedPropertyChain relation, IndexedObjectSomeValuesFrom carry) {
		propagationRelation_ = relation;
		carry_ = carry;
	}

	@Override
	public <R, C> R accept(ConclusionVisitor<R, C> visitor, C parameter) {
		return visitor.visit(this, parameter);
	}

	@Override
	public IndexedPropertyChain getRelation() {
		return propagationRelation_;
	}

	@Override
	public IndexedObjectSomeValuesFrom getCarry() {
		return carry_;
	}

	@Override
	public boolean addToContextBackwardLinkRule(Context context) {
		// no-op
		return false;
	}

	@Override
	public boolean removeFromContextBackwardLinkRule(Context context) {
		// no-op
		return false;
	}

	@Override
	public boolean containsBackwardLinkRule(Context context) {
		// no-op
		return false;
	}

	@Override
	public void apply(BasicSaturationStateWriter writer, Context context) {
		// no-op
	}

}
