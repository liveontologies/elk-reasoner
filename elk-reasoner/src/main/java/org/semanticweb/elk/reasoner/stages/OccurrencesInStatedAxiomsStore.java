/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2017 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.stages;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.visitors.DummyElkObjectVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;
import org.semanticweb.elk.reasoner.indexing.model.IndexingListener;
import org.semanticweb.elk.reasoner.indexing.model.Occurrence;
import org.semanticweb.elk.reasoner.indexing.model.OccurrenceStore;

import com.google.common.collect.ImmutableMap;

/**
 * Keeps track of occurrences in the loaded ontology.
 * <p>
 * The visitors returned by {@link #getPreInsertionVisitor()},
 * {@link #getPostInsertionVisitor()}, {@link #getPreDeletionVisitor()} and
 * {@link #getPostDeletionVisitor()} should visit axioms during loading (before
 * and after they are being inserted or deleted).
 * 
 * @author Peter Skocovsky
 */
class OccurrencesInStatedAxiomsStore
		implements OccurrenceStore, IndexingListener {

	public static final int AT_MOST_N_OCCURRENCES_IN_MESSAGE = 3;

	private final Map<Occurrence, Map<ElkObject, Integer>> occurrences_;

	public OccurrencesInStatedAxiomsStore() {

		final ImmutableMap.Builder<Occurrence, Map<ElkObject, Integer>> builder = ImmutableMap
				.<Occurrence, Map<ElkObject, Integer>> builder();
		for (final Occurrence occurrence : Occurrence.values()) {
			builder.put(occurrence, new HashMap<ElkObject, Integer>());
		}
		occurrences_ = builder.build();

	}

	@Override
	public Collection<? extends ElkObject> occursIn(
			final Occurrence occurrence) {
		return occurrences_.get(occurrence).keySet();
	}

	@Override
	public void onIndexing(final Occurrence occurrence) {
		checkStateOnIndexing();
		final Map<ElkObject, Integer> occurrences = occurrences_
				.get(occurrence);
		if (isInsertion_) {
			Integer noOccurrences = occurrences.get(elkObjectBeingIndexed_);
			if (noOccurrences == null) {
				noOccurrences = 0;
			}
			noOccurrences++;
			occurrences.put(elkObjectBeingIndexed_, noOccurrences);
		} else {
			Integer noOccurrences = occurrences.get(elkObjectBeingIndexed_);
			if (noOccurrences == null) {
				return;
			}
			noOccurrences--;
			if (noOccurrences <= 0) {
				occurrences.remove(elkObjectBeingIndexed_);
			} else {
				occurrences.put(elkObjectBeingIndexed_, noOccurrences);
			}
		}
	}

	// Indexing state.
	private boolean isInsertion_;
	private ElkObject elkObjectBeingIndexed_ = null;

	private void checkStateOnIndexing() throws IllegalStateException {
		if (elkObjectBeingIndexed_ == null) {
			throw new IllegalStateException(
					"Indexing listener notified while no axiom is being loaded!");
		}
	}

	private final ElkObjectVisitor<Void> preInsertionVisitor_ = new DummyElkObjectVisitor<Void>() {

		protected Void defaultVisit(final ElkObject elkObject) {
			isInsertion_ = true;
			elkObjectBeingIndexed_ = elkObject;
			return null;
		};

	};

	ElkObjectVisitor<Void> getPreInsertionVisitor() {
		return preInsertionVisitor_;
	}

	private final ElkObjectVisitor<Void> postInsertionVisitor_ = new DummyElkObjectVisitor<Void>() {

		protected Void defaultVisit(final ElkObject elkObject) {
			elkObjectBeingIndexed_ = null;
			return null;
		};

	};

	ElkObjectVisitor<Void> getPostInsertionVisitor() {
		return postInsertionVisitor_;
	}

	private final ElkObjectVisitor<Void> preDeletionVisitor_ = new DummyElkObjectVisitor<Void>() {

		protected Void defaultVisit(final ElkObject elkObject) {
			isInsertion_ = false;
			elkObjectBeingIndexed_ = elkObject;
			return null;
		};

	};

	ElkObjectVisitor<Void> getPreDeletionVisitor() {
		return preDeletionVisitor_;
	}

	private final ElkObjectVisitor<Void> postDeletionVisitor_ = new DummyElkObjectVisitor<Void>() {

		protected Void defaultVisit(final ElkObject elkObject) {
			elkObjectBeingIndexed_ = null;
			return null;
		};

	};

	ElkObjectVisitor<Void> getPostDeletionVisitor() {
		return postDeletionVisitor_;
	}

}
