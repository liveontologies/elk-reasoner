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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;

/**
 * An {@link IndexedClass} that may be defined to be equivalent to other
 * classes<br>
 * 
 * @author "Yevgeny Kazakov"
 */
public interface IndexedDefinedClass extends IndexedClass {

	/**
	 * @return The {@link IndexedClassExpression} corresponding to an
	 *         {@link ElkClassExpression} defined equivalent to this
	 *         {@link IndexedDefinedClass} in the ontology. There can be several
	 *         such equivalent {@link ElkClassExpression}s in the ontology, but
	 *         at most one of them should be chosen as the definition; the value
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

		O visit(IndexedDefinedClass element);

	}

}
