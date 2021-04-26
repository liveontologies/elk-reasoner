package org.semanticweb.elk.reasoner.indexing.classes;

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

import org.semanticweb.elk.reasoner.indexing.conversion.ElkIndexingException;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObject;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.model.OccurrenceIncrement;
import org.semanticweb.elk.reasoner.indexing.model.StructuralIndexedSubObject;

/**
 * A {@link ModifiableIndexedObject.Factory} that constructs objects using
 * another {@link ModifiableIndexedObject.Factory} and updates the occurrence
 * counts for the constructed objects using the provided
 * {@link OccurrenceIncrement}.
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public class UpdatingModifiableIndexedObjectFactory
		extends ModifiableIndexedObjectBaseFactory {

	private final ModifiableOntologyIndex index_;

	private final OccurrenceIncrement increment_;

	public UpdatingModifiableIndexedObjectFactory(
			final ModifiableOntologyIndex index,
			final OccurrenceIncrement increment) {
		this.index_ = index;
		this.increment_ = increment;
	}

	@Override
	protected <T extends StructuralIndexedSubObject<T>> T filter(T input) {
		T result = index_.resolve(input);
		if (result == null) {
			result = input;
		}
		if (!result.occurs()) {
			index_.add(result);
		}
		if (!result.getIndexingAction(index_, increment_).apply())
			throw new ElkIndexingException(
					result.toString() + ": cannot update in Index for "
							+ increment_ + " occurrences!");
		if (!result.occurs()) {
			index_.remove(result);
		}
		return result;
	}

	@Override
	protected <T extends ModifiableIndexedAxiom> T filter(T input) {
		if (increment_.totalIncrement > 0) {
			for (int i = 0; i < increment_.totalIncrement; i++) {
				if (!input.addOccurrence(index_))
					throw new ElkIndexingException(
							input.toString() + ": cannot be added to Index!");
			}
		}
		if (increment_.totalIncrement < 0) {
			for (int i = 0; i < -increment_.totalIncrement; i++) {
				if (!input.removeOccurrence(index_))
					throw new ElkIndexingException(input.toString()
							+ ": cannot be removed from Index!");
			}
		}
		return input;
	}

}
