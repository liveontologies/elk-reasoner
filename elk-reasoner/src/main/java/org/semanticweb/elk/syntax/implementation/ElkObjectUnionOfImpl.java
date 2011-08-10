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

import org.semanticweb.elk.syntax.ElkClassExpressionVisitor;
import org.semanticweb.elk.syntax.ElkObjectVisitor;
import org.semanticweb.elk.syntax.interfaces.ElkClassExpression;
import org.semanticweb.elk.syntax.interfaces.ElkObjectUnionOf;

/**
 * ELK implementation of ElkObjectUnionOf.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class ElkObjectUnionOfImpl extends ElkClassExpressionListObject
		implements ElkObjectUnionOf {

	private static final int constructorHash_ = "ElkObjectUnionOf".hashCode();

	ElkObjectUnionOfImpl(List<? extends ElkClassExpression> classExpressions) {
		super(classExpressions);
		this.structuralHashCode = ElkObjectImpl.computeCompositeHash(
				constructorHash_, classExpressions);
	}

	@Override
	public String toString() {
		return buildFssString("ObjectUnionOf");
	}

	public boolean structuralEquals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof ElkObjectUnionOf) {
			return elkObjects.equals(((ElkObjectUnionOf) object)
					.getClassExpressions());
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
