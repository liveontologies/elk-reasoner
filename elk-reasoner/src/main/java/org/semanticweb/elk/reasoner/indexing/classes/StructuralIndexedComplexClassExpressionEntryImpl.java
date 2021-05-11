package org.semanticweb.elk.reasoner.indexing.classes;

import org.semanticweb.elk.RevertibleAction;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkUnexpectedIndexingException;

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

import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedComplexClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.model.OccurrenceIncrement;
import org.semanticweb.elk.reasoner.indexing.model.StructuralIndexedComplexClassExpressionEntry;

/**
 * Implements {@link StructuralIndexedComplexClassExpressionEntry}.
 * 
 * @param <T>
 *            The type of structured objects this object can be compared with
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
abstract class StructuralIndexedComplexClassExpressionEntryImpl<T extends StructuralIndexedComplexClassExpressionEntryImpl<T>>
		extends
		ModifiableIndexedClassExpressionImpl<T, StructuralIndexedComplexClassExpressionEntry<?>>
		implements StructuralIndexedComplexClassExpressionEntry<T> {

	/**
	 * Counts how often this object occurred negatively. Some indexing
	 * operations are only needed when encountering objects negatively for the
	 * first time.
	 */
	private int negativeOccurrenceNo_ = 0;

	/**
	 * This counts how often this object occurred positively. Some indexing
	 * operations are only needed when encountering objects positively for the
	 * first time.
	 */
	private int positiveOccurrenceNo_ = 0;

	StructuralIndexedComplexClassExpressionEntryImpl(int structuralHash) {
		super(structuralHash);
	}

	@Override
	public boolean occursNegatively() {
		return negativeOccurrenceNo_ > 0;
	}

	@Override
	public boolean occursPositively() {
		return positiveOccurrenceNo_ > 0;
	}
	
	@Override
	public boolean occurs() {	
		return occursNegatively() || occursPositively();
	}

	@Override
	public String printOccurrenceNumbers() {
		return "neg=" + negativeOccurrenceNo_ + "; pos="
				+ positiveOccurrenceNo_;
	}

	void checkOccurrenceNumbers() {
		if (negativeOccurrenceNo_ < 0)
			throw new ElkUnexpectedIndexingException(
					toString() + " has a negative occurrence: "
							+ printOccurrenceNumbers());
		if (positiveOccurrenceNo_ < 0)
			throw new ElkUnexpectedIndexingException(
					toString() + " has a negative occurrence: "
							+ printOccurrenceNumbers());
	}

	@Override
	public RevertibleAction getIndexingAction(ModifiableOntologyIndex index,
			OccurrenceIncrement increment) {
		return RevertibleAction.create(() -> {
			negativeOccurrenceNo_ += increment.negativeIncrement;
			positiveOccurrenceNo_ += increment.positiveIncrement;
			return true;
		}, () -> {
			negativeOccurrenceNo_ -= increment.negativeIncrement;
			positiveOccurrenceNo_ -= increment.positiveIncrement;
		});
	}

	@Override
	public final <O> O accept(IndexedClassExpression.Visitor<O> visitor) {
		return accept((IndexedComplexClassExpression.Visitor<O>) visitor);
	}

}
