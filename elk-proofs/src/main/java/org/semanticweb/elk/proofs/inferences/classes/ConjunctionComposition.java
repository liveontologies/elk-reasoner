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

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.expressions.SingleAxiomExpression;
import org.semanticweb.elk.proofs.inferences.InferenceVisitor;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ConjunctionComposition extends
		AbstractClassInference {

	private final Expression<ElkSubClassOfAxiom> firstPremise_;
	
	private final Expression<ElkSubClassOfAxiom> secondPremise_;
	
	ConjunctionComposition(ElkClassExpression sub, ElkClassExpression firstConjunct, ElkClassExpression secondConjunct, ElkObjectFactory factory) {
		super(factory.getSubClassOfAxiom(sub, factory.getObjectIntersectionOf(firstConjunct, secondConjunct)));

		firstPremise_ = new SingleAxiomExpression<ElkSubClassOfAxiom>(factory.getSubClassOfAxiom(sub, firstConjunct));
		secondPremise_ = new SingleAxiomExpression<ElkSubClassOfAxiom>(factory.getSubClassOfAxiom(sub, secondConjunct));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Expression<ElkSubClassOfAxiom>> getPremises() {
		return Arrays.asList(firstPremise_, secondPremise_);
	}

	@Override
	public <I, O> O accept(InferenceVisitor<I, O> visitor, I input) {
		//return visitor.visit(this, input);
		return null;
	}

}
