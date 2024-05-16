/*
 * #%L
 * ELK OWL Object Interfaces
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
package org.semanticweb.elk.owl.interfaces;

import org.semanticweb.elk.owl.visitors.ElkDataMaxCardinalityUnqualifiedVisitor;

/**
 * Corresponds to an <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Maximum_Cardinality_2">maximum cardinality
 * restriction</a> in the OWL 2 specification in the case the qualified data
 * range is empty.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface ElkDataMaxCardinalityUnqualified extends ElkDataMaxCardinality {

	/**
	 * Accept an {@link ElkDataMaxCardinalityUnqualifiedVisitor}.
	 * 
	 * @param visitor
	 *            the visitor that can work with this object type
	 * @param <O>
	 *            the type of the output of the visitor
	 * @return the output of the visitor
	 */
	public <O> O accept(ElkDataMaxCardinalityUnqualifiedVisitor<O> visitor);
	
	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory {

		/**
		 * Create an {@link ElkDataMaxCardinalityUnqualified}.
		 * 
		 * @param property
		 *            the {@link ElkDataPropertyExpression} for which the object
		 *            should be created
		 * @param cardinality
		 *            the cardinality for which the object should be created
		 * @return an {@link ElkDataMaxCardinalityUnqualified} corresponding to the
		 *         input
		 */
		public ElkDataMaxCardinalityUnqualified getDataMaxCardinalityUnqualified(
				ElkDataPropertyExpression property, int cardinality);
	}

}
