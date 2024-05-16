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

import org.semanticweb.elk.owl.visitors.ElkCardinalityRestrictionQualifiedVisitor;

/**
 * A generic interface for restrictions on properties with fillers (qualifiers)
 * and cardinality constraints.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <P>
 *            the type of the property of this restriction
 * @param <F>
 *            the type of the filler of this restriction
 */
public interface ElkCardinalityRestrictionQualified<P, F> extends
		ElkCardinalityRestriction<P>, ElkPropertyRestrictionQualified<P, F> {

	/**
	 * Accept an {@link ElkCardinalityRestrictionQualifiedVisitor}.
	 * 
	 * @param visitor
	 *            the visitor that can work with this object type
	 * @param <O>
	 *            the type of the output of the visitor
	 * @return the output of the visitor
	 */
	public <O> O accept(ElkCardinalityRestrictionQualifiedVisitor<O> visitor);

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory extends ElkDataExactCardinalityQualified.Factory,
			ElkDataMaxCardinalityQualified.Factory,
			ElkDataMinCardinalityQualified.Factory,
			ElkObjectExactCardinalityQualified.Factory,
			ElkObjectMaxCardinalityQualified.Factory,
			ElkObjectMinCardinalityQualified.Factory {

		// combined interface

	}

}
