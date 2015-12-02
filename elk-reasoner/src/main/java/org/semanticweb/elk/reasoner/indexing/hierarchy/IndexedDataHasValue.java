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
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkDataProperty;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;

/**
 * Represents all occurrences of an {@link ElkDataHasValue} in an ontology.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public interface IndexedDataHasValue extends IndexedClassExpression {

	/**
	 * @return the {@link ElkDataProperty} property of the
	 *         {@link ElkDataHasValue} represented by this
	 *         {@link IndexedDataHasValue}
	 * 
	 * 
	 */
	ElkDataProperty getRelation();

	/**
	 * @return the {@link ElkLiteral} filler of the the {@link ElkDataHasValue}
	 *         represented by this {@link IndexedDataHasValue}
	 * 
	 * @see ElkDataHasValue#getFiller()
	 */
	ElkLiteral getFiller();

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> {
		
		O visit(IndexedDataHasValue element);
		
	}	

}
