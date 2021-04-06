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

import org.semanticweb.elk.RevertibleAction;
import org.semanticweb.elk.reasoner.completeness.Feature;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedComplexClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedObjectHasSelf;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedComplexClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.model.OccurrenceIncrement;

/**
 * Implements {@link CachedIndexedObjectSomeValuesFrom}
 * 
 * @author "Yevgeny Kazakov"
 */
class CachedIndexedObjectHasSelfImpl extends
		CachedIndexedComplexClassExpressionImpl<CachedIndexedObjectHasSelf>
		implements CachedIndexedObjectHasSelf {

	private final ModifiableIndexedObjectProperty property_;

	CachedIndexedObjectHasSelfImpl(ModifiableIndexedObjectProperty property) {
		super(CachedIndexedObjectHasSelf.Helper.structuralHashCode(property));
		this.property_ = property;
	}
	
	// TODO: support composition rules

	@Override
	public final ModifiableIndexedObjectProperty getProperty() {
		return property_;
	}

	@Override
	public final CachedIndexedObjectHasSelf structuralEquals(Object other) {
		return CachedIndexedObjectHasSelf.Helper.structuralEquals(this, other);
	}

	@Override
	public RevertibleAction getIndexingAction(ModifiableOntologyIndex index,
			OccurrenceIncrement increment) {
		return super.getIndexingAction(index, increment)
				.then(RevertibleAction.create(() -> {
					index.occurrenceChanged(
							Feature.OBJECT_HAS_SELF_NEGATIVE,
							increment.negativeIncrement);
					return true;
				}, () -> index.occurrenceChanged(
						Feature.OBJECT_HAS_SELF_NEGATIVE,
						-increment.negativeIncrement)));
	}
	
	@Override
	public final <O> O accept(IndexedComplexClassExpression.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public final CachedIndexedObjectHasSelf accept(
			CachedIndexedComplexClassExpression.Filter filter) {
		return filter.filter(this);
	}

}
