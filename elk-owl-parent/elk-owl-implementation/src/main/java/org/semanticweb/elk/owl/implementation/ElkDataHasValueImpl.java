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
package org.semanticweb.elk.owl.implementation;

import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.owl.visitors.ElkClassExpressionVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * ELK implementation of ElkDataHasValue. 
 * 
 * @author Markus Kroetzsch
 * 
 */
public class ElkDataHasValueImpl extends ElkDataPropertyExpressionObject
		implements ElkDataHasValue {

	protected final ElkLiteral literal;

	/* package-private */ElkDataHasValueImpl(
			ElkDataPropertyExpression dataPropertyExpression,
			ElkLiteral literal) {
		super(dataPropertyExpression);
		this.literal = literal;
	}

	public ElkLiteral getLiteral() {
		return literal;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("DataHasValue(");
		result.append(dataPropertyExpression.toString());
		result.append(" ");
		result.append(literal.toString());
		result.append(")");
		return result.toString();
	}

	public <O> O accept(ElkClassExpressionVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
