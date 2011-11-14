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
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkClass;
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

	protected final Set<ElkClass> equivalent;
	protected final List<IndexedClass> directSuperClasses;

	public TransitiveReductionOutputSatisfiable(R root,
			Set<ElkClass> equivalent, List<IndexedClass> superClasses) {
		super(root);
		this.equivalent = equivalent;
		this.directSuperClasses = superClasses;
	}

	/**
	 * Returns the set of classes that are equivalent to the root of this node.
	 * 
	 * @return the set of classes that are equivalent to the root of this node
	 */
	public Set<ElkClass> getEquivalent() {
		return equivalent;
	}

	/**
	 * Returns the direct, i.e., transitively reduced list of indexed super
	 * classes of this node.
	 * 
	 * @return the list of direct indexed super classes of this node
	 */
	public Iterable<IndexedClass> getDirectSuperClasses() {
		return directSuperClasses;
	}

	@Override
	public <O> O accept(TransitiveReductionOutputVisitor<R, O> visitor) {
		return visitor.visit(this);
	}

}
