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

import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;

/**
 * Represents a complex {@link ElkSubObjectPropertyExpression}s. The chain
 * consists of two components: an {@link IndexedObjectProperty} on the left and
 * an {@link IndexedPropertyChain} on the right. This reflects the fact that
 * property inclusions are binarized during index constructions. The auxiliary
 * {@link IndexedBinaryPropertyChain}s may not represent any ElkObject in the
 * ontology.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */

public interface IndexedBinaryPropertyChain extends IndexedPropertyChain {

	/**
	 * @return The left component of this (binary) complex property inclusion
	 *         axiom.
	 */
	public IndexedObjectProperty getLeftProperty();

	/**
	 * @return The right component of this (binary) complex property inclusion
	 *         axiom.
	 */
	public IndexedPropertyChain getRightProperty();

}
