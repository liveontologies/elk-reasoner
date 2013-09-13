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

import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.DecompositionRuleApplicationVisitor;

/**
 * TODO: documentation
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
	public boolean isDatatypeExpression() {
		return true;
	}

	@Override
	public <O> O accept(IndexedClassExpressionVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public void accept(DecompositionRuleApplicationVisitor visitor, Context context) {
		visitor.visit(this, context);
	}

	@Override
	void updateOccurrenceNumbers(ModifiableOntologyIndex index, int increment, int positiveIncrement, int negativeIncrement) {
		if (negativeOccurrenceNo == 0 && negativeIncrement > 0) {
			// first negative occurrence of this expression
			index.add(property, this);
		}

		positiveOccurrenceNo += positiveIncrement;
		negativeOccurrenceNo += negativeIncrement;

		checkOccurrenceNumbers();

		if (negativeOccurrenceNo == 0 && negativeIncrement < 0) {
			// no negative occurrences of this expression left
			index.remove(property, this);
		}
	}


	@Override
	public String toStringStructural() {
		// TODO: complete all cases
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
	
}
