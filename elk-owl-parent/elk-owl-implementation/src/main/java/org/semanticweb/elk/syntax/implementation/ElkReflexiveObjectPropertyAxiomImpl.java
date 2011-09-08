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

import org.semanticweb.elk.syntax.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.syntax.interfaces.ElkReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.syntax.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.syntax.visitors.ElkObjectPropertyAxiomVisitor;
import org.semanticweb.elk.syntax.visitors.ElkObjectVisitor;
import org.semanticweb.elk.util.HashGenerator;

/**
 * ELK implementation of ElkReflexiveObjectPropertyAxiom.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class ElkReflexiveObjectPropertyAxiomImpl extends
		ElkObjectPropertyExpressionObject implements
		ElkReflexiveObjectPropertyAxiom {

	private static final int constructorHash_ = "ElkReflexiveObjectPropertyAxiom"
			.hashCode();

	/* package-private */ElkReflexiveObjectPropertyAxiomImpl(
			ElkObjectPropertyExpression objectPropertyExpression) {
		super(objectPropertyExpression);
		this.structuralHashCode = HashGenerator
				.combineListHash(constructorHash_,
						objectPropertyExpression.structuralHashCode());
	}

	@Override
	public String toString() {
		return buildFssString("ReflexiveObjectProperty");
	}

	public boolean structuralEquals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof ElkReflexiveObjectPropertyAxiom) {
			return objectPropertyExpression
					.equals(((ElkReflexiveObjectPropertyAxiomImpl) object).objectPropertyExpression);
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
