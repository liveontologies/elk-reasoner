package org.semanticweb.elk.reasoner.indexing.classes;

import java.util.ArrayList;
import java.util.List;

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
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedOwlThing;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.model.OccurrenceIncrement;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.OwlThingContextInitRule;

/**
 * Implements {@link CachedIndexedOwlThing}
 * 
 * @author Yevgeny Kazakov
 * 
 */
final class CachedIndexedOwlThingImpl extends CachedIndexedClassImpl
		implements CachedIndexedOwlThing {

	private int negativeOccurrenceNo = 0;

	private final List<CachedIndexedOwlThing.ChangeListener> listeners_;

	CachedIndexedOwlThingImpl(ElkClass entity) {
		super(entity);
		this.listeners_ = new ArrayList<CachedIndexedOwlThing.ChangeListener>();
	}

	@Override
	public boolean occursNegatively() {
		return negativeOccurrenceNo > 0;
	}

	boolean updateNegativeOccurrenceNo(final ModifiableOntologyIndex index,
			int negativeIncrement) {

		negativeOccurrenceNo += negativeIncrement;

		if (negativeOccurrenceNo > 0) {
			if (negativeOccurrenceNo <= negativeIncrement) {
				// positiveOccurrenceNo just became > 0
				if (!OwlThingContextInitRule.addRuleFor(this, index)) {
					// revert the changes
					negativeOccurrenceNo -= negativeIncrement;
					return false;
				}
				// else
				for (int i = 0; i < listeners_.size(); i++) {
					listeners_.get(i).negativeOccurrenceAppeared();
				}
			}
		} else {
			if (negativeOccurrenceNo > negativeIncrement) {
				// positiveOccurrenceNo just became <= 0
				if (!OwlThingContextInitRule.removeRuleFor(this, index)) {
					// revert the changes
					negativeOccurrenceNo -= negativeIncrement;
					return false;
				}
				// else
				for (int i = 0; i < listeners_.size(); i++) {
					listeners_.get(i).negativeOccurrenceDisappeared();
				}
			}
		}

		return true;
	}

	@Override
	public final boolean updateOccurrenceNumbers(
			final ModifiableOntologyIndex index,
			OccurrenceIncrement increment) {

		if (!updateNegativeOccurrenceNo(index, increment.negativeIncrement)) {
			return false;
		}

		if (!super.updateOccurrenceNumbers(index, increment)) {
			// revert the changes
			updateNegativeOccurrenceNo(index, -increment.negativeIncrement);
			return false;
		}

		return true;
	}

	@Override
	public boolean addListener(ChangeListener listener) {
		return listeners_.add(listener);
	}

	@Override
	public boolean removeListener(ChangeListener listener) {
		return listeners_.remove(listener);
	}

}
