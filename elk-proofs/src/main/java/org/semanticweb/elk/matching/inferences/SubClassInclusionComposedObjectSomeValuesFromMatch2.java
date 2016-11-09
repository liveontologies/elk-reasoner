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
import org.semanticweb.elk.matching.conclusions.PropagationMatch1;
import org.semanticweb.elk.matching.conclusions.PropagationMatch1Watch;
import org.semanticweb.elk.matching.root.IndexedContextRootMatch;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;

public class SubClassInclusionComposedObjectSomeValuesFromMatch2 extends
		AbstractInferenceMatch<SubClassInclusionComposedObjectSomeValuesFromMatch1>
		implements PropagationMatch1Watch {

	private final ElkObjectProperty propagationRelationMatch_;

	private final IndexedContextRootMatch originMatch_;

	SubClassInclusionComposedObjectSomeValuesFromMatch2(
			SubClassInclusionComposedObjectSomeValuesFromMatch1 parent,
			BackwardLinkMatch2 firstPremiseMatch) {
		super(parent);
		this.propagationRelationMatch_ = firstPremiseMatch.getRelationMatch();
		this.originMatch_ = firstPremiseMatch.getDestinationMatch();
		checkEquals(firstPremiseMatch, getFirstPremiseMatch(DEBUG_FACTORY));
	}

	public ElkObjectProperty getPropagationRelationMatch() {
		return propagationRelationMatch_;
	}

	IndexedContextRootMatch getOriginMatch() {
		return originMatch_;
	}

	BackwardLinkMatch2 getFirstPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory
				.getBackwardLinkMatch2(
						factory.getBackwardLinkMatch1(
								getParent().getParent()
										.getFirstPremise(factory),
								getParent().getDestinationMatch()),
						getPropagationRelationMatch(), getOriginMatch());
	}

	public PropagationMatch1 getSecondPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getPropagationMatch1(
				getParent().getParent().getSecondPremise(factory),
				getOriginMatch(), getPropagationRelationMatch(),
				getParent().getConclusionSubsumerMatch());
	}

	@Override
	public <O> O accept(InferenceMatch.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(PropagationMatch1Watch.Visitor<O> visitor) {
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

		O visit(SubClassInclusionComposedObjectSomeValuesFromMatch2 inferenceMatch2);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		SubClassInclusionComposedObjectSomeValuesFromMatch2 getSubClassInclusionComposedObjectSomeValuesFromMatch2(
				SubClassInclusionComposedObjectSomeValuesFromMatch1 parent,
				BackwardLinkMatch2 secondPremiseMatch);

	}

}
