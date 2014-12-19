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
package org.semanticweb.elk.reasoner.indexing.impl;

import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkDataProperty;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedClassExpressionFilter;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedDataHasValueVisitor;

/**
 * Implements {@link CachedIndexedDataHasValue}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
class CachedIndexedDataHasValueImpl extends
		CachedIndexedComplexClassExpressionImpl<CachedIndexedDataHasValue>
		implements CachedIndexedDataHasValue {

	private final ElkDataProperty property_;

	private final ElkLiteral filler_;

	private CachedIndexedDataHasValueImpl(ElkDataProperty property,
			ElkLiteral filler) {
		super(CachedIndexedDataHasValue.Helper
				.structuralHashCode(property, filler));
		this.property_ = property;
		this.filler_ = filler;
	}

	CachedIndexedDataHasValueImpl(ElkDataHasValue elkDataHasValue) {
		this((ElkDataProperty) elkDataHasValue.getProperty(), elkDataHasValue
				.getFiller());
	}

	@Override
	public final ElkDataProperty getRelation() {
		return property_;
	}

	@Override
	public final ElkLiteral getFiller() {
		return filler_;
	}

	@Override
	public final CachedIndexedDataHasValue structuralEquals(Object other) {
		return CachedIndexedDataHasValue.Helper.structuralEquals(this, other);
	}

	@Override
	public final boolean updateOccurrenceNumbers(
			final ModifiableOntologyIndex index, int increment,
			int positiveIncrement, int negativeIncrement) {
		positiveOccurrenceNo += positiveIncrement;
		negativeOccurrenceNo += negativeIncrement;
		return true;
	}

	@Override
	public final String toStringStructural() {
		return "DataHasValue(" + '<'
				+ this.property_.getIri().getFullIriAsString() + "> \""
				+ this.filler_.getLexicalForm() + "\"^^<"
				+ this.filler_.getDatatype().getIri().getFullIriAsString()
				+ ">)";
	}

	@Override
	public final <O> O accept(IndexedDataHasValueVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public final <O> O accept(IndexedClassExpressionVisitor<O> visitor) {
		return accept((IndexedDataHasValueVisitor<O>) visitor);
	}

	@Override
	public CachedIndexedDataHasValue accept(
			CachedIndexedClassExpressionFilter filter) {
		return filter.filter(this);
	}

}