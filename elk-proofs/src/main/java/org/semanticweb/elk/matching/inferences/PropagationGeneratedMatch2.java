package org.semanticweb.elk.matching.inferences;

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
import org.semanticweb.elk.matching.conclusions.ConclusionMatchExpressionFactory;
import org.semanticweb.elk.matching.conclusions.PropagationMatch2;
import org.semanticweb.elk.matching.conclusions.PropagationMatch2Watch;
import org.semanticweb.elk.matching.conclusions.SubPropertyChainMatch2;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;

public class PropagationGeneratedMatch2
		extends AbstractInferenceMatch<PropagationGeneratedMatch1>
		implements PropagationMatch2Watch {

	private final ElkObjectProperty subPropertyMatch_;

	PropagationGeneratedMatch2(PropagationGeneratedMatch1 parent,
			SubPropertyChainMatch2 thirdPremiseMatch) {
		super(parent);
		ElkSubObjectPropertyExpression subChainMatch = thirdPremiseMatch
				.getFullSubChainMatch();
		int subChainMatchStartPos = thirdPremiseMatch.getSubChainStartPos();
		if (subChainMatch instanceof ElkObjectProperty) {
			subPropertyMatch_ = (ElkObjectProperty) subChainMatch;
		} else {
			throw new ElkMatchException(
					thirdPremiseMatch.getParent().getParent().getSubChain(),
					subChainMatch, subChainMatchStartPos);
		}
	}

	public ElkObjectProperty getSubPropertyMatch() {
		return subPropertyMatch_;
	}

	public PropagationMatch2 getConclusionMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getPropagationMatch2(
				getParent().getConclusionMatch(factory), subPropertyMatch_);
	}

	@Override
	public <O> O accept(InferenceMatch.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(PropagationMatch2Watch.Visitor<O> visitor) {
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
	public interface Visitor<O> {

		O visit(PropagationGeneratedMatch2 inferenceMatch2);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		PropagationGeneratedMatch2 getPropagationGeneratedMatch2(
				PropagationGeneratedMatch1 parent,
				SubPropertyChainMatch2 thirdPremiseMatch);

	}

}
