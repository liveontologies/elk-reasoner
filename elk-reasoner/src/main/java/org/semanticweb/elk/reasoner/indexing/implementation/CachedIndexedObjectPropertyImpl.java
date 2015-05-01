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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedPropertyChainFilter;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedComplexPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.modifiable.OccurrenceIncrement;
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
	 * {@link IndexedPropertyChain} occurs on the left.
	 */
	private Collection<IndexedComplexPropertyChain> leftChains_;

	/**
	 * The {@link IndexedPropertyChain} that are subsumed by this
	 * {@link IndexedComplexPropertyChain} according to axioms
	 */
	private ArrayList<IndexedPropertyChain> toldSubChains_;

	/**
	 * The corresponding {@link ElkAxiom}s that resulted for the sub property
	 * chains
	 */
	private ArrayList<ElkAxiom> toldSubChainsReasons_;

	CachedIndexedObjectPropertyImpl(ElkObjectProperty entity) {
		super(CachedIndexedObjectProperty.Helper.structuralHashCode(entity));
		this.property_ = entity;
	}

	@Override
	public final ElkObjectProperty getElkEntity() {
		return property_;
	}

	@Override
	public final ArrayList<IndexedPropertyChain> getToldSubChains() {
		return toldSubChains_ == null ? (CachedIndexedPropertyChainImpl
				.<IndexedPropertyChain> emptyArrayList()) : toldSubChains_;
	}

	@Override
	public ArrayList<ElkAxiom> getToldSubChainsReasons() {
		return toldSubChainsReasons_ == null ? (CachedIndexedPropertyChainImpl
				.<ElkAxiom> emptyArrayList()) : toldSubChainsReasons_;
	}

	@Override
	public final Collection<IndexedComplexPropertyChain> getLeftChains() {
		return leftChains_ == null ? Collections
				.<IndexedComplexPropertyChain> emptySet() : Collections
				.unmodifiableCollection(leftChains_);
	}

	@Override
	public final CachedIndexedObjectProperty structuralEquals(Object other) {
		return CachedIndexedObjectProperty.Helper.structuralEquals(this, other);
	}

	@Override
	public final boolean addLeftChain(IndexedComplexPropertyChain chain) {
		if (leftChains_ == null)
			leftChains_ = new ArrayList<IndexedComplexPropertyChain>(1);
		return leftChains_.add(chain);
	}

	@Override
	public final boolean removeLeftChain(IndexedComplexPropertyChain chain) {
		boolean success = false;
		if (leftChains_ != null) {
			success = leftChains_.remove(chain);
			if (leftChains_.isEmpty())
				leftChains_ = null;
		}
		return success;
	}

	@Override
	public final boolean addToldSubPropertyChain(IndexedPropertyChain subChain,
			ElkAxiom reason) {
		if (toldSubChains_ == null) {
			toldSubChains_ = new ArrayList<IndexedPropertyChain>(1);
		}
		if (toldSubChainsReasons_ == null) {
			toldSubChainsReasons_ = new ArrayList<ElkAxiom>(1);
		}
		toldSubChains_.add(subChain);
		toldSubChainsReasons_.add(reason);
		return true;
	}

	@Override
	public final boolean removeToldSubPropertyChain(
			IndexedPropertyChain subChain, ElkAxiom reason) {
		int i = indexOf(subChain, reason);
		if (i < 0)
			return false;
		// else success
		toldSubChains_.remove(i);
		toldSubChainsReasons_.remove(i);
		if (toldSubChains_.isEmpty())
			toldSubChains_ = null;
		if (toldSubChainsReasons_.isEmpty())
			toldSubChainsReasons_ = null;
		return true;
	}

	// TODO: create a generic method for this operation (used in other places)
	private int indexOf(IndexedPropertyChain subChain, ElkAxiom reason) {
		for (int i = 0; i < toldSubChains_.size(); i++) {
			if (toldSubChains_.get(i).equals(subChain)
					&& toldSubChainsReasons_.get(i).equals(reason))
				return i;
		}
		// else not found
		return -1;
	}

	@Override
	public final boolean updateOccurrenceNumbers(ModifiableOntologyIndex index,
			OccurrenceIncrement increment) {
		totalOccurrenceNo += increment.totalIncrement;
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
