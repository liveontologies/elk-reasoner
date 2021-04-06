package org.semanticweb.elk.reasoner.indexing.model;

import org.semanticweb.elk.RevertibleAction;

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

/**
 * An {@link ModifiableIndexedObject} that occurs inside axioms.
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public interface ModifiableIndexedSubObject
		extends ModifiableIndexedObject, IndexedSubObject {

	/**
	 * Returns a {@link RevertibleAction} that tries to change the number of
	 * occurrences of this {@link ModifiableIndexedSubObject} in the given
	 * {@link ModifiableOntologyIndex} according to the given
	 * {@link OccurrenceIncrement}.
	 * 
	 * @param index
	 *            the {@link ModifiableOntologyIndex} representing the logical
	 *            structure of the ontology
	 * 
	 * @param increment
	 *            how many occurrences should be added (if positive) or removed
	 *            (if negative)
	 * 
	 * @return a {@code RevertibleAction} whose successful application changes
	 *         the number of occurrences of this
	 *         {@link ModifiableIndexedSubObject} according to the
	 *         {@link OccurrenceIncrement}; if the application is not successful
	 *         the provided {@link ModifiableOntologyIndex} should not logically
	 *         change, i.e., it should correspond to the same logical
	 *         representation of the ontology as before the operation
	 */
	RevertibleAction getIndexingAction(ModifiableOntologyIndex index,
			OccurrenceIncrement increment);

	interface Factory extends ModifiableIndexedClassExpression.Factory,
			ModifiableIndexedClassExpressionList.Factory,
			ModifiableIndexedEntity.Factory,
			ModifiableIndexedPropertyChain.Factory {

		// combined interface

	}

}
