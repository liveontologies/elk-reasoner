package org.semanticweb.elk.reasoner.indexing.caching;

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

import org.semanticweb.elk.reasoner.indexing.conversion.ElkIndexingException;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObject;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedSubObject;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.modifiable.OccurrenceIncrement;

/**
 * A {@link CachedIndexedObject.Factory} that constructs objects using another
 * {@link CachedIndexedObject.Factory} and updates the occurrence counts for the
 * constructed objects using the provided {@link OccurrenceIncrement}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @see ModifiableIndexedObject#updateOccurrenceNumbers
 */
public class UpdatingCachedIndexedObjectFactory extends
		DelegatingCachedIndexedObjectFactory {

	private final OccurrenceIncrement increment_;

	private final ModifiableOntologyIndex index_;

	public UpdatingCachedIndexedObjectFactory(
			CachedIndexedObject.Factory baseFactory,
			ModifiableOntologyIndex index, OccurrenceIncrement increment) {
		super(baseFactory);
		this.index_ = index;
		this.increment_ = increment;
	}

	@Override
	<T extends CachedIndexedSubObject<T>> T filter(T input) {
		T result = resolve(input);
		update(result);
		if (!result.occurs()) {
			index_.remove(result);
		}
		return result;
	}

	<T extends CachedIndexedObject<T>> T resolve(T input) {
		T result = index_.resolve(input);
		if (result == null) {
			result = input;
		}
		if (!result.occurs()) {
			index_.add(result);
		}
		return result;
	}

	<T extends ModifiableIndexedSubObject> T update(T input) {
		if (!input.updateOccurrenceNumbers(index_, increment_))
			throw new ElkIndexingException(input.toString()
					+ ": cannot update in Index for " + increment_
					+ " occurrences!");
		return input;
	}

	<T extends ModifiableIndexedAxiom> T update(T input) {
		if (increment_.totalIncrement > 0) {
			for (int i = 0; i < increment_.totalIncrement; i++) {
				if (!input.addOccurrence(index_))
					throw new ElkIndexingException(input.toString()
							+ ": cannot be added to Index!");
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
