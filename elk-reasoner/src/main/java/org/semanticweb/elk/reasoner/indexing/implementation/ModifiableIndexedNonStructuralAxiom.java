package org.semanticweb.elk.reasoner.indexing.implementation;

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

import org.semanticweb.elk.reasoner.indexing.conversion.ElkUnexpectedIndexingException;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableOntologyIndex;

/**
 * A {@code ModifiableIndexedAxiom} which is treated as structurally new axiom
 * each time it is inserted or deleted even if structurally equal axioms were
 * added or removed previously.
 * 
 * @author "Yevgeny Kazakov"
 *
 */
abstract class ModifiableIndexedNonStructuralAxiom extends
		ModifiableIndexedAxiomImpl {

	/**
	 * Adds this {@link ModifiableIndexedNonStructuralAxiom} once to the given
	 * {@link ModifiableOntologyIndex}
	 * 
	 * @param index
	 *            the {@link ModifiableOntologyIndex} to which this
	 *            {@link ModifiableIndexedNonStructuralAxiom} should be added
	 * @return {@code true} if this operation was successful and {@code false}
	 *         otherwise; if {@code false} is returned, the index should not
	 *         logically change as the result of calling this method
	 */
	abstract boolean addOnce(ModifiableOntologyIndex index);

	/**
	 * Removes this {@link ModifiableIndexedNonStructuralAxiom} once from the
	 * given {@link ModifiableOntologyIndex}
	 * 
	 * @param index
	 *            the {@link ModifiableOntologyIndex} from which this
	 *            {@link ModifiableIndexedNonStructuralAxiom} should be removed
	 * @return {@code true} if this operation was successful and {@code false}
	 *         otherwise; if {@code false} is returned, the index should not
	 *         logically change as the result of calling this method
	 */
	abstract boolean removeOnce(ModifiableOntologyIndex index);

	@Override
	public final boolean updateOccurrenceNumbers(ModifiableOntologyIndex index,
			int increment) {
		// we add or delete this axiom the correspondent number of times from
		// the index; if one of this operation fails, all previous modifications
		// need to be reverted
		boolean success = true;
		if (increment > 0) {
			int added = 0;
			for (int i = 0; i < increment; i++) {
				if (addOnce(index))
					added++;
				else {
					success = false;
					break;
				}
			}
			if (!success) {
				// revert the changes
				for (; added > 0; added--) {
					if (!removeOnce(index))
						throw new ElkUnexpectedIndexingException(this);
				}
			}
		} else {
			// increment <= 0
			int removed = 0;
			for (int i = 0; i < -increment; i++) {
				if (removeOnce(index))
					removed++;
				else {
					success = false;
					break;
				}
			}
			if (!success) {
				// revert the changes
				for (; removed > 0; removed--) {
					if (!addOnce(index))
						throw new ElkUnexpectedIndexingException(this);
				}
			}
		}
		return success;
	}

}
