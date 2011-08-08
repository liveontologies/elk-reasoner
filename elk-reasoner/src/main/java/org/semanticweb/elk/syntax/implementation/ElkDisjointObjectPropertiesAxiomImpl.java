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

import java.util.List;

import org.semanticweb.elk.syntax.ElkAxiomVisitor;
import org.semanticweb.elk.syntax.ElkObjectPropertyAxiomVisitor;
import org.semanticweb.elk.syntax.ElkObjectVisitor;
import org.semanticweb.elk.syntax.interfaces.ElkDisjointObjectPropertiesAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkObject;
import org.semanticweb.elk.syntax.interfaces.ElkObjectPropertyExpression;

/**
 * Corresponds to an <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Disjoint_Object_Properties">Disjoint
 * Object Properties Axiom<a> in the OWL 2 specification.
 * 
 * @author Markus Kroetzsch
 */
public class ElkDisjointObjectPropertiesAxiomImpl extends
		ElkObjectPropertyExpressionListObject implements
		ElkDisjointObjectPropertiesAxiom {

	private static final int constructorHash_ = "ElkDisjointObjectPropertiesAxiom"
			.hashCode();

	private ElkDisjointObjectPropertiesAxiomImpl(
			List<? extends ElkObjectPropertyExpression> disjointObjectPropertyExpressions) {
		super(disjointObjectPropertyExpressions);
		this.structuralHashCode = ElkObjectImpl.computeCompositeHash(
				constructorHash_, disjointObjectPropertyExpressions);
	}

	public static ElkDisjointObjectPropertiesAxiomImpl create(
			List<? extends ElkObjectPropertyExpression> disjointObjectPropertyExpressions) {
		return (ElkDisjointObjectPropertiesAxiomImpl) factory
				.put(new ElkDisjointObjectPropertiesAxiomImpl(
						disjointObjectPropertyExpressions));
	}

	public static ElkDisjointObjectPropertiesAxiom create(
			ElkObjectPropertyExpression firstObjectPropertyExpression,
			ElkObjectPropertyExpression secondObjectPropertyExpression,
			ElkObjectPropertyExpression... otherObjectPropertyExpressions) {
		return (ElkDisjointObjectPropertiesAxiom) factory
				.put(new ElkDisjointObjectPropertiesAxiomImpl(varArgsToList(
						firstObjectPropertyExpression,
						secondObjectPropertyExpression,
						otherObjectPropertyExpressions)));
	}

	@Override
	public String toString() {
		return buildFssString("DisjointObjectProperties");
	}

	public boolean structuralEquals(ElkObject object) {
		if (this == object) {
			return true;
		} else if (object instanceof ElkDisjointObjectPropertiesAxiom) {
			return elkObjects
					.equals(((ElkDisjointObjectPropertiesAxiom) object)
							.getObjectPropertyExpressions());
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
