/*
 * #%L
 * elk-reasoner
 * 
 * $Id: ElkFunctionalDataPropertyAxiom.java 68 2011-06-04 21:49:01Z mak@aifb.uni-karlsruhe.de $
 * $HeadURL: https://elk-reasoner.googlecode.com/svn/trunk/elk-reasoner/src/main/java/org/semanticweb/elk/syntax/ElkFunctionalDataPropertyAxiom.java $
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
package org.semanticweb.elk.owl.implementation;

import org.semanticweb.elk.owl.interfaces.ElkFunctionalDataPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyExpression;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkDataPropertyAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * Corresponds to a <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Functional_Data_Properties">Functional
 * Data Property Axiom<a> in the OWL 2 specification.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class ElkFunctionalDataPropertyAxiomImpl extends
		ElkDataPropertyExpressionObject implements
		ElkFunctionalDataPropertyAxiom {

	private static final int constructorHash_ = "ElkFunctionalDataPropertyAxiom"
			.hashCode();

	/* package-private */ElkFunctionalDataPropertyAxiomImpl(
			ElkDataPropertyExpression dataPropertyExpression) {
		super(dataPropertyExpression);
		this.structuralHashCode = HashGenerator
				.combineListHash(constructorHash_,
						dataPropertyExpression.structuralHashCode());
	}

	@Override
	public String toString() {
		return buildFssString("FunctionalDataProperty");
	}

	public boolean structuralEquals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof ElkFunctionalDataPropertyAxiomImpl) {
			return dataPropertyExpression
					.equals(((ElkFunctionalDataPropertyAxiomImpl) object)
							.getDataPropertyExpression());
		} else {
			return false;
		}
	}

	public <O> O accept(ElkDataPropertyAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public <O> O accept(ElkAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
