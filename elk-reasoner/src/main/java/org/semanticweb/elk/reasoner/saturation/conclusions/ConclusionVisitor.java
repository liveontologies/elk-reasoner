/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.conclusions;

import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface ConclusionVisitor<R> {

	public R visit(NegativeSuperClassExpression negSCE, Context context);
	
	public R visit(PositiveSuperClassExpression posSCE, Context context);
	
	public R visit(BackwardLink link, Context context);
	
	public R visit(ForwardLink link, Context context);

	public R visit(IndexChange indexChange, Context context);
	
	public R visit(Bottom bot, Context context);

	public R visit(Propagation propagation, Context context);

	public R visit(DisjointnessAxiom disjointnessAxiom, Context context);
}
