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

import org.semanticweb.elk.syntax.ElkClassExpressionVisitor;
import org.semanticweb.elk.syntax.ElkObjectVisitor;
import org.semanticweb.elk.syntax.interfaces.ElkClassExpression;
import org.semanticweb.elk.syntax.interfaces.ElkObjectMinCardinality;
import org.semanticweb.elk.syntax.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.util.HashGenerator;

/**
 * ELK implementation of ElkObjectMinCardinality.
 * 
 * @author Markus Kroetzsch
 */
public class ElkObjectMinCardinalityImpl extends
		ElkObjectCardinalityRestriction implements ElkObjectMinCardinality {

	private static final int constructorHash_ = "ElkObjectMinCardinality"
			.hashCode();

	ElkObjectMinCardinalityImpl(
			ElkObjectPropertyExpression objectPropertyExpression,
			int cardinality, ElkClassExpression classExpression) {
		super(objectPropertyExpression, cardinality, classExpression);
		this.structuralHashCode = HashGenerator.combineListHash(
				constructorHash_,
				objectPropertyExpression.structuralHashCode(),
				classExpression.structuralHashCode(), cardinality);
	}

	@Override
	public String toString() {
		return buildFssString("ObjectMinCardinality");
	}

	public boolean structuralEquals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof ElkObjectMinCardinality) {
			return (cardinality == ((ElkObjectMinCardinality) object)
					.getCardinality())
					&& objectPropertyExpression
							.equals(((ElkObjectMinCardinality) object)
									.getObjectPropertyExpression())
					&& classExpression
							.equals(((ElkObjectMinCardinality) object)
									.getClassExpression());
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
