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

import java.util.Collection;
import java.util.Set;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.EmptyValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.NegativeSubsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.properties.SaturatedDataProperty;
import org.semanticweb.elk.reasoner.saturation.rules.DecompositionRuleApplicationVisitor;

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
			property.addNegativeDatatypeExpression(this);
		}

		positiveOccurrenceNo += positiveIncrement;
		negativeOccurrenceNo += negativeIncrement;

		checkOccurrenceNumbers();

		if (negativeOccurrenceNo == 0 && negativeIncrement < 0) {
			// no negative occurrences of this expression left
			property.removeNegativeDatatypeExpression(this);
		}
	}

	
	@Override
	public String toStringStructural() {
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

	public void applyRule(Context context, IndexedDatatypeExpression ide, BasicSaturationStateWriter writer) {
		IndexedDataProperty idp = ide.getProperty();
		ValueSpace vs = ide.getValueSpace();
		if (vs == EmptyValueSpace.INSTANCE) {
			// this means that value space is inconsistent; in this
			// case we are done
			writer.produce(context, Contradiction.getInstance());
		}
		SaturatedDataProperty saturatedDataProperty = idp.getSaturated();
		if (saturatedDataProperty != null) {
			for (IndexedDataProperty superProperty : saturatedDataProperty.getSuperProperties()) {
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
						writer.produce(context, new NegativeSubsumer(candidate));
					}
				}
			}
		}
	}

}
