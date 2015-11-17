package org.semanticweb.elk.reasoner.saturation.conclusions.model;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpressionList;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedContextRoot;

/**
 * A {@code Conclusion} representing that some {@link IndexedClassExpression}
 * member of an {@link IndexedClassExpressionList} was derived as a subsumer in
 * the root.
 * 
 * @see IndexedClassExpressionList#getDisjointMembers()
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 */
public interface DisjointSubsumer extends ClassConclusion {

	public static final String NAME = "Disjoint Subsumer";

	/**
	 * @return the {@link IndexedClassExpressionList} to which the member
	 *         belongs
	 */
	public IndexedClassExpressionList getDisjointExpressions();

	/**
	 * @return the position of the {@link IndexedClassExpressionList} at which
	 *         the member occurs
	 */
	public int getPosition();

	/**
	 * @return the {@link ElkAxiom} that is responsible for the
	 *         {@link IndexedClassExpressionList}
	 */
	public ElkAxiom getReason();

	public <O> O accept(Visitor<O> visitor);

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory {

		DisjointSubsumer getDisjointSubsumer(IndexedContextRoot root,
				IndexedClassExpressionList disjointExpressions, int position,
				ElkAxiom reason);

	}

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> {

		public O visit(DisjointSubsumer conclusion);

	}

}
