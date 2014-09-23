/**
 * 
 */
package org.semanticweb.elk.proofs.expressions;


/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class MultiAxiomExpression implements Expression {

	private final Iterable<Explanation> explanations_;
	
	// single explanation
	public MultiAxiomExpression(Iterable<Explanation> expl) {
		explanations_ = expl;
	}

	@Override
	public Iterable<Explanation> getExplanations() {
		return explanations_;
	}

}
