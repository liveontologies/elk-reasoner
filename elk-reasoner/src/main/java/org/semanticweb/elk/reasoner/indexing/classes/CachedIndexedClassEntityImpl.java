package org.semanticweb.elk.reasoner.indexing.classes;

import org.semanticweb.elk.RevertibleAction;

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
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedClassEntity;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassEntity;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.model.OccurrenceIncrement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements {@link CachedIndexedClassEntity}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <N>
 *            The type of the elements in the set where this entry is used
 */
abstract class CachedIndexedClassEntityImpl<N>
		extends CachedIndexedClassExpressionImpl<N>
		implements CachedIndexedClassEntity {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(CachedIndexedClassEntityImpl.class);

	/**
	 * This counts how many times this object occurred in the ontology.
	 */
	protected int totalOccurrenceNo = 0;

	CachedIndexedClassEntityImpl(int structuralHash) {
		super(structuralHash);
	}

	@Override
	public final boolean occurs() {
		return totalOccurrenceNo > 0;
	}

	@Override
	public String printOccurrenceNumbers() {
		return "[all=" + totalOccurrenceNo + "; pos=" + positiveOccurrenceNo
				+ "; neg=" + negativeOccurrenceNo + "]";
	}

	private void checkTotalOccurrenceNumbers() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace(toString() + " occurences: "
					+ printOccurrenceNumbers());
		if (totalOccurrenceNo < 0)
			throw new ElkUnexpectedIndexingException(
					toString() + " has a negative total occurrence: "
							+ printOccurrenceNumbers());
	}

	@Override
	public RevertibleAction getIndexingAction(ModifiableOntologyIndex index,
			OccurrenceIncrement increment) {
		return RevertibleAction.create(() -> {
			totalOccurrenceNo += increment.totalIncrement;
			checkTotalOccurrenceNumbers();
			return true;
		}, () -> {
			totalOccurrenceNo -= increment.totalIncrement;
		}).then(super.getIndexingAction(index, increment));

	}

	@Override
	public final <O> O accept(IndexedClassExpression.Visitor<O> visitor) {
		return accept((IndexedClassEntity.Visitor<O>) visitor);
	}

	@Override
	public final CachedIndexedClassExpression accept(CachedIndexedClassExpression.Filter filter) {
		return accept((CachedIndexedClassEntity.Filter) filter);
	}

}
