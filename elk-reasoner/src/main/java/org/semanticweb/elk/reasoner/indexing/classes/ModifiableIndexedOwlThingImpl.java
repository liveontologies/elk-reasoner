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
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkUnexpectedIndexingException;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassEntity;
import org.semanticweb.elk.reasoner.indexing.model.IndexedPredefinedClass;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.model.OccurrenceIncrement;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.OwlThingContextInitRule;

/**
 * Represents {@code owl:Thing}.
 * 
 * @author Yevgeny Kazakov
 * 
 */
final class ModifiableIndexedOwlThingImpl
		extends StructuralIndexedClassEntryImpl
		implements ModifiableIndexedClass, IndexedPredefinedClass {

	/**
	 * Some rules depend on whether {@code owl:Thing} occur negatively
	 */
	private int negativeOccurrenceNo_ = 0;

	ModifiableIndexedOwlThingImpl(ElkClass owlThing) {
		super(owlThing);
	}

	public boolean occursNegatively() {
		return negativeOccurrenceNo_ > 0;
	}

	@Override
	public String printOccurrenceNumbers() {
		return super.printOccurrenceNumbers() + "; neg="
				+ negativeOccurrenceNo_;
	}

	@Override
	void checkOccurrenceNumbers() {
		super.checkOccurrenceNumbers();
		if (negativeOccurrenceNo_ < 0)
			throw new ElkUnexpectedIndexingException(
					toString() + " has a negative occurrence: "
							+ printOccurrenceNumbers());
	}

	@Override
	public RevertibleAction getIndexingAction(ModifiableOntologyIndex index,
			OccurrenceIncrement increment) {
		return RevertibleAction.create(
				() -> !occursNegatively() && increment.negativeIncrement > 0,
				() -> OwlThingContextInitRule.addRuleFor(this, index),
				() -> OwlThingContextInitRule.removeRuleFor(this, index))
				.then(RevertibleAction.create(() -> {
					negativeOccurrenceNo_ += increment.negativeIncrement;
					return true;
				}, () -> {
					negativeOccurrenceNo_ -= increment.negativeIncrement;
				})).then(super.getIndexingAction(index, increment))
				.then(RevertibleAction.create(
						() -> !occursNegatively()
								&& increment.negativeIncrement < 0,
						() -> OwlThingContextInitRule.removeRuleFor(this,
								index),
						() -> OwlThingContextInitRule.addRuleFor(this, index)));
	}

	@Override
	public <O> O accept(IndexedClassEntity.Visitor<O> visitor) {
		return visitor.visit(this);
	}

}
