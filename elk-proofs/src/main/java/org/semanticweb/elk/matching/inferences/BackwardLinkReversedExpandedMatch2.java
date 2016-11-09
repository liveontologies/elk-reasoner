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
import org.semanticweb.elk.matching.conclusions.IndexedSubObjectPropertyOfAxiomMatch2;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;

public class BackwardLinkReversedExpandedMatch2
		extends AbstractInferenceMatch<BackwardLinkReversedExpandedMatch1>
		implements ForwardLinkMatch1Watch {

	private final ElkSubObjectPropertyExpression subChainMatch_;

	private final ElkObjectProperty relationMatch_;

	BackwardLinkReversedExpandedMatch2(
			BackwardLinkReversedExpandedMatch1 parent,
			IndexedSubObjectPropertyOfAxiomMatch2 secondPremiseMatch) {
		super(parent);
		this.subChainMatch_ = secondPremiseMatch.getSubPropertyChainMatch();
		this.relationMatch_ = secondPremiseMatch.getSuperPropertyMatch();
		checkEquals(secondPremiseMatch,
				getSecondPremiseMatch(DEBUG_FACTORY));
	}

	public ElkSubObjectPropertyExpression getSubChainMatch() {
		return subChainMatch_;
	}

	public ElkObjectProperty getRelationMatch() {
		return relationMatch_;
	}

	public ForwardLinkMatch1 getFirstPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getForwardLinkMatch1(
				getParent().getParent().getFirstPremise(factory),
				getParent().getOriginMatch(), subChainMatch_, 0);
	}

	IndexedSubObjectPropertyOfAxiomMatch2 getSecondPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getIndexedSubObjectPropertyOfAxiomMatch2(
				getParent().getSecondPremiseMatch(factory), getSubChainMatch(),
				getRelationMatch());
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

		O visit(BackwardLinkReversedExpandedMatch2 inferenceMatch2);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		BackwardLinkReversedExpandedMatch2 getBackwardLinkReversedExpandedMatch2(
				BackwardLinkReversedExpandedMatch1 parent,
				IndexedSubObjectPropertyOfAxiomMatch2 secondPremiseMatch);

	}

}
