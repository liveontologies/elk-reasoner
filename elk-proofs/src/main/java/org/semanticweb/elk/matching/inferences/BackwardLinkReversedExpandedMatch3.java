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

import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch2;
import org.semanticweb.elk.matching.conclusions.ConclusionMatchExpressionFactory;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch3;
import org.semanticweb.elk.matching.conclusions.IndexedContextRootMatch;
import org.semanticweb.elk.matching.conclusions.IndexedContextRootMatchChain;

public class BackwardLinkReversedExpandedMatch3
		extends AbstractInferenceMatch<BackwardLinkReversedExpandedMatch2> {

	private IndexedContextRootMatchChain intermediateRoots_;

	private final IndexedContextRootMatch destinationMatch_;

	BackwardLinkReversedExpandedMatch3(
			BackwardLinkReversedExpandedMatch2 parent,
			ForwardLinkMatch3 firstPremiseMatch) {
		super(parent);
		intermediateRoots_ = firstPremiseMatch.getIntermediateRoots();
		destinationMatch_ = firstPremiseMatch.getTargetMatch();
	}

	public IndexedContextRootMatchChain getIntermediateRoots() {
		return intermediateRoots_;
	}

	public IndexedContextRootMatch getDestinationMatch() {
		return destinationMatch_;
	}

	public BackwardLinkMatch2 getConclusionMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getBackwardLinkMatch2(
				getParent().getParent().getConclusionMatch(factory),
				getParent().getRelationMatch(), getDestinationMatch());
	}

	@Override
	public <O> O accept(InferenceMatch.Visitor<O> visitor) {
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

		O visit(BackwardLinkReversedExpandedMatch3 inferenceMatch3);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		BackwardLinkReversedExpandedMatch3 getBackwardLinkReversedExpandedMatch3(
				BackwardLinkReversedExpandedMatch2 parent,
				ForwardLinkMatch3 firstPremiseMatch);

	}

}
