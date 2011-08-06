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
package org.semanticweb.elk.reasoner.indexing;

import org.semanticweb.elk.util.Triple;

/**
 * Binary role inclusion axiom.
 * 
 * @author Frantisek
 *
 */
public class ComplexRoleInclusion extends 
		Triple<IndexedObjectProperty, IndexedPropertyExpression, IndexedPropertyExpression> {

	protected boolean isSafe;
	
	public ComplexRoleInclusion(IndexedObjectProperty leftSubProperty,
			IndexedPropertyExpression rightSubProperty,
			IndexedPropertyExpression superProperty) {
		super(leftSubProperty, rightSubProperty, superProperty);
	}
	
	public IndexedObjectProperty getLeftSubProperty() {
		return first;
	}
	
	public IndexedPropertyExpression getRightSubProperty() {
		return second;
	}
	
	public IndexedPropertyExpression getSuperProperty() {
		return third;
	}

	public boolean isSafe() {
		return isSafe;
	}

	public void setSafe(boolean isSafe) {
		this.isSafe = isSafe;
	}
}