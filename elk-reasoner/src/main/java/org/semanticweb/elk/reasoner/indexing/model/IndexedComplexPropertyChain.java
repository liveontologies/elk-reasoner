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
package org.semanticweb.elk.reasoner.indexing.model;

import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyChain;

/**
 * Represents occurrences of an {@link ElkObjectPropertyChain} consisting of two
 * or more {@link ElkObjectProperty}.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public interface IndexedComplexPropertyChain extends IndexedPropertyChain {

	/**
	 * @return the {@link IndexedObjectProperty} that represents the first
	 *         {@link ElkObjectProperty} of the {@link ElkObjectPropertyChain}
	 *         represented by this {@link IndexedComplexPropertyChain}.
	 * 
	 * @see ElkObjectPropertyChain#getObjectPropertyExpressions()
	 */
	IndexedObjectProperty getFirstProperty();

	/**
	 * @return {@link IndexedPropertyChain} that represents the sub-chain of the
	 *         {@link ElkObjectPropertyChain} represented by this
	 *         {@link IndexedComplexPropertyChain} without the first element.
	 * 
	 * @see ElkObjectPropertyChain#getObjectPropertyExpressions()
	 */
	IndexedPropertyChain getSuffixChain();
	
	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> {

		O visit(IndexedComplexPropertyChain element);

	}

}
