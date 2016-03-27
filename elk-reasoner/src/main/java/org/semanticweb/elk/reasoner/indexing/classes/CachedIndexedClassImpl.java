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
package org.semanticweb.elk.reasoner.indexing.classes;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassEntity;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedEntity;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.model.OccurrenceIncrement;
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

	/**
	 * The {@link ElkAxiom} from which {@link #definition_} originates
	 */
	private ElkAxiom definitionReason_;

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
	public ElkAxiom getDefinitionReason() {
		return this.definitionReason_;
	}

	@Override
	public boolean setDefinition(ModifiableIndexedClassExpression definition,
			ElkAxiom reason) {
		if (definition_ != null)
			return false;
		// else
		this.definition_ = definition;
		this.definitionReason_ = reason;
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
	public final <O> O accept(IndexedClassExpression.Visitor<O> visitor) {
		return accept((IndexedClassEntity.Visitor<O>) visitor);
	}

	@Override
	public final <O> O accept(IndexedEntity.Visitor<O> visitor) {
		return accept((IndexedClassEntity.Visitor<O>) visitor);
	}

	@Override
	public final <O> O accept(IndexedClassEntity.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public CachedIndexedClass accept(CachedIndexedClassExpression.Filter filter) {
		return filter.filter(this);
	}

}