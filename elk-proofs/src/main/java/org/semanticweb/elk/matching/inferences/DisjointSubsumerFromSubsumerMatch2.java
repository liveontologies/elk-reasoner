package org.semanticweb.elk.matching.inferences;

import java.util.List;

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
import org.semanticweb.elk.matching.conclusions.IndexedDisjointClassesAxiomMatch2;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionComposedMatch1;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionComposedMatch1Watch;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.reasoner.saturation.inferences.DisjointSubsumerFromSubsumer;

public class DisjointSubsumerFromSubsumerMatch2
		extends AbstractInferenceMatch<DisjointSubsumerFromSubsumerMatch1>
		implements SubClassInclusionComposedMatch1Watch {

	private final List<? extends ElkClassExpression> disjointExpressionsMatch_;

	DisjointSubsumerFromSubsumerMatch2(
			DisjointSubsumerFromSubsumerMatch1 parent,
			IndexedDisjointClassesAxiomMatch2 secondPremiseMatch) {
		super(parent);
		this.disjointExpressionsMatch_ = secondPremiseMatch.getMemberMatches();
		checkEquals(secondPremiseMatch, getSecondPremiseMatch(DEBUG_FACTORY));
	}

	public List<? extends ElkClassExpression> getDisjointExpressionsMatch() {
		return disjointExpressionsMatch_;
	}

	public SubClassInclusionComposedMatch1 getFirstPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		DisjointSubsumerFromSubsumer originalInference = getParent()
				.getParent();
		return factory.getSubClassInclusionComposedMatch1(
				originalInference.getFirstPremise(factory),
				getParent().getOriginMatch(), getDisjointExpressionsMatch()
						.get(originalInference.getPosition()));
	}

	IndexedDisjointClassesAxiomMatch2 getSecondPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getIndexedDisjointClassesAxiomMatch2(
				getParent().getSecondPremiseMatch(factory),
				getDisjointExpressionsMatch());
	}

	@Override
	public <O> O accept(InferenceMatch.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(
			SubClassInclusionComposedMatch1Watch.Visitor<O> visitor) {
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

		O visit(DisjointSubsumerFromSubsumerMatch2 inferenceMatch2);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		DisjointSubsumerFromSubsumerMatch2 getDisjointSubsumerFromSubsumerMatch2(
				DisjointSubsumerFromSubsumerMatch1 parent,
				IndexedDisjointClassesAxiomMatch2 secondPremiseMatch);

	}

}
