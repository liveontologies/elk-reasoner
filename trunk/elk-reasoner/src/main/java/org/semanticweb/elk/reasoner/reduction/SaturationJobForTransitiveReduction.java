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
import org.semanticweb.elk.reasoner.saturation.SaturationJob;

/**
 * Instances of saturation jobs for computing super-classes
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <R>
 *            the type of the root indexed class expression for which transitive
 *            reduction needs to be computed
 * @param <I>
 *            the type of the indexed class expression for which saturation is
 *            required to be computed
 * @param <J>
 *            the type of the transitive reduction job
 */
abstract class SaturationJobForTransitiveReduction<R extends IndexedClassExpression, I extends IndexedClassExpression, J extends TransitiveReductionJob<R>>
		extends SaturationJob<I> {

	public SaturationJobForTransitiveReduction(I input) {
		super(input);
	}

	abstract void accept(SaturationJobVisitor<R, J> visitor)
			throws InterruptedException;

}
