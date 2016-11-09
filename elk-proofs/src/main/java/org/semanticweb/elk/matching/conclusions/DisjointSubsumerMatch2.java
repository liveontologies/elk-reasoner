package org.semanticweb.elk.matching.conclusions;

import java.util.List;

import org.semanticweb.elk.matching.root.IndexedContextRootMatch;

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

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;

public class DisjointSubsumerMatch2
		extends AbstractClassConclusionMatch<DisjointSubsumerMatch1> {

	private final IndexedContextRootMatch extendedDestinationMatch_;

	private final List<? extends ElkClassExpression> disjointExpressionsMatch_;

	DisjointSubsumerMatch2(DisjointSubsumerMatch1 parent,
			IndexedContextRootMatch extendedDestinationMatch,
			List<? extends ElkClassExpression> disjointExpressionsMatch) {
		super(parent);
		this.extendedDestinationMatch_ = extendedDestinationMatch;
		this.disjointExpressionsMatch_ = disjointExpressionsMatch;
	}

	public IndexedContextRootMatch getExtendedDestinationMatch() {
		return extendedDestinationMatch_;
	}

	public List<? extends ElkClassExpression> getDisjointExpressionsMatch() {
		return disjointExpressionsMatch_;
	}

	@Override
	public <O> O accept(ClassConclusionMatch.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		DisjointSubsumerMatch2 getDisjointSubsumerMatch2(
				DisjointSubsumerMatch1 parent,
				IndexedContextRootMatch extendedDestinationMatch,
				List<? extends ElkClassExpression> disjointExpressionsMatch);

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

		O visit(DisjointSubsumerMatch2 conclusionMatch);

	}

}
