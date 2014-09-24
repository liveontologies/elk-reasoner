/**
 * 
 */
package org.semanticweb.elk.proofs.expressions;

import java.util.List;

import org.semanticweb.elk.proofs.utils.ProofUtils;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.Operations.Transformation;

/**
 * An expression represented by the Cartesian product of lists of explanations.
 * This typically happens when a conclusion is represented as a combination of
 * explanations for each of the premises.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
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
			// expanding the cartesian
			// TODO lazy eval here?
			explanations_ = ProofUtils.fromPremiseExplanations(explanationList_);
		}
		
		return explanations_;
	}

	@Override
	public String toString() {
		if (explanations_ != null) {
			return Operations.toString(explanations_);
		}
		
		return "Cartesian(" + Operations.toString(Operations.map(explanationList_, new Transformation<Iterable<Explanation>, String>(){

			@Override
			public String transform(Iterable<Explanation> expl) {
				return Operations.toString(expl);
			}
			
		})) + ")";
	}

}
