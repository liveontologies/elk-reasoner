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

/**
 * An {@link IndexedAxiom} that can be modified as a result of updating the
 * {@link ModifiableOntologyIndex} where this object is stored.
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public interface ModifiableIndexedAxiom
		extends ModifiableIndexedObject, IndexedAxiom {

	/**
	 * Adds this {@link ModifiableIndexedAxiom} once to the given
	 * {@link ModifiableOntologyIndex}
	 * 
	 * @param index
	 *            the {@link ModifiableOntologyIndex} to which this
	 *            {@link ModifiableIndexedAxiom} should be added
	 * 
	 * @return {@code true} if this operation was successful and {@code false}
	 *         otherwise; if {@code false} is returned, the index should not
	 *         logically change as the result of calling this method
	 */
	boolean addOccurrence(ModifiableOntologyIndex index);

	/**
	 * Removes this {@link ModifiableIndexedAxiom} once from the given
	 * {@link ModifiableOntologyIndex}
	 * 
	 * @param index
	 *            the {@link ModifiableOntologyIndex} from which this
	 *            {@link ModifiableIndexedAxiom} should be removed
	 * 
	 * @return {@code true} if this operation was successful and {@code false}
	 *         otherwise; if {@code false} is returned, the index should not
	 *         logically change as the result of calling this method
	 */
	boolean removeOccurrence(ModifiableOntologyIndex index);

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory extends ModifiableIndexedDisjointClassesAxiom.Factory,
			ModifiableIndexedSubClassOfAxiom.Factory,
			ModifiableIndexedEquivalentClassesAxiom.Factory,
			ModifiableIndexedSubObjectPropertyOfAxiom.Factory,
			ModifiableIndexedObjectPropertyRangeAxiom.Factory,
			ModifiableIndexedDeclarationAxiom.Factory {

		// combined interface

	}

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O>
			extends ModifiableIndexedDisjointClassesAxiom.Visitor<O>,
			ModifiableIndexedSubClassOfAxiom.Visitor<O>,
			ModifiableIndexedEquivalentClassesAxiom.Visitor<O>,
			ModifiableIndexedSubObjectPropertyOfAxiom.Visitor<O>,
			ModifiableIndexedObjectPropertyRangeAxiom.Visitor<O>,
			ModifiableIndexedDeclarationAxiom.Visitor<O> {

		// combined interface

	}

	<O> O accept(Visitor<O> visitor);

}
