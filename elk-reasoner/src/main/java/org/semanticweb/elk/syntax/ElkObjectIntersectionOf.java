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
/**
 * @author Yevgeny Kazakov, Apr 8, 2011
 */
package org.semanticweb.elk.syntax;

import java.util.List;

/**
 * Corresponds to an <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Intersection_of_Class_Expressions"
 * >Intersection of Class Expressions<a> in the OWL 2 specification.
 * 
 * @author Yevgeny Kazakov
 * 
 */
public class ElkObjectIntersectionOf extends ElkClassExpression {
	
	protected final List<? extends ElkClassExpression> classExpressions;
	
	private ElkObjectIntersectionOf(
			List<? extends ElkClassExpression> classExpressions) {
		this.classExpressions = classExpressions;
	}
	
	public static ElkObjectIntersectionOf create(
			List<? extends ElkClassExpression> classExpressions) {
		return (ElkObjectIntersectionOf) factory.put(
				new ElkObjectIntersectionOf(classExpressions));	
	}
	
	public List<? extends ElkClassExpression> getClassExpressions() {
		return classExpressions;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("ObjectIntersectionOf(");
		for (ElkClassExpression ce : classExpressions) {
			result.append(ce.toString());
			result.append(" ");
		}
		result.setCharAt(result.length()-1, ')');
		return result.toString();
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.semanticweb.elk.reasoner.ElkObject#structuralHhashCode()
	 */
	@Override
	public int structuralHashCode() {
		return computeCompositeHash(constructorHash_, classExpressions);
	}
	
	private static final int constructorHash_ = "ElkObjectIntersectionOf".hashCode();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.semanticweb.elk.reasoner.ElkObject#structuralEquals(java.lang.Object)
	 */
	@Override
	public boolean structuralEquals(ElkObject object) {
		if (this == object)
			return true;
		
		if (object instanceof ElkObjectIntersectionOf)
			return classExpressions.equals(
					((ElkObjectIntersectionOf) object).classExpressions);
		
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.semanticweb.elk.reasoner.ELKClassExpression#accept(org.semanticweb
	 * .elk.reasoner.ELKClassExpressionVisitor)
	 */
	@Override
	public <O> O accept(ElkClassExpressionVisitor<O> visitor) {
		return visitor.visit(this);
	}
	
}
