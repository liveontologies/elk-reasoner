package org.semanticweb.elk.reasoner.indexing.model;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDisjointUnionAxiom;

/**
 * Represents a transformation of an {@link ElkDisjointUnionAxiom} to an
 * {@link IndexedSubClassOfAxiom} representing the disjointness of two members
 * of the union.
 *
 * @see ElkDisjointUnionAxiom#getClassExpressions()
 * 
 * @author Yevgeny Kazakov
 *
 */
public interface ElkDisjointUnionAxiomBinaryConversion
		extends
			IndexedSubClassOfAxiomInference {

	@Override
	ElkDisjointUnionAxiom getOriginalAxiom();

	/**
	 * @return the position of the first {@link ElkClassExpression} in the
	 *         member list of the union of the {@link ElkDisjointUnionAxiom}
	 *         used in the binary disjointness represented by the
	 *         {@link IndexedSubClassOfAxiom}.
	 *
	 * @see ElkDisjointUnionAxiom#getClassExpressions()
	 * @see #getSecondDisjunctPosition()
	 */
	int getFirstDisjunctPosition();

	/**
	 * @return the position of the second {@link ElkClassExpression} in the
	 *         member list of the union of the {@link ElkDisjointUnionAxiom}
	 *         used in the binary disjointness represented by the
	 *         {@link IndexedSubClassOfAxiom}.
	 * 
	 * @see ElkDisjointUnionAxiom#getClassExpressions()
	 * @see #getFirstDisjunctPosition()
	 */
	int getSecondDisjunctPosition();

	IndexedSubClassOfAxiom getConclusion(
			IndexedSubClassOfAxiom.Factory factory);

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> {

		O visit(ElkDisjointUnionAxiomBinaryConversion inference);

	}

}
