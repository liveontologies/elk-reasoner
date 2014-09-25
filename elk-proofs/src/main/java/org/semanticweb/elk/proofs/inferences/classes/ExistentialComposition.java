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
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.proofs.expressions.AxiomExpression;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.inferences.InferenceVisitor;
import org.semanticweb.elk.proofs.utils.InferencePrinter;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ExistentialComposition extends AbstractClassInference {

	private final Expression existentialPremise_;

	private final Expression subsumerPremise_;

	private final Expression propertyPremise_;

	public ExistentialComposition(
			ElkClassExpression sub, 
			ElkClassExpression sup,
			ElkSubClassOfAxiom exPremise, 
			ElkSubClassOfAxiom subPremise,
			ElkSubObjectPropertyOfAxiom propPremise,
			ElkObjectFactory factory) {
		super(factory.getSubClassOfAxiom(sub, sup));

		existentialPremise_ = new AxiomExpression(exPremise);
		subsumerPremise_ = new AxiomExpression(subPremise);
		propertyPremise_ = new AxiomExpression(propPremise);
	}
	
	@Override
	public <I, O> O accept(InferenceVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

	@Override
	public Collection<? extends Expression> getPremises() {
		return Arrays.asList(existentialPremise_, subsumerPremise_,
				propertyPremise_);
	}

	@Override
	public String toString() {
		return InferencePrinter.print(this);
	}
}
