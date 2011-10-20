/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.owl.implementation;

import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasValue;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.visitors.ElkClassExpressionVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * Corresponds to an <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Individual_Value_Restriction">Individual
 * Value Restriction for Object Properties<a> in the OWL 2 specification.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class ElkObjectHasValueImpl extends ElkObjectPropertyExpressionObject
		implements ElkObjectHasValue {

	protected final ElkIndividual individual;

	/* package-private */ElkObjectHasValueImpl(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkIndividual individual) {
		super(objectPropertyExpression);
		this.individual = individual;
	}

	public ElkIndividual getIndividual() {
		return individual;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("ObjectHasValue(");
		result.append(objectPropertyExpression.toString());
		result.append(" ");
		result.append(individual.toString());
		result.append(")");
		return result.toString();
	}

	public <O> O accept(ElkClassExpressionVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
