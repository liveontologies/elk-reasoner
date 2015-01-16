/**
 * 
 */
package org.semanticweb.elk.proofs.inferences.properties;
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

import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAxiom;
import org.semanticweb.elk.proofs.expressions.LemmaExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedAxiomExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.proofs.inferences.InferenceRule;
import org.semanticweb.elk.proofs.inferences.InferenceVisitor;
import org.semanticweb.elk.proofs.inferences.PropertyInferenceVisitor;
import org.semanticweb.elk.proofs.utils.InferencePrinter;

/**
 * TODO
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ChainSubsumption extends AbstractPropertyInference {

	private final DerivedExpression firstPremise_;

	private final DerivedExpression secondPremise_;
	// conclusion is representable in OWL
	public ChainSubsumption(
			DerivedAxiomExpression<? extends ElkObjectPropertyAxiom> conclusion,
			DerivedExpression first, 
			DerivedExpression second) {
		super(conclusion);
		
		firstPremise_ = first;
		secondPremise_ = second;
	}
	// conclusion is not representable in OWL, i.e. is a lemma
	public ChainSubsumption(
			LemmaExpression conclusion,
			DerivedExpression first, 
			DerivedExpression second) {
		super(conclusion);
		
		firstPremise_ = first;
		secondPremise_ = second;
	}

	@Override
	public Collection<DerivedExpression> getRawPremises() {
		return Arrays.asList(firstPremise_, secondPremise_);
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
		return InferenceRule.R_CHAIN_SUBSUMPTION;
	}
	
	@Override
	public <I, O> O accept(PropertyInferenceVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}
}
