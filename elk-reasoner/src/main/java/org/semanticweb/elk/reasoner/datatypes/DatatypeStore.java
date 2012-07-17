/**
 * 
 */
package org.semanticweb.elk.reasoner.datatypes;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;

/**
 * Manages all value spaces for a given datatype (several datatypes connected
 * with parent/child relationships) that occur in indexed datatype expressions
 * and answers queries of the form: give all value spaces (or associated
 * datatype expressions) that subsume the given value space.
 * 
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface DatatypeStore<C> {

	/**
	 * 
	 * @param vs
	 *            Value space whose subsumers need to be found
	 * @return An iteration over elements, e.g., indexed datatype expressions,
	 *         associated with value spaces which subsume the given value space
	 */
	public Iterable<C> getSubsumingValueSpaces(ValueSpace vs);
}
