/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.RevertibleAction;
import org.semanticweb.elk.reasoner.completeness.Feature;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkUnexpectedIndexingException;
import org.semanticweb.elk.reasoner.indexing.model.IndexedComplexClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.model.OccurrenceIncrement;
import org.semanticweb.elk.reasoner.indexing.model.StructuralIndexedSubObject;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ContradictionFromNegationRule;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * Implements {@link ModifiableIndexedObjectComplementOf}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
class ModifiableIndexedObjectComplementOfImpl extends
		StructuralIndexedComplexClassExpressionEntryImpl<ModifiableIndexedObjectComplementOfImpl>
		implements ModifiableIndexedObjectComplementOf {

	private final ModifiableIndexedClassExpression negated_;

	/**
	 * This counts how often this object occurred positively. Some indexing
	 * operations are only needed when encountering objects positively for the
	 * first time.
	 */
	private int positiveOccurrenceNo_ = 0;

	
	public boolean occursPositively() {
		return positiveOccurrenceNo_ > 0;
	}
	
	@Override
	public String printOccurrenceNumbers() {
		return super.printOccurrenceNumbers() + "; pos=" + positiveOccurrenceNo_;
	}

	@Override void checkOccurrenceNumbers() {
		super.checkOccurrenceNumbers();
		if (positiveOccurrenceNo_ < 0)
			throw new ElkUnexpectedIndexingException(
					toString() + " has a negative occurrence: "
							+ printOccurrenceNumbers());
	}

	
	ModifiableIndexedObjectComplementOfImpl(
			ModifiableIndexedClassExpression negated) {
		super(structuralHashCode(negated));
		this.negated_ = negated;
	}

	@Override
	public final ModifiableIndexedClassExpression getNegated() {
		return negated_;
	}

	@Override
	public RevertibleAction getIndexingAction(ModifiableOntologyIndex index,
			OccurrenceIncrement increment) {
		return RevertibleAction.create(
				() -> !occursPositively()
						&& increment.positiveIncrement > 0,
				() -> ContradictionFromNegationRule.addRulesFor(this, index),
				() -> ContradictionFromNegationRule.removeRulesFor(this, index))
				.then(super.getIndexingAction(index, increment))
				.then(RevertibleAction.create(
						() -> !occursPositively()
								&& increment.positiveIncrement < 0,
						() -> ContradictionFromNegationRule.removeRulesFor(this,
								index),
						() -> ContradictionFromNegationRule.addRulesFor(this,
								index)))
				.then(RevertibleAction.create(() -> {
					index.occurrenceChanged(
							Feature.OBJECT_COMPLEMENT_OF_NEGATIVE,
							increment.negativeIncrement);
					index.occurrenceChanged(
							Feature.OBJECT_COMPLEMENT_OF_POSITIVE,
							increment.positiveIncrement);
					return true;
				}, () -> {
					index.occurrenceChanged(
							Feature.OBJECT_COMPLEMENT_OF_NEGATIVE,
							-increment.negativeIncrement);
					index.occurrenceChanged(
							Feature.OBJECT_COMPLEMENT_OF_POSITIVE,
							-increment.positiveIncrement);
				}));

	}

	static int structuralHashCode(ModifiableIndexedClassExpression negated) {
		return HashGenerator.combinedHashCode(
				ModifiableIndexedObjectComplementOfImpl.class, negated);
	}

	@Override
	public ModifiableIndexedObjectComplementOfImpl structuralEquals(Object other) {
		if (this == other) {
			return this;
		}
		if (other instanceof ModifiableIndexedObjectComplementOfImpl) {
			ModifiableIndexedObjectComplementOfImpl secondEntry = (ModifiableIndexedObjectComplementOfImpl) other;
			if (getNegated().equals(secondEntry.getNegated()))
				return secondEntry;
		}
		// else
		return null;
	}

	@Override
	public final <O> O accept(
			IndexedComplexClassExpression.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(StructuralIndexedSubObject.Visitor<O> visitor) {
		return visitor.visit(this);
	}

}
