package org.semanticweb.elk.reasoner.saturation.conclusions.model;

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedContextRoot;

/**
 * A {@link ClassConclusion} representing a subsumption between a
 * {@link IndexedContextRoot} and an {@link IndexedClassExpression}. It is
 * logically equivalent to a {@link ElkSubClassOfAxiom} for the corresponding
 * instances {@link ElkClassExpression}.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public interface SubClassInclusion extends ClassConclusion {

	/**
	 * @return the {@code IndexedContextRoot} corresponding to the
	 *         sub-expression of the {@link ElkSubClassOfAxiom} represented by
	 *         this {@link SubClassInclusion}
	 * 
	 * @see ElkSubClassOfAxiom#getSubClassExpression()
	 */
	public IndexedContextRoot getSubExpression();

	/**
	 * @return the {@code IndexedClassExpression} corresponding to the
	 *         super-expression of the {@link ElkSubClassOfAxiom} represented by
	 *         this {@link SubClassInclusion}
	 * 
	 * @see ElkSubClassOfAxiom#getSuperClassExpression()
	 */
	public IndexedClassExpression getSuperExpression();

	public <I, O> O accept(Visitor<I, O> visitor, I input);

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory
			extends
				SubClassInclusionComposed.Factory,
				SubClassInclusionDecomposed.Factory {

		// combined interface

	}

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <I>
	 *            the type of the input
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<I, O>
			extends
				SubClassInclusionComposed.Visitor<I, O>,
				SubClassInclusionDecomposed.Visitor<I, O> {

		// combined interface

	}

}
