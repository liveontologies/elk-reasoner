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

import org.semanticweb.elk.matching.ElkMatchException;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;

public class SubsumerPartialConjunctionMatch extends SubsumerMatch {

	private final ElkObjectIntersectionOf fullConjunctionMatch_;

	private final int conjunctionPrefixLength_;

	SubsumerPartialConjunctionMatch(
			ElkObjectIntersectionOf fullConjunctionMatch,
			int conjunctionPrefixLength) {
		this.fullConjunctionMatch_ = fullConjunctionMatch;
		this.conjunctionPrefixLength_ = conjunctionPrefixLength;
	}

	ElkObjectIntersectionOf getFullConjunctionMatch() {
		return fullConjunctionMatch_;
	}

	int getConjunctionPrefixLength() {
		return conjunctionPrefixLength_;
	}

	@Override
	public ElkClassExpression getGeneralMatch(IndexedClassExpression subsumer) {
		if (conjunctionPrefixLength_ == fullConjunctionMatch_
				.getClassExpressions().size()) {
			return fullConjunctionMatch_;
		} else if (conjunctionPrefixLength_ == 1) {
			return fullConjunctionMatch_.getClassExpressions().get(0);
		}
		// else
		throw new ElkMatchException(subsumer, fullConjunctionMatch_,
				conjunctionPrefixLength_);
	}

	@Override
	public ElkObjectIntersectionOf getFullConjunctionMatch(
			IndexedClassExpression subsumer) {
		return fullConjunctionMatch_;
	}

	@Override
	public int getConjunctionPrefixLength(IndexedClassExpression subsumer) {
		return conjunctionPrefixLength_;
	}

	@Override
	public <O> O accept(SubsumerMatch.Visitor<O> visitor) {
		return visitor.visit(this);
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

		O visit(SubsumerPartialConjunctionMatch subsumerMatch);

	}

}
