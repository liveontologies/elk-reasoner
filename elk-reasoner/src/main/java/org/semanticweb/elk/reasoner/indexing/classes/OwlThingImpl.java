package org.semanticweb.elk.reasoner.indexing.classes;

import org.semanticweb.elk.RevertibleAction;

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
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.model.OccurrenceIncrement;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.OwlThingContextInitRule;

/**
 * Represents {@code owl:Thing}.
 * 
 * @author Yevgeny Kazakov
 * 
 */
final class OwlThingImpl extends StructuralIndexedClassEntryImpl {

	OwlThingImpl(ElkClass entity) {
		super(entity);
	}

	@Override
	public RevertibleAction getIndexingAction(ModifiableOntologyIndex index,
			OccurrenceIncrement increment) {
		return RevertibleAction.create(
				() -> negativeOccurrenceNo == 0
						&& increment.negativeIncrement > 0,
				() -> OwlThingContextInitRule.addRuleFor(this, index),
				() -> OwlThingContextInitRule.removeRuleFor(this, index))
				.then(super.getIndexingAction(index, increment))
				.then(RevertibleAction.create(
						() -> negativeOccurrenceNo == 0
								&& increment.negativeIncrement < 0,
						() -> OwlThingContextInitRule.removeRuleFor(this,
								index),
						() -> OwlThingContextInitRule.addRuleFor(this, index)));
	}

}
