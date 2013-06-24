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
import org.semanticweb.elk.reasoner.datatypes.valuespaces.EmptyValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import static org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression.LOGGER_;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.NegativeSubsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.properties.SaturatedDataProperty;
import org.semanticweb.elk.reasoner.saturation.rules.DatatypeRule;
import org.semanticweb.elk.reasoner.saturation.rules.DecompositionRuleApplicationVisitor;
import org.semanticweb.elk.util.hashing.HashGenerator;

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
			index.add(property, new ThisDatatypeRule(this));
		}

		positiveOccurrenceNo += positiveIncrement;
		negativeOccurrenceNo += negativeIncrement;

		checkOccurrenceNumbers();

		if (negativeOccurrenceNo == 0 && negativeIncrement < 0) {
			// no negative occurrences of this expression left
			index.remove(property, new ThisDatatypeRule(this));
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

	public void applyRule(Context context, BasicSaturationStateWriter writer) {
		IndexedDataProperty idp = getProperty();
		ValueSpace vs = getValueSpace();
		if (vs == EmptyValueSpace.INSTANCE) {
			// this means that value space is inconsistent; in this
			// case we are done
			writer.produce(context, Contradiction.getInstance());
		}
		SaturatedDataProperty saturatedDataProperty = idp.getSaturated();
		if (saturatedDataProperty != null) {
			for (IndexedDataProperty superProperty : 
					saturatedDataProperty.getSuperProperties()) {
				Collection<DatatypeRule> assosiatedDatatypeRules = 
					superProperty.getAssosiatedDatatypeRules(this);
				if (assosiatedDatatypeRules != null) {
					for (DatatypeRule<Context> datatypeRule : assosiatedDatatypeRules) {
						datatypeRule.apply(writer, this, context);
					}
				}
			}
		}
	}
	

	public static class ThisDatatypeRule implements DatatypeRule<Context> {

		private static final String NAME = "Datatype Expression Composition";
		
		private final IndexedDatatypeExpression negExistential_;

		ThisDatatypeRule(IndexedDatatypeExpression negExistential) {
			this.negExistential_ = negExistential;
			hashCode_ = HashGenerator.combinedHashCode(ThisDatatypeRule.class, negExistential_);
		}

		@Override
		public String getName() {
			return NAME;
		}

		@Override
		public ValueSpace getValueSpace() {
			return negExistential_.getValueSpace();
		}

		public IndexedDatatypeExpression getNegExistential() {
			return negExistential_;
		}

		@Override
		public void apply(BasicSaturationStateWriter writer, IndexedDatatypeExpression datatypeExpression, Context context) {
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace("Applying " + NAME + " to " + context + " [" + datatypeExpression + "]");
			}
			if (negExistential_ != datatypeExpression) {
				writer.produce(context, new NegativeSubsumer(negExistential_));
			}
		}
		
		private final int hashCode_;

		@Override
		public final int hashCode() {
			return hashCode_;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final ThisDatatypeRule other = (ThisDatatypeRule) obj;
			if (this.negExistential_ != other.negExistential_ && (this.negExistential_ == null || !this.negExistential_.equals(other.negExistential_))) {
				return false;
			}
			return true;
		}
		
	}
}
