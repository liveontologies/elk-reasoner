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
/**
 * @author Markus Kroetzsch, Aug 8, 2011
 */
package org.semanticweb.elk.owl.interfaces;

import java.util.List;

import org.semanticweb.elk.owl.visitors.ElkDataOneOfVisitor;

/**
 * Corresponds to an
 * <a href= "http://www.w3.org/TR/owl2-syntax/#Enumeration_of_Literals">
 * Enumeration of Literals</a> in the OWL 2 specification.
 * 
 * @author Markus Kroetzsch
 */
public interface ElkDataOneOf extends ElkDataRange {

	/**
	 * Get the list of literals that this expression refers to. The order of
	 * literals does not affect the semantics but it is relevant to the syntax
	 * of OWL.
	 * 
	 * @return list of literals
	 */
	public List<? extends ElkLiteral> getLiterals();

	/**
	 * Accept an {@link ElkDataOneOfVisitor}.
	 * 
	 * @param visitor
	 *            the visitor that can work with this object type
	 * @param <O>
	 *            the type of the output of the visitor
	 * @return the output of the visitor
	 */
	public <O> O accept(ElkDataOneOfVisitor<O> visitor);

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory {

		/**
		 * Create an {@link ElkDataOneOf}.
		 * 
		 * @param first
		 *            the {@link ElkLiteral} for which the object should be
		 *            created
		 * @param other
		 *            other {@link ElkLiteral}s for which the object should be
		 *            created
		 * @return an {@link ElkDataOneOf} corresponding to the input
		 */
		public ElkDataOneOf getDataOneOf(ElkLiteral first,
				ElkLiteral... other);

		/**
		 * Create an {@link ElkDataOneOf}.
		 * 
		 * @param members
		 *            the {@link ElkLiteral}s for which the object should be
		 *            created
		 * @return an {@link ElkDataOneOf} corresponding to the input
		 */
		public ElkDataOneOf getDataOneOf(List<? extends ElkLiteral> members);

	}

}
