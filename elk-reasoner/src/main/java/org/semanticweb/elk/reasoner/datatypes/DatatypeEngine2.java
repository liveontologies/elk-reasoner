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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDatatypeExpression;

/**
 * The central entry-point to datatype reasoning. Answers queries of the form:
 * give me all (negatively occurring) indexed datatype expressions which subsume
 * the given datatype expression.
 * 
 * Internally manages a set of datatype stores for each data property and (comparable)
 * datatypes which occur in datatype expressions with that property.
 * 
 * It is not specified but advisable that this class does not use a single,
 * global registry of datatype stores but maintains them locally, each next to
 * the indexed data property (for performance reasons). In other words, each
 * property has a set of root datatypes (often just one) each of which is mapped
 * to its datatype store.
 * 
 * It seems possible to create all value spaces during loading/indexing and fill
 * up all datatype stores during a dedicate reasoning stage.
 * 
 * This class should not have static members.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface DatatypeEngine2 {

	public Iterable<IndexedDatatypeExpression> getSubsumingNegExistentials(
			IndexedDatatypeExpression datatypeExpression);
}
