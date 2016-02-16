package org.semanticweb.elk.reasoner.indexing.model;

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;

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

import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;

/**
 * An {@link IndexedContextRoot} constructed from an
 * {@link IndexedObjectProperty} and an {@link IndexedClassExpression}.<br>
 * 
 * Notation:
 * 
 * <pre>
 * C ⊓ ∃R-
 * </pre>
 * 
 * It is logically equivalent to an OWL class expression
 * {@code ObjectIntersectionOf(C ObjectSomeValuesFrom(ObjectInverseOf(R) owl:Thing))}
 * <br>
 * 
 * The parameters can be obtained as follows:<br>
 * 
 * C = {@link #getFiller()}<br>
 * R = {@link #getProperty()}<br>
 * 
 * @see IndexedObjectSomeValuesFrom#getRangeFiller()
 * 
 */
public interface IndexedRangeFiller extends IndexedContextRoot {

	/**
	 * @return The representation of the {@link ElkObjectProperty} which range
	 *         this {@link IndexedRangeFiller} subsumes. It is the property of
	 *         the {@link ElkObjectSomeValuesFrom} corresponding to this
	 *         {@link IndexedRangeFiller}.
	 */
	IndexedObjectProperty getProperty();

	/**
	 * @return The representation of the {@link ElkClassExpression} which this
	 *         {@link IndexedRangeFiller} subsumes. It is the filler of the
	 *         {@link ElkObjectSomeValuesFrom} corresponding to this
	 *         {@link IndexedRangeFiller}.
	 */
	IndexedClassExpression getFiller();

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> {

		O visit(IndexedRangeFiller element);

	}

}
