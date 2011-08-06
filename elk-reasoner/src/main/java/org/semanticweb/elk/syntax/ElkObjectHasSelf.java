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
package org.semanticweb.elk.syntax;

import org.semanticweb.elk.util.HashGenerator;

/**
 * Corresponds to a <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Self-Restriction">Self-Restriction<a> in
 * the OWL 2 specification.
 * 
 * @author Markus Kroetzsch
 */
public class ElkObjectHasSelf extends ElkClassExpression {

	protected final ElkObjectPropertyExpression objectPropertyExpression;

	private static final int constructorHash_ = "ElkObjectHasSelf".hashCode();

	private ElkObjectHasSelf(
			ElkObjectPropertyExpression objectPropertyExpression) {
		this.objectPropertyExpression = objectPropertyExpression;
		this.structuralHashCode = HashGenerator
				.combineListHash(constructorHash_,
						objectPropertyExpression.structuralHashCode());
	}

	public static ElkObjectHasSelf create(
			ElkObjectPropertyExpression objectPropertyExpression) {
		return (ElkObjectHasSelf) factory.put(new ElkObjectHasSelf(
				objectPropertyExpression));
	}

	public ElkObjectPropertyExpression getObjectPropertyExpression() {
		return objectPropertyExpression;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.semanticweb.elk.reasoner.ELKClassExpression#accept(org.
	 * semanticweb.elk.reasoner.ELKClassExpressionVisitor)
	 */
	@Override
	public <O> O accept(ElkClassExpressionVisitor<O> visitor) {
		return visitor.visit(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.semanticweb.elk.reasoner.ElkObject#structuralEquals(java.lang.Object)
	 */
	@Override
	public boolean structuralEquals(ElkObject object) {
		if (this == object) {
			return true;
		} else if (object instanceof ElkObjectHasSelf) {
			return objectPropertyExpression
					.equals(((ElkObjectHasSelf) object).objectPropertyExpression);
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("ObjectHasSelf(");
		result.append(objectPropertyExpression.toString());
		result.append(")");
		return result.toString();
	}

}
