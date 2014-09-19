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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.inferences.InferenceVisitor;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ExistentialComposition extends AbstractClassInference {

	private final Expression<?> existentialPremise_;

	private final Expression<?> subsumerPremise_;

	private final Expression<ElkObjectPropertyAxiom> propertyPremise_;

	ExistentialComposition(ElkClassExpression sub, ElkObjectSomeValuesFrom sup,
			Expression<?> exPremise, Expression<?> subPremise,
			Expression<ElkObjectPropertyAxiom> propPremise,
			ElkObjectFactory factory) {
		super(factory.getSubClassOfAxiom(sub, sup));

		existentialPremise_ = exPremise;
		subsumerPremise_ = subPremise;
		propertyPremise_ = propPremise;
	}

	@Override
	public <I, O> O accept(InferenceVisitor<I, O> visitor, I input) {
		// return visitor.visit(this, input);
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<? extends Expression<? extends ElkAxiom>> getPremises() {
		return Arrays.asList(existentialPremise_, subsumerPremise_,
				propertyPremise_);
	}

}
