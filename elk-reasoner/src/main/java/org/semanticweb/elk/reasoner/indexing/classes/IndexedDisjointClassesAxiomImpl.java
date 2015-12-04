package org.semanticweb.elk.reasoner.indexing.classes;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpressionList;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDisjointClassesAxiom;

/**
 * Implements {@link IndexedDisjointClassesAxiom}
 * 
 * @author Yevgeny Kazakov
 *
 * @param <A>
 *            the type of the defined class originates
 * @param <M>
 *            the type of the disjont class members list
 */
class IndexedDisjointClassesAxiomImpl<A extends ElkAxiom, M extends IndexedClassExpressionList>
		extends
			IndexedAxiomImpl<A>
		implements IndexedDisjointClassesAxiom {

	/**
	 * the {@link IndexedClassExpression}s stated to be disjoint. Note that same
	 * may appear two times in this list, in which case they should be
	 * inconsistent (disjoint with itself)
	 */
	private final M members_;

	protected IndexedDisjointClassesAxiomImpl(A originalAxiom, M members) {
		super(originalAxiom);
		this.members_ = members;
	}

	@Override
	public final M getMembers() {
		return members_;
	}

	@Override
	public final String toStringStructural() {
		return "DisjointClasses(" + members_ + ")";
	}

	@Override
	public final <O> O accept(IndexedAxiom.Visitor<O> visitor) {
		return visitor.visit(this);
	}

}
