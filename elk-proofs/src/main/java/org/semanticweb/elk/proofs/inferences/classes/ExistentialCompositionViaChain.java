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

import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAxiom;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
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

	private final ElkObjectPropertyAxiom chainAxiom_;
	
	private final DerivedExpression conclusion_;

	public ExistentialCompositionViaChain(
			DerivedExpression conclusion,
			DerivedExpression firstExPremise,
			DerivedExpression secondExPremise,
			DerivedExpression propSubsumption,
			DerivedExpression chainSubsumption,
			ElkObjectPropertyAxiom chainAxiom) {
		conclusion_ = conclusion;
		firstExistentialPremise_ = firstExPremise;
		secondExistentialPremise_ = secondExPremise;
		firstPropertySubsumptionPremise_ = propSubsumption;
		secondPropertySubsumptionPremise_ = chainSubsumption;
		chainAxiom_ = chainAxiom;
	}	
	
	@Override
	public Collection<? extends DerivedExpression> getPremises() {
		return Arrays.asList(firstExistentialPremise_, secondExistentialPremise_, firstPropertySubsumptionPremise_, secondPropertySubsumptionPremise_);
	}
	
	@Override
	public AxiomPresenceCondition<ElkObjectPropertyAxiom> getSideCondition() {
		return chainAxiom_ == null ? null : new AxiomPresenceCondition<ElkObjectPropertyAxiom>(chainAxiom_);
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
