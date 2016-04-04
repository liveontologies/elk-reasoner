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

import org.semanticweb.elk.owl.visitors.ElkDisjointClassesAxiomVisitor;

/**
 * Corresponds to an
 * <a href= "http://www.w3.org/TR/owl2-syntax/#Disjoint_Classes">Disjoint
 * Classes Axiom<a> in the OWL 2 specification.
 * 
 * @author Markus Kroetzsch
 */
public interface ElkDisjointClassesAxiom extends ElkClassAxiom {

	/**
	 * Get the list of disjoint class expressions that this axiom refers to. The
	 * order of class expressions does not affect the semantics but it is
	 * relevant to the syntax of OWL.
	 * 
	 * @return list of disjoint class expressions
	 */
	public List<? extends ElkClassExpression> getClassExpressions();

	/**
	 * Accept an {@link ElkDisjointClassesAxiomVisitor}.
	 * 
	 * @param visitor
	 *            the visitor that can work with this axiom type
	 * @return the output of the visitor
	 */
	public abstract <O> O accept(ElkDisjointClassesAxiomVisitor<O> visitor);

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory {

		/**
		 * Create an {@link ElkDisjointClassesAxiom}.
		 * 
		 * @param first
		 *            the first {@link ElkClassExpression} for which the axiom
		 *            should be created
		 * @param second
		 *            the second {@link ElkClassExpression} for which the axiom
		 *            should be created
		 * @param other
		 *            other {@link ElkClassExpression} for which the axiom
		 *            should be created
		 * @return an {@link ElkDisjointClassesAxiom} corresponding to the input
		 */
		public ElkDisjointClassesAxiom getDisjointClassesAxiom(
				ElkClassExpression first,
				ElkClassExpression second,
				ElkClassExpression... other);

		/**
		 * Create an {@link ElkDisjointClassesAxiom}.
		 * 
		 * @param disjointClassExpressions
		 *            the {@link ElkClassExpression}s for which the axiom should
		 *            be created
		 * @return an {@link ElkDisjointClassesAxiom} corresponding to the input
		 */
		public ElkDisjointClassesAxiom getDisjointClassesAxiom(
				List<? extends ElkClassExpression> disjointClassExpressions);
	}

}
