/**
 * 
 */
package org.semanticweb.elk.proofs.inferences.properties;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.expressions.SingleAxiomExpression;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.proofs.inferences.InferenceVisitor;
import org.semanticweb.elk.proofs.sideconditions.AxiomPresenceCondition;
import org.semanticweb.elk.proofs.sideconditions.SideCondition;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ChainSubsumption implements Inference {

	private final Expression<ElkObjectPropertyAxiom> firstPremise_;

	private final Expression<ElkObjectPropertyAxiom> secondPremise_;

	private final Expression<? extends ElkObjectPropertyAxiom> conclusion_;

	private final ElkSubObjectPropertyOfAxiom sideCondition_;

	// one premise and a side condition
	public ChainSubsumption(ElkSubObjectPropertyOfAxiom conclusion,
			Expression<ElkObjectPropertyAxiom> premise,
			ElkSubObjectPropertyOfAxiom sideCondition) {
		firstPremise_ = premise;
		secondPremise_ = null;
		conclusion_ = new SingleAxiomExpression<ElkSubObjectPropertyOfAxiom>(
				conclusion);
		sideCondition_ = sideCondition;
	}

	// two premises and no side condition
	public ChainSubsumption(ElkSubObjectPropertyOfAxiom conclusion,
			Expression<ElkObjectPropertyAxiom> first,
			Expression<ElkObjectPropertyAxiom> second) {
		firstPremise_ = first;
		secondPremise_ = second;
		conclusion_ = new SingleAxiomExpression<ElkSubObjectPropertyOfAxiom>(
				conclusion);
		sideCondition_ = null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<? extends Expression<? extends ElkAxiom>> getPremises() {
		if (secondPremise_ == null) {
			return Collections.singletonList(firstPremise_);
		}

		return Arrays.asList(firstPremise_, secondPremise_);
	}

	@Override
	public Expression<? extends ElkObjectPropertyAxiom> getConclusion() {
		return conclusion_;
	}

	@Override
	public SideCondition getSideCondition() {
		return sideCondition_ == null ? null
				: new AxiomPresenceCondition<ElkSubObjectPropertyOfAxiom>(
						sideCondition_);
	}

	@Override
	public <I, O> O accept(InferenceVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

}
