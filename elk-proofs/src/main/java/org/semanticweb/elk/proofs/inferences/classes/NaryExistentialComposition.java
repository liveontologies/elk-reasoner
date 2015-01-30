/**
 * 
 */
package org.semanticweb.elk.proofs.inferences.classes;

import java.util.List;

import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.proofs.inferences.AbstractInference;
import org.semanticweb.elk.proofs.inferences.InferenceRule;

/**
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
public abstract class NaryExistentialComposition<D extends DerivedExpression> extends AbstractInference<D> {

	protected final List<? extends DerivedExpression> existentialPremises;
	
	protected NaryExistentialComposition(D conclusion, List<? extends DerivedExpression> exPremises) {
		super(conclusion);
		
		existentialPremises = exPremises;
	}

	public List<? extends DerivedExpression> getExistentialPremises() {
		return existentialPremises;
	}
	
	@Override
	public InferenceRule getRule() {
		return InferenceRule.R_EXIST_CHAIN_COMPOSITION;
	}
}
