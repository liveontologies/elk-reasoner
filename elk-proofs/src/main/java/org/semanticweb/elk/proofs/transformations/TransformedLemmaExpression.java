/**
 * 
 */
package org.semanticweb.elk.proofs.transformations;

import org.semanticweb.elk.proofs.expressions.LemmaExpression;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemma;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.util.collections.Operations;

/**
 * TODO
 * 
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
public class TransformedLemmaExpression<T extends Operations.Transformation<Inference, Iterable<Inference>>> 
				extends TransformedExpression<LemmaExpression, T> implements LemmaExpression {

	protected TransformedLemmaExpression(LemmaExpression expr, T f) {
		super(expr, f);
	}

	@Override
	public ElkLemma getLemma() {
		return expression.getLemma();
	}

}
