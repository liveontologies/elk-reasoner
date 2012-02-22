/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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

/**
 * The intermediate state of the transitive reduction computation.
 * 
 * @param <R>
 *            the type of the root indexed class expression for which transitive
 *            reduction needs to be computed
 * 
 * @param <J>
 *            the type of the transitive reduction job
 */
class TransitiveReductionState<R extends IndexedClassExpression, J extends TransitiveReductionJob<R>> {

	/**
	 * The transitive reduction job for which this state was created
	 */
	final J initiatorJob;
	/**
	 * The partially computed transitive reduction output for the job
	 */
	final TransitiveReductionOutputEquivalentDirect<R> output;
	/**
	 * The (current state of the) iterator over derived super indexed class
	 * expressions
	 */
	final Iterator<IndexedClassExpression> superClassExpressionsIterator;

	/**
	 * Constructing the state for the initiator job. It is required that the
	 * saturation is already computed for the root of the initiator job.
	 * 
	 * @param initiatorJob
	 */
	TransitiveReductionState(J initiatorJob) {
		this.initiatorJob = initiatorJob;
		R root = initiatorJob.getInput();
		this.output = new TransitiveReductionOutputEquivalentDirect<R>(root);
		this.superClassExpressionsIterator = root.getSaturated()
				.getSuperClassExpressions().iterator();
	}
}
