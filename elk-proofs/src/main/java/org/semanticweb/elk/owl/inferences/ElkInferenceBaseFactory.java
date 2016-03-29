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

public class ElkInferenceBaseFactory implements ElkInference.Factory {

	@Override
	public ElkClassInclusionExistentialFillerExpansion getElkClassInclusionExistentialFillerUnfolding(
			ElkClassExpression subClass, ElkObjectPropertyExpression property,
			ElkClassExpression subFiller, ElkClassExpression superFiller) {
		return new ElkClassInclusionExistentialFillerExpansion(subClass,
				property, subFiller, superFiller);
	}

	@Override
	public ElkClassInclusionExistentialOfObjectHasSelf getElkClassInclusionExistentialOfObjectHasSelf(
			ElkClassExpression subClass, ElkObjectPropertyExpression property) {
		return new ElkClassInclusionExistentialOfObjectHasSelf(subClass,
				property);
	}

	@Override
	public ElkClassInclusionExistentialPropertyExpansion getElkClassInclusionExistentialPropertyUnfolding(
			ElkClassExpression subExpression,
			ElkObjectPropertyExpression subProperty, ElkClassExpression filler,
			ElkObjectPropertyExpression superProperty) {
		return new ElkClassInclusionExistentialPropertyExpansion(subExpression,
				subProperty, filler, superProperty);
	}

	@Override
	public ElkClassInclusionExistentialPropertyExpansion getElkClassInclusionExistentialPropertyUnfolding(
			List<? extends ElkClassExpression> classExpressions,
			List<? extends ElkObjectPropertyExpression> subChain,
			ElkObjectPropertyExpression superProperty) {
		return new ElkClassInclusionExistentialPropertyExpansion(
				classExpressions, subChain, superProperty);
	}

	@Override
	public ElkClassInclusionHierarchy getElkClassInclusionHierarchy(
			ElkClassExpression first, ElkClassExpression second,
			ElkClassExpression third) {
		return new ElkClassInclusionHierarchy(first, second, third);
	}

	@Override
	public ElkClassInclusionHierarchy getElkClassInclusionHierarchy(
			List<? extends ElkClassExpression> expressions) {
		return new ElkClassInclusionHierarchy(expressions);
	}

	@Override
	public ElkClassInclusionObjectIntersectionOfComposition getElkClassInclusionObjectIntersectionOfComposition(
			ElkClassExpression subExpression,
			List<? extends ElkClassExpression> conjuncts) {
		return new ElkClassInclusionObjectIntersectionOfComposition(
				subExpression, conjuncts);
	}

	@Override
	public ElkClassInclusionObjectIntersectionOfDecomposition getElkClassInclusionObjectIntersectionOfDecomposition(
			ElkClassExpression subExpression,
			List<? extends ElkClassExpression> conjuncts, int conjunctPos) {
		return new ElkClassInclusionObjectIntersectionOfDecomposition(
				subExpression, conjuncts, conjunctPos);
	}

	@Override
	public ElkClassInclusionObjectUnionOfComposition getElkClassInclusionObjectUnionOfComposition(
			ElkClassExpression subExpression,
			List<? extends ElkClassExpression> disjuncts, int disjunctPos) {
		return new ElkClassInclusionObjectUnionOfComposition(subExpression,
				disjuncts, disjunctPos);
	}

	@Override
	public ElkClassInclusionOfEquivalence getElkClassInclusionOfEquivalence(
			ElkClassExpression first, ElkClassExpression second,
			boolean sameOrder) {
		return new ElkClassInclusionOfEquivalence(first, second, sameOrder);
	}

	@Override
	public ElkClassInclusionOfEquivalence getElkClassInclusionOfEquivalence(
			List<? extends ElkClassExpression> expressions, int subPos,
			int superPos) {
		return new ElkClassInclusionOfEquivalence(expressions, subPos,
				superPos);
	}

	@Override
	public ElkClassInclusionOfObjectPropertyDomain getElkClassInclusionOfObjectPropertyDomain(
			ElkObjectPropertyExpression property, ElkClassExpression domain) {
		return new ElkClassInclusionOfObjectPropertyDomain(property, domain);
	}

	@Override
	public ElkClassInclusionOfReflexiveObjectProperty getElkClassInclusionOfReflexiveObjectProperty(
			ElkObjectPropertyExpression property) {
		return new ElkClassInclusionOfReflexiveObjectProperty(property);
	}

	@Override
	public ElkClassInclusionOwlThing getElkClassInclusionOwlThing(
			ElkClassExpression subClass) {
		return new ElkClassInclusionOwlThing(subClass);
	}

	@Override
	public ElkClassInclusionReflexivePropertyRange getElkClassInclusionReflexivePropertyRange(
			ElkClassExpression subClass, ElkObjectPropertyExpression property,
			ElkClassExpression range) {
		return new ElkClassInclusionReflexivePropertyRange(subClass, property,
				range);
	}

	@Override
	public ElkClassInclusionTautology getElkClassInclusionTautology(
			ElkClassExpression expression) {
		return new ElkClassInclusionTautology(expression);
	}

	@Override
	public ElkPropertyInclusionHierarchy getElkPropertyInclusionHierarchy(
			ElkSubObjectPropertyExpression first,
			ElkObjectPropertyExpression second,
			ElkObjectPropertyExpression third) {
		return new ElkPropertyInclusionHierarchy(first, second, third);
	}

	@Override
	public ElkPropertyInclusionHierarchy getElkPropertyInclusionHierarchy(
			ElkSubObjectPropertyExpression subExpression,
			List<? extends ElkObjectPropertyExpression> expressions) {
		return new ElkPropertyInclusionHierarchy(subExpression, expressions);
	}

	@Override
	public ElkPropertyInclusionOfTransitiveObjectProperty getElkPropertyInclusionOfTransitiveObjectProperty(
			ElkObjectPropertyExpression property) {
		return new ElkPropertyInclusionOfTransitiveObjectProperty(property);
	}

	@Override
	public ElkPropertyInclusionTautology getElkPropertyInclusionTautology(
			ElkObjectPropertyExpression expression) {
		return new ElkPropertyInclusionTautology(expression);
	}

	@Override
	public ElkPropertyRangePropertyExpansion getElkPropertyRangePropertyUnfolding(
			ElkObjectPropertyExpression superProperty, ElkClassExpression range,
			ElkObjectPropertyExpression subProperty) {
		return new ElkPropertyRangePropertyExpansion(superProperty, range,
				subProperty);
	}

}
