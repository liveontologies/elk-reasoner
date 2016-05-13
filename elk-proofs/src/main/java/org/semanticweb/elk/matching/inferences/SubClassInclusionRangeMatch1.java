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
import org.semanticweb.elk.matching.conclusions.PropertyRangeMatch1;
import org.semanticweb.elk.matching.conclusions.PropertyRangeMatch1Watch;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionDecomposedMatch1;
import org.semanticweb.elk.matching.root.IndexedContextRootMatch;
import org.semanticweb.elk.matching.root.IndexedContextRootMatchDummyVisitor;
import org.semanticweb.elk.matching.root.IndexedContextRootRangeMatch;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionRange;

public class SubClassInclusionRangeMatch1
		extends AbstractInferenceMatch<SubClassInclusionRange>
		implements PropertyRangeMatch1Watch {

	private final IndexedContextRootMatch originMatch_;

	SubClassInclusionRangeMatch1(final SubClassInclusionRange parent,
			SubClassInclusionDecomposedMatch1 conclusionMatch) {
		super(parent);
		originMatch_ = conclusionMatch.getDestinationMatch();
	}

	public IndexedContextRootMatch getOriginMatch() {
		return originMatch_;
	}

	ElkObjectProperty getPropertyMatch() {
		return originMatch_.accept(
				new IndexedContextRootMatchDummyVisitor<ElkObjectProperty>() {

					@Override
					protected ElkObjectProperty defaultVisit(
							IndexedContextRootMatch match) {
						throw new ElkMatchException(getParent().getOrigin(),
								match);
					}

					@Override
					protected ElkObjectProperty defaultVisit(
							IndexedContextRootRangeMatch match) {
						ElkObjectPropertyExpression property = match
								.getPropertyMatch();
						if (property instanceof ElkObjectProperty) {
							return (ElkObjectProperty) property;
						}
						// else
						return defaultVisit((IndexedContextRootMatch) match);
					}
				});
	}

	public SubClassInclusionDecomposedMatch1 getConclusionMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getSubClassInclusionDecomposedMatch1(
				getParent().getConclusion(factory), originMatch_);
	}

	public PropertyRangeMatch1 getPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getPropertyRangeMatch1(
				getParent().getSecondPremise(factory), getPropertyMatch());
	}

	@Override
	public <O> O accept(InferenceMatch.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(PropertyRangeMatch1Watch.Visitor<O> visitor) {
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

		O visit(SubClassInclusionRangeMatch1 inferenceMatch1);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		SubClassInclusionRangeMatch1 getSubClassInclusionRangeMatch1(
				SubClassInclusionRange parent,
				SubClassInclusionDecomposedMatch1 conclusionMatch);

	}

}
