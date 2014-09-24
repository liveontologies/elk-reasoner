/**
 * 
 */
package org.semanticweb.elk.proofs.expressions;

import java.util.List;

import org.semanticweb.elk.proofs.utils.ProofUtils;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class CartesianExpression implements Expression {

	private final List<Iterable<Explanation>> explanationList_;
	
	private Iterable<Explanation> explanations_;
	
	public CartesianExpression(List<Iterable<Explanation>> explanationList) {
		explanationList_ = explanationList;
	}
	
	@Override
	public Iterable<Explanation> getExplanations() {
		if (explanations_ == null) {
			explanations_ = ProofUtils.fromPremiseExplanations(explanationList_);
		}
		
		return explanations_;
	}

}
