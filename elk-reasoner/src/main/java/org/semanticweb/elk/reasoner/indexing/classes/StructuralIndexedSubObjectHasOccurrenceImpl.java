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

import org.semanticweb.elk.RevertibleAction;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.model.OccurrenceIncrement;
import org.semanticweb.elk.reasoner.indexing.model.StructuralIndexedSubObject;

/**
 * A {@link StructuralIndexedSubObject} with occurrence counter.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            The type of structured objects this object can be compared with
 * @param <N>
 *            The type of the elements in the collection where this entry is
 *            used
 * 
 */
abstract class StructuralIndexedSubObjectHasOccurrenceImpl<T extends StructuralIndexedSubObjectHasOccurrenceImpl<T, N>, N>
		extends StructuralIndexedSubObjectImpl<T, N>
		implements HasOccurrenceDefaults {

	StructuralIndexedSubObjectHasOccurrenceImpl(int structuralHash) {
		super(structuralHash);
	}

	/**
	 * This counts how many times this object occurred in the ontology.
	 */
	private int totalOccurrenceNo_ = 0;

	@Override
	public int getTotalOccurrenceNumber() {
		return totalOccurrenceNo_;
	}
	
	@Override
	public String printOccurrenceNumbers() {
		return HasOccurrenceDefaults.super.printOccurrenceNumbers();
	}

	@Override
	public void updateTotalOccurrenceNumber(int increment) {
		totalOccurrenceNo_ += increment;
	}
	
	@Override
	public RevertibleAction getIndexingAction(ModifiableOntologyIndex index,
			OccurrenceIncrement increment) {
		return HasOccurrenceDefaults.super.getIndexingAction(index, increment);
	}

}
