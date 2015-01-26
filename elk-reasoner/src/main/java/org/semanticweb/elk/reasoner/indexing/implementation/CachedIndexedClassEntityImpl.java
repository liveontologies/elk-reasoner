package org.semanticweb.elk.reasoner.indexing.implementation;

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

import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedClassEntity;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkUnexpectedIndexingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements {@link CachedIndexedClassEntity}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of objects this object can be structurally equal to
 */
abstract class CachedIndexedClassEntityImpl<T extends CachedIndexedClassEntity<T>>
		extends CachedIndexedClassExpressionImpl<T, T> implements
		CachedIndexedClassEntity<T> {

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
		return "[all=" + totalOccurrenceNo + "]";
	}

	public void checkOccurrenceNumbers() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace(toString() + " occurences: "
					+ printOccurrenceNumbers());
		if (totalOccurrenceNo < 0)
			throw new ElkUnexpectedIndexingException(toString()
					+ " has a negative occurrence: " + printOccurrenceNumbers());
	}

}
