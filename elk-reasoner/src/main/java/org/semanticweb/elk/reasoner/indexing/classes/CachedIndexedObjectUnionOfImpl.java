/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.elk.reasoner.completeness.Feature;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedObjectUnionOf;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.model.OccurrenceIncrement;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ObjectUnionFromDisjunctRule;

/**
 * Implements {@link CachedIndexedObjectUnionOf}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
class CachedIndexedObjectUnionOfImpl extends
		CachedIndexedComplexClassExpressionImpl<CachedIndexedObjectUnionOf>
		implements CachedIndexedObjectUnionOf {

	private final List<ModifiableIndexedClassExpression> disjuncts_;

	CachedIndexedObjectUnionOfImpl(
			List<? extends ModifiableIndexedClassExpression> disjuncts) {
		this(new Initializer(disjuncts));
	}

	private CachedIndexedObjectUnionOfImpl(Initializer init) {
		super(CachedIndexedObjectUnionOf.Helper
				.structuralHashCode(init.disjuncts_));
		this.disjuncts_ = init.disjuncts_;

	}

	private static class Initializer {
		private final List<ModifiableIndexedClassExpression> disjuncts_;

		Initializer(
				List<? extends ModifiableIndexedClassExpression> disjuncts) {
			this.disjuncts_ = new ArrayList<ModifiableIndexedClassExpression>(
					2);
			for (ModifiableIndexedClassExpression disjunct : disjuncts) {
				this.disjuncts_.add(disjunct);
			}
		}
	}

	@Override
	public final List<ModifiableIndexedClassExpression> getDisjuncts() {
		return disjuncts_;
	}

	@Override
	public final CachedIndexedObjectUnionOf structuralEquals(Object other) {
		return CachedIndexedObjectUnionOf.Helper.structuralEquals(this, other);
	}

	@Override
	public final boolean updateOccurrenceNumbers(ModifiableOntologyIndex index,
			OccurrenceIncrement increment) {

		if (negativeOccurrenceNo == 0 && increment.negativeIncrement > 0) {
			// first negative occurrence of this expression
			if (!ObjectUnionFromDisjunctRule.addRulesFor(this, index))
				return false;
		}

		positiveOccurrenceNo += increment.positiveIncrement;
		negativeOccurrenceNo += increment.negativeIncrement;

		checkOccurrenceNumbers();

		if (negativeOccurrenceNo == 0 && increment.negativeIncrement < 0) {
			// no negative occurrences of this expression left
			if (!ObjectUnionFromDisjunctRule.removeRulesFor(this, index)) {
				// revert all changes
				positiveOccurrenceNo -= increment.positiveIncrement;
				negativeOccurrenceNo -= increment.negativeIncrement;
				return false;
			}
		}
		
		// positive occurrences are unsupported
		index.occurrenceChanged(
				Feature.OBJECT_UNION_OF_POSITIVE,
				increment.positiveIncrement);

		return true;
	}

	@Override
	public final <O> O accept(IndexedClassExpression.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public CachedIndexedObjectUnionOf accept(
			CachedIndexedClassExpression.Filter filter) {
		return filter.filter(this);
	}

}
