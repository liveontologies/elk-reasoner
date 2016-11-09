package org.semanticweb.elk.matching.inferences;

import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch3;
import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch3Watch;

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

import org.semanticweb.elk.matching.conclusions.ConclusionMatchExpressionFactory;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch4;
import org.semanticweb.elk.matching.root.IndexedContextRootMatchChain;

public class ForwardLinkCompositionMatch6
		extends AbstractInferenceMatch<ForwardLinkCompositionMatch5>
		implements BackwardLinkMatch3Watch {

	private final IndexedContextRootMatchChain forwardChainExtendedDomains_;

	ForwardLinkCompositionMatch6(ForwardLinkCompositionMatch5 parent,
			ForwardLinkMatch4 thirdPremiseMatch) {
		super(parent);
		this.forwardChainExtendedDomains_ = thirdPremiseMatch
				.getExtendedDomains();
		checkEquals(thirdPremiseMatch, getThirdPremiseMatch(DEBUG_FACTORY));
	}

	public IndexedContextRootMatchChain getForwardChainExtendedDomains() {
		return forwardChainExtendedDomains_;
	}

	public BackwardLinkMatch3 getFirstPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getBackwardLinkMatch3(
				getParent().getParent().getParent().getParent()
						.getFirstPremiseMatch(factory),
				getForwardChainExtendedDomains().getHead());
	}

	ForwardLinkMatch4 getThirdPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getForwardLinkMatch4(
				getParent().getThirdPremiseMatch(factory),
				getForwardChainExtendedDomains());
	}

	@Override
	public <O> O accept(InferenceMatch.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(BackwardLinkMatch3Watch.Visitor<O> visitor) {
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

		O visit(ForwardLinkCompositionMatch6 inferenceMatch6);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		ForwardLinkCompositionMatch6 getForwardLinkCompositionMatch6(
				ForwardLinkCompositionMatch5 parent,
				ForwardLinkMatch4 thirdPremiseMatch);

	}

}
