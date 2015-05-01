package org.semanticweb.elk.reasoner.indexing.caching;

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
import org.semanticweb.elk.reasoner.indexing.conversion.ElkIndexingException;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObject;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedSubObject;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.modifiable.OccurrenceIncrement;

/**
 * A {@link CachedIndexedObjectFactory} that constructs objects using another
 * {@link CachedIndexedObjectFactory} and updates the occurrence counts for the
 * constructed objects using the provided {@link OccurrenceIncrement}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @see ModifiableIndexedObject#updateOccurrenceNumbers
 */
public class UpdatingCachedIndexedObjectFactory extends
		DelegatingCachedIndexedObjectFactory {

	private final ModifiableOntologyIndex index_;

	private final OccurrenceIncrement increment_;

	public UpdatingCachedIndexedObjectFactory(
			CachedIndexedObjectFactory baseFactory,
			ModifiableOntologyIndex index, OccurrenceIncrement increment) {
		super(baseFactory);
		this.index_ = index;
		this.increment_ = increment;
	}

	<T extends ModifiableIndexedAxiom> T update(T input, ElkAxiom reason) {
		if (increment_.totalIncrement > 0) {
			for (int i = 0; i < increment_.totalIncrement; i++) {
				if (!input.addOccurrence(index_, reason))
					throw new ElkIndexingException(input.toString()
							+ ": cannot be added to Index!");
			}
		}
		if (increment_.totalIncrement < 0) {
			for (int i = 0; i < -increment_.totalIncrement; i++) {
				if (!input.removeOccurrence(index_, reason))
					throw new ElkIndexingException(input.toString()
							+ ": cannot be removed from Index!");
			}
		}
		return input;
	}

	<T extends ModifiableIndexedSubObject> T update(T input) {
		if (!input.updateOccurrenceNumbers(index_, increment_))
			throw new ElkIndexingException(input.toString()
					+ ": cannot update in Index for " + increment_
					+ " occurrences!");
		return input;
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

	@Override
	<T extends CachedIndexedAxiom<T>> T filter(T input, ElkAxiom reason) {
		T result = resolve(input);
		update(result, reason);
		if (!result.occurs()) {
			index_.remove(result);
		}
		return result;
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

}
