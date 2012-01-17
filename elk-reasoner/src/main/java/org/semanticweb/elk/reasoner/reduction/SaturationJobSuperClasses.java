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
import java.util.NoSuchElementException;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;

/**
 * A job for computing saturation for the input atomic super-class of the root
 * that is required for transitive reduction of the root.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
class SaturationJobSuperClasses<R extends IndexedClassExpression, J extends TransitiveReductionJob<R>>
		extends SaturationJobForTransitiveReduction<IndexedClass, R, J> {

	SaturationJobSuperClasses(J initiatorJob) {
		super(initiatorJob);
	}

	@Override
	void accept(SaturationJobVisitor<R, J> visitor) throws InterruptedException {
		visitor.visit(this);
	}

	public Iterator<IndexedClass> iterator() {
		// return Operations.filter(
		// /* the saturation for the root concept should be already computed */
		// this.initiatorJob.getInput().getSaturated().getSuperClassExpressions(),
		// IndexedClass.class).iterator();
		return new Iterator<IndexedClass>() {
			Iterator<IndexedClassExpression> i = initiatorJob.getInput()
					.getSaturated().getSuperClassExpressions().iterator();
			IndexedClass next;
			boolean hasNext = advance();

			public boolean hasNext() {
				return hasNext;
			}

			public IndexedClass next() {
				if (hasNext) {
					IndexedClass result = next;
					hasNext = advance();
					return result;
				}
				throw new NoSuchElementException();
			}

			public void remove() {
				i.remove();
			}

			boolean advance() {
				while (i.hasNext()) {
					IndexedClassExpression nextClassExpression = i.next();
					if (nextClassExpression instanceof IndexedClass) {
						next = (IndexedClass) nextClassExpression;
						return true;
					}
				}
				return false;
			}
		};
	}

}
