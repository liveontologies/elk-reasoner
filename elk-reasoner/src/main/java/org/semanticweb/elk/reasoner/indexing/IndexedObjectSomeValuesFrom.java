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
 * Represents all occurrences of an ElkObjectSomeValuesFrom in an ontology.
 * 
 * @author Frantisek Simancik
 *
 */
public class IndexedObjectSomeValuesFrom extends IndexedClassExpression {
	
	protected final IndexedObjectProperty relation; 
	
	protected final IndexedClassExpression filler;


	protected IndexedObjectSomeValuesFrom(
			IndexedObjectProperty relation,
			IndexedClassExpression filler) {
		super (new ArrayList<ElkClassExpression> (1));
		this.relation = relation;
		this.filler = filler;
	}
	
	
	/**
	 * @return The indexed object property comprising this ObjectSomeValuesFrom.
	 */
	public IndexedObjectProperty getRelation() {
		return relation;
	}
	
	
	/**
	 * @return The indexed class expression comprising this ObjectSomeValuesFrom. 
	 */
	public IndexedClassExpression getFiller() {
		return filler;
	}


	
	@Override
	public <O> O accept(IndexedClassExpressionVisitor<O> visitor) {
		return visitor.visit(this);
	}
}