package org.semanticweb.elk.reasoner.indexing.classes;

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
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedSubObject;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObject;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedSubObject;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.model.OccurrenceIncrement;

/**
 * A {@link CachedIndexedSubObject.Factory} that constructs objects using
 * another {@link CachedIndexedSubObject.Factory} and updates the occurrence
 * counts for the constructed objects using the provided
 * {@link OccurrenceIncrement}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @see ModifiableIndexedObject#updateOccurrenceNumbers
 */
class UpdatingCachedIndexedObjectFactory
		extends DelegatingCachedIndexedObjectFactory {

	public UpdatingCachedIndexedObjectFactory(
			CachedIndexedSubObject.Factory baseFactory,
			final ModifiableOntologyIndex index,
			final OccurrenceIncrement increment) {
		super(baseFactory,
				new DelegatingCachedIndexedObjectFilter(index.getResolver()) {

					private CachedIndexedSubObject filteredElement_;

					@Override
					<T extends CachedIndexedSubObject> T preFilter(
							T element) {
						filteredElement_ = element;
						return element;
					}

					@SuppressWarnings("unchecked")
					@Override
					<T extends CachedIndexedSubObject> T postFilter(
							T element) {
						if (element == null) {
							element = (T) filteredElement_;
						}
						if (!element.occurs()) {
							index.add(element);
						}
						update(element);
						if (!element.occurs()) {
							index.remove(element);
						}
						return element;
					}

					<T extends ModifiableIndexedSubObject> T update(T input) {
						if (!input.getIndexingAction(index, increment).apply())
							throw new ElkIndexingException(input.toString()
									+ ": cannot update in Index for "
									+ increment + " occurrences!");
						return input;
					}

				});
	}

}
