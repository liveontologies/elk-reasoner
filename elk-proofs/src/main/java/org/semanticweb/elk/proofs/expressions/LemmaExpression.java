/**
 * 
 */
package org.semanticweb.elk.proofs.expressions;

import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemma;
import org.semanticweb.elk.proofs.utils.ElkLemmaPrinter;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class LemmaExpression implements Expression {

	private final ElkLemma lemma_;
	
	public LemmaExpression(ElkLemma lemma) {
		lemma_ = lemma;
	}
	
	public ElkLemma getLemma() {
		return lemma_;
	}

	@Override
	public String toString() {
		return ElkLemmaPrinter.print(lemma_);
	}
	
	@Override
	public <I, O> O accept(ExpressionVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}	
}
