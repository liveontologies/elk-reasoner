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

import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedClassExpressionFilter;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.modifiable.OccurrenceIncrement;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassEntityVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedEntityVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedIndividualVisitor;

/**
 * Implements {@link CachedIndexedIndividual}
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 */
class CachedIndexedIndividualImpl extends
		CachedIndexedClassEntityImpl<CachedIndexedIndividual> implements
		CachedIndexedIndividual {

	/**
	 * The represented {@link ElkNamedIndividual}
	 */
	private final ElkNamedIndividual elkNamedIndividual_;

	CachedIndexedIndividualImpl(ElkNamedIndividual entity) {
		super(CachedIndexedIndividual.Helper.structuralHashCode(entity));
		this.elkNamedIndividual_ = entity;
	}

	@Override
	public ElkNamedIndividual getElkEntity() {
		return elkNamedIndividual_;
	}

	@Override
	public CachedIndexedIndividual structuralEquals(Object other) {
		return CachedIndexedIndividual.Helper.structuralEquals(this, other);
	}

	@Override
	public boolean updateOccurrenceNumbers(final ModifiableOntologyIndex index,
			OccurrenceIncrement increment) {

		totalOccurrenceNo += increment.totalIncrement;
		return true;
	}

	@Override
	public String toStringStructural() {
		return "ObjectOneOf(<"
				+ elkNamedIndividual_.getIri().getFullIriAsString() + ">)";
	}

	@Override
	public String getEntityType() {
		return "Individual";
	}

	@Override
	public final <O> O accept(IndexedIndividualVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public final <O> O accept(IndexedClassEntityVisitor<O> visitor) {
		return accept((IndexedIndividualVisitor<O>) visitor);
	}

	@Override
	public final <O> O accept(IndexedEntityVisitor<O> visitor) {
		return accept((IndexedIndividualVisitor<O>) visitor);
	}

	@Override
	public final <O> O accept(IndexedClassExpressionVisitor<O> visitor) {
		return accept((IndexedIndividualVisitor<O>) visitor);
	}

	@Override
	public CachedIndexedIndividual accept(
			CachedIndexedClassExpressionFilter filter) {
		return filter.filter(this);
	}

}