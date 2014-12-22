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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedPropertyChainFilter;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedEntityVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedObjectPropertyVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyChainVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyChainVisitorEx;

/**
 * Implements {@link CachedIndexedObjectProperty}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
final class CachedIndexedObjectPropertyImpl
		extends
		CachedIndexedPropertyChainImpl<CachedIndexedObjectProperty, CachedIndexedObjectProperty>
		implements CachedIndexedObjectProperty {

	private final ElkObjectProperty property_;

	/**
	 * Collections of all binary role chains in which this
	 * {@link IndexedBinaryPropertyChain} occurs on the left.
	 */
	private Collection<IndexedBinaryPropertyChain> leftChains_;

	/**
	 * Correctness of axioms deletions requires that toldSubProperties is a
	 * List.
	 */
	private List<IndexedPropertyChain> toldSubProperties_;

	/**
	 * Number of occurrence in reflexivity axioms
	 */
	private int reflexiveAxiomOccurrenceNo_ = 0;

	CachedIndexedObjectPropertyImpl(ElkObjectProperty entity) {
		super(CachedIndexedObjectProperty.Helper.structuralHashCode(entity));
		this.property_ = entity;
	}

	@Override
	public final ElkObjectProperty getElkEntity() {
		return property_;
	}

	@Override
	public final List<IndexedPropertyChain> getToldSubProperties() {
		return toldSubProperties_ == null ? Collections
				.<IndexedPropertyChain> emptyList() : Collections
				.<IndexedPropertyChain> unmodifiableList(toldSubProperties_);
	}

	@Override
	public final Collection<IndexedBinaryPropertyChain> getLeftChains() {
		return leftChains_ == null ? Collections
				.<IndexedBinaryPropertyChain> emptySet() : Collections
				.unmodifiableCollection(leftChains_);
	}

	@Override
	public final boolean isToldReflexive() {
		return reflexiveAxiomOccurrenceNo_ > 0;
	}

	@Override
	public final CachedIndexedObjectProperty structuralEquals(Object other) {
		return CachedIndexedObjectProperty.Helper.structuralEquals(this, other);
	}

	@Override
	public final boolean addLeftChain(IndexedBinaryPropertyChain chain) {
		if (leftChains_ == null)
			leftChains_ = new ArrayList<IndexedBinaryPropertyChain>(1);
		return leftChains_.add(chain);
	}

	@Override
	public final boolean removeLeftChain(IndexedBinaryPropertyChain chain) {
		boolean success = false;
		if (leftChains_ != null) {
			success = leftChains_.remove(chain);
			if (leftChains_.isEmpty())
				leftChains_ = null;
		}
		return success;
	}

	@Override
	public final boolean addToldSubPropertyChain(
			IndexedPropertyChain subObjectProperty) {
		if (toldSubProperties_ == null)
			toldSubProperties_ = new ArrayList<IndexedPropertyChain>(1);
		toldSubProperties_.add(subObjectProperty);
		return true;
	}

	@Override
	public final boolean removeToldSubPropertyChain(
			IndexedPropertyChain subObjectProperty) {
		boolean success = false;
		if (toldSubProperties_ != null) {
			success = toldSubProperties_.remove(subObjectProperty);
			if (toldSubProperties_.isEmpty())
				toldSubProperties_ = null;
		}
		return success;
	}

	@Override
	public final boolean updateOccurrenceNumbers(ModifiableOntologyIndex index,
			int increment, int positiveIncrement, int negativeIncrement) {
		occurrenceNo += increment;
		return true;
	}

	@Override
	public final boolean updateReflexiveOccurrenceNumber(int increment) {
		reflexiveAxiomOccurrenceNo_ += increment;
		return true;
	}

	/**
	 * 
	 * @return The string representation of the {@link ElkObjectProperty}
	 *         corresponding to this object.
	 */
	@Override
	public final String toStringStructural() {
		return '<' + getElkEntity().getIri().getFullIriAsString() + '>';
	}

	@Override
	public final String getEntityType() {
		return "ObjectProperty";
	}

	public final <O> O accept(IndexedObjectPropertyVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public final <O> O accept(IndexedEntityVisitor<O> visitor) {
		return accept((IndexedObjectPropertyVisitor<O>) visitor);
	}

	@Override
	public final <O> O accept(IndexedPropertyChainVisitor<O> visitor) {
		return accept((IndexedObjectPropertyVisitor<O>) visitor);
	}

	@Override
	public final <O, P> O accept(IndexedPropertyChainVisitorEx<O, P> visitor,
			P parameter) {
		return visitor.visit(this, parameter);
	}

	@Override
	public CachedIndexedObjectProperty accept(
			CachedIndexedPropertyChainFilter filter) {
		return filter.filter(this);
	}

}
