/**
 * 
 */
package org.semanticweb.elk.proofs.transformations.lemmas;
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

import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.proofs.expressions.derived.DerivedAxiomExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.proofs.inferences.InferenceRule;
import org.semanticweb.elk.proofs.inferences.InferenceVisitor;
import org.semanticweb.elk.proofs.inferences.classes.AbstractClassInference;
import org.semanticweb.elk.util.collections.Operations;

/**
 * TODO
 * 
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
public class ReflexivityElimination extends AbstractClassInference<DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom>> {

	private final DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> firstPremise_;
	
	private final Iterable<DerivedAxiomExpression<ElkReflexiveObjectPropertyAxiom>> reflexivePremises_;
	
	public ReflexivityElimination(DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> c, DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> first, Iterable<DerivedAxiomExpression<ElkReflexiveObjectPropertyAxiom>> reflexivePremises) {
		super(c);
		
		firstPremise_ = first;
		reflexivePremises_ = reflexivePremises;
	}
	
	public ReflexivityElimination(DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> c, DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> first, DerivedAxiomExpression<ElkReflexiveObjectPropertyAxiom> reflexivePremise) {
		super(c);
		
		firstPremise_ = first;
		reflexivePremises_ = Collections.singletonList(reflexivePremise);
	}

	@Override
	public InferenceRule getRule() {
		return InferenceRule.R_REFLEXIVITY_ELIMINATION;
	}

	@Override
	public <I, O> O accept(InferenceVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

	@Override
	protected Iterable<DerivedExpression> getRawPremises() {
		return Operations.concat(Collections.<DerivedExpression>singletonList(firstPremise_), reflexivePremises_);
	}

}
