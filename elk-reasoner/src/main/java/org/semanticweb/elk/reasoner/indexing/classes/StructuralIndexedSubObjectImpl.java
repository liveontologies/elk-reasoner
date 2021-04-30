/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.indexing.classes;

import org.semanticweb.elk.RevertibleAction;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkUnexpectedIndexingException;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObject;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubObject;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedSubObject;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.model.OccurrenceIncrement;
import org.semanticweb.elk.reasoner.indexing.model.StructuralIndexedSubObject;
import org.semanticweb.elk.util.collections.entryset.Entry;
import org.semanticweb.elk.util.collections.entryset.EntryCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements {@link StructuralIndexedSubObject} and {@link Entry} so that these
 * objects can be stored in {@link EntryCollection} together with other
 * elements.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            The type of structured objects this object can be compared with
 * @param <N>
 *            The type of the elements in the collection where this entry is used
 * 
 */
abstract class StructuralIndexedSubObjectImpl<T extends StructuralIndexedSubObjectImpl<T, N>, N>
		extends IndexedObjectImpl implements ModifiableIndexedSubObject,
		StructuralIndexedSubObject<T>, Entry<N> {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(StructuralIndexedSubObjectImpl.class);
	
	/**
	 * The reference to the next element
	 */
	private N next_;

	private final int structuralHash_;
	
	/**
	 * This counts how many times this object occurred in the ontology.
	 */
	private int totalOccurrenceNo_ = 0;

	StructuralIndexedSubObjectImpl(int structuralHash) {
		this.structuralHash_ = structuralHash;
	}

	@Override
	public final boolean occurs() {
		return totalOccurrenceNo_ > 0;
	}

	@Override
	public String printOccurrenceNumbers() {
		return "all=" + totalOccurrenceNo_;
	}

	/**
	 * verifies that occurrence numbers are not negative
	 */
	void checkOccurrenceNumbers() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace(
					toString() + " occurences: " + printOccurrenceNumbers());
		if (totalOccurrenceNo_ < 0)
			throw new ElkUnexpectedIndexingException(
					toString() + " has a negative total occurrence: "
							+ printOccurrenceNumbers());
	}	
	
	@Override
	public RevertibleAction getIndexingAction(ModifiableOntologyIndex index,
			OccurrenceIncrement increment) {
		return RevertibleAction.create(() -> {
			totalOccurrenceNo_ += increment.totalIncrement;
			checkOccurrenceNumbers();
			return true;
		}, () -> {
			totalOccurrenceNo_ -= increment.totalIncrement;
		});
	}
	
	@Override
	public final void setNext(N next) {
		this.next_ = next;
	}

	@Override
	public final N getNext() {
		return next_;
	}

	@Override
	public final int structuralHashCode() {
		return structuralHash_;
	}

	@Override
	public final <O> O accept(IndexedObject.Visitor<O> visitor) {
		return accept((IndexedSubObject.Visitor<O>) visitor);
	}

}
