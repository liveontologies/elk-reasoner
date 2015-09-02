package org.semanticweb.elk.reasoner.indexing.modifiable;

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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;

/**
 * An {@link IndexedClass} that can be modified as a result of updating the
 * {@link ModifiableOntologyIndex} where this object is stored.
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public interface ModifiableIndexedClass extends ModifiableIndexedClassEntity,
		IndexedClass {

	/**
	 * Set the given {@link ModifiableIndexedClassExpression} as a definition of
	 * this {@link IndexedClass} if is not defined
	 * 
	 * @param definition
	 *            the new definition for this {@link IndexedClass}
	 * @param reason
	 *            the {@link ElkAxiom} from which the definition originates
	 * @return {@code true} if this operation is successful or {@code false}
	 *         otherwise; the letter is returned if this {@link IndexedClass}
	 *         was already defined ({@link #getDefinition()} returns
	 *         {@code null})
	 */
	boolean setDefinition(ModifiableIndexedClassExpression definition,
			ElkAxiom reason);

	/**
	 * Removes the definition for this {@link IndexedClass}; after calling this
	 * method, {@link #getDefinition()} returns {@code null}
	 */
	void removeDefinition();

}
