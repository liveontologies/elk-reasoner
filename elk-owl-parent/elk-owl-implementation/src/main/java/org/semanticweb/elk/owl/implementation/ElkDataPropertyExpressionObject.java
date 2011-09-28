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

import org.semanticweb.elk.owl.interfaces.ElkDataPropertyExpression;

/**
 * Implementation of ElkObjects that store one DataPropertyExpression.
 * 
 * @author Markus Kroetzsch
 */
public abstract class ElkDataPropertyExpressionObject extends ElkObjectImpl {

	protected final ElkDataPropertyExpression dataPropertyExpression;

	/* package-private */ElkDataPropertyExpressionObject(
			ElkDataPropertyExpression dataPropertyExpression) {
		this.dataPropertyExpression = dataPropertyExpression;
	}

	public ElkDataPropertyExpression getDataPropertyExpression() {
		return dataPropertyExpression;
	}

	public String buildFssString(String operatorName) {
		StringBuilder result = new StringBuilder(operatorName);
		result.append("(");
		result.append(dataPropertyExpression.toString());
		result.append(")");
		return result.toString();
	}

}
