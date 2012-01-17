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

import java.util.Iterator;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.util.collections.Operations;

/**
 * A job for computing direct super-classes for the input indexed class
 * expression.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
class SaturationJobRoot<I extends IndexedClassExpression, J extends TransitiveReductionJob<I>>
		extends SaturationJobForTransitiveReduction<I, I, J> {

	SaturationJobRoot(J initiatorJob) {
		super(initiatorJob);
	}

	@Override
	void accept(SaturationJobVisitor<I, J> visitor) throws InterruptedException {
		visitor.visit(this);
	}

	@Override
	public J getInitiatorJob() {
		return this.initiatorJob;
	}

	public Iterator<I> iterator() {
		return Operations.singleton(initiatorJob.getInput()).iterator();
	}
}
