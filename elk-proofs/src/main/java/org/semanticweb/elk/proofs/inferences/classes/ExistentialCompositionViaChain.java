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
import org.semanticweb.elk.proofs.expressions.AxiomExpression;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.expressions.LemmaExpression;
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
	
	// inference with a side condition, a simple right property premise and a simple conclusion
	public ExistentialCompositionViaChain(
			ElkSubClassOfAxiom conclusion,
			ElkSubClassOfAxiom firstExPremise,
			ElkSubClassOfAxiom secondExPremise,
			ElkSubObjectPropertyOfAxiom propSubsumption,
			ElkSubObjectPropertyOfAxiom chainSubsumption,
			ElkSubObjectPropertyOfAxiom chainAxiom) {
		this(new AxiomExpression(conclusion),
			new AxiomExpression(firstExPremise), 
			new AxiomExpression(secondExPremise), 
			new AxiomExpression(propSubsumption), 
			new AxiomExpression(chainSubsumption), 
			chainAxiom);
	}

	// inference with a side condition, a complex right property premise and a simple conclusion
	public ExistentialCompositionViaChain(
			ElkSubClassOfAxiom conclusion,
			ElkSubClassOfAxiom firstExPremise,
			ElkSubClassOfLemma secondExPremise,
			ElkSubObjectPropertyOfAxiom propSubsumption,
			ElkSubPropertyChainOfLemma chainSubsumption,
			ElkSubObjectPropertyOfAxiom chainAxiom) {
		this(new AxiomExpression(conclusion),
			new AxiomExpression(firstExPremise), 
			new LemmaExpression(secondExPremise), 
			new AxiomExpression(propSubsumption), 
			new LemmaExpression(chainSubsumption), 
			chainAxiom);
	}
	
	// inference with a complex chain premise, a complex existential in the conclusion and no side condition
	public ExistentialCompositionViaChain(
			ElkSubClassOfLemma conclusion,
			ElkSubClassOfAxiom firstExPremise,
			ElkSubClassOfLemma secondExPremise,
			ElkSubObjectPropertyOfAxiom propSubsumption,
			ElkSubPropertyChainOfLemma chainSubsumption
			) {
		this(	new LemmaExpression(conclusion), 
				new AxiomExpression(firstExPremise), 
				new LemmaExpression(secondExPremise), 
				new AxiomExpression(propSubsumption), 
				new LemmaExpression(chainSubsumption), 
				null);
	}
	
	// inference with a simple chain premise, complex existential in the conclusion and no side condition
	public ExistentialCompositionViaChain(
			ElkSubClassOfLemma conclusion,
			ElkSubClassOfAxiom firstExPremise,
			ElkSubClassOfAxiom secondExPremise,
			ElkSubObjectPropertyOfAxiom leftPropSubsumption,
			ElkSubObjectPropertyOfAxiom rightPropSubsumption
			) {
		this(	new LemmaExpression(conclusion),
				new AxiomExpression(firstExPremise),	
				new AxiomExpression(secondExPremise), 
				new AxiomExpression(leftPropSubsumption), 
				new AxiomExpression(rightPropSubsumption), null);
	}
	
	@Override
	public Collection<? extends Expression> getPremises() {
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
