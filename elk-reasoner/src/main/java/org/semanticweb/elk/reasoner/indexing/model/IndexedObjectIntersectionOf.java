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
package org.semanticweb.elk.reasoner.indexing.model;

import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;

/**
 * Represents occurrences of an {@link ElkObjectIntersectionOf} in an ontology.
 * An {@link IndexedObjectIntersectionOf} is binary (has exactly two conjuncts).
 * In order to convert an (n-ary) {@link ElkObjectIntersectionOf} to an
 * {@link IndexedObjectIntersectionOf}, conjunctions must be binarized.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public interface IndexedObjectIntersectionOf extends IndexedClassExpression {

	/**
	 * @return the first conjunction of this {@link IndexedObjectIntersectionOf}
	 */
	IndexedClassExpression getFirstConjunct();

	/**
	 * @return the second conjunction of this
	 *         {@link IndexedObjectIntersectionOf}
	 */
	IndexedClassExpression getSecondConjunct();

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> {
		
		O visit(IndexedObjectIntersectionOf element);
		
	}
	
}
