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

import org.semanticweb.elk.matching.conclusions.ConclusionMatchExpressionFactory;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch1;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch1Watch;
import org.semanticweb.elk.matching.conclusions.SubPropertyChainMatch2;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;

public class ForwardLinkCompositionMatch4
		extends AbstractInferenceMatch<ForwardLinkCompositionMatch3>
		implements ForwardLinkMatch1Watch {

	private final ElkSubObjectPropertyExpression fullPremiseForwardChainMatch_;

	private final int premiseForwardChainStartPos_;

	ForwardLinkCompositionMatch4(ForwardLinkCompositionMatch3 parent,
			SubPropertyChainMatch2 fourthPremiseMatch) {
		super(parent);
		this.fullPremiseForwardChainMatch_ = fourthPremiseMatch
				.getFullSubChainMatch();
		this.premiseForwardChainStartPos_ = fourthPremiseMatch
				.getSubChainStartPos();
	}

	public ElkSubObjectPropertyExpression getPremiseFullForwardChainMatch() {
		return fullPremiseForwardChainMatch_;
	}

	public int getPremiseForwardChainStartPos() {
		return premiseForwardChainStartPos_;
	}

	public SubPropertyChainMatch2 getFourthPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getSubPropertyChainMatch2(
				getParent().getFourthPremiseMatch(factory),
				fullPremiseForwardChainMatch_, premiseForwardChainStartPos_);
	}

	public ForwardLinkMatch1 getThirdPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getForwardLinkMatch1(
				getParent().getParent().getParent().getParent()
						.getThirdPremise(factory),
				getParent().getOriginMatch(), fullPremiseForwardChainMatch_,
				premiseForwardChainStartPos_);
	}

	@Override
	public <O> O accept(InferenceMatch.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(ForwardLinkMatch1Watch.Visitor<O> visitor) {
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

		O visit(ForwardLinkCompositionMatch4 inferenceMatch4);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		ForwardLinkCompositionMatch4 getForwardLinkCompositionMatch4(
				ForwardLinkCompositionMatch3 parent,
				SubPropertyChainMatch2 fourthPremiseMatch);

	}

}
