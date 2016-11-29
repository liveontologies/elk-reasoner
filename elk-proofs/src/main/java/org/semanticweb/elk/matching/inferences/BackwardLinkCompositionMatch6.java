package org.semanticweb.elk.matching.inferences;

/*-
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
import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch2Watch;
import org.semanticweb.elk.matching.conclusions.ConclusionMatchExpressionFactory;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch2;
import org.semanticweb.elk.matching.root.IndexedContextRootMatch;

public class BackwardLinkCompositionMatch6
		extends AbstractInferenceMatch<BackwardLinkCompositionMatch5>
		implements BackwardLinkMatch2Watch {

	private final IndexedContextRootMatch destinationMatch_;

	BackwardLinkCompositionMatch6(BackwardLinkCompositionMatch5 parent,
			ForwardLinkMatch2 thirdPremiseMatch) {
		super(parent);
		this.destinationMatch_ = thirdPremiseMatch.getTargetMatch();
		checkEquals(thirdPremiseMatch, getThirdPremiseMatch(DEBUG_FACTORY));
	}

	IndexedContextRootMatch getDestinationMatch() {
		return destinationMatch_;
	}

	ForwardLinkMatch2 getThirdPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getForwardLinkMatch2(
				getParent().getThirdPremiseMatch(factory),
				getDestinationMatch());
	}

	public BackwardLinkMatch2 getConclusionMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getBackwardLinkMatch2(
				getParent().getParent().getParent().getParent().getParent()
						.getConclusionMatch(factory),
				getParent().getParent().getParent().getParent()
						.getConclusionRelationMatch(),
				getDestinationMatch());
	}

	@Override
	public <O> O accept(InferenceMatch.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(BackwardLinkMatch2Watch.Visitor<O> visitor) {
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

		O visit(BackwardLinkCompositionMatch6 inferenceMatch6);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		BackwardLinkCompositionMatch6 getBackwardLinkCompositionMatch6(
				BackwardLinkCompositionMatch5 parent,
				ForwardLinkMatch2 thirdPremiseMatch);

	}

}
