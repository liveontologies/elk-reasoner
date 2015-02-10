/**
 * 
 */
package org.semanticweb.elk.proofs.inferences.classes;
/*
 * #%L
 * ELK Reasoner
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
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDisjointClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.proofs.expressions.derived.DerivedAxiomExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpressionFactory;
import org.semanticweb.elk.proofs.inferences.AbstractInference;
import org.semanticweb.elk.proofs.inferences.InferenceRule;
import org.semanticweb.elk.proofs.inferences.InferenceVisitor;
import org.semanticweb.elk.proofs.utils.InferencePrinter;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class DisjointnessContradiction extends AbstractInference<DerivedAxiomExpression<? extends ElkClassAxiom>> {

	private final DerivedExpression firstPremise_;
	
	private final DerivedExpression secondPremise_;
	
	private final DerivedExpression axiom_;
	
	public DisjointnessContradiction(
			ElkClassExpression sub, 
			ElkClassExpression firstSup, 
			ElkClassExpression secondSup, 
			ElkDisjointClassesAxiom sideCondition, 
			ElkObjectFactory factory, 
			DerivedExpressionFactory exprFactory) {
		super(exprFactory.create(factory.getSubClassOfAxiom(sub, PredefinedElkClass.OWL_NOTHING)));

		firstPremise_ = exprFactory.create(factory.getSubClassOfAxiom(sub, firstSup));
		secondPremise_ = exprFactory.create(factory.getSubClassOfAxiom(sub, secondSup));
		axiom_ = exprFactory.createAsserted(sideCondition);
	}

	@Override
	public Collection<DerivedExpression> getRawPremises() {
		return Arrays.asList(firstPremise_, secondPremise_, axiom_);
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
		return InferenceRule.R_CONTRADITION_FROM_DISJOINTNESS;
	}
}
