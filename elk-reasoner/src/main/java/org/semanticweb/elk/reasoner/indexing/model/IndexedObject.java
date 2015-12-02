package org.semanticweb.elk.reasoner.indexing.model;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkObject;

/**
 * Represents occurrences of an {@link ElkObject} in an ontology.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface IndexedObject {

	/**
	 * @return a string representation of the object; if two indexed objects
	 *         have the same string representation, they must be equal according
	 *         to {@link Object#equals(Object)}
	 */
	String toStringStructural();

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O>
			extends
				IndexedClassExpression.Visitor<O>,
				IndexedPropertyChain.Visitor<O>,
				IndexedAxiom.Visitor<O>,
				IndexedEntity.Visitor<O>,
				IndexedClassExpressionList.Visitor<O>,
				IndexedContextRoot.Visitor<O> {

		// combined interface

	}
	
	<O> O accept(Visitor<O> visitor);

}
