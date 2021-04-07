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
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.model.IndexedComplexClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.model.OccurrenceIncrement;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ObjectIntersectionFromFirstConjunctRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ObjectIntersectionFromSecondConjunctRule;

/**
 * Implements {@link CachedIndexedObjectIntersectionOf}
 * 
 * @author "Yevgeny Kazakov"
 */
class CachedIndexedObjectIntersectionOfImpl
		extends CachedIndexedComplexClassExpressionImpl
		implements CachedIndexedObjectIntersectionOf {

	private final ModifiableIndexedClassExpression firstConjunct_,
			secondConjunct_;

	CachedIndexedObjectIntersectionOfImpl(
			ModifiableIndexedClassExpression firstConjunct,
			ModifiableIndexedClassExpression secondConjunct) {
		super(CachedIndexedObjectIntersectionOf.structuralHashCode(
				firstConjunct, secondConjunct));
		this.firstConjunct_ = firstConjunct;
		this.secondConjunct_ = secondConjunct;
	}

	@Override
	public final ModifiableIndexedClassExpression getFirstConjunct() {
		return firstConjunct_;
	}

	@Override
	public final ModifiableIndexedClassExpression getSecondConjunct() {
		return secondConjunct_;
	}

	@Override
	public RevertibleAction getIndexingAction(ModifiableOntologyIndex index,
			OccurrenceIncrement increment) {
		return RevertibleAction
				.create(() -> negativeOccurrenceNo == 0
						&& increment.negativeIncrement > 0,
						() -> ObjectIntersectionFromFirstConjunctRule
								.addRulesFor(this, index),
						() -> ObjectIntersectionFromFirstConjunctRule
								.removeRulesFor(this, index))
				.then(RevertibleAction.create(
						() -> negativeOccurrenceNo == 0
								&& increment.negativeIncrement > 0,
						() -> ObjectIntersectionFromSecondConjunctRule
								.addRulesFor(this, index),
						() -> ObjectIntersectionFromSecondConjunctRule
								.removeRulesFor(this, index)))
				.then(super.getIndexingAction(index, increment))
				.then(RevertibleAction.create(
						() -> negativeOccurrenceNo == 0
								&& increment.negativeIncrement < 0,
						() -> ObjectIntersectionFromFirstConjunctRule
								.removeRulesFor(this, index),
						() -> ObjectIntersectionFromFirstConjunctRule
								.addRulesFor(this, index)))
				.then(RevertibleAction.create(
						() -> negativeOccurrenceNo == 0
								&& increment.negativeIncrement < 0,
						() -> ObjectIntersectionFromSecondConjunctRule
								.removeRulesFor(this, index),
						() -> ObjectIntersectionFromSecondConjunctRule
								.addRulesFor(this, index)));

	}

	@Override
	public final <O> O accept(IndexedComplexClassExpression.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public final CachedIndexedObjectIntersectionOf accept(
			CachedIndexedComplexClassExpression.Filter filter) {
		return filter.filter(this);
	}

}
