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

import org.semanticweb.elk.owl.visitors.ElkDifferentIndividualsAxiomVisitor;

/**
 * Corresponds to an
 * <a href= "http://www.w3.org/TR/owl2-syntax/#Individual_Inequality">individual
 * inequality axiom</a> in the OWL 2 specification.
 * 
 * @author Markus Kroetzsch
 */
public interface ElkDifferentIndividualsAxiom extends ElkAssertionAxiom {

	/**
	 * Get the list of individuals that this axiom refers to. The order of
	 * individuals does not affect the semantics but it is relevant to the
	 * syntax of OWL.
	 * 
	 * @return list of individuals
	 */
	public List<? extends ElkIndividual> getIndividuals();

	/**
	 * Accept an {@link ElkDifferentIndividualsAxiomVisitor}.
	 * 
	 * @param visitor
	 *            the visitor that can work with this axiom type
	 * @param <O>
	 *            the type of the output of the visitor
	 * @return the output of the visitor
	 */
	public <O> O accept(ElkDifferentIndividualsAxiomVisitor<O> visitor);

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory {

		/**
		 * Create an {@link ElkDifferentIndividualsAxiom}.
		 * 
		 * @param first
		 *            the first {@link ElkIndividual} for which the axiom should
		 *            be created
		 * @param second
		 *            the second {@link ElkIndividual} for which the axiom
		 *            should be created
		 * @param other
		 *            other {@link ElkIndividual} for which the axiom should be
		 *            created
		 * @return an {@link ElkAnnotation} corresponding to the input
		 */
		public ElkDifferentIndividualsAxiom getDifferentIndividualsAxiom(
				ElkIndividual first, ElkIndividual second,
				ElkIndividual... other);

		/**
		 * Create an {@link ElkDifferentIndividualsAxiom}.
		 * 
		 * @param individuals
		 *            the {@link ElkIndividual}s for which the axiom should be
		 *            created
		 * @return an {@link ElkDifferentIndividualsAxiom} corresponding to the
		 *         input
		 */
		public ElkDifferentIndividualsAxiom getDifferentIndividualsAxiom(
				List<? extends ElkIndividual> individuals);

	}

}
