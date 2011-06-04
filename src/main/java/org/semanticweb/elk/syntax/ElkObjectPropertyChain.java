/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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

import java.util.List;


public class ElkObjectPropertyChain extends ElkObject {

	private static final int constructorHash_ = "ElkObjectPropertyChain"
		.hashCode();
	
	protected final List<? extends ElkObjectPropertyExpression> objectPropertyExpressions;

	private ElkObjectPropertyChain(
			List<? extends ElkObjectPropertyExpression> objectPropertyExpressions) {
		this.objectPropertyExpressions = objectPropertyExpressions;
		this.structuralHashCode = ElkObject.computeCompositeHash(constructorHash_,
				objectPropertyExpressions);
	}

	public static ElkObjectPropertyChain create(
			List<? extends ElkObjectPropertyExpression> objectPropertyExpressions) {
		return (ElkObjectPropertyChain) factory.put(new ElkObjectPropertyChain(
				objectPropertyExpressions));
	}

	public List<? extends ElkObjectPropertyExpression> getObjectPropertyExpressions() {
		return objectPropertyExpressions;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("ObjectPropertyChain(");
		for (ElkObjectPropertyExpression ope : objectPropertyExpressions) {
			result.append(ope.toString());
			result.append(" ");
		}
		result.setCharAt(result.length() - 1, ')');
		return result.toString();
	}

	@Override
	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public boolean structuralEquals(ElkObject object) {
		if (this == object)
			return true;

		if (object instanceof ElkObjectPropertyChain)
			return objectPropertyExpressions
					.equals(((ElkObjectPropertyChain) object).objectPropertyExpressions);

		return false;
	}
}
