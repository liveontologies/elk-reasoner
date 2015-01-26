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

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedClass;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedClassExpressionFilter;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.modifiable.OccurrenceIncrement;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassEntityVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedEntityVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.OwlThingContextInitRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ContradictionFromOwlNothingRule;

/**
 * Implements an equality view for instances of {@link IndexedClass}
 * 
 * @author "Yevgeny Kazakov"
 */
final class CachedIndexedClassImpl extends
		CachedIndexedClassEntityImpl<CachedIndexedClass> implements
		CachedIndexedClass {

	/**
	 * The represented {@link ElkClass}
	 */
	private final ElkClass elkClass_;

	/**
	 * The equivalent {@link ModifiableIndexedClassExpression} if there exists
	 * one or {@code null} otherwise
	 */
	private ModifiableIndexedClassExpression definition_;

	CachedIndexedClassImpl(ElkClass entity) {
		super(CachedIndexedClass.Helper.structuralHashCode(entity));
		elkClass_ = entity;
	}

	@Override
	public final ElkClass getElkEntity() {
		return elkClass_;
	}

	@Override
	public IndexedClassExpression getDefinition() {
		return this.definition_;
	}

	@Override
	public boolean setDefinition(ModifiableIndexedClassExpression definition) {
		if (definition_ != null)
			return false;
		// else
		this.definition_ = definition;
		return true;
	}

	@Override
	public void removeDefinition() {
		this.definition_ = null;
	}

	@Override
	public final CachedIndexedClass structuralEquals(Object other) {
		return CachedIndexedClass.Helper.structuralEquals(this, other);
	}

	boolean updateTotalOccurrenceNo(final ModifiableOntologyIndex index,
			int totalIncrement) {

		if (totalOccurrenceNo == 0 && totalIncrement > 0) {
			if (elkClass_ == PredefinedElkClass.OWL_NOTHING
					&& !ContradictionFromOwlNothingRule.addRuleFor(this, index)) {
				return false;
			}
		}
		totalOccurrenceNo += totalIncrement;

		if (totalOccurrenceNo == 0 && totalIncrement < 0) {
			if (elkClass_ == PredefinedElkClass.OWL_NOTHING
					&& !ContradictionFromOwlNothingRule.removeRuleFor(this,
							index)) {
				return false;
			}
		}
		return true;
	}

	boolean updateNegativeOccurrenceNo(final ModifiableOntologyIndex index,
			int negativeIncrement) {
		if (elkClass_ != PredefinedElkClass.OWL_THING)
			return true;

		if (!index.hasNegativeOwlThing() && negativeIncrement > 0) {
			if (!OwlThingContextInitRule.addRuleFor(this, index)) {
				return false;
			}
		}

		index.updateNegativeOwlThingOccurrenceNo(negativeIncrement);

		if (!index.hasNegativeOwlThing() && negativeIncrement < 0) {
			if (!OwlThingContextInitRule.removeRuleFor(this, index)) {
				// revert the changes
				index.updateNegativeOwlThingOccurrenceNo(-negativeIncrement);
				return false;
			}
		}
		return true;
	}

	boolean updatePositiveOccurrenceNo(final ModifiableOntologyIndex index,
			int positiveIncrement) {
		if (elkClass_ != PredefinedElkClass.OWL_NOTHING)
			return true;

		index.updatePositiveOwlNothingOccurrenceNo(positiveIncrement);

		return true;
	}

	@Override
	public final boolean updateOccurrenceNumbers(
			final ModifiableOntologyIndex index, OccurrenceIncrement increment) {

		if (!updateTotalOccurrenceNo(index, increment.totalIncrement)) {
			return false;
		}
		if (!updatePositiveOccurrenceNo(index, increment.positiveIncrement)) {
			// revert the changes
			updateTotalOccurrenceNo(index, -increment.positiveIncrement);
			return false;
		}
		if (!updateNegativeOccurrenceNo(index, increment.negativeIncrement)) {
			// revert the changes
			updateTotalOccurrenceNo(index, -increment.negativeIncrement);
			updatePositiveOccurrenceNo(index, -increment.positiveIncrement);
			return false;
		}

		return true;
	}

	@Override
	public final String toStringStructural() {
		return '<' + getElkEntity().getIri().getFullIriAsString() + '>';
	}

	@Override
	public final <O> O accept(IndexedClassVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public final <O> O accept(IndexedClassExpressionVisitor<O> visitor) {
		return accept((IndexedClassEntityVisitor<O>) visitor);
	}

	@Override
	public final <O> O accept(IndexedEntityVisitor<O> visitor) {
		return accept((IndexedClassEntityVisitor<O>) visitor);
	}

	@Override
	public final <O> O accept(IndexedClassEntityVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public CachedIndexedClass accept(CachedIndexedClassExpressionFilter filter) {
		return filter.filter(this);
	}

}