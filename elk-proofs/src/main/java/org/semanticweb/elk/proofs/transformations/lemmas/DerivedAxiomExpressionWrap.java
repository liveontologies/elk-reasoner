/**
 * 
 */
package org.semanticweb.elk.proofs.transformations.lemmas;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.proofs.expressions.ExpressionVisitor;
import org.semanticweb.elk.proofs.expressions.derived.DerivedAxiomExpression;
import org.semanticweb.elk.proofs.inferences.Inference;


/**
 * TODO
 * 
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
public class DerivedAxiomExpressionWrap<E extends ElkAxiom> implements DerivedAxiomExpression<E> {

	private final DerivedAxiomExpression<E> expr_;
	
	private final List<Inference> inferences_;
	
	public DerivedAxiomExpressionWrap(DerivedAxiomExpression<E> expr) {
		expr_ = expr;
		inferences_ = new ArrayList<Inference>();
	}
	
	@Override
	public E getAxiom() {
		return expr_.getAxiom();
	}

	@Override
	public boolean isAsserted() {
		return expr_.isAsserted();
	}

	@Override
	public <I, O> O accept(ExpressionVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}
	
	public void addInference(Inference inf) {
		inferences_.add(inf);
	}

	@Override
	public Iterable<? extends Inference> getInferences() throws ElkException {
		//return Operations.concat(expr_.getInferences(), inferences_);
		return inferences_;
	}

	@Override
	public boolean equals(Object obj) {
		return expr_.equals(obj);
	}

	@Override
	public int hashCode() {
		return expr_.hashCode();
	}

	@Override
	public String toString() {
		return expr_.toString();
	}
}
