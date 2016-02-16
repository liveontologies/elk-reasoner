package org.semanticweb.elk.reasoner.indexing.model;

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

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkObjectUnionOf;

/**
 * An {@link IndexedClassExpression} constructed from a list of
 * {@link IndexedClassExpression}s.<br>
 * 
 * Notation:
 * 
 * <pre>
 * C1 ⊔ C2 ⊔ ... ⊔ Cn
 * </pre>
 * 
 * It is logically equivalent to OWL class expression
 * {@code ObjectUnionOf(C1 C2 ... Cn)} <br>
 * 
 * The parameters can be obtained as follows:<br>
 * 
 * Ci = the i-th position of {@link #getDisjuncts()}<br>
 * 
 * @author "Yevgeny Kazakov"
 */
public interface IndexedObjectUnionOf extends IndexedClassExpression {

	/**
	 * @return the {@link IndexedClassExpression}s representing the disjuncts of
	 *         the {@link ElkObjectUnionOf} represented by this
	 *         {@link IndexedObjectUnionOf}.
	 * 
	 * @see IndexedObjectUnionOf#getDisjuncts()
	 */
	List<? extends IndexedClassExpression> getDisjuncts();

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> {

		O visit(IndexedObjectUnionOf element);

	}

}
