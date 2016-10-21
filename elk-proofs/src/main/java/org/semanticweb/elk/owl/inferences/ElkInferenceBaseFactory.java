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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;

public class ElkInferenceBaseFactory implements ElkInference.Factory {

	@Override
	public ElkClassInclusionEmptyObjectOneOfOwlNothing getElkClassInclusionEmptyObjectOneOfOwlNothing() {
		return new ElkClassInclusionEmptyObjectOneOfOwlNothing();
	}

	@Override
	public ElkClassInclusionEmptyObjectUnionOfOwlNothing getElkClassInclusionEmptyObjectUnionOfOwlNothing() {
		return new ElkClassInclusionEmptyObjectUnionOfOwlNothing();
	}

	@Override
	public ElkClassInclusionExistentialFillerExpansion getElkClassInclusionExistentialFillerExpansion(
			ElkClassExpression subClass, ElkClassExpression superClass,
			ElkObjectPropertyExpression property) {
		return new ElkClassInclusionExistentialFillerExpansion(subClass,
				superClass, property);
	}

	@Override
	public ElkClassInclusionExistentialOfObjectHasSelf getElkClassInclusionExistentialOfObjectHasSelf(
			ElkClassExpression subClass, ElkObjectPropertyExpression property) {
		return new ElkClassInclusionExistentialOfObjectHasSelf(subClass,
				property);
	}

	@Override
	public ElkClassInclusionExistentialOwlNothing getElkClassInclusionExistentialOwlNothing(
			ElkObjectPropertyExpression property) {
		return new ElkClassInclusionExistentialOwlNothing(property);
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
	public ElkClassInclusionExistentialTransitivity getElkClassInclusionExistentialTransitivity(
			List<? extends ElkClassExpression> classExpressions,
			ElkObjectPropertyExpression transitiveProperty) {
		return new ElkClassInclusionExistentialTransitivity(classExpressions,
				transitiveProperty);
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
	public ElkClassInclusionNegationClash getElkClassInclusionNegationClash(
			ElkClassExpression expression) {
		return new ElkClassInclusionNegationClash(expression);
	}

	@Override
	public ElkClassInclusionObjectIntersectionOfComposition getElkClassInclusionObjectIntersectionOfComposition(
			ElkClassExpression subExpression, ElkClassExpression firstConjunct,
			ElkClassExpression secondConjunct) {
		return new ElkClassInclusionObjectIntersectionOfComposition(
				subExpression, firstConjunct, secondConjunct);
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
			List<? extends ElkClassExpression> conjuncts, int conjunctPos) {
		return new ElkClassInclusionObjectIntersectionOfDecomposition(conjuncts,
				conjunctPos);
	}

	@Override
	public ElkClassInclusionObjectOneOfInclusion getElkClassInclusionObjectOneOfInclusion(
			List<? extends ElkIndividual> superIndividuals,
			List<Integer> subPositions) {
		return new ElkClassInclusionObjectOneOfInclusion(superIndividuals,
				subPositions);
	}

	@Override
	public ElkClassInclusionObjectUnionOfComposition getElkClassInclusionObjectUnionOfComposition(
			List<? extends ElkClassExpression> disjuncts, int disjunctPos) {
		return new ElkClassInclusionObjectUnionOfComposition(disjuncts,
				disjunctPos);
	}

	@Override
	public ElkClassInclusionOfClassAssertion getElkClassInclusionOfClassAssertion(
			ElkIndividual instance, ElkClassExpression type) {
		return new ElkClassInclusionOfClassAssertion(instance, type);
	}

	@Override
	public ElkClassInclusionOfDisjointClasses getElkClassInclusionOfDisjointClasses(
			List<? extends ElkClassExpression> expressions, int firstPos,
			int secondPos) {
		return new ElkClassInclusionOfDisjointClasses(expressions, firstPos,
				secondPos);
	}

	@Override
	public ElkClassInclusionOfEquivaletClasses getElkClassInclusionOfEquivaletClasses(
			ElkClassExpression first, ElkClassExpression second,
			boolean sameOrder) {
		return new ElkClassInclusionOfEquivaletClasses(first, second,
				sameOrder);
	}

	@Override
	public ElkClassInclusionOfEquivaletClasses getElkClassInclusionOfEquivaletClasses(
			List<? extends ElkClassExpression> expressions, int subPos,
			int superPos) {
		return new ElkClassInclusionOfEquivaletClasses(expressions, subPos,
				superPos);
	}

	@Override
	public ElkClassInclusionOfInconsistentIndividual getElkClassInclusionOfInconsistentIndividual(
			ElkIndividual inconsistent) {
		return new ElkClassInclusionOfInconsistentIndividual(inconsistent);
	}

	@Override
	public ElkClassInclusionOfObjectPropertyAssertion getElkClassInclusionOfObjectPropertyAssertion(
			ElkIndividual subject, ElkObjectPropertyExpression property,
			ElkIndividual object) {
		return new ElkClassInclusionOfObjectPropertyAssertion(subject, property,
				object);
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
	public ElkClassInclusionOwlNothing getElkClassInclusionOwlNothing(
			ElkClassExpression superClass) {
		return new ElkClassInclusionOwlNothing(superClass);
	}

	@Override
	public ElkClassInclusionOwlThing getElkClassInclusionOwlThing(
			ElkClassExpression subClass) {
		return new ElkClassInclusionOwlThing(subClass);
	}

	@Override
	public ElkClassInclusionOwlThingEmptyObjectIntersectionOf getElkClassInclusionOwlThingEmptyObjectIntersectionOf() {
		return new ElkClassInclusionOwlThingEmptyObjectIntersectionOf();
	}

	@Override
	public ElkClassInclusionReflexivePropertyRange getElkClassInclusionReflexivePropertyRange(
			ElkClassExpression subClass, ElkObjectPropertyExpression property,
			ElkClassExpression range) {
		return new ElkClassInclusionReflexivePropertyRange(subClass, property,
				range);
	}

	@Override
	public ElkClassInclusionSingletonObjectUnionOfDecomposition getElkClassInclusionSingletonObjectUnionOfDecomposition(
			ElkClassExpression disjunct) {
		return new ElkClassInclusionSingletonObjectUnionOfDecomposition(
				disjunct);
	}

	@Override
	public ElkClassInclusionTautology getElkClassInclusionTautology(
			ElkClassExpression expression) {
		return new ElkClassInclusionTautology(expression);
	}

	@Override
	public ElkDisjointClassesOfDifferentIndividuals getElkDisjointClassesOfDifferentIndividuals(
			List<? extends ElkIndividual> different) {
		return new ElkDisjointClassesOfDifferentIndividuals(different);
	}

	@Override
	public ElkDisjointClassesOfDisjointUnion getElkDisjointClassesOfDisjointUnion(
			ElkClass defined, List<? extends ElkClassExpression> disjoint) {
		return new ElkDisjointClassesOfDisjointUnion(defined, disjoint);
	}

	@Override
	public ElkEquivalentClassesCycle getElkEquivalentClassesCycle(
			ElkClassExpression first, ElkClassExpression second) {
		return new ElkEquivalentClassesCycle(first, second);
	}

	@Override
	public ElkEquivalentClassesCycle getElkEquivalentClassesCycle(
			List<? extends ElkClassExpression> expressions) {
		return new ElkEquivalentClassesCycle(expressions);
	}

	@Override
	public ElkEquivalentClassesObjectHasValue getElkEquivalentClassesObjectHasValue(
			ElkObjectPropertyExpression property, ElkIndividual value) {
		return new ElkEquivalentClassesObjectHasValue(property, value);
	}

	@Override
	public ElkEquivalentClassesObjectOneOf getElkEquivalentClassesObjectOneOf(
			List<? extends ElkIndividual> members) {
		return new ElkEquivalentClassesObjectOneOf(members);
	}

	@Override
	public ElkEquivalentClassesOfDisjointUnion getElkEquivalentClassesOfDisjointUnion(
			ElkClass defined, List<? extends ElkClassExpression> disjoint) {
		return new ElkEquivalentClassesOfDisjointUnion(defined, disjoint);
	}

	@Override
	public ElkEquivalentClassesOfSameIndividual getElkEquivalentClassesOfSameIndividual(
			List<? extends ElkIndividual> same) {
		return new ElkEquivalentClassesOfSameIndividual(same);
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
	public ElkPropertyInclusionOfEquivalence getElkPropertyInclusionOfEquivalence(
			ElkObjectPropertyExpression first,
			ElkObjectPropertyExpression second, boolean sameOrder) {
		return new ElkPropertyInclusionOfEquivalence(first, second, sameOrder);
	}

	@Override
	public ElkPropertyInclusionOfEquivalence getElkPropertyInclusionOfEquivalence(
			List<? extends ElkObjectPropertyExpression> expressions, int subPos,
			int superPos) {
		return new ElkPropertyInclusionOfEquivalence(expressions, subPos,
				superPos);
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

	@Override
	public ElkToldAxiom getElkToldAxiom(ElkAxiom axiom) {
		return new ElkToldAxiom(axiom);
	}

}
