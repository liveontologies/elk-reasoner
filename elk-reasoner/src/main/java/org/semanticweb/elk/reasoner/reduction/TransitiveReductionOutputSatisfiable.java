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

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.util.Comparators;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;

/**
 * The result of the transitive reduction for satisfiable indexed class
 * expression; it contains information about equivalent classes and direct
 * super-classes.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class TransitiveReductionOutputSatisfiable<R extends IndexedClassExpression>
		extends TransitiveReductionOutput<R> {

	protected final List<ElkClass> equivalent;
	protected final List<IndexedClass> directSuperClasses;

	public TransitiveReductionOutputSatisfiable(R root,
			List<ElkClass> equivalent, List<IndexedClass> superClasses) {
		super(root);
		this.equivalent = equivalent;
		this.directSuperClasses = superClasses;
	}

	/**
	 * Returns the list of classes that are equivalent to the root of this node.
	 * 
	 * @return the list of classes that are equivalent to the root of this node
	 */
	public List<ElkClass> getEquivalent() {
		return equivalent;
	}

	/**
	 * Returns the list of direct, i.e., transitively reduced, super classes of
	 * this node. Every direct super class is minimal in its equivalence class
	 * according to the comparator {@link Comparators#ELK_CLASS_COMPARATOR}.
	 * 
	 * @return the list consisting of direct super classes of this node
	 */
	public List<IndexedClass> getDirectSuperClasses() {
		return directSuperClasses;
	}

	@Override
	public void accept(TransitiveReductionOutputVisitor<R> visitor) {
		visitor.visit(this);
	}

}
