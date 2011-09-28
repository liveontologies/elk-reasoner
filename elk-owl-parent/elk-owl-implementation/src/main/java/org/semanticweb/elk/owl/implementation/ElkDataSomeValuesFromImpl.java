/*
 * #%L
 * elk-reasoner
 * 
 * $Id: ElkDataSomeValuesFrom.java 71 2011-06-05 11:11:41Z mak@aifb.uni-karlsruhe.de $
 * $HeadURL: https://elk-reasoner.googlecode.com/svn/trunk/elk-reasoner/src/main/java/org/semanticweb/elk/syntax/ElkDataSomeValuesFrom.java $
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

import org.semanticweb.elk.owl.interfaces.ElkDataRange;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkDataSomeValuesFrom;
import org.semanticweb.elk.owl.visitors.ElkClassExpressionVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * ELK implementation of ElkDataSomeValuesFrom.
 *
 * @author Markus Kroetzsch
 */
public class ElkDataSomeValuesFromImpl extends
		ElkDataPropertyExpressionDataRangeObject implements
		ElkDataSomeValuesFrom {

	private static final int constructorHash_ = "ElkDataSomeValuesFrom"
			.hashCode();

	/* package-private */ElkDataSomeValuesFromImpl(
			ElkDataPropertyExpression dataPropertyExpression,
			ElkDataRange dataRange) {
		super(dataPropertyExpression, dataRange);
		this.structuralHashCode = HashGenerator.combineListHash(
				constructorHash_,
				dataPropertyExpression.structuralHashCode(),
				dataRange.structuralHashCode());
	}

	@Override
	public String toString() {
		return buildFssString("DataSomeValuesFrom");
	}

	public boolean structuralEquals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof ElkDataSomeValuesFrom) {
			return dataPropertyExpression
					.equals(((ElkDataSomeValuesFrom) object)
							.getDataPropertyExpression())
					&& dataRange
							.equals(((ElkDataSomeValuesFrom) object)
									.getDataRange());
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
