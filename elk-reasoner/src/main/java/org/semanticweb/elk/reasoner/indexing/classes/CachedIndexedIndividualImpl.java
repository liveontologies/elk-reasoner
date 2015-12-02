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

import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassEntity;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedEntity;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.model.OccurrenceIncrement;

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
	public final <O> O accept(IndexedClassEntity.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public final <O> O accept(IndexedEntity.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public final <O> O accept(IndexedClassExpression.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public CachedIndexedIndividual accept(
			CachedIndexedClassExpression.Filter filter) {
		return filter.filter(this);
	}

}