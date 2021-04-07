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
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedComplexClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedComplexClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedRangeFiller;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.model.OccurrenceIncrement;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.PropagationFromExistentialFillerRule;

/**
 * Implements {@link CachedIndexedObjectSomeValuesFrom}
 * 
 * @author "Yevgeny Kazakov"
 */
class CachedIndexedObjectSomeValuesFromImpl
		extends CachedIndexedComplexClassExpressionImpl
		implements CachedIndexedObjectSomeValuesFrom {

	private final ModifiableIndexedRangeFillerImpl rangeFiller_;

	CachedIndexedObjectSomeValuesFromImpl(
			ModifiableIndexedObjectProperty property,
			ModifiableIndexedClassExpression filler) {
		super(CachedIndexedObjectSomeValuesFrom.structuralHashCode(
				property, filler));
		this.rangeFiller_ = new ModifiableIndexedRangeFillerImpl(property,
				filler);
	}

	@Override
	public final ModifiableIndexedObjectProperty getProperty() {
		return rangeFiller_.getProperty();
	}

	@Override
	public final ModifiableIndexedClassExpression getFiller() {
		return rangeFiller_.getFiller();
	}

	@Override
	public ModifiableIndexedRangeFiller getRangeFiller() {
		return rangeFiller_;
	}

	@Override
	public RevertibleAction getIndexingAction(ModifiableOntologyIndex index,
			OccurrenceIncrement increment) {
		return RevertibleAction
				.create(() -> negativeOccurrenceNo == 0
						&& increment.negativeIncrement > 0,
						() -> PropagationFromExistentialFillerRule
								.addRuleFor(this, index),
						() -> PropagationFromExistentialFillerRule
								.removeRuleFor(this, index))
				.then(super.getIndexingAction(index, increment))
				.then(RevertibleAction.create(
						() -> negativeOccurrenceNo == 0
								&& increment.negativeIncrement < 0,
						() -> PropagationFromExistentialFillerRule
								.removeRuleFor(this, index),
						() -> PropagationFromExistentialFillerRule
								.addRuleFor(this, index)));
	}

	@Override
	public final <O> O accept(IndexedComplexClassExpression.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public final CachedIndexedObjectSomeValuesFrom accept(
			CachedIndexedComplexClassExpression.Filter filter) {
		return filter.filter(this);
	}

}
