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
package org.semanticweb.elk.syntax.implementation;

import org.semanticweb.elk.syntax.interfaces.ElkIndividual;
import org.semanticweb.elk.syntax.interfaces.ElkObjectHasValue;
import org.semanticweb.elk.syntax.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.syntax.visitors.ElkClassExpressionVisitor;
import org.semanticweb.elk.syntax.visitors.ElkObjectVisitor;
import org.semanticweb.elk.util.HashGenerator;

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

	private static final int constructorHash_ = "ElkObjectHasValue".hashCode();

	/* package-private */ElkObjectHasValueImpl(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkIndividual individual) {
		super(objectPropertyExpression);
		this.individual = individual;
		this.structuralHashCode = HashGenerator.combineListHash(
				constructorHash_,
				objectPropertyExpression.structuralHashCode(),
				individual.structuralHashCode());
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

	public boolean structuralEquals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof ElkObjectHasValue) {
			return objectPropertyExpression.equals(((ElkObjectHasValue) object)
					.getObjectPropertyExpression())
					&& individual.equals(((ElkObjectHasValue) object)
							.getIndividual());
		} else {
			return false;
		}
	}

	public <O> O accept(ElkClassExpressionVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
