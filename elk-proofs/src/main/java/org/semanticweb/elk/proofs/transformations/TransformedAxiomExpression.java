/**
 * 
 */
package org.semanticweb.elk.proofs.transformations;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.proofs.expressions.derived.DerivedAxiomExpression;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.util.collections.Operations;

/**
 * TODO
 * 
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
public class TransformedAxiomExpression<T extends Operations.Transformation<Inference, Iterable<Inference>>> 
				extends TransformedExpression<DerivedAxiomExpression<?>, T> implements DerivedAxiomExpression<ElkAxiom> {

	protected TransformedAxiomExpression(DerivedAxiomExpression<?> expr, T f) {
		super(expr, f);
	}

	@Override
	public ElkAxiom getAxiom() {
		return expression.getAxiom();
	}

	@Override
	public boolean isAsserted() {
		return expression.isAsserted();
	}

}
