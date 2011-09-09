/*
 * #%L
 * elk-reasoner
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
package org.semanticweb.elk.reasoner.indexing;

import java.util.ArrayList;

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;


/**
 * Represents all occurrences of an ElkObjectIntersectionOf in an ontology.
 * 
 * @author Frantisek Simancik
 *
 */
public class IndexedObjectIntersectionOf extends IndexedClassExpression {
	/**
	 * There are only two conjuncts. This reflects the fact that conjunctions
	 * are binarized during indexing construction. The conjuncts may not
	 * correspond to any ElkClassExpression in the ontology.
	 */
	protected final IndexedClassExpression firstConjunct, secondConjunct;
	
	protected IndexedObjectIntersectionOf(
			IndexedClassExpression firstConjunct,
			IndexedClassExpression secondConjunct) {
		super (new ArrayList<ElkClassExpression> (1));
		this.firstConjunct = firstConjunct;
		this.secondConjunct = secondConjunct;
	}

	
	
	public IndexedClassExpression getFirstConjunct() {
		return firstConjunct;
	}
	
	public IndexedClassExpression getSecondConjunct() {
		return secondConjunct;
	}


	
	@Override
	public <O> O accept(IndexedClassExpressionVisitor<O> visitor) {
		return visitor.visit(this);
	}

}