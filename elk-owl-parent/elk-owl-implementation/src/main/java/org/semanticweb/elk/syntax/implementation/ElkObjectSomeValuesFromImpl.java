/*
 * #%L
 * elk-reasoner
 * 
 * $Id: ElkObjectSomeValuesFrom.java 71 2011-06-05 11:11:41Z mak@aifb.uni-karlsruhe.de $
 * $HeadURL: https://elk-reasoner.googlecode.com/svn/trunk/elk-reasoner/src/main/java/org/semanticweb/elk/syntax/ElkObjectSomeValuesFrom.java $
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

import org.semanticweb.elk.syntax.interfaces.ElkClassExpression;
import org.semanticweb.elk.syntax.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.syntax.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.syntax.visitors.ElkClassExpressionVisitor;
import org.semanticweb.elk.syntax.visitors.ElkObjectVisitor;
import org.semanticweb.elk.util.HashGenerator;

/**
 * Corresponds to an <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Existential_Quantification">Existential
 * Quantification Object Property Restriction<a> in the OWL 2 specification.
 * 
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 */
public class ElkObjectSomeValuesFromImpl extends
		ElkObjectPropertyExpressionClassExpressionObject implements
		ElkObjectSomeValuesFrom {

	private static final int constructorHash_ = "ElkObjectSomeValuesFrom"
			.hashCode();

	/* package-private */ElkObjectSomeValuesFromImpl(
			ElkObjectPropertyExpression objectPropertyExpression,
			ElkClassExpression classExpression) {
		super(objectPropertyExpression, classExpression);
		this.structuralHashCode = HashGenerator.combineListHash(
				constructorHash_,
				objectPropertyExpression.structuralHashCode(),
				classExpression.structuralHashCode());
	}

	@Override
	public String toString() {
		return buildFssString("ObjectSomeValuesFrom");
	}

	public boolean structuralEquals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof ElkObjectSomeValuesFrom) {
			return objectPropertyExpression
					.equals(((ElkObjectSomeValuesFrom) object)
							.getObjectPropertyExpression())
					&& classExpression
							.equals(((ElkObjectSomeValuesFrom) object)
									.getClassExpression());
		} else {
			return false;
		}
	}

	public <O> O accept(ElkClassExpressionVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return visitor.visit(this);
	}
}
