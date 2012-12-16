/*
 * #%L
 * ELK Reasoner
 * *
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import org.semanticweb.elk.reasoner.datatypes.valuespaces.EmptyValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedDatatypeExpressionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.NegativeSuperClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.PositiveSuperClassExpression;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.RuleEngine;
import org.semanticweb.elk.util.logging.CachedTimeThread;

/**
 *
 * @author Pospishnyi Olexandr
 * @author "Yevgeny Kazakov"
 */
public class IndexedDatatypeExpression extends IndexedClassExpression {

	protected final IndexedDataProperty property;

	protected final ValueSpace valueSpace;

	public IndexedDatatypeExpression(IndexedDataProperty property,
			ValueSpace valueSpace) {
		this.property = property;
		this.valueSpace = valueSpace;
	}

	public IndexedDataProperty getProperty() {
		return this.property;
	}

	public ValueSpace getValueSpace() {
		return this.valueSpace;
	}

	@Override
	protected void updateOccurrenceNumbers(int increment,
			int positiveIncrement, int negativeIncrement) {
		if (negativeOccurrenceNo == 0 && negativeIncrement > 0) {
			// first negative occurrence of this expression
			property.addNegativeDatatypeExpression(this);
		}

		positiveOccurrenceNo += positiveIncrement;
		negativeOccurrenceNo += negativeIncrement;

		if (negativeOccurrenceNo == 0 && negativeIncrement < 0) {
			// no negative occurrences of this expression left
			property.removeNegativeDatatypeExpression(this);
		}
	}

	public <O> O accept(IndexedDatatypeExpressionVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(IndexedClassExpressionVisitor<O> visitor) {
		return accept((IndexedDatatypeExpressionVisitor<O>) visitor);
	}

	@Override
	public String toString() {
		switch (valueSpace.getType()) {
			case BINARY_VALUE:
			case DATETIME_VALUE:
			case LITERAL_VALUE:
			case NUMERIC_VALUE:
				return "DataHasValue(" + valueSpace.toString() + ")";
			case DATETIME_INTERVAL:
			case LENGTH_RESTRICTED:
			case NUMERIC_INTERVAL:
			case PATTERN:
			case ENTIRE:
			case EMPTY:
				return "DataSomeValuesFrom(" + valueSpace.toString() + ")";
			default:
				return null;
		}
	}

	@Override
	public void applyDecompositionRule(RuleEngine ruleEngine, Context context) {
		RuleStatistics stats = ruleEngine.getRulesTimer();

		stats.timeDatatypeExpressionDecompositionRule -= CachedTimeThread.currentTimeMillis;
		stats.countDatatypeExpressionDecompositionRule++;

		try {
			IndexedDataProperty idp = getProperty();
			ValueSpace vs = getValueSpace();
			if (vs == EmptyValueSpace.INSTANCE) {
				// this means that value space is inconsistent; in this
				// case we are done
				ruleEngine.produce(context, new PositiveSuperClassExpression(
						ruleEngine.getOwlNothing()));
			}
			for (IndexedDataProperty superProperty : idp.getSaturated()
					.getSuperProperties()) {
				Iterable<IndexedDatatypeExpression> negativeDatatypeExpressions = superProperty
						.getNegativeDatatypeExpressions();
				if (negativeDatatypeExpressions == null) {
					continue;
				}
				for (IndexedDatatypeExpression candidate : negativeDatatypeExpressions) {
					if (candidate == this) // already derived
					{
						continue;
					}
					// check if the candidate value space subsumes the current
					// value space
					if (vs.isSubsumedBy(candidate.getValueSpace())) {
						ruleEngine.produce(context,
								// no decomposition rule should be applied to the result
								new NegativeSuperClassExpression(candidate));
					}
				}
			}
		} finally {
			stats.timeDatatypeExpressionDecompositionRule += CachedTimeThread.currentTimeMillis;
		}
	}

}
