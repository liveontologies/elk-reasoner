package org.semanticweb.elk.reasoner.indexing.classes;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedOwlNothing;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.model.OccurrenceIncrement;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ContradictionFromOwlNothingRule;

/**
 * Implements {@link CachedIndexedOwlNothing}
 * 
 * @author Yevgeny Kazakov
 *
 */
final class CachedIndexedOwlNothingImpl extends CachedIndexedClassImpl
		implements CachedIndexedOwlNothing {

	private int positiveOccurrenceNo = 0;

	CachedIndexedOwlNothingImpl(ElkClass entity) {
		super(entity);
	}

	@Override
	public boolean occursPositively() {
		return positiveOccurrenceNo > 0;
	}

	boolean updateTotalOccurrenceNo(final ModifiableOntologyIndex index,
			int totalIncrement) {

		if (totalOccurrenceNo == 0 && totalIncrement > 0) {
			if (!ContradictionFromOwlNothingRule.addRuleFor(this, index)) {
				return false;
			}
		}
		totalOccurrenceNo += totalIncrement;

		if (totalOccurrenceNo == 0 && totalIncrement < 0) {
			if (!ContradictionFromOwlNothingRule.removeRuleFor(this, index)) {
				return false;
			}
		}
		return true;
	}

	boolean updatePositiveOccurrenceNo(int positiveIncrement) {

		positiveOccurrenceNo += positiveIncrement;

		return true;
	}

	@Override
	public final boolean updateOccurrenceNumbers(
			final ModifiableOntologyIndex index,
			OccurrenceIncrement increment) {

		if (!updateTotalOccurrenceNo(index, increment.totalIncrement)) {
			return false;
		}
		if (!updatePositiveOccurrenceNo(increment.positiveIncrement)) {
			// revert the changes
			updateTotalOccurrenceNo(index, -increment.positiveIncrement);
			return false;
		}

		return true;
	}

}
