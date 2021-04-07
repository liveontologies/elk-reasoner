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
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedComplexPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.model.OccurrenceIncrement;

/**
 * Implements {@link CachedIndexedComplexPropertyChain}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
final class CachedIndexedComplexPropertyChainImpl extends
		CachedIndexedPropertyChainImpl<CachedIndexedComplexPropertyChain>
		implements CachedIndexedComplexPropertyChain {

	private final ModifiableIndexedObjectProperty firstProperty_;

	private final ModifiableIndexedPropertyChain suffixChain_;

	/**
	 * Used for creating auxiliary inclusions during binarization.
	 * 
	 * @param firstProperty
	 * @param suffixChain
	 */
	public CachedIndexedComplexPropertyChainImpl(
			ModifiableIndexedObjectProperty firstProperty,
			ModifiableIndexedPropertyChain suffixChain) {
		super(CachedIndexedComplexPropertyChain.structuralHashCode(
				firstProperty, suffixChain));
		this.firstProperty_ = firstProperty;
		this.suffixChain_ = suffixChain;
	}

	@Override
	public final ModifiableIndexedObjectProperty getFirstProperty() {
		return firstProperty_;
	}

	@Override
	public final ModifiableIndexedPropertyChain getSuffixChain() {
		return suffixChain_;
	}

	@Override
	public RevertibleAction getIndexingAction(ModifiableOntologyIndex index,
			OccurrenceIncrement increment) {
		return RevertibleAction
				.create(() -> totalOccurrenceNo == 0
						&& increment.totalIncrement > 0,
						() -> suffixChain_.addRightChain(this),
						() -> suffixChain_.removeRightChain(this))
				.then(RevertibleAction.create(
						() -> totalOccurrenceNo == 0
								&& increment.totalIncrement > 0,
						() -> firstProperty_.addLeftChain(this),
						() -> firstProperty_.removeLeftChain(this)))
				.then(super.getIndexingAction(index, increment))
				.then(RevertibleAction.create(
						() -> totalOccurrenceNo == 0
								&& increment.totalIncrement < 0,
						() -> suffixChain_.removeRightChain(this),
						() -> suffixChain_.addRightChain(this)))
				.then(RevertibleAction.create(
						() -> totalOccurrenceNo == 0
								&& increment.totalIncrement < 0,
						() -> firstProperty_.removeLeftChain(this),
						() -> firstProperty_.addLeftChain(this)))
				.then(RevertibleAction.create(() -> {
					index.occurrenceChanged(Feature.OBJECT_PROPERTY_CHAIN,
							increment.totalIncrement);
					return true;
				}, () -> {
					index.occurrenceChanged(Feature.OBJECT_PROPERTY_CHAIN,
							-increment.totalIncrement);
				}));

	}

	/**
	 * @param ipc
	 * @return the property chain which is composable with the given property in
	 *         this chain or null
	 */
	public final IndexedPropertyChain getComposable(IndexedPropertyChain ipc) {
		return ipc == firstProperty_ ? suffixChain_
				: (ipc == suffixChain_ ? firstProperty_ : null);
	}

	@Override
	public final <O> O accept(IndexedPropertyChain.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public CachedIndexedComplexPropertyChain accept(
			CachedIndexedPropertyChain.Filter filter) {
		return filter.filter(this);
	}

}
