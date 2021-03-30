package org.semanticweb.elk.reasoner.indexing.model;

/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2021 Department of Computer Science, University of Oxford
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

/**
 * Represents occurrences of a {@link ElkClassExpression} that use at least one
 * constructor in an ontology.
 * 
 * @author "Yevgeny Kazakov"
 */
public interface IndexedComplexClassExpression
		extends IndexedClassExpression {

	/**
	 * @return {@code true} if this {@link IndexedObject} occurs with the
	 *         positive polarity in the current ontology, i.e., in the
	 *         right-hand side of concept inclusions or in complex equivalences
	 */
	boolean occursPositively();
	
	/**
	 * @return {@code true} if this {@link IndexedObject} occurs with the
	 *         negative polarity in the current ontology, i.e., in the left-hand
	 *         side of concept inclusions or in complex equivalences
	 */
	boolean occursNegatively();

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
				IndexedDataHasValue.Visitor<O>,
				IndexedObjectComplementOf.Visitor<O>,
				IndexedObjectHasSelf.Visitor<O>,
				IndexedObjectIntersectionOf.Visitor<O>,
				IndexedObjectSomeValuesFrom.Visitor<O>,
				IndexedObjectUnionOf.Visitor<O> {

		// combined interface

	}
	
	<O> O accept(Visitor<O> visitor);

}
