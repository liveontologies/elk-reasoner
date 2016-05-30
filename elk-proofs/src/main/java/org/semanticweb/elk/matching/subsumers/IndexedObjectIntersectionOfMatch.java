package org.semanticweb.elk.matching.subsumers;

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

import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;

public class IndexedObjectIntersectionOfMatch extends AbstractSubsumerMatch {

	private final ElkObjectIntersectionOf fullConjunctionMatch_;

	private final int conjunctionPrefixLength_;

	IndexedObjectIntersectionOfMatch(ElkObjectIntersectionOf value) {
		this.fullConjunctionMatch_ = value;
		this.conjunctionPrefixLength_ = value.getClassExpressions().size();
	}

	IndexedObjectIntersectionOfMatch(
			ElkObjectIntersectionOf fullConjunctionMatch,
			int conjunctionPrefixLength) {
		int conjunctsCount = fullConjunctionMatch.getClassExpressions().size();
		if (conjunctsCount <= 1) {
			throw new IllegalArgumentException(
					"ElkObjectIntersectionOf must have at least 2 conjuncts: "
							+ fullConjunctionMatch);
		}
		if (conjunctionPrefixLength <= 1
				|| conjunctionPrefixLength > conjunctsCount) {
			throw new IllegalArgumentException(
					"Prefix length should be > 1 and <= " + conjunctsCount
							+ ": " + +conjunctionPrefixLength);
		}
		this.fullConjunctionMatch_ = fullConjunctionMatch;
		this.conjunctionPrefixLength_ = conjunctionPrefixLength;
	}

	@Override
	public <O> O accept(SubsumerMatch.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	public ElkObjectIntersectionOf getFullValue() {
		return fullConjunctionMatch_;
	}

	public int getPrefixLength() {
		return conjunctionPrefixLength_;
	}

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	public interface Visitor<O> {

		O visit(IndexedObjectIntersectionOfMatch match);
	}

}
