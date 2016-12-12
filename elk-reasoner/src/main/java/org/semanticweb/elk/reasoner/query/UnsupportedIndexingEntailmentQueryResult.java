/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.query;

/**
 * An {@link EntailmentQueryResult} of query whose indexing is not supported,
 * e.g., an axiom entailment query that contains
 * {@link org.semanticweb.elk.owl.interfaces.ElkObjectAllValuesFrom
 * ElkObjectAllValuesFrom}.
 * 
 * @author Peter Skocovsky
 */
public interface UnsupportedIndexingEntailmentQueryResult
		extends EntailmentQueryResult {

	<O> O accept(Visitor<O> visitor) throws ElkQueryException;

	public static interface Visitor<O> {
		O visit(UnsupportedIndexingEntailmentQueryResult unsupportedIndexingEntailmentQueryResult);
	}

}
