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

import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObject;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubObject;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ExtendedContext;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ChainableSubsumerRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.LinkedSubsumerRule;
import org.semanticweb.elk.util.collections.chains.AbstractChain;
import org.semanticweb.elk.util.collections.chains.Chain;

/**
 * Implements {@link ModifiableIndexedClassExpression}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            The type of structured objects this object can be compared with
 * @param <N>
 *            The type of the elements in the collection where this entry is
 *            used
 */
abstract class ModifiableIndexedClassExpressionImpl<T extends ModifiableIndexedClassExpressionImpl<T, N>, N>
		extends StructuralIndexedSubObjectImpl<T, N>
		implements ModifiableIndexedClassExpression {
	
	/**
	 * The first composition rule assigned to this
	 * {@link IndexedClassExpression}
	 */
	ChainableSubsumerRule compositionRuleHead;
		
	/**
	 * the reference to a {@link Context} assigned to this {@link IndexedObject}
	 */
	private volatile ExtendedContext context_ = null;

	ModifiableIndexedClassExpressionImpl(int structuralHash) {
		super(structuralHash);
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
	public final <O> O accept(IndexedContextRoot.Visitor<O> visitor) {
		return accept((IndexedClassExpression.Visitor<O>) visitor);
	}	
	
	@Override
	public final <O> O accept(IndexedSubObject.Visitor<O> visitor) {
		return accept((IndexedClassExpression.Visitor<O>) visitor);
	}	

}
