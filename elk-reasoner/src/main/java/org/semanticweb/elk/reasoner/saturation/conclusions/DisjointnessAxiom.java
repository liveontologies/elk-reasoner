/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.conclusions;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointnessAxiom;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class DisjointnessAxiom implements Conclusion {

	private final IndexedDisjointnessAxiom axiom_;
	
	public DisjointnessAxiom(IndexedDisjointnessAxiom axiom) {
		axiom_ = axiom;
	}
	
	public IndexedDisjointnessAxiom getAxiom() {
		return axiom_;
	}
	
	@Override
	public void deapply(SaturationState state, Context context) {
		apply(state, context);
	}

	@Override
	public void apply(SaturationState state, Context context) {
		if (context.containsDisjointnessAxiom(axiom_) > 1) {
			state.produce(context, new PositiveSuperClassExpression(state.getOwlNothing()));
		}
	}

	@Override
	public <R> R accept(ConclusionVisitor<R> visitor, Context context) {
		return visitor.visit(this, context);
	}

	@Override
	public String toString() {
		return axiom_.toString();
	}
}