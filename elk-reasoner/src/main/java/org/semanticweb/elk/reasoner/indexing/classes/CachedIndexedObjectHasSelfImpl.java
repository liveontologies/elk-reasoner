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

import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedObjectHasSelf;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
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

	@Override
	public final ModifiableIndexedObjectProperty getProperty() {
		return property_;
	}

	@Override
	public final CachedIndexedObjectHasSelf structuralEquals(Object other) {
		return CachedIndexedObjectHasSelf.Helper.structuralEquals(this, other);
	}

	@Override
	public final boolean updateOccurrenceNumbers(ModifiableOntologyIndex index,
			OccurrenceIncrement increment) {

		// TODO: support composition rules

		negativeOccurrenceNo += increment.negativeIncrement;
		positiveOccurrenceNo += increment.positiveIncrement;
		return true;

	}

	@Override
	public final String toStringStructural() {
		return "ObjectHasSelf(" + getProperty() + ')';
	}

	@Override
	public final <O> O accept(IndexedClassExpression.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public CachedIndexedObjectHasSelf accept(
			CachedIndexedClassExpression.Filter filter) {
		return filter.filter(this);
	}

}
