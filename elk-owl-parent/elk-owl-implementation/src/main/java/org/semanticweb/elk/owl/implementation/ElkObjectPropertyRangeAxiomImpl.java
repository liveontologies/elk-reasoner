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

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyRangeAxiom;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectPropertyAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * ELK implementation of ElkObjectPropertyRangeAxiom.
 * 
 * @author Markus Kroetzsch
 */
public class ElkObjectPropertyRangeAxiomImpl extends
		ElkObjectPropertyExpressionClassExpressionObject implements
		ElkObjectPropertyRangeAxiom {

	private static final int constructorHash_ = "ElkObjectPropertyRangeAxiom"
			.hashCode();

	ElkObjectPropertyRangeAxiomImpl(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkClassExpression classExpression) {
		super(objectPropertyExpression, classExpression);
		this.structuralHashCode = HashGenerator.combineListHash(
				constructorHash_,
				objectPropertyExpression.structuralHashCode(),
				classExpression.structuralHashCode());
	}

	@Override
	public String toString() {
		return buildFssString("ObjectPropertyRange");
	}

	public boolean structuralEquals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof ElkObjectPropertyRangeAxiom) {
			return objectPropertyExpression
					.equals(((ElkObjectPropertyRangeAxiom) object)
							.getObjectPropertyExpression())
					&& classExpression
							.equals(((ElkObjectPropertyRangeAxiom) object)
									.getClassExpression());
		} else {
			return false;
		}
	}

	public <O> O accept(ElkObjectPropertyAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public <O> O accept(ElkAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
