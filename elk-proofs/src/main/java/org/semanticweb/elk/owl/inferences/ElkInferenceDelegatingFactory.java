package org.semanticweb.elk.owl.inferences;

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

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;

public class ElkInferenceDelegatingFactory implements ElkInference.Factory {

	private final ElkInference.Factory mainFactory_;

	public ElkInferenceDelegatingFactory(ElkInference.Factory mainFactory) {
		this.mainFactory_ = mainFactory;
	}

	protected <I extends ElkInference> I filter(I inference) {
		return inference;
	}

	@Override
	public ElkClassInclusionExistentialFillerExpansion getElkClassInclusionExistentialFillerUnfolding(
			ElkClassExpression subClass, ElkObjectPropertyExpression property,
			ElkClassExpression subFiller, ElkClassExpression superFiller) {
		return filter(
				mainFactory_.getElkClassInclusionExistentialFillerUnfolding(
						subClass, property, subFiller, superFiller));
	}

	@Override
	public ElkClassInclusionExistentialOfObjectHasSelf getElkClassInclusionExistentialOfObjectHasSelf(
			ElkClassExpression subClass, ElkObjectPropertyExpression property) {
		return filter(
				mainFactory_.getElkClassInclusionExistentialOfObjectHasSelf(
						subClass, property));
	}

	@Override
	public ElkClassInclusionExistentialPropertyExpansion getElkClassInclusionExistentialPropertyUnfolding(
			ElkClassExpression subExpression,
			ElkObjectPropertyExpression subProperty, ElkClassExpression filler,
			ElkObjectPropertyExpression superProperty) {
		return filter(
				mainFactory_.getElkClassInclusionExistentialPropertyUnfolding(
						subExpression, subProperty, filler, superProperty));
	}

	@Override
	public ElkClassInclusionExistentialPropertyExpansion getElkClassInclusionExistentialPropertyUnfolding(
			List<? extends ElkClassExpression> classExpressions,
			List<? extends ElkObjectPropertyExpression> subChain,
			ElkObjectPropertyExpression superProperty) {
		return filter(
				mainFactory_.getElkClassInclusionExistentialPropertyUnfolding(
						classExpressions, subChain, superProperty));
	}

	@Override
	public ElkClassInclusionHierarchy getElkClassInclusionHierarchy(
			ElkClassExpression first, ElkClassExpression second,
			ElkClassExpression third) {
		return filter(mainFactory_.getElkClassInclusionHierarchy(first, second,
				third));
	}

	@Override
	public ElkClassInclusionHierarchy getElkClassInclusionHierarchy(
			List<? extends ElkClassExpression> expressions) {
		return filter(mainFactory_.getElkClassInclusionHierarchy(expressions));
	}

	@Override
	public ElkClassInclusionObjectIntersectionOfComposition getElkClassInclusionObjectIntersectionOfComposition(
			ElkClassExpression subExpression,
			List<? extends ElkClassExpression> conjuncts) {
		return filter(mainFactory_
				.getElkClassInclusionObjectIntersectionOfComposition(
						subExpression, conjuncts));
	}

	@Override
	public ElkClassInclusionObjectIntersectionOfDecomposition getElkClassInclusionObjectIntersectionOfDecomposition(
			ElkClassExpression subExpression,
			List<? extends ElkClassExpression> conjuncts, int conjunctPos) {
		return filter(mainFactory_
				.getElkClassInclusionObjectIntersectionOfDecomposition(
						subExpression, conjuncts, conjunctPos));
	}

	@Override
	public ElkClassInclusionObjectUnionOfComposition getElkClassInclusionObjectUnionOfComposition(
			ElkClassExpression subExpression,
			List<? extends ElkClassExpression> disjuncts, int disjunctPos) {
		return filter(mainFactory_.getElkClassInclusionObjectUnionOfComposition(
				subExpression, disjuncts, disjunctPos));
	}

	@Override
	public ElkClassInclusionOfEquivalence getElkClassInclusionOfEquivalence(
			ElkClassExpression first, ElkClassExpression second,
			boolean sameOrder) {
		return filter(mainFactory_.getElkClassInclusionOfEquivalence(first,
				second, sameOrder));
	}

	@Override
	public ElkClassInclusionOfEquivalence getElkClassInclusionOfEquivalence(
			List<? extends ElkClassExpression> expressions, int subPos,
			int superPos) {
		return filter(mainFactory_.getElkClassInclusionOfEquivalence(
				expressions, subPos, superPos));
	}

	@Override
	public ElkClassInclusionOfObjectPropertyDomain getElkClassInclusionOfObjectPropertyDomain(
			ElkObjectPropertyExpression property, ElkClassExpression domain) {
		return filter(mainFactory_
				.getElkClassInclusionOfObjectPropertyDomain(property, domain));
	}

	@Override
	public ElkClassInclusionOfReflexiveObjectProperty getElkClassInclusionOfReflexiveObjectProperty(
			ElkObjectPropertyExpression property) {
		return filter(mainFactory_
				.getElkClassInclusionOfReflexiveObjectProperty(property));
	}

	@Override
	public ElkClassInclusionOwlThing getElkClassInclusionOwlThing(
			ElkClassExpression subClass) {
		return filter(mainFactory_.getElkClassInclusionOwlThing(subClass));
	}

	@Override
	public ElkClassInclusionReflexivePropertyRange getElkClassInclusionReflexivePropertyRange(
			ElkClassExpression subClass, ElkObjectPropertyExpression property,
			ElkClassExpression range) {
		return filter(mainFactory_.getElkClassInclusionReflexivePropertyRange(
				subClass, property, range));
	}

	@Override
	public ElkClassInclusionTautology getElkClassInclusionTautology(
			ElkClassExpression expression) {
		return filter(mainFactory_.getElkClassInclusionTautology(expression));
	}

	@Override
	public ElkPropertyInclusionHierarchy getElkPropertyInclusionHierarchy(
			ElkSubObjectPropertyExpression first,
			ElkObjectPropertyExpression second,
			ElkObjectPropertyExpression third) {
		return filter(mainFactory_.getElkPropertyInclusionHierarchy(first,
				second, third));
	}

	@Override
	public ElkPropertyInclusionHierarchy getElkPropertyInclusionHierarchy(
			ElkSubObjectPropertyExpression subExpression,
			List<? extends ElkObjectPropertyExpression> expressions) {
		return filter(mainFactory_
				.getElkPropertyInclusionHierarchy(subExpression, expressions));
	}

	@Override
	public ElkPropertyInclusionOfTransitiveObjectProperty getElkPropertyInclusionOfTransitiveObjectProperty(
			ElkObjectPropertyExpression property) {
		return filter(mainFactory_
				.getElkPropertyInclusionOfTransitiveObjectProperty(property));
	}

	@Override
	public ElkPropertyInclusionTautology getElkPropertyInclusionTautology(
			ElkObjectPropertyExpression expression) {
		return filter(
				mainFactory_.getElkPropertyInclusionTautology(expression));
	}

	@Override
	public ElkPropertyRangePropertyExpansion getElkPropertyRangePropertyUnfolding(
			ElkObjectPropertyExpression superProperty, ElkClassExpression range,
			ElkObjectPropertyExpression subProperty) {
		return filter(mainFactory_.getElkPropertyRangePropertyUnfolding(
				superProperty, range, subProperty));
	}

}
