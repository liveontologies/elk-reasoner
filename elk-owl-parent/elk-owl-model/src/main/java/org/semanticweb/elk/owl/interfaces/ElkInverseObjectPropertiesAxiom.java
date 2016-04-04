/*
 * #%L
 * elk-reasoner
 * 
 * $Id: ElkInverseObjectPropertiesAxiom.java 295 2011-08-10 11:43:29Z mak@aifb.uni-karlsruhe.de $
 * $HeadURL: https://elk-reasoner.googlecode.com/svn/trunk/elk-reasoner/src/main/java/org/semanticweb/elk/syntax/interfaces/ElkInverseObjectPropertiesAxiom.java $
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

import org.semanticweb.elk.owl.visitors.ElkInverseObjectPropertiesAxiomVisitor;

/**
 * Corresponds to an
 * <a href= "http://www.w3.org/TR/owl2-syntax/#Inverse_Object_Properties_2">
 * Inverse Object Properties Axiom<a> in the OWL 2 specification.
 * 
 * @author Markus Kroetzsch
 */
public interface ElkInverseObjectPropertiesAxiom
		extends ElkObjectPropertyAxiom {

	/**
	 * Get the first object property that this axiom refers to.
	 * 
	 * @return the first object property
	 */
	public ElkObjectPropertyExpression getFirstObjectPropertyExpression();

	/**
	 * Get the second object property that this axiom refers to.
	 * 
	 * @return the second object property
	 */
	public ElkObjectPropertyExpression getSecondObjectPropertyExpression();

	/**
	 * Accept an {@link ElkInverseObjectPropertiesAxiomVisitor}.
	 * 
	 * @param visitor
	 *            the visitor that can work with this axiom type
	 * @return the output of the visitor
	 */
	public abstract <O> O accept(
			ElkInverseObjectPropertiesAxiomVisitor<O> visitor);

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory {

		/**
		 * Create an {@link ElkInverseObjectPropertiesAxiom}.
		 * 
		 * @param first
		 *            the first {@link ElkObjectPropertyExpression} that should
		 *            be the inverse of the second one for which the axiom
		 *            should be created
		 * @param second
		 *            the second {@link ElkObjectPropertyExpression} that should
		 *            be the inverse of the first one for which the axiom should
		 *            be created
		 * @return an {@link ElkInverseObjectPropertiesAxiom} corresponding to
		 *         the input
		 */
		public ElkInverseObjectPropertiesAxiom getInverseObjectPropertiesAxiom(
				ElkObjectPropertyExpression first,
				ElkObjectPropertyExpression second);

	}

}
