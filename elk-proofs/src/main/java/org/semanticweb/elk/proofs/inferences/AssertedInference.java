/**
 * 
 */
package org.semanticweb.elk.proofs.inferences;

import java.util.Collection;
import java.util.Collections;

import org.semanticweb.elk.proofs.expressions.derived.DerivedAxiomExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class AssertedInference implements Inference {

	private final DerivedAxiomExpression expression_;
	
	public AssertedInference(DerivedAxiomExpression e) {
		expression_ = e;
	}
	
	@Override
	public Collection<? extends DerivedExpression> getPremises() {
		return Collections.emptyList();
	}

	@Override
	public DerivedExpression getConclusion() {
		return expression_;
	}

	@Override
	public <I, O> O accept(InferenceVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

}
