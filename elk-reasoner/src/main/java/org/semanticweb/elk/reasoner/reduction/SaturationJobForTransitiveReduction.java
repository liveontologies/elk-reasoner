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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;

/**
 * Instances of saturation jobs for computing super-classes
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
abstract class SaturationJobForTransitiveReduction<I extends IndexedClassExpression, R extends IndexedClassExpression, J extends TransitiveReductionJob<R>>
		implements Iterable<I> {

	/**
	 * The link to the transitive reduction job that has initiated this job
	 */
	protected final J initiatorJob;

	SaturationJobForTransitiveReduction(J initiatorJob) {
		this.initiatorJob = initiatorJob;
	}

	public J getInitiatorJob() {
		return initiatorJob;
	}

	abstract void accept(SaturationJobVisitor<R, J> visitor)
			throws InterruptedException;

}
