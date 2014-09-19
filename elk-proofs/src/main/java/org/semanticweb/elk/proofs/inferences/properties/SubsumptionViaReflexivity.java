/**
 * 
 */
package org.semanticweb.elk.proofs.inferences.properties;

import java.util.Collection;
import java.util.Collections;

import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAxiom;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.proofs.inferences.InferenceVisitor;
import org.semanticweb.elk.proofs.sideconditions.SideCondition;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class SubsumptionViaReflexivity implements Inference  {
	
	private final Expression<ElkObjectPropertyAxiom> premise_;
	
	private final Expression<ElkObjectPropertyAxiom> conclusion_;
	
	public SubsumptionViaReflexivity(Expression<ElkObjectPropertyAxiom> premise, Expression<ElkObjectPropertyAxiom> conclusion) {
		premise_ = premise;
		conclusion_ = conclusion;
	}
	
	@Override
	public Collection<? extends Expression<? extends ElkObjectPropertyAxiom>> getPremises() {
		return Collections.singletonList(premise_);
	}

	@Override
	public SideCondition getSideCondition() {
		return null;
	}

	@Override
	public <I, O> O accept(InferenceVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

	@Override
	public Expression<ElkObjectPropertyAxiom> getConclusion() {
		return conclusion_;
	}

}
