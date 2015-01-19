/**
 * 
 */
package org.semanticweb.elk.proofs.expressions.derived;

import java.util.List;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyChain;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.proofs.expressions.ExpressionVisitor;
import org.semanticweb.elk.proofs.inferences.Inference;

/**
 * Represents expressions of the form R1 o R2 o ... o Rn <= V for which we may not have recorded inferences.
 * Instead, we know the sub-property chain axiom S1 o S2 o ... o Sm <= V from which the given expression follows (using other axioms).
 * This class recreates such inferences using recorded inferences for substrings of R1 o R2 o ... o Rn.
 * 
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
public class SubPropertyChainExpression implements DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> {

	private final DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> expr_;
	
	private final List<? extends ElkSubObjectPropertyExpression> targetChain_;
	
	public SubPropertyChainExpression(DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> expr, ElkSubObjectPropertyOfAxiom targetAxiom) {
		expr_ = expr;
		targetChain_ = ((ElkObjectPropertyChain) targetAxiom.getSubObjectPropertyExpression()).getObjectPropertyExpressions();
	}

	@Override
	public ElkSubObjectPropertyOfAxiom getAxiom() {
		return expr_.getAxiom();
	}

	@Override
	public boolean isAsserted() {
		return expr_.isAsserted();
	}

	@Override
	public <I, O> O accept(ExpressionVisitor<I, O> visitor, I input) {
		return expr_.accept(visitor, input);
	}

	@Override
	public Iterable<? extends Inference> getInferences() throws ElkException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		return expr_.equals(obj);
	}

	@Override
	public int hashCode() {
		return expr_.hashCode();
	}
	
}
