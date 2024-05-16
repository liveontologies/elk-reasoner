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
 * @author Markus Kroetzsch, Aug 10, 2011
 */
package org.semanticweb.elk.owl.interfaces;

import org.semanticweb.elk.owl.visitors.ElkObjectPropertyAssertionAxiomVisitor;

/**
 * Corresponds to an <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Positive_Object_Property_Assertions" >
 * positive object property assertion axiom</a> in the OWL 2 specification.
 * 
 * @author Markus Kroetzsch
 * @author "Yevgeny Kazakov"
 */
public interface ElkObjectPropertyAssertionAxiom extends
		ElkPropertyAssertionAxiom<ElkObjectPropertyExpression, ElkIndividual, ElkIndividual> {

	/**
	 * Accept an {@link ElkObjectPropertyAssertionAxiomVisitor}.
	 * 
	 * @param visitor
	 *            the visitor that can work with this axiom type
	 * @param <O>
	 *            the type of the output of the visitor
	 * @return the output of the visitor
	 */
	public abstract <O> O accept(
			ElkObjectPropertyAssertionAxiomVisitor<O> visitor);

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory {

		/**
		 * Create an {@link ElkObjectPropertyAssertionAxiom}.
		 * 
		 * @param property
		 *            the {@link ElkObjectPropertyExpression} for which the
		 *            axiom should be created
		 * @param subject
		 *            the first {@link ElkIndividual} for which the axiom should
		 *            be created
		 * @param object
		 *            the second {@link ElkIndividual} for which the axiom
		 *            should be created
		 * @return an {@link ElkObjectPropertyAssertionAxiom} corresponding to
		 *         the input
		 */
		public ElkObjectPropertyAssertionAxiom getObjectPropertyAssertionAxiom(
				ElkObjectPropertyExpression property,
				ElkIndividual subject, ElkIndividual object);
	}
}
