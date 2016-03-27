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

import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;

public class SubPropertyChainMatch2
		extends AbstractObjectPropertyConclusionMatch<SubPropertyChainMatch1> {

	private final ElkSubObjectPropertyExpression fullSubChainMatch_;

	private final int subChainStartPos_;

	SubPropertyChainMatch2(SubPropertyChainMatch1 parent,
			ElkSubObjectPropertyExpression fullSubChainMatch,
			int subChainStartPos) {
		super(parent);
		checkChainMatch(fullSubChainMatch, subChainStartPos);
		this.fullSubChainMatch_ = fullSubChainMatch;
		this.subChainStartPos_ = subChainStartPos;
	}

	public ElkSubObjectPropertyExpression getFullSubChainMatch() {
		return this.fullSubChainMatch_;
	}

	public int getSubChainStartPos() {
		return subChainStartPos_;
	}

	@Override
	public <O> O accept(ObjectPropertyConclusionMatch.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		SubPropertyChainMatch2 getSubPropertyChainMatch2(
				SubPropertyChainMatch1 parent,
				ElkSubObjectPropertyExpression fullSubChainMatch,
				int subChainStartPos);

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

		O visit(SubPropertyChainMatch2 conclusionMatch);

	}

}
