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
import org.semanticweb.elk.proofs.inferences.AbstractInference;
import org.semanticweb.elk.proofs.inferences.InferenceRule;
import org.semanticweb.elk.proofs.inferences.InferenceVisitor;

/**
 * The existential inference based on role composition which derives a lemma expression. 
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ExistentialLemmaChainComposition extends AbstractInference<LemmaExpression<ElkSubClassOfLemma>> {

	private final AxiomExpression<ElkSubClassOfAxiom> firstExistentialPremise_;

	private final Expression secondExistentialPremise_;

	private final AxiomExpression<ElkSubObjectPropertyOfAxiom> firstPropertySubsumptionPremise_;

	private final Expression secondPropertySubsumptionPremise_;
	
	public ExistentialLemmaChainComposition(
			LemmaExpression<ElkSubClassOfLemma> conclusion,
			AxiomExpression<ElkSubClassOfAxiom> firstExPremise,
			Expression secondExPremise,
			AxiomExpression<ElkSubObjectPropertyOfAxiom> propSubsumption,
			Expression chainSubsumption) {
		super(conclusion);
		firstExistentialPremise_ = firstExPremise;
		secondExistentialPremise_ = secondExPremise;
		firstPropertySubsumptionPremise_ = propSubsumption;
		secondPropertySubsumptionPremise_ = chainSubsumption;
	}
	
	@Override
	public Collection<Expression> getRawPremises() {
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
	
	public AxiomExpression<ElkSubClassOfAxiom> getFirstExistentialPremise() {
		return firstExistentialPremise_;
	}
	
	public Expression getSecondExistentialPremise() {
		return secondExistentialPremise_;
	}
	
	public AxiomExpression<ElkSubObjectPropertyOfAxiom> getFirstPropertyPremise() {
		return firstPropertySubsumptionPremise_;
	}
	
	public Expression getSecondPropertyPremise() {
		return secondPropertySubsumptionPremise_;
	}
}
