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

import org.semanticweb.elk.owl.visitors.ElkCardinalityRestrictionVisitor;

/**
 * A generic interface on property restrictions with cardinality constraints.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <P>
 */
public interface ElkCardinalityRestriction<P>
		extends ElkPropertyRestriction<P> {

	/**
	 * @return the cardinality that this expression refers to.
	 */
	int getCardinality();

	/**
	 * Accept an {@link ElkCardinalityRestrictionVisitor}.
	 * 
	 * @param visitor
	 *            the visitor that can work with this object type
	 * @return the output of the visitor
	 */
	public <O> O accept(ElkCardinalityRestrictionVisitor<O> visitor);

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory extends ElkCardinalityRestrictionQualified.Factory,
			ElkDataExactCardinality.Factory, ElkDataMaxCardinality.Factory,
			ElkDataMinCardinality.Factory, ElkObjectExactCardinality.Factory,
			ElkObjectMaxCardinality.Factory, ElkObjectMinCardinality.Factory {

		// combined interface

	}

}
