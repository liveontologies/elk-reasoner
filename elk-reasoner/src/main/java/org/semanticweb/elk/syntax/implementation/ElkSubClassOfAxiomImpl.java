/*
 * #%L
 * elk-reasoner
 * 
 * $Id: ElkSubClassOfAxiom.java 71 2011-06-05 11:11:41Z mak@aifb.uni-karlsruhe.de $
 * $HeadURL: https://elk-reasoner.googlecode.com/svn/trunk/elk-reasoner/src/main/java/org/semanticweb/elk/syntax/ElkSubClassOfAxiom.java $
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
package org.semanticweb.elk.syntax.implementation;

import org.semanticweb.elk.syntax.ElkAxiomVisitor;
import org.semanticweb.elk.syntax.ElkClassAxiomVisitor;
import org.semanticweb.elk.syntax.ElkObjectVisitor;
import org.semanticweb.elk.syntax.interfaces.ElkClassExpression;
import org.semanticweb.elk.syntax.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.util.HashGenerator;

/**
 * Corresponds to a <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Subclass_Axioms">Subclass Axiom<a> in the
 * OWL 2 specification.
 * 
 * @author Yevgeny Kazakov
 * 
 */
public class ElkSubClassOfAxiomImpl extends ElkObjectImpl implements
		ElkSubClassOfAxiom {

	protected final ElkClassExpression subClassExpression,
			superClassExpression;

	private static final int constructorHash_ = "ElkSubClassOfAxiom".hashCode();

	/* package-private */ElkSubClassOfAxiomImpl(
			ElkClassExpression subClassExpression,
			ElkClassExpression superClassExpression) {
		this.subClassExpression = subClassExpression;
		this.superClassExpression = superClassExpression;
		this.structuralHashCode = HashGenerator.combineListHash(
				constructorHash_, subClassExpression.structuralHashCode(),
				superClassExpression.structuralHashCode());
	}

	public ElkClassExpression getSubClassExpression() {
		return subClassExpression;
	}

	public ElkClassExpression getSuperClassExpression() {
		return superClassExpression;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("SubClassOf(");
		result.append(subClassExpression.toString());
		result.append(" ");
		result.append(superClassExpression.toString());
		result.append(")");
		return result.toString();
	}

	public boolean structuralEquals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof ElkSubClassOfAxiom) {
			return subClassExpression.equals(((ElkSubClassOfAxiom) object)
					.getSubClassExpression())
					&& superClassExpression
							.equals(((ElkSubClassOfAxiom) object)
									.getSuperClassExpression());
		} else {
			return false;
		}
	}

	public <O> O accept(ElkClassAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public <O> O accept(ElkAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return visitor.visit(this);
	}
}
