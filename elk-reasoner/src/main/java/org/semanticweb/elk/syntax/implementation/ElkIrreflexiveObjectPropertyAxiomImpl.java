/*
 * #%L
 * elk-reasoner
 * 
 * $Id: ElkTransitiveObjectPropertyAxiom.java 71 2011-06-05 11:11:41Z mak@aifb.uni-karlsruhe.de $
 * $HeadURL: https://elk-reasoner.googlecode.com/svn/trunk/elk-reasoner/src/main/java/org/semanticweb/elk/syntax/ElkTransitiveObjectPropertyAxiom.java $
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
package org.semanticweb.elk.syntax.implementation;

import org.semanticweb.elk.syntax.ElkAxiomVisitor;
import org.semanticweb.elk.syntax.ElkObjectPropertyAxiomVisitor;
import org.semanticweb.elk.syntax.ElkObjectVisitor;
import org.semanticweb.elk.syntax.interfaces.ElkIrreflexiveObjectPropertyAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.util.HashGenerator;

/**
 * ELK implementation of ElkIrreflexiveObjectPropertyAxiom.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class ElkIrreflexiveObjectPropertyAxiomImpl extends
		ElkObjectPropertyExpressionObject implements
		ElkIrreflexiveObjectPropertyAxiom {

	private static final int constructorHash_ = "ElkIrreflexiveObjectPropertyAxiom"
			.hashCode();

	ElkIrreflexiveObjectPropertyAxiomImpl(
			ElkObjectPropertyExpression objectPropertyExpression) {
		super(objectPropertyExpression);
		this.structuralHashCode = HashGenerator
				.combineListHash(constructorHash_,
						objectPropertyExpression.structuralHashCode());
	}

	@Override
	public String toString() {
		return buildFssString("IrreflexiveObjectProperty");
	}

	public boolean structuralEquals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof ElkIrreflexiveObjectPropertyAxiom) {
			return objectPropertyExpression
					.equals(((ElkIrreflexiveObjectPropertyAxiom) object)
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
