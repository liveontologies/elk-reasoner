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

import java.util.LinkedList;
import java.util.List;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;

/**
 * The result of the transitive reduction for satisfiable indexed class
 * expression; it contains information about equivalent classes and direct
 * super-classes.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <I>
 *            the type of the input for which the output equivalent and direct
 *            super classes are computed
 */
public class TransitiveReductionOutputEquivalentDirect<I extends IndexedClassExpression>
		extends TransitiveReductionOutputEquivalent<I> {

	final List<TransitiveReductionOutputEquivalent<IndexedClass>> directSuperClasses;

	public TransitiveReductionOutputEquivalentDirect(I root) {
		super(root);
		this.directSuperClasses = new LinkedList<TransitiveReductionOutputEquivalent<IndexedClass>>();
	}

	/**
	 * Returns the list of partial outputs of transitive reduction (only
	 * equivalent classes) for direct, i.e., transitively reduced, super classes
	 * of the root.
	 * 
	 * @return the list consisting of partial output of transitive reduction for
	 *         direct super classes of the root
	 */
	public List<TransitiveReductionOutputEquivalent<IndexedClass>> getDirectSuperClasses() {
		return directSuperClasses;
	}

	@Override
	public void accept(TransitiveReductionOutputVisitor<I> visitor) {
		visitor.visit(this);
	}

}
