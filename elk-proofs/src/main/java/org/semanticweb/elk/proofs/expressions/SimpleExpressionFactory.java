/**
 * 
 */
package org.semanticweb.elk.proofs.expressions;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemma;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class SimpleExpressionFactory implements ExpressionFactory {

	@Override
	public AxiomExpression create(ElkAxiom axiom) {
		return new AxiomExpression(axiom);
	}

	@Override
	public LemmaExpression create(ElkLemma lemma) {
		return new LemmaExpression(lemma);
	}

}
