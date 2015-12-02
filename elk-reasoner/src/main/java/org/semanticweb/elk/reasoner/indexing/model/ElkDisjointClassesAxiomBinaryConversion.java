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
import org.semanticweb.elk.owl.interfaces.ElkDisjointClassesAxiom;

/**
 * Represents a transformation of an {@link ElkDisjointClassesAxiom} to an
 * {@link IndexedSubClassOfAxiom} representing that two selected
 * {@link ElkClassExpression}s are disjoint.
 * 
 * @see ElkDisjointClassesAxiom#getClassExpressions()
 * 
 * @author Yevgeny Kazakov
 *
 */
public interface ElkDisjointClassesAxiomBinaryConversion
		extends
			IndexedSubClassOfAxiomInference {

	@Override
	ElkDisjointClassesAxiom getOriginalAxiom();

	/**
	 * @return the position of the first {@link ElkClassExpression} in the
	 *         member list of the {@link ElkDisjointClassesAxiom} that is stated
	 *         to be disjoint with the second one using the
	 *         {@link IndexedSubClassOfAxiom}.
	 * 
	 * @see ElkDisjointClassesAxiom#getClassExpressions()
	 * @see #getSecondClassPosition()
	 */
	int getFirstClassPosition();

	/**
	 * @return the position of the second {@link ElkClassExpression} in the
	 *         member list of the {@link ElkDisjointClassesAxiom} that is stated
	 *         to be disjoint with the first one using the
	 *         {@link IndexedSubClassOfAxiom}.
	 * 
	 * @see ElkDisjointClassesAxiom#getClassExpressions()
	 * @see #getFirstClassPosition()
	 */
	int getSecondClassPosition();
	
	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> {
		
		O visit(ElkDisjointClassesAxiomBinaryConversion inference);
		
	}

}
