/**
 * 
 */
package org.semanticweb.elk.owlapi.proofs;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.proofs.expressions.derived.DerivedAxiomExpression;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapitools.proofs.expressions.OWLAxiomExpression;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpressionVisitor;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class AxiomExpressionWrap extends BaseDerivedExpressionWrap<DerivedAxiomExpression<? extends ElkAxiom>> implements OWLAxiomExpression {

	AxiomExpressionWrap(DerivedAxiomExpression<? extends ElkAxiom> expr) {
		super(expr);
	}

	@Override
	public <O> O accept(OWLExpressionVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public boolean isAsserted() {
		return expression.isAsserted();
	}

	@Override
	public OWLAxiom getAxiom() {
		return ElkToOwlProofConverter.convert(expression.getAxiom());
	}
	
}
