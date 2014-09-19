/**
 * 
 */
package org.semanticweb.elk.proofs.inferences.classes;

import java.util.Arrays;
import java.util.Collection;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
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
public class ExistentialViaChainComposition implements Inference {

	private final Expression<?> firstExistentialPremise_;

	private final Expression<?> secondExistentialPremise_;

	private final Expression<ElkSubObjectPropertyOfAxiom> firstPropertySubsumptionPremise_;

	private final Expression<ElkSubObjectPropertyOfAxiom> secondPropertySubsumptionPremise_;

	private final ElkSubObjectPropertyOfAxiom chainAxiom_;
	
	private final Expression<?> conclusion_;

	private ExistentialViaChainComposition(
			Expression<?> conclusion,
			Expression<?> firstExPremise,
			Expression<?> secondExPremise,
			Expression<ElkSubObjectPropertyOfAxiom> propSubsumption,
			Expression<ElkSubObjectPropertyOfAxiom> chainSubsumption,
			ElkSubObjectPropertyOfAxiom chainAxiom) {
		conclusion_ = conclusion;
		firstExistentialPremise_ = firstExPremise;
		secondExistentialPremise_ = secondExPremise;
		firstPropertySubsumptionPremise_ = propSubsumption;
		secondPropertySubsumptionPremise_ = chainSubsumption;
		chainAxiom_ = chainAxiom;
	}	
	
	// inference with a side condition and a single subsumption axiom with a simple existential on the right as the conclusion
	public ExistentialViaChainComposition(ElkSubClassOfAxiom firstExPremise,
			Expression<?> secondExPremise,
			ElkClassExpression existential,
			ElkSubObjectPropertyOfAxiom propSubsumption,
			Expression<ElkSubObjectPropertyOfAxiom> chainSubsumption,
			ElkSubObjectPropertyOfAxiom chainAxiom, ElkObjectFactory factory) {
		this(new SingleAxiomExpression<ElkSubClassOfAxiom>(factory.getSubClassOfAxiom(
				firstExPremise.getSubClassExpression(), 
				factory.getObjectSomeValuesFrom(
						ProofUtils.asObjectProperty(chainAxiom.getSuperObjectPropertyExpression()),
						existential))),
			new SingleAxiomExpression<ElkSubClassOfAxiom>(firstExPremise), 
			secondExPremise, 
			new SingleAxiomExpression<ElkSubObjectPropertyOfAxiom>(propSubsumption), 
			chainSubsumption, 
			chainAxiom);
	}
	
	// inference with a complex existential in the conclusion and no side condition
	public ExistentialViaChainComposition(
			Expression<?> firstExPremise,
			Expression<?> secondExPremise,
			Expression<ElkSubObjectPropertyOfAxiom> propSubsumption,
			Expression<ElkSubObjectPropertyOfAxiom> chainSubsumption
			) {
		this(ProofUtils.merge(firstExPremise, secondExPremise, propSubsumption, chainSubsumption),
			firstExPremise, 
			secondExPremise, 
			propSubsumption, 
			chainSubsumption, 
			null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<? extends Expression<? extends ElkAxiom>> getPremises() {
		return Arrays.asList(firstExistentialPremise_, secondExistentialPremise_, firstPropertySubsumptionPremise_, secondPropertySubsumptionPremise_);
	}
	
	@Override
	public AxiomPresenceCondition<ElkSubObjectPropertyOfAxiom> getSideCondition() {
		return new AxiomPresenceCondition<ElkSubObjectPropertyOfAxiom>(chainAxiom_);
	}

	@Override
	public <I, O> O accept(InferenceVisitor<I, O> visitor, I input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression<?> getConclusion() {
		return conclusion_;
	}

}
