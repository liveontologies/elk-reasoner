package org.semanticweb.elk.matching.conclusions;

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyChain;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;

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
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubPropertyChain;

public class SubPropertyChainMatch1
		extends AbstractObjectPropertyConclusionMatch<SubPropertyChain> {

	private final ElkSubObjectPropertyExpression fullSuperChainMatch_;

	private final int superChainStartPos_;

	SubPropertyChainMatch1(SubPropertyChain parent,
			ElkSubObjectPropertyExpression fullSuperChainMatch,
			int superChainStartPos) {
		super(parent);
		checkChainMatch(fullSuperChainMatch, superChainStartPos);
		if (fullSuperChainMatch instanceof ElkObjectPropertyChain) {
			List<? extends ElkObjectPropertyExpression> expressions = ((ElkObjectPropertyChain) fullSuperChainMatch)
					.getObjectPropertyExpressions();
			if (superChainStartPos == expressions.size() - 1) {
				// only the last property is matched
				this.fullSuperChainMatch_ = expressions.get(superChainStartPos);
				this.superChainStartPos_ = 0;
				return;
			}
		}
		// else
		this.fullSuperChainMatch_ = fullSuperChainMatch;
		this.superChainStartPos_ = superChainStartPos;
	}

	public ElkSubObjectPropertyExpression getFullSuperChainMatch() {
		return fullSuperChainMatch_;
	}

	public int getSuperChainStartPos() {
		return superChainStartPos_;
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

		SubPropertyChainMatch1 getSubPropertyChainMatch1(
				SubPropertyChain parent,
				ElkSubObjectPropertyExpression fullSuperChainMatch,
				int superChainStartPos);

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

		O visit(SubPropertyChainMatch1 conclusionMatch);

	}

}
