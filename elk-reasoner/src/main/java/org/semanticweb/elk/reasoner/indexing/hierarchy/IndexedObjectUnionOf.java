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

import java.util.List;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkObjectUnionOf;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedObjectUnionOfVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ObjectUnionFromDisjunctRule;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.logging.LogLevel;
import org.semanticweb.elk.util.logging.LoggerWrap;

/**
 * Represents all occurrences of an {@link ElkObjectUnionOf} in an ontology.
 * 
 * @author "Yevgeny Kazakov"
 */
public class IndexedObjectUnionOf extends IndexedClassExpression {

	private final Set<IndexedClassExpression> disjuncts_;

	IndexedObjectUnionOf(List<IndexedClassExpression> disjuncts) {
		this.disjuncts_ = new ArrayHashSet<IndexedClassExpression>(2);
		for (IndexedClassExpression disjunct : disjuncts) {
			disjuncts_.add(disjunct);
		}
	}

	public Set<IndexedClassExpression> getDisjuncts() {
		return disjuncts_;
	}

	public <O> O accept(IndexedObjectUnionOfVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(IndexedClassExpressionVisitor<O> visitor) {
		return accept((IndexedObjectUnionOfVisitor<O>) visitor);
	}

	@Override
	void updateOccurrenceNumbers(ModifiableOntologyIndex index, int increment,
			int positiveIncrement, int negativeIncrement) {

		if (negativeOccurrenceNo == 0 && negativeIncrement > 0) {
			// first negative occurrence of this expression
			ObjectUnionFromDisjunctRule.addRulesFor(this, index);
		}

		if (positiveOccurrenceNo == 0 && positiveIncrement > 0) {
			// first positive occurrence of this expression
			if (LOGGER_.isWarnEnabled()) {
				LoggerWrap
						.log(LOGGER_,
								LogLevel.WARN,
								"reasoner.indexing.IndexedObjectUnionOf",
								"ELK does not support positive occurrences of ObjectUnionOf. Reasoning might be incomplete!");
			}
		}

		positiveOccurrenceNo += positiveIncrement;
		negativeOccurrenceNo += negativeIncrement;

		checkOccurrenceNumbers();

		if (negativeOccurrenceNo == 0 && negativeIncrement < 0) {
			// no negative occurrences of this expression left
			ObjectUnionFromDisjunctRule.removeRulesFor(this, index);
		}
	}

	@Override
	public String toStringStructural() {
		return "ObjectUnionOf(" + disjuncts_ + ')';
	}

}
