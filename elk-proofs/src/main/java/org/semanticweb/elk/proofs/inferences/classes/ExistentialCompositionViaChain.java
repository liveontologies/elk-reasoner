/**
 * 
 */
package org.semanticweb.elk.proofs.inferences.classes;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.proofs.expressions.Explanation;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.expressions.SingleAxiomExpression;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.proofs.inferences.InferenceVisitor;
import org.semanticweb.elk.proofs.sideconditions.AxiomPresenceCondition;
import org.semanticweb.elk.proofs.utils.ProofUtils;

/**
 * The existential inference based on role composition. 
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ExistentialCompositionViaChain implements Inference {

	private final Expression firstExistentialPremise_;

	private final Expression secondExistentialPremise_;

	private final Expression firstPropertySubsumptionPremise_;

	private final Expression secondPropertySubsumptionPremise_;

	private final ElkSubObjectPropertyOfAxiom chainAxiom_;
	
	private final Expression conclusion_;

	private ExistentialCompositionViaChain(
			Expression conclusion,
			Expression firstExPremise,
			Expression secondExPremise,
			Expression propSubsumption,
			Expression chainSubsumption,
			ElkSubObjectPropertyOfAxiom chainAxiom) {
		conclusion_ = conclusion;
		firstExistentialPremise_ = firstExPremise;
		secondExistentialPremise_ = secondExPremise;
		firstPropertySubsumptionPremise_ = propSubsumption;
		secondPropertySubsumptionPremise_ = chainSubsumption;
		chainAxiom_ = chainAxiom;
	}	
	
	// inference with a side condition and a single subsumption axiom with a simple existential on the right as the conclusion
	public ExistentialCompositionViaChain(
			ElkClassExpression existential,
			ElkSubClassOfAxiom firstExPremise,
			Expression secondExPremise,
			ElkSubObjectPropertyOfAxiom propSubsumption,
			Expression chainSubsumption,
			ElkSubObjectPropertyOfAxiom chainAxiom, 
			ElkObjectFactory factory) {
		this(new SingleAxiomExpression(factory.getSubClassOfAxiom(
				firstExPremise.getSubClassExpression(), 
				factory.getObjectSomeValuesFrom(
						ProofUtils.asObjectProperty(chainAxiom.getSuperObjectPropertyExpression()),
						existential))),
			new SingleAxiomExpression(firstExPremise), 
			secondExPremise, 
			new SingleAxiomExpression(propSubsumption), 
			chainSubsumption, 
			chainAxiom);
	}
	
	// inference with a complex existential in the conclusion and no side condition
	@SuppressWarnings("unchecked")
	public ExistentialCompositionViaChain(
			ElkSubClassOfAxiom firstExPremise,
			Expression secondExPremise,
			ElkSubObjectPropertyOfAxiom propSubsumption,
			Expression chainSubsumption
			) {
		this(ProofUtils.fromPremiseExplanations(
				Arrays.asList(
						Collections.singletonList(new Explanation(firstExPremise)),
						secondExPremise.getExplanations(),
						Collections.singletonList(new Explanation(propSubsumption)),
						chainSubsumption.getExplanations())), 
				new SingleAxiomExpression(firstExPremise),	secondExPremise, new SingleAxiomExpression(propSubsumption), chainSubsumption, null);
	}

	@Override
	public Collection<? extends Expression> getPremises() {
		return Arrays.asList(firstExistentialPremise_, secondExistentialPremise_, firstPropertySubsumptionPremise_, secondPropertySubsumptionPremise_);
	}
	
	@Override
	public AxiomPresenceCondition<ElkSubObjectPropertyOfAxiom> getSideCondition() {
		return new AxiomPresenceCondition<ElkSubObjectPropertyOfAxiom>(chainAxiom_);
	}

	@Override
	public <I, O> O accept(InferenceVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

	@Override
	public Expression getConclusion() {
		return conclusion_;
	}

}
