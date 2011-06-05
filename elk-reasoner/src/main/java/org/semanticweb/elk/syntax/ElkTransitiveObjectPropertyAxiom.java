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
/**
 * @author Yevgeny Kazakov, Apr 8, 2011
 */
package org.semanticweb.elk.syntax;

import org.semanticweb.elk.util.HashGenerator;

/**
 * Corresponds to a <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Transitive_Object_Properties">Transitive
 * Object Property Axiom<a> in the OWL 2 specification.
 * 
 * @author Yevgeny Kazakov
 * 
 */
public class ElkTransitiveObjectPropertyAxiom extends ElkObjectPropertyAxiom {

	protected final ElkObjectPropertyExpression objectPropertyExpression;

	private static final int constructorHash_ = "ElkTransitiveObjectPropertyAxiom"
			.hashCode();

	protected ElkTransitiveObjectPropertyAxiom(
			ElkObjectPropertyExpression objectPropertyExpression) {
		this.objectPropertyExpression = objectPropertyExpression;
		this.structuralHashCode = HashGenerator
				.combineListHash(constructorHash_, objectPropertyExpression
						.structuralHashCode());
	}

	public static ElkTransitiveObjectPropertyAxiom create(
			ElkObjectPropertyExpression objectPropertyExpression) {
		return (ElkTransitiveObjectPropertyAxiom) factory
				.put(new ElkTransitiveObjectPropertyAxiom(
						objectPropertyExpression));
	}

	public ElkObjectPropertyExpression getObjectPropertyExpression() {
		return objectPropertyExpression;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("TransitiveObjectProperty(");
		result.append(objectPropertyExpression.toString());
		result.append(")");
		return result.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.semanticweb.elk.reasoner.ELKObjectPropertyAxiom#accept(org.semanticweb
	 * .elk.reasoner.ELKObjectPropertyAxiomVisitor)
	 */
	@Override
	public <O> O accept(ElkObjectPropertyAxiomVisitor<O> visitor) {

		return visitor.visit(this);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.semanticweb.elk.reasoner.ElkObject#equals(java.lang.Object)
	 */
	@Override
	public boolean structuralEquals(ElkObject object) {
		if (this == object)
			return true;

		if (object instanceof ElkTransitiveObjectPropertyAxiom)
			return objectPropertyExpression
					.equals(((ElkTransitiveObjectPropertyAxiom) object).objectPropertyExpression);

		return false;
	}

}
