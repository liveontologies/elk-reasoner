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

import org.semanticweb.elk.owl.interfaces.ElkClassAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.proofs.expressions.LemmaExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedAxiomExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.proofs.inferences.InferenceRule;
import org.semanticweb.elk.proofs.inferences.InferenceVisitor;

/**
 * The existential inference based on role composition which derives a lemma expression. 
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ExistentialLemmaChainComposition extends AbstractClassInference<LemmaExpression> {

	private final DerivedAxiomExpression<ElkSubClassOfAxiom> firstExistentialPremise_;

	private final DerivedExpression secondExistentialPremise_;

	private final DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> firstPropertySubsumptionPremise_;

	private final DerivedExpression secondPropertySubsumptionPremise_;
	
	public ExistentialLemmaChainComposition(
			LemmaExpression conclusion,
			DerivedAxiomExpression<ElkSubClassOfAxiom> firstExPremise,
			DerivedExpression secondExPremise,
			DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> propSubsumption,
			DerivedExpression chainSubsumption) {
		super(conclusion);
		firstExistentialPremise_ = firstExPremise;
		secondExistentialPremise_ = secondExPremise;
		firstPropertySubsumptionPremise_ = propSubsumption;
		secondPropertySubsumptionPremise_ = chainSubsumption;
	}
	
	@Override
	public Collection<DerivedExpression> getRawPremises() {
		return Arrays.asList(firstExistentialPremise_, secondExistentialPremise_, firstPropertySubsumptionPremise_, secondPropertySubsumptionPremise_);
	}
	
	@Override
	public <I, O> O accept(InferenceVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

	@Override
	public InferenceRule getRule() {
		return InferenceRule.R_EXIST_CHAIN_COMPOSITION;
	}
	
	public DerivedAxiomExpression<? extends ElkClassAxiom> getFirstExistentialPremise() {
		return firstExistentialPremise_;
	}
	
	public DerivedExpression getSecondExistentialPremise() {
		return secondExistentialPremise_;
	}
	
	public DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> getFirstPropertyPremise() {
		return firstPropertySubsumptionPremise_;
	}
	
	public DerivedExpression getSecondPropertyPremise() {
		return secondPropertySubsumptionPremise_;
	}
}
