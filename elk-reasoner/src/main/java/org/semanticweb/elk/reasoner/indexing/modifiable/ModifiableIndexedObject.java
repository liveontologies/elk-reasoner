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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObject;

/**
 * An {@link IndexedObject} than be modified as a result of updating the
 * {@link ModifiableOntologyIndex} where this object is stored.
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public interface ModifiableIndexedObject extends IndexedObject {

	/**
	 * Tries to change the number of occurrences of this
	 * {@link ModifiableIndexedObject} in the given
	 * {@link ModifiableOntologyIndex} according to the given
	 * {@link OccurrenceIncrement}
	 * 
	 * @param index
	 *            the {@link ModifiableOntologyIndex} representing the logical
	 *            structure of the ontology
	 * 
	 * @param increment
	 *            describes how occurrences of this
	 *            {@link ModifiableIndexedObject} should changed
	 * 
	 * @return {@code true} if the operation has been successful or
	 *         {@code false} otherwise. If {@code false} is return, the provided
	 *         {@link ModifiableOntologyIndex} should not logically change,
	 *         i.e., it should correspond to the same logical representation of
	 *         the ontology as before the operation
	 */
	boolean updateOccurrenceNumbers(ModifiableOntologyIndex index,
			OccurrenceIncrement increment);

}
