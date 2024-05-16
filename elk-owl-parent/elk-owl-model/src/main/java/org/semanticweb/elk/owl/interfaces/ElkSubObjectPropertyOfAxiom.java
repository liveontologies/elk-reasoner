/*
 * #%L
 * elk-reasoner
 * 
 * $Id: ElkSubObjectPropertyOfAxiom.java 295 2011-08-10 11:43:29Z mak@aifb.uni-karlsruhe.de $
 * $HeadURL: https://elk-reasoner.googlecode.com/svn/trunk/elk-reasoner/src/main/java/org/semanticweb/elk/syntax/interfaces/ElkSubObjectPropertyOfAxiom.java $
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

import org.semanticweb.elk.owl.visitors.ElkSubObjectPropertyOfAxiomVisitor;

/**
 * Corresponds to an <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Object_Subproperties">Object Subproperty
 * Axiom</a> in the OWL 2 specification.
 * 
 * @author Markus Kroetzsch
 */
public interface ElkSubObjectPropertyOfAxiom extends ElkObjectPropertyAxiom {

	/**
	 * Get the sub object property expression of this axiom.
	 * 
	 * @return sub object property expression
	 */
	public ElkSubObjectPropertyExpression getSubObjectPropertyExpression();

	/**
	 * Get the super object property expression of this axiom.
	 * 
	 * @return super object property expression
	 */
	public ElkObjectPropertyExpression getSuperObjectPropertyExpression();

	/**
	 * Accept an {@link ElkSubObjectPropertyOfAxiomVisitor}.
	 * 
	 * @param visitor
	 *            the visitor that can work with this axiom type
	 * @param <O>
	 *            the type of the output of the visitor
	 * @return the output of the visitor
	 */
	public abstract <O> O accept(ElkSubObjectPropertyOfAxiomVisitor<O> visitor);
	
	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory {

		/**
		 * Create an {@link ElkSubObjectPropertyOfAxiom}.
		 * 
		 * @param subProperty
		 *            the {@link ElkSubObjectPropertyExpression} for which the axiom
		 *            should be created
		 * @param superProperty
		 *            the super-{@link ElkObjectPropertyExpression} for which the
		 *            axiom should be created
		 * @return an {@link ElkSubObjectPropertyOfAxiom} corresponding to the input
		 */
		public ElkSubObjectPropertyOfAxiom getSubObjectPropertyOfAxiom(
				ElkSubObjectPropertyExpression subProperty,
				ElkObjectPropertyExpression superProperty);

	}

}
