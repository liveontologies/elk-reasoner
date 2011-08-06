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
 * "http://www.w3.org/TR/owl2-syntax/#Disjoint_Object_Properties">Disjoint
 * Object Properties Axiom<a> in the OWL 2 specification.
 * 
 * @author Markus Kroetzsch
 */
public class ElkDisjointObjectPropertiesAxiom extends ElkObjectPropertyAxiom {

	private static final int constructorHash_ = "ElkDisjointObjectPropertiesAxiom"
			.hashCode();

	protected final List<? extends ElkObjectPropertyExpression> disjointObjectPropertyExpressions;

	private ElkDisjointObjectPropertiesAxiom(
			List<? extends ElkObjectPropertyExpression> disjointObjectPropertyExpressions) {
		this.disjointObjectPropertyExpressions = disjointObjectPropertyExpressions;
		this.structuralHashCode = ElkObject.computeCompositeHash(
				constructorHash_, disjointObjectPropertyExpressions);
	}

	public static ElkDisjointObjectPropertiesAxiom create(
			List<? extends ElkObjectPropertyExpression> disjointObjectPropertyExpressions) {
		return (ElkDisjointObjectPropertiesAxiom) factory
				.put(new ElkDisjointObjectPropertiesAxiom(
						disjointObjectPropertyExpressions));
	}

	public static ElkDisjointObjectPropertiesAxiom create(
			ElkObjectPropertyExpression firstObjectPropertyExpression,
			ElkObjectPropertyExpression secondObjectPropertyExpression,
			ElkObjectPropertyExpression... otherObjectPropertyExpressions) {
		List<ElkObjectPropertyExpression> objectPropertyExpressions = new ArrayList<ElkObjectPropertyExpression>(
				2 + otherObjectPropertyExpressions.length);
		objectPropertyExpressions.add(firstObjectPropertyExpression);
		objectPropertyExpressions.add(secondObjectPropertyExpression);
		for (int i = 0; i < otherObjectPropertyExpressions.length; i++)
			objectPropertyExpressions.add(otherObjectPropertyExpressions[i]);
		return (ElkDisjointObjectPropertiesAxiom) factory
				.put(new ElkDisjointObjectPropertiesAxiom(
						objectPropertyExpressions));
	}

	public List<? extends ElkObjectPropertyExpression> getDisjointObjectPropertyExpressions() {
		return disjointObjectPropertyExpressions;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("DisjointObjectProperties(");
		for (ElkObjectPropertyExpression ope : disjointObjectPropertyExpressions) {
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
		} else if (object instanceof ElkDisjointObjectPropertiesAxiom) {
			return disjointObjectPropertyExpressions
					.equals(((ElkDisjointObjectPropertiesAxiom) object).disjointObjectPropertyExpressions);
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
