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
package org.semanticweb.elk.reasoner.indexing.classes;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.elk.RevertibleAction;
import org.semanticweb.elk.reasoner.completeness.Feature;
import org.semanticweb.elk.reasoner.indexing.model.IndexedComplexClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectUnionOf;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.model.OccurrenceIncrement;
import org.semanticweb.elk.reasoner.indexing.model.StructuralIndexedSubObject;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ObjectUnionFromDisjunctRule;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * Implements {@link ModifiableIndexedObjectUnionOf}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
class ModifiableIndexedObjectUnionOfImpl extends
		StructuralIndexedComplexClassExpressionEntryImpl<ModifiableIndexedObjectUnionOfImpl>
		implements ModifiableIndexedObjectUnionOf {

	private final List<ModifiableIndexedClassExpression> disjuncts_;

	ModifiableIndexedObjectUnionOfImpl(
			List<? extends ModifiableIndexedClassExpression> disjuncts) {
		this(new Initializer(disjuncts));
	}

	private ModifiableIndexedObjectUnionOfImpl(Initializer init) {
		super(structuralHashCode(init.disjuncts_));
		this.disjuncts_ = init.disjuncts_;

	}

	private static class Initializer {
		private final List<ModifiableIndexedClassExpression> disjuncts_;

		Initializer(
				List<? extends ModifiableIndexedClassExpression> disjuncts) {
			this.disjuncts_ = new ArrayList<ModifiableIndexedClassExpression>(
					2);
			for (ModifiableIndexedClassExpression disjunct : disjuncts) {
				this.disjuncts_.add(disjunct);
			}
		}
	}

	@Override
	public final List<ModifiableIndexedClassExpression> getDisjuncts() {
		return disjuncts_;
	}

	@Override
	public RevertibleAction getIndexingAction(ModifiableOntologyIndex index,
			OccurrenceIncrement increment) {
		return RevertibleAction.create(
				() -> !occursNegatively()
						&& increment.negativeIncrement > 0,
				() -> ObjectUnionFromDisjunctRule.addRulesFor(this, index),
				() -> ObjectUnionFromDisjunctRule.removeRulesFor(this, index))
				.then(super.getIndexingAction(index, increment))
				.then(RevertibleAction.create(
						() -> !occursNegatively()
								&& increment.negativeIncrement < 0,
						() -> ObjectUnionFromDisjunctRule.removeRulesFor(this,
								index),
						() -> ObjectUnionFromDisjunctRule.addRulesFor(this,
								index)))
				.then(RevertibleAction.create(() -> {
					index.occurrenceChanged(Feature.OBJECT_UNION_OF_POSITIVE,
							increment.positiveIncrement);
					return true;
				}, () -> index.occurrenceChanged(
						Feature.OBJECT_UNION_OF_POSITIVE,
						-increment.positiveIncrement)));
	}

	static int structuralHashCode(
			List<ModifiableIndexedClassExpression> disjuncts) {
		return HashGenerator.combinedHashCode(
				ModifiableIndexedObjectUnionOfImpl.class, disjuncts);
	}

	@Override
	public ModifiableIndexedObjectUnionOfImpl structuralEquals(Object other) {
		if (this == other) {
			return this;
		}
		if (other instanceof ModifiableIndexedObjectUnionOfImpl) {
			ModifiableIndexedObjectUnionOfImpl secondEntry = (ModifiableIndexedObjectUnionOfImpl) other;
			if (getDisjuncts().equals(secondEntry.getDisjuncts()))
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
