/**
 * 
 */
package org.semanticweb.elk.owlapi.proofs;

import org.semanticweb.elk.proofs.expressions.derived.DerivedLemmaExpression;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpressionVisitor;
import org.semanticweb.owlapitools.proofs.expressions.OWLLemmaExpression;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class LemmaExpressionWrap extends BaseDerivedExpressionWrap<DerivedLemmaExpression> implements OWLLemmaExpression { 

	LemmaExpressionWrap(DerivedLemmaExpression expr) {
		super(expr);
	}

	@Override
	public <O> O accept(OWLExpressionVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
