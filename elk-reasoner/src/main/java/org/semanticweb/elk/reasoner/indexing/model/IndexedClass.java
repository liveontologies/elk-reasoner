/*
 * #%L
 * elk-reasoner
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
package org.semanticweb.elk.reasoner.indexing.model;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;

/**
 * An {@link IndexedClassExpression} constructed form an {@link ElkClass}.<br>
 * 
 * Notation:
 * 
 * <pre>
 * A
 * </pre>
 * 
 * The parameters can be obtained as follows:<br>
 * 
 * A = {@link #getElkEntity()}<br>
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 */
public interface IndexedClass extends IndexedClassEntity {

	@Override
	ElkClass getElkEntity();

	/**
	 * @return The {@link IndexedClassExpression} corresponding to an
	 *         {@link ElkClassExpression} defined equivalent to the enclosed
	 *         {@link ElkClass} in the ontology. There can be several such
	 *         equivalent {@link ElkClassExpression}s in the ontology, but at
	 *         most one of them should be chosen as the definition; the value
	 *         can be {@code null} if there are no such equivalent
	 *         {@link ElkClassExpression}s.
	 */
	IndexedClassExpression getDefinition();

	/**
	 * @return the {@link ElkAxiom} from which the definition of this
	 *         {@link IndexedClass} originates or {@code null} if this
	 *         {@link IndexedClass} is not defined.
	 */
	ElkAxiom getDefinitionReason();

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> {

		O visit(IndexedClass element);

	}

}
