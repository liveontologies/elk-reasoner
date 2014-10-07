/**
 * 
 */
package org.semanticweb.elk.proofs.inferences.classes;
/*
 * #%L
 * ELK Proofs Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Arrays;
import java.util.Collection;

import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpressionFactory;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubClassOfLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubPropertyChainOfLemma;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.proofs.inferences.InferenceVisitor;
import org.semanticweb.elk.proofs.sideconditions.AxiomPresenceCondition;
import org.semanticweb.elk.proofs.utils.InferencePrinter;

/**
 * The existential inference based on role composition. 
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ExistentialCompositionViaChain implements Inference {

	private final DerivedExpression firstExistentialPremise_;

	private final DerivedExpression secondExistentialPremise_;

	private final DerivedExpression firstPropertySubsumptionPremise_;

	private final DerivedExpression secondPropertySubsumptionPremise_;

	private final ElkSubObjectPropertyOfAxiom chainAxiom_;
	
	private final DerivedExpression conclusion_;

	private ExistentialCompositionViaChain(
			DerivedExpression conclusion,
			DerivedExpression firstExPremise,
			DerivedExpression secondExPremise,
			DerivedExpression propSubsumption,
			DerivedExpression chainSubsumption,
			ElkSubObjectPropertyOfAxiom chainAxiom) {
		conclusion_ = conclusion;
		firstExistentialPremise_ = firstExPremise;
		secondExistentialPremise_ = secondExPremise;
		firstPropertySubsumptionPremise_ = propSubsumption;
		secondPropertySubsumptionPremise_ = chainSubsumption;
		chainAxiom_ = chainAxiom;
	}	
	
	// inference with a side condition, a simple right property premise and a simple conclusion
	public ExistentialCompositionViaChain(
			ElkSubClassOfAxiom conclusion,
			ElkSubClassOfAxiom firstExPremise,
			ElkSubClassOfAxiom secondExPremise,
			ElkSubObjectPropertyOfAxiom propSubsumption,
			ElkSubObjectPropertyOfAxiom chainSubsumption,
			ElkSubObjectPropertyOfAxiom chainAxiom,
			DerivedExpressionFactory exprFactory) {
		this(	exprFactory.create(conclusion),
				exprFactory.create(firstExPremise), 
				exprFactory.create(secondExPremise), 
				exprFactory.create(propSubsumption), 
				exprFactory.create(chainSubsumption), 
				chainAxiom);
	}

	// inference with a side condition, a complex right property premise and a simple conclusion
	public ExistentialCompositionViaChain(
			ElkSubClassOfAxiom conclusion,
			ElkSubClassOfAxiom firstExPremise,
			ElkSubClassOfLemma secondExPremise,
			ElkSubObjectPropertyOfAxiom propSubsumption,
			ElkSubPropertyChainOfLemma chainSubsumption,
			ElkSubObjectPropertyOfAxiom chainAxiom,
			DerivedExpressionFactory exprFactory) {
		this(	exprFactory.create(conclusion),
				exprFactory.create(firstExPremise), 
				exprFactory.create(secondExPremise), 
				exprFactory.create(propSubsumption), 
				exprFactory.create(chainSubsumption), 
				chainAxiom);
	}
	
	// inference with a complex chain premise, a complex existential in the conclusion and no side condition
	public ExistentialCompositionViaChain(
			ElkSubClassOfLemma conclusion,
			ElkSubClassOfAxiom firstExPremise,
			ElkSubClassOfLemma secondExPremise,
			ElkSubObjectPropertyOfAxiom propSubsumption,
			ElkSubPropertyChainOfLemma chainSubsumption,
			DerivedExpressionFactory exprFactory) {
		this(	exprFactory.create(conclusion), 
				exprFactory.create(firstExPremise), 
				exprFactory.create(secondExPremise), 
				exprFactory.create(propSubsumption), 
				exprFactory.create(chainSubsumption), 
				null);
	}
	
	// inference with a simple chain premise, complex existential in the conclusion and no side condition
	public ExistentialCompositionViaChain(
			ElkSubClassOfLemma conclusion,
			ElkSubClassOfAxiom firstExPremise,
			ElkSubClassOfAxiom secondExPremise,
			ElkSubObjectPropertyOfAxiom leftPropSubsumption,
			ElkSubObjectPropertyOfAxiom rightPropSubsumption,
			DerivedExpressionFactory exprFactory) {
		this(	exprFactory.create(conclusion),
				exprFactory.create(firstExPremise),	
				exprFactory.create(secondExPremise), 
				exprFactory.create(leftPropSubsumption), 
				exprFactory.create(rightPropSubsumption), null);
	}
	
	@Override
	public Collection<? extends DerivedExpression> getPremises() {
		return Arrays.asList(firstExistentialPremise_, secondExistentialPremise_, firstPropertySubsumptionPremise_, secondPropertySubsumptionPremise_);
	}
	
	@Override
	public AxiomPresenceCondition<ElkSubObjectPropertyOfAxiom> getSideCondition() {
		return chainAxiom_ == null ? null : new AxiomPresenceCondition<ElkSubObjectPropertyOfAxiom>(chainAxiom_);
	}

	@Override
	public <I, O> O accept(InferenceVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

	@Override
	public Expression getConclusion() {
		return conclusion_;
	}

	@Override
	public String toString() {
		return InferencePrinter.print(this);
	}
}
