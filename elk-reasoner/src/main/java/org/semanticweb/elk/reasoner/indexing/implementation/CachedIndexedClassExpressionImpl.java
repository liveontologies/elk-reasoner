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

import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedClassExpressionFilter;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedObjectFilter;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObject;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedObjectVisitor;
import org.semanticweb.elk.reasoner.saturation.ExtendedContext;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ChainableSubsumerRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.LinkedSubsumerRule;
import org.semanticweb.elk.util.collections.chains.AbstractChain;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.entryset.Entry;
import org.semanticweb.elk.util.collections.entryset.EntryCollection;

/**
 * Implements {@link LinkedIndexedClassExpression} and {@link Entry} so that
 * these objects can be stored in {@link EntryCollection} together with other
 * elements.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of objects this object can be structurally equal to
 * 
 * @param <N>
 *            The type of the elements in the set where this entry is used
 */
abstract class CachedIndexedClassExpressionImpl<T extends CachedIndexedClassExpression<T> & Entry<T, N>, N>
		extends CachedIndexedObjectImpl<T, N> implements
		ModifiableIndexedClassExpression, CachedIndexedClassExpression<T>,
		Entry<T, N> {

	/**
	 * The first composition rule assigned to this
	 * {@link IndexedClassExpression}
	 */
	ChainableSubsumerRule compositionRuleHead;

	/**
	 * the reference to a {@link Context} assigned to this {@link IndexedObject}
	 */
	private volatile ExtendedContext context_ = null;

	CachedIndexedClassExpressionImpl(int structuralHash) {
		super(structuralHash);
	}

	@Override
	public final int compareTo(ModifiableIndexedClassExpression o) {
		if (this == o)
			return 0;
		// else
		int thisHash = hashCode();
		int otherHash = o.hashCode();
		if (thisHash == otherHash) {
			/*
			 * hash code collision for different elements should happen very
			 * rarely; in this case we rely on the unique string representation
			 * of indexed objects to compare them
			 */
			return this.toString().compareTo(o.toString());
		}
		// else
		return (thisHash < otherHash ? -1 : 1);
	}

	@Override
	public final LinkedSubsumerRule getCompositionRuleHead() {
		return compositionRuleHead;
	}

	@Override
	public final Chain<ChainableSubsumerRule> getCompositionRuleChain() {
		return new AbstractChain<ChainableSubsumerRule>() {
			@Override
			public ChainableSubsumerRule next() {
				return compositionRuleHead;
			}

			@Override
			public void setNext(ChainableSubsumerRule tail) {
				compositionRuleHead = tail;
			}
		};
	}

	@Override
	public final ExtendedContext getContext() {
		return this.context_;
	}

	@Override
	public final synchronized ExtendedContext setContextIfAbsent(
			ExtendedContext context) {
		if (context_ != null)
			return context_;
		// else
		context_ = context;
		return null;
	}

	@Override
	public final synchronized void resetContext() {
		context_ = null;
	}

	@Override
	public final <O> O accept(IndexedObjectVisitor<O> visitor) {
		return accept((IndexedClassExpressionVisitor<O>) visitor);
	}

	@Override
	public T accept(CachedIndexedObjectFilter filter) {
		return accept((CachedIndexedClassExpressionFilter) filter);
	}

}
