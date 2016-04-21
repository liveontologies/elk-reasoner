package org.semanticweb.elk.matching.conclusions;

/*
 * #%L
 * ELK Proofs Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;

public class IndexedEquivalentClassesAxiomMatch2
		extends AbstractIndexedAxiomMatch<IndexedEquivalentClassesAxiomMatch1> {

	private final ElkClassExpression firstMemberMatch_;

	private final ElkClassExpression secondMemberMatch_;

	IndexedEquivalentClassesAxiomMatch2(
			IndexedEquivalentClassesAxiomMatch1 parent,
			ElkClassExpression firstMemberMatch,
			ElkClassExpression secondMemberMatch) {
		super(parent);
		this.firstMemberMatch_ = firstMemberMatch;
		this.secondMemberMatch_ = secondMemberMatch;
	}

	public ElkClassExpression getFirstMemberMatch() {
		return firstMemberMatch_;
	}

	public ElkClassExpression getSecondMemberMatch() {
		return secondMemberMatch_;
	}

	@Override
	public <O> O accept(IndexedAxiomMatch.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		IndexedEquivalentClassesAxiomMatch2 getIndexedEquivalentClassesAxiomMatch2(
				IndexedEquivalentClassesAxiomMatch1 parent,
				ElkClassExpression firstMemberMatch,
				ElkClassExpression secondMemberMatch);

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

		O visit(IndexedEquivalentClassesAxiomMatch2 conclusionMatch);

	}

}
