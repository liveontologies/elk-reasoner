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

import org.semanticweb.elk.owl.interfaces.ElkDataSomeValuesFrom;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.DecompositionRuleApplicationVisitor;

/**
 * Represents all occurrences of an {@link ElkDataSomeValuesFrom} in the
 * ontology.
 * 
 * @author Pospishnyi Olexandr
 * @author "Yevgeny Kazakov"
 */
public class IndexedDatatypeExpression extends IndexedClassExpression {

	private final IndexedDataProperty property_;

	private final ValueSpace<?> valueSpace_;

	public IndexedDatatypeExpression(IndexedDataProperty property,
			ValueSpace<?> valueSpace) {
		this.property_ = property;
		this.valueSpace_ = valueSpace;
	}

	public IndexedDataProperty getProperty() {
		return this.property_;
	}

	public ValueSpace<?> getValueSpace() {
		return this.valueSpace_;
	}

	@Override
	public <O> O accept(IndexedClassExpressionVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public void accept(DecompositionRuleApplicationVisitor visitor,
			Context context) {
		visitor.visit(this, context);
	}

	@Override
	void updateOccurrenceNumbers(ModifiableOntologyIndex index, int increment,
			int positiveIncrement, int negativeIncrement) {
		if (negativeOccurrenceNo == 0 && negativeIncrement > 0) {
			// first negative occurrence of this expression
			index.add(property_, this);
		}

		positiveOccurrenceNo += positiveIncrement;
		negativeOccurrenceNo += negativeIncrement;

		checkOccurrenceNumbers();

		if (negativeOccurrenceNo == 0 && negativeIncrement < 0) {
			// no negative occurrences of this expression left
			index.remove(property_, this);
		}
	}

	@Override
	public String toStringStructural() {
		return "DataSomeValuesFrom( " + property_.toString()  + " " +  valueSpace_.toString() + " )";
	}

}
