/*
 * #%L
 * elk-reasoner
 * 
 * $Id: ElkObjectSomeValuesFrom.java 295 2011-08-10 11:43:29Z mak@aifb.uni-karlsruhe.de $
 * $HeadURL: https://elk-reasoner.googlecode.com/svn/trunk/elk-reasoner/src/main/java/org/semanticweb/elk/syntax/interfaces/ElkObjectSomeValuesFrom.java $
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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

import org.semanticweb.elk.owl.visitors.ElkObjectExactCardinalityQualifiedVisitor;

/**
 * Corresponds to an
 * <a href= "http://www.w3.org/TR/owl2-syntax/#Exact_Cardinality">exact
 * cardinality restriction</a> in the OWL 2 specification in the case the
 * qualified class expression is specified.
 * 
 * @author Markus Kroetzsch
 */
public interface ElkObjectExactCardinalityQualified
		extends ElkObjectExactCardinality,
		ElkCardinalityRestrictionQualified<ElkObjectPropertyExpression, ElkClassExpression> {

	/**
	 * Accept an {@link ElkObjectExactCardinalityQualifiedVisitor}.
	 * 
	 * @param visitor
	 *            the visitor that can work with this object type
	 * @param <O>
	 *            the type of the output of the visitor
	 * @return the output of the visitor
	 */
	public <O> O accept(ElkObjectExactCardinalityQualifiedVisitor<O> visitor);

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory {

		/**
		 * Create an {@link ElkObjectExactCardinalityQualified}.
		 * 
		 * @param property
		 *            the {@link ElkObjectPropertyExpression} for which the
		 *            object should be created
		 * @param cardinality
		 *            the cardinality for which the object should be created
		 * @param filler
		 *            the {@link ElkClassExpression} for which the object should
		 *            be created
		 * @return an {@link ElkObjectExactCardinalityQualified} corresponding
		 *         to the input
		 */
		public ElkObjectExactCardinalityQualified getObjectExactCardinalityQualified(
				ElkObjectPropertyExpression property,
				int cardinality, ElkClassExpression filler);

	}
}
