/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.conclusions;


/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class BaseConclusionVisitor<R, C> implements ConclusionVisitor<R, C> {

	protected R defaultVisit(Conclusion conclusion, C cxt) {
		return null;
	}
	
	@Override
	public R visit(NegativeSubsumer negSCE, C context) {
		return defaultVisit(negSCE, context);
	}

	@Override
	public R visit(PositiveSubsumer posSCE, C context) {
		return defaultVisit(posSCE, context);
	}

	@Override
	public R visit(BackwardLink link, C context) {
		return defaultVisit(link, context);
	}

	@Override
	public R visit(ForwardLink link, C context) {
		return defaultVisit(link, context);
	}

	@Override
	public R visit(Contradiction bot, C context) {
		return defaultVisit(bot, context);
	}

	@Override
	public R visit(Propagation propagation, C context) {
		return defaultVisit(propagation, context);
	}

	@Override
	public R visit(DisjointnessAxiom disjointnessAxiom, C context) {
		return defaultVisit(disjointnessAxiom, context);
	}

}
