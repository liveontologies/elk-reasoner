/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
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

import java.util.List;

import org.semanticweb.elk.syntax.interfaces.ElkObjectPropertyChain;
import org.semanticweb.elk.syntax.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.syntax.visitors.ElkObjectVisitor;
import org.semanticweb.elk.syntax.visitors.ElkSubObjectPropertyExpressionVisitor;

public class ElkObjectPropertyChainImpl extends
		ElkObjectPropertyExpressionListObject implements ElkObjectPropertyChain {

	private static final int constructorHash_ = "ElkObjectPropertyChain"
			.hashCode();

	/* package-private */ElkObjectPropertyChainImpl(
			List<? extends ElkObjectPropertyExpression> objectPropertyExpressions) {
		super(objectPropertyExpressions);
		this.structuralHashCode = ElkObjectImpl.computeCompositeHash(
				constructorHash_, objectPropertyExpressions);
	}

	@Override
	public String toString() {
		return buildFssString("ObjectPropertyChain");
	}

	public boolean structuralEquals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof ElkObjectPropertyChain) {
			return elkObjects.equals(((ElkObjectPropertyChain) object)
					.getObjectPropertyExpressions());
		} else {
			return false;
		}
	}

	public <O> O accept(ElkSubObjectPropertyExpressionVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return visitor.visit(this);
	}
}
