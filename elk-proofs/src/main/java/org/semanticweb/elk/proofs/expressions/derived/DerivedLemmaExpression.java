/**
 * 
 */
package org.semanticweb.elk.proofs.expressions.derived;

import org.semanticweb.elk.proofs.expressions.ExpressionVisitor;
import org.semanticweb.elk.proofs.expressions.LemmaExpression;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemma;
import org.semanticweb.elk.proofs.inferences.InferenceReader;
import org.semanticweb.elk.proofs.utils.ElkLemmaPrinter;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class DerivedLemmaExpression extends AbstractDerivedExpression implements LemmaExpression {

	private final ElkLemma lemma_;
	
	public DerivedLemmaExpression(ElkLemma lemma, InferenceReader r) {
		super(r);
		lemma_ = lemma;
	}
	
	@Override
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
