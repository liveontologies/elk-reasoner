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
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import java.util.Collections;

import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.proofs.expressions.derived.DerivedAxiomExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.proofs.inferences.AbstractInference;
import org.semanticweb.elk.proofs.inferences.InferenceRule;
import org.semanticweb.elk.proofs.inferences.InferenceVisitor;

/**
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
public class UnsatisfiabilityInference extends AbstractInference<DerivedExpression> {

	private final DerivedAxiomExpression<ElkSubClassOfAxiom> unsatisfiabilityPremise_;
	
	public UnsatisfiabilityInference(
			DerivedExpression conclusion,
			DerivedAxiomExpression<ElkSubClassOfAxiom> inconsistencyPremise) {
		super(conclusion);
		unsatisfiabilityPremise_ = inconsistencyPremise;
	}

	@Override
	public InferenceRule getRule() {
		return InferenceRule.R_UNSATISFIABILITY;
	}

	@Override
	public <I, O> O accept(InferenceVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

	@Override
	protected Iterable<? extends DerivedExpression> getRawPremises() {
		return Collections.singleton(unsatisfiabilityPremise_);
	}

}
