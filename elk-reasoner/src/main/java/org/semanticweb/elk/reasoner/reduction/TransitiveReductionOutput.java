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
 * The abstract class representing the output of the transitive reduction
 * process for a given indexed class expression.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <R>
 *            the type of the input for the transitive reduction job
 */
public abstract class TransitiveReductionOutput<R extends IndexedClassExpression> {

	/**
	 * The indexed class expression for which the transitive reduction was
	 * initiated.
	 */
	protected final R root;

	TransitiveReductionOutput(R root) {
		this.root = root;
	}

	/**
	 * Returns the indexed class expression for which this output was computed.
	 * 
	 * @return the indexed class expression for which this output was computed
	 */
	public R getRoot() {
		return root;
	}

	public abstract void accept(TransitiveReductionOutputVisitor<R> visitor);

}
