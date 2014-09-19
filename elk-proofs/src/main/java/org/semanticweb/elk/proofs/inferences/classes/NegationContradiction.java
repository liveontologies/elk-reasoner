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
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.inferences.InferenceVisitor;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class NegationContradiction extends
		AbstractClassInference {

	private final Expression<?> positivePremise_;
	
	private final Expression<?> negativePremise_;
	
	NegationContradiction(ElkClassExpression sub, Expression<?> firstPremise, Expression<?> secondPremise, ElkObjectFactory factory) {
		super(factory.getSubClassOfAxiom(sub, PredefinedElkClass.OWL_NOTHING));

		positivePremise_ = firstPremise;
		negativePremise_ = secondPremise;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Expression<?>> getPremises() {
		return Arrays.asList(positivePremise_, negativePremise_);
	}

	@Override
	public <I, O> O accept(InferenceVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

}
