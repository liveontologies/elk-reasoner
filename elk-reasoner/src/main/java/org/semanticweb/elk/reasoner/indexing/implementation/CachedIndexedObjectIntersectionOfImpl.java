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
package org.semanticweb.elk.reasoner.indexing.implementation;

import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedClassExpressionFilter;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.modifiable.OccurrenceIncrement;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedObjectIntersectionOfVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ObjectIntersectionFromFirstConjunctRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ObjectIntersectionFromSecondConjunctRule;

/**
 * Implements {@link CachedIndexedObjectIntersectionOf}
 * 
 * @author "Yevgeny Kazakov"
 */
class CachedIndexedObjectIntersectionOfImpl
		extends
		CachedIndexedComplexClassExpressionImpl<CachedIndexedObjectIntersectionOf>
		implements CachedIndexedObjectIntersectionOf {

	private final ModifiableIndexedClassExpression firstConjunct_,
			secondConjunct_;

	CachedIndexedObjectIntersectionOfImpl(
			ModifiableIndexedClassExpression firstConjunct,
			ModifiableIndexedClassExpression secondConjunct) {
		super(CachedIndexedObjectIntersectionOf.Helper.structuralHashCode(
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
	public final CachedIndexedObjectIntersectionOf structuralEquals(Object other) {
		return CachedIndexedObjectIntersectionOf.Helper.structuralEquals(this,
				other);
	}

	@Override
	public final boolean updateOccurrenceNumbers(ModifiableOntologyIndex index,
			OccurrenceIncrement increment) {

		if (negativeOccurrenceNo == 0 && increment.negativeIncrement > 0) {
			if (!ObjectIntersectionFromFirstConjunctRule.addRulesFor(this,
					index)) {
				return false;
			}
			if (!ObjectIntersectionFromSecondConjunctRule.addRulesFor(this,
					index)) {
				// revert all changes
				ObjectIntersectionFromFirstConjunctRule.removeRulesFor(this,
						index);
				return false;
			}
		}

		positiveOccurrenceNo += increment.positiveIncrement;
		negativeOccurrenceNo += increment.negativeIncrement;

		checkOccurrenceNumbers();

		if (negativeOccurrenceNo == 0 && increment.negativeIncrement < 0) {
			if (!ObjectIntersectionFromFirstConjunctRule.removeRulesFor(this,
					index)) {
				// revert all changes
				positiveOccurrenceNo -= increment.positiveIncrement;
				negativeOccurrenceNo -= increment.negativeIncrement;
				return false;
			}
			if (!ObjectIntersectionFromSecondConjunctRule.removeRulesFor(this,
					index)) {
				// revert all changes
				ObjectIntersectionFromFirstConjunctRule
						.addRulesFor(this, index);
				positiveOccurrenceNo -= increment.positiveIncrement;
				negativeOccurrenceNo -= increment.negativeIncrement;
				return false;
			}
		}
		return true;
	}

	@Override
	public final String toStringStructural() {
		return "ObjectIntersectionOf(" + this.firstConjunct_ + ' '
				+ this.secondConjunct_ + ')';
	}

	@Override
	public final <O> O accept(IndexedObjectIntersectionOfVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public final <O> O accept(IndexedClassExpressionVisitor<O> visitor) {
		return accept((IndexedObjectIntersectionOfVisitor<O>) visitor);
	}

	@Override
	public CachedIndexedObjectIntersectionOf accept(
			CachedIndexedClassExpressionFilter filter) {
		return filter.filter(this);
	}

}
