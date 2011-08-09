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
package org.semanticweb.elk.reasoner.saturation;

import org.semanticweb.elk.reasoner.indexing.IndexedClassExpression;

/**
 * Wrapper used for indicating that this IndexedClassExpression has been
 * derived by a composition rule. Decomposition rules need not be applied
 * to such expression.
 *  
 * 
 * @author Frantisek Simancik
 *
 */
public class DecomposedClassExpression implements Queueable {
	protected final IndexedClassExpression classExpression;

	public DecomposedClassExpression(IndexedClassExpression classExpression) {
		assert classExpression != null;
		this.classExpression = classExpression;
	}
	
	public IndexedClassExpression getClassExpression() {
		return classExpression;
	}

	@Override
	public int hashCode() {
		return 19123433 + classExpression.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (obj != null && obj instanceof DecomposedClassExpression)
			return (classExpression == ((DecomposedClassExpression) obj).classExpression);
		
		return false;
	}

	public <O> O accept(QueueableVisitor<O> visitor) {
		return visitor.visit(this);
	}
}
