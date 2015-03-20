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
import org.semanticweb.elk.proofs.expressions.derived.DerivedAxiomExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.proofs.inferences.AbstractInference;
import org.semanticweb.elk.proofs.inferences.InferenceRule;
import org.semanticweb.elk.proofs.utils.InferencePrinter;

/**
 * 
 * TODO
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public abstract class AbstractSubPropertyChainInference<D extends DerivedExpression> extends AbstractInference<D> {
	// the first premise is always an asserted axiom
	private final DerivedAxiomExpression<? extends ElkObjectPropertyAxiom> firstPremise_;

	private final DerivedExpression secondPremise_;

	public AbstractSubPropertyChainInference(D conclusion, DerivedAxiomExpression<? extends ElkObjectPropertyAxiom> first, DerivedExpression second) {
		super(conclusion);
		
		firstPremise_ = first;
		secondPremise_ = second;
	}
	
	public DerivedAxiomExpression<? extends ElkObjectPropertyAxiom> getFirstPremise() {
		return firstPremise_;
	}
	
	public DerivedExpression getSecondPremise() {
		return secondPremise_;
	}

	@Override
	public Collection<DerivedExpression> getRawPremises() {
		return Arrays.asList(firstPremise_, secondPremise_);
	}

	@Override
	public String toString() {
		return InferencePrinter.print(this);
	}
	
	@Override
	public InferenceRule getRule() {
		return InferenceRule.R_CHAIN_SUBSUMPTION;
	}
	
}
