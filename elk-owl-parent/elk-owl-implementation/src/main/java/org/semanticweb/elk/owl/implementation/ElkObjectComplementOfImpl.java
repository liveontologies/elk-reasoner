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
import org.semanticweb.elk.owl.interfaces.ElkObjectComplementOf;
import org.semanticweb.elk.owl.visitors.ElkClassExpressionVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * ELK implementation of ElkObjectComplementOf.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class ElkObjectComplementOfImpl extends ElkObjectImpl implements
		ElkObjectComplementOf {

	protected final ElkClassExpression classExpression;

	private static final int constructorHash_ = "ElkObjectComplementOf"
			.hashCode();

	/* package-private */ElkObjectComplementOfImpl(
			ElkClassExpression classExpression) {

		this.classExpression = classExpression;
		this.structuralHashCode = HashGenerator.combineListHash(
				constructorHash_, classExpression.structuralHashCode());
	}

	public ElkClassExpression getClassExpression() {
		return classExpression;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("ObjectComplementOf(");
		result.append(classExpression.toString());
		result.append(")");
		return result.toString();
	}

	public boolean structuralEquals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof ElkObjectComplementOf) {
			return classExpression.equals(((ElkObjectComplementOf) object)
					.getClassExpression());
		} else {
			return false;
		}
	}

	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public <O> O accept(ElkClassExpressionVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
