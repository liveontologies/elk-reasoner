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

import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedPropertyChainFilter;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.modifiable.OccurrenceIncrement;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedBinaryPropertyChainVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyChainVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyChainVisitorEx;

/**
 * Implements {@link CachedIndexedBinaryPropertyChain}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
final class CachedIndexedBinaryPropertyChainImpl
		extends
		CachedIndexedPropertyChainImpl<CachedIndexedBinaryPropertyChain, CachedIndexedBinaryPropertyChain>
		implements CachedIndexedBinaryPropertyChain {

	private final ModifiableIndexedObjectProperty leftProperty_;

	private final ModifiableIndexedPropertyChain rightProperty_;

	/**
	 * Used for creating auxiliary inclusions during binarization.
	 * 
	 * @param leftProperty
	 * @param rightProperty
	 */
	public CachedIndexedBinaryPropertyChainImpl(
			ModifiableIndexedObjectProperty leftProperty,
			ModifiableIndexedPropertyChain rightProperty) {
		super(CachedIndexedBinaryPropertyChain.Helper.structuralHashCode(
				leftProperty, rightProperty));
		this.leftProperty_ = leftProperty;
		this.rightProperty_ = rightProperty;
	}

	@Override
	public final ModifiableIndexedObjectProperty getLeftProperty() {
		return leftProperty_;
	}

	@Override
	public final ModifiableIndexedPropertyChain getRightPropertyChain() {
		return rightProperty_;
	}

	@Override
	public final CachedIndexedBinaryPropertyChain structuralEquals(Object other) {
		return CachedIndexedBinaryPropertyChain.Helper.structuralEquals(this,
				other);
	}

	@Override
	public final boolean updateOccurrenceNumbers(ModifiableOntologyIndex index,
			OccurrenceIncrement increment) {

		if (totalOccurrenceNo == 0 && increment.totalIncrement > 0) {
			// first occurrence of this expression
			if (!rightProperty_.addRightChain(this))
				return false;
			if (!leftProperty_.addLeftChain(this)) {
				// revert all changes
				rightProperty_.removeRightChain(this);
				return false;
			}
		}

		totalOccurrenceNo += increment.totalIncrement;

		if (totalOccurrenceNo == 0 && increment.totalIncrement < 0) {
			// no occurrences of this conjunction left
			if (!rightProperty_.removeRightChain(this)) {
				// revert all changes
				totalOccurrenceNo -= increment.totalIncrement;
				return false;
			}
			if (!leftProperty_.removeLeftChain(this)) {
				// revert all changes
				rightProperty_.addRightChain(this);
				totalOccurrenceNo -= increment.totalIncrement;
				return false;
			}
		}
		// success!
		return true;
	}

	/**
	 * @param ipc
	 * @return the property chain which is composable with the given property in
	 *         this chain or null
	 */
	public final IndexedPropertyChain getComposable(IndexedPropertyChain ipc) {
		return ipc == leftProperty_ ? rightProperty_
				: (ipc == rightProperty_ ? leftProperty_ : null);
	}

	@Override
	public final String toStringStructural() {
		return "ObjectPropertyChain(" + this.leftProperty_ + ' '
				+ this.rightProperty_ + ')';
	}

	@Override
	public final <O> O accept(IndexedPropertyChainVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public final <O> O accept(IndexedBinaryPropertyChainVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public final <O, P> O accept(IndexedPropertyChainVisitorEx<O, P> visitor,
			P parameter) {
		return visitor.visit(this, parameter);
	}

	@Override
	public CachedIndexedBinaryPropertyChain accept(
			CachedIndexedPropertyChainFilter filter) {
		return filter.filter(this);
	}

}
