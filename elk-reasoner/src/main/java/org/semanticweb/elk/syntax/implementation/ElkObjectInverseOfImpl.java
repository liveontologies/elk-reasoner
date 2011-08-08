/*
 * #%L
 * elk-reasoner
 * 
 * $Id: ElkObjectInverseOf.java 272 2011-08-04 15:27:09Z mak@aifb.uni-karlsruhe.de $
 * $HeadURL: https://elk-reasoner.googlecode.com/svn/trunk/elk-reasoner/src/main/java/org/semanticweb/elk/syntax/ElkObjectInverseOf.java $
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

import org.semanticweb.elk.syntax.ElkObjectPropertyExpressionVisitor;
import org.semanticweb.elk.syntax.ElkObjectVisitor;
import org.semanticweb.elk.syntax.ElkSubObjectPropertyExpressionVisitor;
import org.semanticweb.elk.syntax.interfaces.ElkObject;
import org.semanticweb.elk.syntax.interfaces.ElkObjectInverseOf;
import org.semanticweb.elk.syntax.interfaces.ElkObjectProperty;
import org.semanticweb.elk.util.HashGenerator;

/**
 * Corresponds to an <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Inverse_Object_Properties">Inverse Object
 * Property<a> in the OWL 2 specification.
 * 
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 */
public class ElkObjectInverseOfImpl extends ElkObjectImpl implements
		ElkObjectInverseOf {

	protected final ElkObjectProperty objectProperty;

	private static final int constructorHash_ = "ElkObjectInverseOf".hashCode();

	private ElkObjectInverseOfImpl(ElkObjectProperty objectProperty) {
		this.objectProperty = objectProperty;
		this.structuralHashCode = HashGenerator.combineListHash(
				constructorHash_, objectProperty.structuralHashCode());
	}

	public static ElkObjectInverseOfImpl create(ElkObjectProperty objectProperty) {
		return (ElkObjectInverseOfImpl) factory.put(new ElkObjectInverseOfImpl(
				objectProperty));
	}

	public ElkObjectProperty getObjectProperty() {
		return objectProperty;
	}

	public boolean structuralEquals(ElkObject object) {
		if (this == object) {
			return true;
		} else if (object instanceof ElkObjectInverseOf) {
			return objectProperty.equals(((ElkObjectInverseOf) object)
					.getObjectProperty());
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("ObjectInverseOf(");
		result.append(objectProperty.toString());
		result.append(")");
		return result.toString();
	}

	public <O> O accept(ElkObjectPropertyExpressionVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public <O> O accept(ElkSubObjectPropertyExpressionVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
