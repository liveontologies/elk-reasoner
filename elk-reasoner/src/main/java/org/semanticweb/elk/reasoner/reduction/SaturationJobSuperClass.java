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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;

/**
 * A job for computing saturation for the input atomic super-class of the root
 * that is required for transitive reduction of the root.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
class SaturationJobSuperClass<R extends IndexedClassExpression, J extends TransitiveReductionJob<R>>
		extends SaturationJobForTransitiveReduction<IndexedClass, R, J> {

	/**
	 * The current state of the transitive reduction. It should be used only in
	 * one unprocessed job at a time.
	 */
	protected final TransitiveReductionState<J> transitiveReductionState;

	SaturationJobSuperClass(IndexedClass superClass,
			TransitiveReductionState<J> transitiveReductionState) {
		super(superClass);
		this.transitiveReductionState = transitiveReductionState;
	}

	TransitiveReductionState<J> getTransitiveReductionState() {
		return this.transitiveReductionState;
	}

	@Override
	void accept(SaturationJobVisitor<R, J> visitor) throws InterruptedException {
		visitor.visit(this);
	}

	@Override
	public J getInitiatorJob() {
		return this.transitiveReductionState.getInitiatorJob();
	}

}
