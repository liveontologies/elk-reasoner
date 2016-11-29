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
import org.semanticweb.elk.matching.conclusions.SubPropertyChainMatch1;
import org.semanticweb.elk.matching.conclusions.SubPropertyChainMatch1Watch;
import org.semanticweb.elk.matching.root.IndexedContextRootMatch;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;

public class ForwardLinkCompositionMatch2
		extends AbstractInferenceMatch<ForwardLinkCompositionMatch1>
		implements SubPropertyChainMatch1Watch {

	private final ElkObjectProperty premiseBackwardRelationMatch_;

	private final IndexedContextRootMatch originMatch_;

	ForwardLinkCompositionMatch2(ForwardLinkCompositionMatch1 parent,
			BackwardLinkMatch2 firstPremiseMatch) {
		super(parent);
		this.premiseBackwardRelationMatch_ = firstPremiseMatch
				.getRelationMatch();
		this.originMatch_ = firstPremiseMatch.getDestinationMatch();
		checkEquals(firstPremiseMatch, getFirstPremiseMatch(DEBUG_FACTORY));
	}

	public ElkObjectProperty getPremiseBackwardRelationMatch() {
		return premiseBackwardRelationMatch_;
	}

	IndexedContextRootMatch getOriginMatch() {
		return originMatch_;
	}

	BackwardLinkMatch2 getFirstPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getBackwardLinkMatch2(
				getParent().getFirstPremiseMatch(factory),
				getPremiseBackwardRelationMatch(), getOriginMatch());
	}

	public SubPropertyChainMatch1 getSecondPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getSubPropertyChainMatch1(
				getParent().getParent().getSecondPremise(factory),
				getParent().getFirstProperty(), 0);
	}

	@Override
	public <O> O accept(InferenceMatch.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(SubPropertyChainMatch1Watch.Visitor<O> visitor) {
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

		O visit(ForwardLinkCompositionMatch2 inferenceMatch2);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		ForwardLinkCompositionMatch2 getForwardLinkCompositionMatch2(
				ForwardLinkCompositionMatch1 parent,
				BackwardLinkMatch2 firstPremiseMatch);

	}

}
