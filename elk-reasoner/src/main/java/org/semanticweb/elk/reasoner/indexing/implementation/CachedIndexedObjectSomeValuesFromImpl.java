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
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedFiller;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.modifiable.OccurrenceIncrement;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedContextRootVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedFillerVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedObjectSomeValuesFromVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedObjectVisitor;
import org.semanticweb.elk.reasoner.saturation.ExtendedContext;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.PropagationFromExistentialFillerRule;

/**
 * Implements {@link CachedIndexedObjectSomeValuesFrom}
 * 
 * @author "Yevgeny Kazakov"
 */
class CachedIndexedObjectSomeValuesFromImpl
		extends
		CachedIndexedComplexClassExpressionImpl<CachedIndexedObjectSomeValuesFrom>
		implements CachedIndexedObjectSomeValuesFrom {

	private final ModifiableIndexedFillerImpl filler_;

	CachedIndexedObjectSomeValuesFromImpl(
			ModifiableIndexedObjectProperty property,
			ModifiableIndexedClassExpression fillerConcept) {
		super(CachedIndexedObjectSomeValuesFrom.Helper.structuralHashCode(
				property, fillerConcept));
		this.filler_ = new ModifiableIndexedFillerImpl(property, fillerConcept);
	}

	@Override
	public final ModifiableIndexedObjectProperty getProperty() {
		return filler_.property_;
	}

	@Override
	public final ModifiableIndexedClassExpression getFillerConcept() {
		return filler_.fillerConcept_;
	}

	@Override
	public ModifiableIndexedFiller getFiller() {
		return filler_;
	}

	@Override
	public final CachedIndexedObjectSomeValuesFrom structuralEquals(Object other) {
		return CachedIndexedObjectSomeValuesFrom.Helper.structuralEquals(this,
				other);
	}

	@Override
	public final boolean updateOccurrenceNumbers(ModifiableOntologyIndex index,
			OccurrenceIncrement increment) {

		if (negativeOccurrenceNo == 0 && increment.negativeIncrement > 0) {
			// first negative occurrence of this expression
			if (!PropagationFromExistentialFillerRule.addRuleFor(this, index))
				return false;
		}

		negativeOccurrenceNo += increment.negativeIncrement;

		if (negativeOccurrenceNo == 0 && increment.negativeIncrement < 0) {
			// no negative occurrences of this expression left
			if (!PropagationFromExistentialFillerRule
					.removeRuleFor(this, index)) {
				// revert the changes
				negativeOccurrenceNo -= increment.negativeIncrement;
				return false;
			}
		}
		positiveOccurrenceNo += increment.positiveIncrement;
		return true;

	}

	@Override
	public final String toStringStructural() {
		return "ObjectSomeValuesFrom(" + this.filler_.property_ + ' '
				+ this.filler_.fillerConcept_ + ')';
	}

	@Override
	public final <O> O accept(IndexedObjectSomeValuesFromVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public final <O> O accept(IndexedClassExpressionVisitor<O> visitor) {
		return accept((IndexedObjectSomeValuesFromVisitor<O>) visitor);
	}

	@Override
	public CachedIndexedObjectSomeValuesFrom accept(
			CachedIndexedClassExpressionFilter filter) {
		return filter.filter(this);
	}

	private static class ModifiableIndexedFillerImpl implements
			ModifiableIndexedFiller {

		private final ModifiableIndexedObjectProperty property_;

		private final ModifiableIndexedClassExpression fillerConcept_;

		private volatile ExtendedContext context_ = null;

		ModifiableIndexedFillerImpl(ModifiableIndexedObjectProperty property,
				ModifiableIndexedClassExpression fillerConcept) {
			this.property_ = property;
			this.fillerConcept_ = fillerConcept;
		}

		@Override
		public ModifiableIndexedObjectProperty getProperty() {
			return this.property_;
		}

		@Override
		public ModifiableIndexedClassExpression getFillerConcept() {
			return this.fillerConcept_;
		}

		@Override
		public String toStringStructural() {
			return "ObjectIntersectionOf(" + this.fillerConcept_ + ' '
					+ "ObjectSomeValuesFrom(ObjectInverseOf(" + property_ + ')'
					+ " owl:Thing)";
		}

		@Override
		public final ExtendedContext getContext() {
			return this.context_;
		}

		@Override
		public final synchronized ExtendedContext setContextIfAbsent(
				ExtendedContext context) {
			if (context_ != null)
				return context_;
			// else
			context_ = context;
			return null;
		}

		@Override
		public final synchronized void resetContext() {
			context_ = null;
		}

		@Override
		public <O> O accept(IndexedFillerVisitor<O> visitor) {
			return visitor.visit(this);
		}

		@Override
		public <O> O accept(IndexedContextRootVisitor<O> visitor) {
			return accept((IndexedFillerVisitor<O>) visitor);
		}

		@Override
		public <O> O accept(IndexedObjectVisitor<O> visitor) {
			return accept((IndexedContextRootVisitor<O>) visitor);
		}

	}

}
