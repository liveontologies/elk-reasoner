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

import java.util.List;

import org.semanticweb.elk.RevertibleAction;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkUnexpectedIndexingException;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpressionList;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubObject;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.model.OccurrenceIncrement;
import org.semanticweb.elk.reasoner.indexing.model.StructuralIndexedClassExpressionListEntry;
import org.semanticweb.elk.reasoner.indexing.model.StructuralIndexedSubObject;
import org.semanticweb.elk.util.hashing.HashGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements {@link StructuralIndexedClassExpressionListEntry}
 * 
 * @author "Yevgeny Kazakov"
 */
class StructuralIndexedClassExpressionListEntryImpl extends
		StructuralIndexedSubObjectImpl<StructuralIndexedClassExpressionListEntryImpl, StructuralIndexedClassExpressionListEntry<?>>
		implements
		StructuralIndexedClassExpressionListEntry<StructuralIndexedClassExpressionListEntryImpl> {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(StructuralIndexedClassExpressionListEntryImpl.class);

	/**
	 * The elements of the list
	 */
	private final List<? extends ModifiableIndexedClassExpression> elements_;

	/**
	 * Counts how often this {@link IndexedClassExpressionList} occurs in the
	 * ontology.
	 */
	int totalOccurrenceNo_ = 0;

	StructuralIndexedClassExpressionListEntryImpl(
			List<? extends ModifiableIndexedClassExpression> members) {
		super(structuralHashCode(members));
		this.elements_ = members;
	}

	@Override
	public final boolean occurs() {
		return totalOccurrenceNo_ > 0;
	}

	@Override
	public final List<? extends ModifiableIndexedClassExpression> getElements() {
		return elements_;
	}

	@Override
	public String printOccurrenceNumbers() {
		return "[all=" + totalOccurrenceNo_ + "]";
	}

	private void checkTotalOccurrenceNumbers() {
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
			checkTotalOccurrenceNumbers(); // TODO: perhaps return false instead
											// of failing
			return true;
		}, () -> {
			totalOccurrenceNo_ -= increment.totalIncrement;
		});
	}

	static int structuralHashCode(
			List<? extends ModifiableIndexedClassExpression> members) {
		return HashGenerator.combinedHashCode(
				StructuralIndexedClassExpressionListEntryImpl.class,
				HashGenerator.combinedHashCode(members));
	}

	@Override
	public StructuralIndexedClassExpressionListEntryImpl structuralEquals(Object other) {
		if (this == other) {
			return this;
		}
		if (other instanceof StructuralIndexedClassExpressionListEntryImpl) {
			StructuralIndexedClassExpressionListEntryImpl secondEntry = (StructuralIndexedClassExpressionListEntryImpl) other;
			if (getElements().equals(secondEntry.getElements()))
				return secondEntry;
		}
		// else
		return null;
	}

	@Override
	public final <O> O accept(IndexedSubObject.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(StructuralIndexedSubObject.Visitor<O> visitor) {
		return visitor.visit(this);
	}

}
