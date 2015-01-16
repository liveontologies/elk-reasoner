package org.semanticweb.elk.reasoner.indexing.implementation;

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

import java.util.List;
import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedClassExpressionFilter;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedObjectUnionOf;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.modifiable.OccurrenceIncrement;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedObjectUnionOfVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ObjectUnionFromDisjunctRule;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.logging.LogLevel;
import org.semanticweb.elk.util.logging.LoggerWrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements {@link CachedIndexedObjectUnionOf}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
class CachedIndexedObjectUnionOfImpl extends
		CachedIndexedComplexClassExpressionImpl<CachedIndexedObjectUnionOf>
		implements CachedIndexedObjectUnionOf {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(CachedIndexedObjectUnionOfImpl.class);

	private final Set<ModifiableIndexedClassExpression> disjuncts_;

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
		private final Set<ModifiableIndexedClassExpression> disjuncts_;

		Initializer(List<? extends ModifiableIndexedClassExpression> disjuncts) {
			this.disjuncts_ = new ArrayHashSet<ModifiableIndexedClassExpression>(
					2);
			for (ModifiableIndexedClassExpression disjunct : disjuncts) {
				this.disjuncts_.add(disjunct);
			}
		}
	}

	@Override
	public final Set<ModifiableIndexedClassExpression> getDisjuncts() {
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

		if (positiveOccurrenceNo == 0 && increment.positiveIncrement > 0) {
			// first positive occurrence of this expression
			if (LOGGER_.isWarnEnabled()) {
				LoggerWrap
						.log(LOGGER_,
								LogLevel.WARN,
								"reasoner.indexing.IndexedObjectUnionOf",
								"ELK does not support positive occurrences of ObjectUnionOf. Reasoning might be incomplete!");
			}
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
		return true;
	}

	@Override
	public final String toStringStructural() {
		return "ObjectUnionOf(" + disjuncts_ + ')';
	}

	@Override
	public final <O> O accept(IndexedObjectUnionOfVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public final <O> O accept(IndexedClassExpressionVisitor<O> visitor) {
		return accept((IndexedObjectUnionOfVisitor<O>) visitor);
	}

	@Override
	public CachedIndexedObjectUnionOf accept(
			CachedIndexedClassExpressionFilter filter) {
		return filter.filter(this);
	}

}
