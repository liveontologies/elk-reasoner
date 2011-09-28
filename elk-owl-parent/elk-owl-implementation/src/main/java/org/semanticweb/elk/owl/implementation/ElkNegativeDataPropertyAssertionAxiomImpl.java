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
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.owl.interfaces.ElkNegativeDataPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyExpression;
import org.semanticweb.elk.owl.visitors.ElkAssertionAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * ELK implementation of ElkNegativeDataPropertyAssertion.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class ElkNegativeDataPropertyAssertionAxiomImpl extends
		ElkDataPropertyExpressionObject implements
		ElkNegativeDataPropertyAssertionAxiom {

	protected final ElkIndividual individual;
	protected final ElkLiteral literal;

	private static final int constructorHash_ = "ElkNegativeDataPropertyAssertionAxiom"
			.hashCode();

	/* package-private */ElkNegativeDataPropertyAssertionAxiomImpl(
			ElkDataPropertyExpression dataPropertyExpression,
			ElkIndividual individual, ElkLiteral literal) {
		super(dataPropertyExpression);
		this.individual = individual;
		this.literal = literal;
		this.structuralHashCode = HashGenerator.combineListHash(
				constructorHash_, dataPropertyExpression.structuralHashCode(),
				individual.structuralHashCode(), literal.structuralHashCode());
	}

	public ElkIndividual getIndividual() {
		return individual;
	}

	public ElkLiteral getLiteral() {
		return literal;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder(
				"NegativeDataPropertyAssertion(");
		result.append(dataPropertyExpression.toString());
		result.append(" ");
		result.append(individual.toString());
		result.append(" ");
		result.append(literal.toString());
		result.append(")");
		return result.toString();
	}

	public boolean structuralEquals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof ElkNegativeDataPropertyAssertionAxiom) {
			return dataPropertyExpression
					.equals(((ElkNegativeDataPropertyAssertionAxiom) object)
							.getDataPropertyExpression())
					&& individual
							.equals(((ElkNegativeDataPropertyAssertionAxiom) object)
									.getIndividual())
					&& literal
							.equals(((ElkNegativeDataPropertyAssertionAxiom) object)
									.getLiteral());
		} else {
			return false;
		}
	}

	public <O> O accept(ElkAssertionAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public <O> O accept(ElkAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
