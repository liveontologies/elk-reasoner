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
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch2;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch2Watch;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;

public class BackwardLinkReversedMatch2
		extends AbstractInferenceMatch<BackwardLinkReversedMatch1>
		implements ForwardLinkMatch2Watch {

	private final ElkObjectProperty relationMatch_;

	BackwardLinkReversedMatch2(BackwardLinkReversedMatch1 parent,
			ForwardLinkMatch2 premiseMatch) {
		super(parent);
		ElkSubObjectPropertyExpression fullChainMatch = premiseMatch
				.getFullChainMatch();
		int chainStartPos = premiseMatch.getChainStartPos();
		if (fullChainMatch instanceof ElkObjectProperty && chainStartPos == 0) {
			this.relationMatch_ = (ElkObjectProperty) fullChainMatch;
		} else {
			throw new ElkMatchException(parent.getParent().getRelation(),
					fullChainMatch, chainStartPos);
		}

	}

	public ElkObjectProperty getRelationMatch() {
		return relationMatch_;
	}

	public ForwardLinkMatch2 getPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getForwardLinkMatch2(
				getParent().getPremiseMatch(factory), relationMatch_, 0);

	}

	@Override
	public <O> O accept(InferenceMatch.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(ForwardLinkMatch2Watch.Visitor<O> visitor) {
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

		O visit(BackwardLinkReversedMatch2 inferenceMatch2);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		BackwardLinkReversedMatch2 getBackwardLinkReversedMatch2(
				BackwardLinkReversedMatch1 parent,
				ForwardLinkMatch2 premiseMatch);

	}

}
