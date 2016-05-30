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
import org.semanticweb.elk.matching.conclusions.SubClassInclusionComposedMatch1;
import org.semanticweb.elk.matching.root.IndexedContextRootMatch;
import org.semanticweb.elk.matching.subsumers.IndexedObjectUnionOfMatch;
import org.semanticweb.elk.matching.subsumers.SubsumerObjectOneOfMatch;
import org.semanticweb.elk.matching.subsumers.SubsumerObjectUnionOfMatch;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedObjectUnionOf;

public class SubClassInclusionComposedObjectUnionOfMatch1
		extends AbstractInferenceMatch<SubClassInclusionComposedObjectUnionOf> {

	private final IndexedContextRootMatch originMatch_;

	private final IndexedObjectUnionOfMatch conclusionSubsumerMatch_;

	SubClassInclusionComposedObjectUnionOfMatch1(
			SubClassInclusionComposedObjectUnionOf parent,
			SubClassInclusionComposedMatch1 conclusionMatch) {
		super(parent);
		this.originMatch_ = conclusionMatch.getDestinationMatch();
		conclusionSubsumerMatch_ = conclusionMatch
				.getSubsumerIndexedObjectUnionOfMatch();
	}

	public IndexedContextRootMatch getOriginMatch() {
		return originMatch_;
	}

	public IndexedObjectUnionOfMatch getConclusionSubsumerMatch() {
		return conclusionSubsumerMatch_;
	}

	public int getPosition() {
		return getParent().getPosition();
	}

	public SubClassInclusionComposedMatch1 getConclusionMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getSubClassInclusionComposedMatch1(
				getParent().getConclusion(factory), originMatch_,
				conclusionSubsumerMatch_);
	}

	public SubClassInclusionComposedMatch1 getPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getSubClassInclusionComposedMatch1(
				getParent().getPremise(factory), originMatch_,
				getPremiseSubsumer(factory));
	}

	private ElkClassExpression getPremiseSubsumer(
			final ConclusionMatchExpressionFactory factory) {
		final int pos = getPosition();
		return conclusionSubsumerMatch_.accept(
				new IndexedObjectUnionOfMatch.Visitor<ElkClassExpression>() {

					@Override
					public ElkClassExpression visit(
							SubsumerObjectOneOfMatch match) {
						return factory.getObjectOneOf(
								match.getValue().getIndividuals().get(pos));
					}

					@Override
					public ElkClassExpression visit(
							SubsumerObjectUnionOfMatch match) {
						return match.getValue().getClassExpressions().get(pos);
					}

				});

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

		O visit(SubClassInclusionComposedObjectUnionOfMatch1 inferenceMatch1);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		SubClassInclusionComposedObjectUnionOfMatch1 getSubClassInclusionComposedObjectUnionOfMatch1(
				SubClassInclusionComposedObjectUnionOf parent,
				SubClassInclusionComposedMatch1 conclusionMatch);

	}

}
