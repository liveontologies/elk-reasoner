package org.semanticweb.elk.reasoner.saturation.conclusions.model;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;

/**
 * A {@link ClassConclusion} representing a derived subclass axiom between two
 * class expressions.<br>
 * 
 * Notation:
 * 
 * <pre>
 * [C] ⊑ D
 * </pre>
 * 
 * It is logically equivalent to axiom {@code SubClassInclusion(C D)}<br>
 * 
 * The parameters can be obtained as follows:<br>
 * 
 * C = {@link #getDestination()}<br>
 * D = {@link #getSubsumer()}<br>
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 */
public interface SubClassInclusion extends ClassConclusion {

	/**
	 * @return the {@code IndexedClassExpression} corresponding to the
	 *         super-expression of the {@link ElkSubClassOfAxiom} represented by
	 *         this {@link SubClassInclusion}
	 * 
	 * @see ElkSubClassOfAxiom#getSuperClassExpression()
	 */
	public IndexedClassExpression getSubsumer();

	public <O> O accept(Visitor<O> visitor);

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
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O>
			extends
				SubClassInclusionComposed.Visitor<O>,
				SubClassInclusionDecomposed.Visitor<O> {

		// combined interface

	}

}
