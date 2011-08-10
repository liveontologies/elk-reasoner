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

import org.semanticweb.elk.syntax.interfaces.ElkClassExpression;
import org.semanticweb.elk.syntax.interfaces.ElkObjectPropertyExpression;

public abstract class ElkObjectCardinalityRestriction extends
		ElkObjectPropertyExpressionClassExpressionObject {

	protected final int cardinality;

	ElkObjectCardinalityRestriction(
			ElkObjectPropertyExpression objectPropertyExpression,
			int cardinality, ElkClassExpression classExpression) {
		super(objectPropertyExpression, classExpression);
		this.cardinality = cardinality;
	}

	public int getCardinality() {
		return cardinality;
	}

	public String buildFssString(String operatorName) {
		StringBuilder result = new StringBuilder(operatorName);
		result.append("(");
		result.append(cardinality);
		result.append(" ");
		result.append(objectPropertyExpression.toString());
		if (classExpression != null) {
			result.append(" ");
			result.append(classExpression.toString());
		}
		result.append(")");
		return result.toString();
	}

}
