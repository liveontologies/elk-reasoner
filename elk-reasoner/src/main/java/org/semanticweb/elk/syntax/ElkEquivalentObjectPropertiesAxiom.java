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

import java.util.ArrayList;
import java.util.List;

/**
 * Corresponds to an <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Equivalent_Object_Properties">Equivalent
 * Object Properties Axiom<a> in the OWL 2 specification.
 * 
 * @author Markus Kroetzsch
 */
public class ElkEquivalentObjectPropertiesAxiom extends ElkObjectPropertyAxiom {

	private static final int constructorHash_ = "ElkEquivalentObjectPropertiesAxiom"
			.hashCode();

	protected final List<? extends ElkObjectPropertyExpression> equivalentObjectPropertyExpressions;

	private ElkEquivalentObjectPropertiesAxiom(
			List<? extends ElkObjectPropertyExpression> equivalentObjectPropertyExpressions) {
		this.equivalentObjectPropertyExpressions = equivalentObjectPropertyExpressions;
		this.structuralHashCode = ElkObject.computeCompositeHash(
				constructorHash_, equivalentObjectPropertyExpressions);
	}

	public static ElkEquivalentObjectPropertiesAxiom create(
			List<? extends ElkObjectPropertyExpression> equivalentObjectPropertyExpressions) {
		return (ElkEquivalentObjectPropertiesAxiom) factory
				.put(new ElkEquivalentObjectPropertiesAxiom(
						equivalentObjectPropertyExpressions));
	}

	public static ElkEquivalentObjectPropertiesAxiom create(
			ElkObjectPropertyExpression firstObjectPropertyExpression,
			ElkObjectPropertyExpression secondObjectPropertyExpression,
			ElkObjectPropertyExpression... otherObjectPropertyExpressions) {
		List<ElkObjectPropertyExpression> objectPropertyExpressions = new ArrayList<ElkObjectPropertyExpression>(
				2 + otherObjectPropertyExpressions.length);
		objectPropertyExpressions.add(firstObjectPropertyExpression);
		objectPropertyExpressions.add(secondObjectPropertyExpression);
		for (int i = 0; i < otherObjectPropertyExpressions.length; i++)
			objectPropertyExpressions.add(otherObjectPropertyExpressions[i]);
		return (ElkEquivalentObjectPropertiesAxiom) factory
				.put(new ElkEquivalentObjectPropertiesAxiom(
						objectPropertyExpressions));
	}

	public List<? extends ElkObjectPropertyExpression> getEquivalentObjectPropertyExpressions() {
		return equivalentObjectPropertyExpressions;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("EquivalentObjectProperties(");
		for (ElkObjectPropertyExpression ope : equivalentObjectPropertyExpressions) {
			result.append(ope.toString());
			result.append(" ");
		}
		result.setCharAt(result.length() - 1, ')');
		return result.toString();
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
		} else if (object instanceof ElkEquivalentObjectPropertiesAxiom) {
			return equivalentObjectPropertyExpressions
					.equals(((ElkEquivalentObjectPropertiesAxiom) object).equivalentObjectPropertyExpressions);
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.semanticweb.elk.reasoner.ELKObjectPropertyAxiom#accept(org.semanticweb
	 * .elk .reasoner.ELKObjectPropertyAxiomVisitor)
	 */
	@Override
	public <O> O accept(ElkObjectPropertyAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
