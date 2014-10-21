package org.semanticweb.elk.reasoner.indexing.hierarchy;

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

import org.semanticweb.elk.owl.interfaces.ElkObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedObjectComplementOfVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ContradictionFromNegationRule;
import org.semanticweb.elk.util.logging.LogLevel;
import org.semanticweb.elk.util.logging.LoggerWrap;

/**
 * Represents all occurrences of an {@link ElkObjectComplementOf} in an
 * ontology.
 * 
 * @author "Yevgeny Kazakov"
 */
public class IndexedObjectComplementOf extends IndexedClassExpression {

	private final IndexedClassExpression negated_;

	protected IndexedObjectComplementOf(IndexedClassExpression negated) {
		this.negated_ = negated;
	}

	public IndexedClassExpression getNegated() {
		return negated_;
	}

	public <O> O accept(IndexedObjectComplementOfVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(IndexedClassExpressionVisitor<O> visitor) {
		return accept((IndexedObjectComplementOfVisitor<O>) visitor);
	}

	@Override
	boolean updateOccurrenceNumbers(ModifiableOntologyIndex index,
			int increment, int positiveIncrement, int negativeIncrement) {
		if (positiveOccurrenceNo == 0 && positiveIncrement > 0) {
			// first positive occurrence of this expression
			if (!ContradictionFromNegationRule.addRulesFor(this, index))
				return false;
		}

		if (negativeOccurrenceNo == 0 && negativeIncrement > 0) {
			// first negative occurrence of this expression
			if (LOGGER_.isWarnEnabled()) {
				LoggerWrap
						.log(LOGGER_,
								LogLevel.WARN,
								"reasoner.indexing.IndexedObjectComplementOf",
								"ELK does not support negative occurrences of ObjectComplementOf. Reasoning might be incomplete!");
			}
		}

		positiveOccurrenceNo += positiveIncrement;
		negativeOccurrenceNo += negativeIncrement;

		checkOccurrenceNumbers();

		if (positiveOccurrenceNo == 0 && positiveIncrement < 0) {
			// no positive occurrences of this expression left
			if (!ContradictionFromNegationRule.removeRulesFor(this, index)) {
				// revert all changes
				positiveOccurrenceNo -= positiveIncrement;
				negativeOccurrenceNo -= negativeIncrement;
				return false;
			}
		}
		return true;
	}

	@Override
	public String toStringStructural() {
		return "ObjectComplementOf(" + this.negated_ + ')';
	}

}
