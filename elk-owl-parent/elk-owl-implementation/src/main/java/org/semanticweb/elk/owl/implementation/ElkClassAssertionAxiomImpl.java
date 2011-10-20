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

import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.visitors.ElkAssertionAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * ELK implementation of ElkClassAssertionAxiom.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class ElkClassAssertionAxiomImpl extends ElkObjectImpl implements
		ElkClassAssertionAxiom {

	protected final ElkIndividual individual;
	protected final ElkClassExpression classExpression;

	/* package-private */ElkClassAssertionAxiomImpl(
			ElkClassExpression classExpression, ElkIndividual individual) {

		this.individual = individual;
		this.classExpression = classExpression;
	}

	public ElkClassExpression getClassExpression() {
		return classExpression;
	}

	public ElkIndividual getIndividual() {
		return individual;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("ClassAssertion(");
		result.append(classExpression.toString());
		result.append(" ");
		result.append(individual.toString());
		result.append(")");
		return result.toString();
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
