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
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.modifiable.OccurrenceIncrement;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedObjectIntersectionOfVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ObjectIntersectionFromConjunctRule;

/**
 * Implements {@link CachedIndexedObjectIntersectionOf}
 * 
 * @author "Yevgeny Kazakov"
 */
class CachedIndexedObjectIntersectionOfImpl
		extends
		CachedIndexedComplexClassExpressionImpl<CachedIndexedObjectIntersectionOf>
		implements CachedIndexedObjectIntersectionOf {

	/**
	 * The conjunction has only two conjuncts. To ensure uniqueness of a
	 * conjunction for the conjuncts, the conjuncts are sorted according to the
	 * comparator of {@link IndexedClassExpression}. This is required for
	 * correct construction of {@link ObjectIntersectionFromConjunctRule}
	 * because conjunctions (A & B) and (B & A) result in the same rules.
	 */
	private final ModifiableIndexedClassExpression firstConjunct_,
			secondConjunct_;

	CachedIndexedObjectIntersectionOfImpl(
			ModifiableIndexedClassExpression conjunctA,
			ModifiableIndexedClassExpression conjunctB) {
		this(new Initializer(conjunctA, conjunctB));
	}

	private CachedIndexedObjectIntersectionOfImpl(Initializer init) {
		super(CachedIndexedObjectIntersectionOf.Helper.structuralHashCode(
				init.firstConjunct_, init.secondConjunct_));
		this.firstConjunct_ = init.firstConjunct_;
		this.secondConjunct_ = init.secondConjunct_;
	}

	private static class Initializer {
		private final ModifiableIndexedClassExpression firstConjunct_,
				secondConjunct_;

		Initializer(ModifiableIndexedClassExpression conjunctA,
				ModifiableIndexedClassExpression conjunctB) {
			if (conjunctA.compareTo(conjunctB) < 0) {
				this.firstConjunct_ = conjunctA;
				this.secondConjunct_ = conjunctB;
			} else {
				this.firstConjunct_ = conjunctB;
				this.secondConjunct_ = conjunctA;
			}
		}

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
			if (!ObjectIntersectionFromConjunctRule.addRulesFor(this, index)) {
				return false;
			}
		}

		positiveOccurrenceNo += increment.positiveIncrement;
		negativeOccurrenceNo += increment.negativeIncrement;

		checkOccurrenceNumbers();

		if (negativeOccurrenceNo == 0 && increment.negativeIncrement < 0) {
			if (!ObjectIntersectionFromConjunctRule.removeRulesFor(this, index)) {
				// revert all changes
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
