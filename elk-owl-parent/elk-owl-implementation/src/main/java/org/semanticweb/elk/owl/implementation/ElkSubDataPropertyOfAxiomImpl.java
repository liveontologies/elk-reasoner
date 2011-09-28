/*
 * #%L
 * elk-reasoner
 * 
 * $Id: ElkSubDataPropertyOfAxiom.java 273 2011-08-04 15:33:14Z mak@aifb.uni-karlsruhe.de $
 * $HeadURL: https://elk-reasoner.googlecode.com/svn/trunk/elk-reasoner/src/main/java/org/semanticweb/elk/syntax/ElkSubDataPropertyOfAxiom.java $
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
package org.semanticweb.elk.owl.implementation;

import org.semanticweb.elk.owl.interfaces.ElkDataPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubDataPropertyOfAxiom;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkDataPropertyAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * ELK implementation of ElkSubDataPropertyOfAxiom.
 *
 * @author Markus Kroetzsch 
 */
public class ElkSubDataPropertyOfAxiomImpl extends ElkObjectImpl implements
		ElkSubDataPropertyOfAxiom {

	protected final ElkDataPropertyExpression subDataPropertyExpression;
	protected final ElkDataPropertyExpression superDataPropertyExpression;

	private static final int constructorHash_ = "ElkSubDataPropertyOfAxiom"
			.hashCode();

	/* package-private */ElkSubDataPropertyOfAxiomImpl(
			ElkDataPropertyExpression subDataPropertyExpression,
			ElkDataPropertyExpression superDataPropertyExpression) {
		this.subDataPropertyExpression = subDataPropertyExpression;
		this.superDataPropertyExpression = superDataPropertyExpression;
		this.structuralHashCode = HashGenerator.combineListHash(
				constructorHash_,
				subDataPropertyExpression.structuralHashCode(),
				superDataPropertyExpression.structuralHashCode());
	}

	public ElkDataPropertyExpression getSubDataPropertyExpression() {
		return subDataPropertyExpression;
	}

	public ElkDataPropertyExpression getSuperDataPropertyExpression() {
		return superDataPropertyExpression;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("SubDataPropertyOf(");
		result.append(subDataPropertyExpression.toString());
		result.append(" ");
		result.append(superDataPropertyExpression.toString());
		result.append(")");
		return result.toString();
	}

	public boolean structuralEquals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof ElkSubDataPropertyOfAxiom) {
			return subDataPropertyExpression
					.equals(((ElkSubDataPropertyOfAxiom) object)
							.getSubDataPropertyExpression())
					&& superDataPropertyExpression
							.equals(((ElkSubDataPropertyOfAxiom) object)
									.getSuperDataPropertyExpression());
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
