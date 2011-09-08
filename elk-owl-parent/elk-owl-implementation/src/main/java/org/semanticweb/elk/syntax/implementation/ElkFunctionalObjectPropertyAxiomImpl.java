/*
 * #%L
 * elk-reasoner
 * 
 * $Id: ElkFunctionalObjectPropertyAxiom.java 68 2011-06-04 21:49:01Z mak@aifb.uni-karlsruhe.de $
 * $HeadURL: https://elk-reasoner.googlecode.com/svn/trunk/elk-reasoner/src/main/java/org/semanticweb/elk/syntax/ElkFunctionalObjectPropertyAxiom.java $
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
 * @author Markus Kroetzsch, Aug 8, 2011
 */
package org.semanticweb.elk.syntax.implementation;

import org.semanticweb.elk.syntax.interfaces.ElkFunctionalObjectPropertyAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.syntax.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.syntax.visitors.ElkObjectPropertyAxiomVisitor;
import org.semanticweb.elk.syntax.visitors.ElkObjectVisitor;
import org.semanticweb.elk.util.HashGenerator;

/**
 * Corresponds to a <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Functional_Object_Properties">Functional
 * Object Property Axiom<a> in the OWL 2 specification.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class ElkFunctionalObjectPropertyAxiomImpl extends
		ElkObjectPropertyExpressionObject implements
		ElkFunctionalObjectPropertyAxiom {

	private static final int constructorHash_ = "ElkFunctionalObjectPropertyAxiom"
			.hashCode();

	/* package-private */ElkFunctionalObjectPropertyAxiomImpl(
			ElkObjectPropertyExpression objectPropertyExpression) {
		super(objectPropertyExpression);
		this.structuralHashCode = HashGenerator
				.combineListHash(constructorHash_,
						objectPropertyExpression.structuralHashCode());
	}

	@Override
	public String toString() {
		return buildFssString("FunctionalObjectProperty");
	}

	public boolean structuralEquals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof ElkFunctionalObjectPropertyAxiomImpl) {
			return objectPropertyExpression
					.equals(((ElkFunctionalObjectPropertyAxiomImpl) object)
							.getObjectPropertyExpression());
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
