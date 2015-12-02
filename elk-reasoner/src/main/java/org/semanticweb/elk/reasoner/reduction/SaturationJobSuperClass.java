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
package org.semanticweb.elk.reasoner.reduction;

import org.semanticweb.elk.reasoner.indexing.model.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;

/**
 * A job for computing saturation for the input indexed class that is required
 * for transitive reduction of the root.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <R>
 *            the type of the root indexed class expression for which transitive
 *            reduction needs to be computed
 * 
 * @param <J>
 *            the type of the transitive reduction job
 */
class SaturationJobSuperClass<R extends IndexedClassExpression, J extends TransitiveReductionJob<R>>
		extends SaturationJobForTransitiveReduction<R, IndexedClass, J> {

	/**
	 * The current state of the transitive reduction computation, which should
	 * be resumed after saturation for the candidate is computed.
	 */
	protected final TransitiveReductionState<R, J> state;

	/**
	 * Creating the saturation job for the input candidate indexed super-class
	 * of the root of the transitive reduction job, and current state of
	 * transitive reduction
	 * 
	 * @param candidate
	 *            the candidate indexed super-class for which saturation should
	 *            be computed
	 * @param state
	 *            the suspended state of transitive reduction computation
	 */
	SaturationJobSuperClass(IndexedClass candidate,
			TransitiveReductionState<R, J> state) {
		super(candidate);
		this.state = state;
	}

	@Override
	void accept(SaturationJobVisitor<R, J> visitor) throws InterruptedException {
		visitor.visit(this);
	}
}
