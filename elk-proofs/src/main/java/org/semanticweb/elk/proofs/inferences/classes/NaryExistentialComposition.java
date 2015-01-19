/**
 * 
 */
package org.semanticweb.elk.proofs.inferences.classes;

import java.util.Collections;
import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkClassAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.proofs.expressions.derived.DerivedAxiomExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.proofs.inferences.InferenceRule;
import org.semanticweb.elk.proofs.inferences.InferenceVisitor;
import org.semanticweb.elk.util.collections.Operations;

/**
 * TODO
 * 
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
public class NaryExistentialComposition extends AbstractClassInference<DerivedAxiomExpression<ElkSubClassOfAxiom>> {

	private final List<? extends DerivedExpression> existentialPremises_;
	
	//private final List<? extends DerivedExpression> rolePremises_;
	
	private final DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> chainAxiom_;
	
	public NaryExistentialComposition(DerivedAxiomExpression<ElkSubClassOfAxiom> conclusion,
			List<? extends DerivedExpression> premises,
			//List<? extends DerivedExpression> rolePremises,
			DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> chainAxiom) {
		super(conclusion);
		
		existentialPremises_ = premises;
		//rolePremises_ = rolePremises;
		chainAxiom_ = chainAxiom;
	}

	@Override
	public InferenceRule getRule() {
		return InferenceRule.R_EXIST_CHAIN_COMPOSITION;
	}

	@Override
	public <I, O> O accept(InferenceVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

	@Override
	protected Iterable<DerivedExpression> getRawPremises() {
		//return Operations.concat(Arrays.asList(existentialPremises_, rolePremises_, Collections.singletonList(chainAxiom_)));
		return Operations.concat(existentialPremises_, Collections.singletonList(chainAxiom_));
	}
	
	public List<? extends DerivedExpression> getExistentialPremises() {
		return existentialPremises_;
	}
	
	/*public List<? extends DerivedExpression> getRolePremises() {
		return rolePremises_;
	}*/
	
	public DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> getChainPremise() {
		return chainAxiom_;
	}
}
