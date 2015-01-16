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
import org.semanticweb.elk.proofs.expressions.LemmaExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedAxiomExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.proofs.inferences.ClassInferenceVisitor;
import org.semanticweb.elk.proofs.inferences.InferenceRule;
import org.semanticweb.elk.proofs.inferences.InferenceVisitor;
import org.semanticweb.elk.proofs.utils.InferencePrinter;

/**
 * The existential inference based on role composition. 
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ExistentialCompositionViaChain extends AbstractClassInference {

	private final DerivedExpression firstExistentialPremise_;

	private final DerivedExpression secondExistentialPremise_;

	private final DerivedExpression firstPropertySubsumptionPremise_;

	private final DerivedExpression secondPropertySubsumptionPremise_;

	private final DerivedExpression chainPremise_;
	// conclusion is an axiom
	public ExistentialCompositionViaChain(
			DerivedAxiomExpression<ElkSubClassOfAxiom> conclusion,
			DerivedExpression firstExPremise,
			DerivedExpression secondExPremise,
			DerivedExpression propSubsumption,
			DerivedExpression chainSubsumption,
			DerivedExpression chainAxiom) {
		super(conclusion);
		firstExistentialPremise_ = firstExPremise;
		secondExistentialPremise_ = secondExPremise;
		firstPropertySubsumptionPremise_ = propSubsumption;
		secondPropertySubsumptionPremise_ = chainSubsumption;
		chainPremise_ = chainAxiom;
	}	
	// conclusion is a lemma
	public ExistentialCompositionViaChain(
			LemmaExpression conclusion,
			DerivedExpression firstExPremise,
			DerivedExpression secondExPremise,
			DerivedExpression propSubsumption,
			DerivedExpression chainSubsumption,
			DerivedExpression chainAxiom) {
		super(conclusion);
		firstExistentialPremise_ = firstExPremise;
		secondExistentialPremise_ = secondExPremise;
		firstPropertySubsumptionPremise_ = propSubsumption;
		secondPropertySubsumptionPremise_ = chainSubsumption;
		chainPremise_ = chainAxiom;
	}
	
	@Override
	public Collection<DerivedExpression> getRawPremises() {
		if (chainPremise_ != null) {
			return Arrays.asList(firstExistentialPremise_, secondExistentialPremise_, firstPropertySubsumptionPremise_, secondPropertySubsumptionPremise_, chainPremise_);
		}
		else {
			return Arrays.asList(firstExistentialPremise_, secondExistentialPremise_, firstPropertySubsumptionPremise_, secondPropertySubsumptionPremise_);
		}
	}
	
	@Override
	public <I, O> O accept(InferenceVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

	@Override
	public String toString() {
		return InferencePrinter.print(this);
	}
	
	@Override
	public InferenceRule getRule() {
		return InferenceRule.R_EXIST_CHAIN_COMPOSITION;
	}
	
	@Override
	public <I, O> O accept(ClassInferenceVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}
}
