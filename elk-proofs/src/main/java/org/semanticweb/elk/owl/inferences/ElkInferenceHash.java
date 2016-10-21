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

import org.semanticweb.elk.util.hashing.HashGenerator;

public class ElkInferenceHash implements ElkInference.Visitor<Integer> {

	private static final ElkInferenceHash INSTANCE_ = new ElkInferenceHash();

	private static int combinedHashCode(int... hashes) {
		return HashGenerator.combineListHash(hashes);
	}

	public static ElkInference.Visitor<Integer> getHashVisitor() {
		return INSTANCE_;
	}

	private static int hashCode(int n) {
		return n;
	}

	private static int hashCode(Object o) {
		return o.hashCode();
	}

	// forbid construction; only static methods should be used
	private ElkInferenceHash() {

	}

	@Override
	public Integer visit(
			ElkClassInclusionEmptyObjectOneOfOwlNothing inference) {
		return combinedHashCode(hashCode(
				ElkClassInclusionOwlThingEmptyObjectIntersectionOf.class));
	}

	@Override
	public Integer visit(
			ElkClassInclusionEmptyObjectUnionOfOwlNothing inference) {
		return combinedHashCode(hashCode(
				ElkClassInclusionOwlThingEmptyObjectIntersectionOf.class));
	}

	@Override
	public Integer visit(
			ElkClassInclusionExistentialFillerExpansion inference) {
		return combinedHashCode(
				hashCode(ElkClassInclusionExistentialFillerExpansion.class),
				hashCode(inference.getSubClass()),
				hashCode(inference.getSuperClass()),
				hashCode(inference.getProperty()));
	}

	@Override
	public Integer visit(
			ElkClassInclusionExistentialOfObjectHasSelf inference) {
		return combinedHashCode(
				hashCode(ElkClassInclusionExistentialOfObjectHasSelf.class),
				hashCode(inference.getSubClass()),
				hashCode(inference.getProperty()));
	}

	@Override
	public Integer visit(ElkClassInclusionExistentialOwlNothing inference) {
		return combinedHashCode(
				hashCode(ElkClassInclusionExistentialOwlNothing.class),
				hashCode(inference.getProperty()));
	}

	@Override
	public Integer visit(
			ElkClassInclusionExistentialPropertyExpansion inference) {
		return combinedHashCode(
				hashCode(ElkClassInclusionExistentialPropertyExpansion.class),
				hashCode(inference.getClassExpressions()),
				hashCode(inference.getSubChain()),
				hashCode(inference.getSuperProperty()));
	}

	@Override
	public Integer visit(ElkClassInclusionExistentialTransitivity inference) {
		return combinedHashCode(
				hashCode(ElkClassInclusionExistentialTransitivity.class),
				hashCode(inference.getClassExpressions()),
				hashCode(inference.getTransitiveProperty()));
	}

	@Override
	public Integer visit(ElkClassInclusionHierarchy inference) {
		return combinedHashCode(hashCode(ElkClassInclusionHierarchy.class),
				hashCode(inference.getExpressions()));
	}

	@Override
	public Integer visit(ElkClassInclusionNegationClash inference) {
		return combinedHashCode(hashCode(ElkClassInclusionNegationClash.class),
				hashCode(inference.getExpression()));
	}

	@Override
	public Integer visit(
			ElkClassInclusionObjectIntersectionOfComposition inference) {
		return combinedHashCode(
				hashCode(
						ElkClassInclusionObjectIntersectionOfComposition.class),
				hashCode(inference.getSubExpression()),
				hashCode(inference.getConjuncts()));
	}

	@Override
	public Integer visit(
			ElkClassInclusionObjectIntersectionOfDecomposition inference) {
		return combinedHashCode(
				hashCode(
						ElkClassInclusionObjectIntersectionOfDecomposition.class),
				hashCode(inference.getConjuncts()),
				hashCode(inference.getConjunctPos()));
	}

	@Override
	public Integer visit(ElkClassInclusionObjectOneOfInclusion inference) {
		return combinedHashCode(
				hashCode(ElkClassInclusionObjectOneOfInclusion.class),
				hashCode(inference.getSuperIndividuals()),
				hashCode(inference.getSubIndividualPositions()));
	}

	@Override
	public Integer visit(ElkClassInclusionObjectUnionOfComposition inference) {
		return combinedHashCode(
				hashCode(ElkClassInclusionObjectUnionOfComposition.class),
				hashCode(inference.getDisjuncts()),
				hashCode(inference.getDisjunctPos()));
	}

	@Override
	public Integer visit(ElkClassInclusionOfClassAssertion inference) {
		return combinedHashCode(
				hashCode(ElkClassInclusionOfClassAssertion.class),
				hashCode(inference.getInstance()),
				hashCode(inference.getType()));
	}

	@Override
	public Integer visit(ElkClassInclusionOfDisjointClasses inference) {
		return combinedHashCode(
				hashCode(ElkClassInclusionOfDisjointClasses.class),
				hashCode(inference.getExpressions()),
				hashCode(inference.getFirstPos()),
				hashCode(inference.getSecondPos()));
	}

	@Override
	public Integer visit(ElkClassInclusionOfEquivaletClasses inference) {
		return combinedHashCode(
				hashCode(ElkClassInclusionOfEquivaletClasses.class),
				hashCode(inference.getExpressions()),
				hashCode(inference.getSubPos()),
				hashCode(inference.getSuperPos()));
	}

	@Override
	public Integer visit(ElkClassInclusionOfInconsistentIndividual inference) {
		return combinedHashCode(
				hashCode(ElkClassInclusionOfInconsistentIndividual.class),
				hashCode(inference.getInconsistent()));
	}

	@Override
	public Integer visit(ElkClassInclusionOfObjectPropertyAssertion inference) {
		return combinedHashCode(
				hashCode(ElkClassInclusionOfObjectPropertyAssertion.class),
				hashCode(inference.getSubject()),
				hashCode(inference.getProperty()),
				hashCode(inference.getObject()));
	}

	@Override
	public Integer visit(ElkClassInclusionOfObjectPropertyDomain inference) {
		return combinedHashCode(
				hashCode(ElkClassInclusionOfObjectPropertyDomain.class),
				hashCode(inference.getProperty()),
				hashCode(inference.getDomain()));
	}

	@Override
	public Integer visit(ElkClassInclusionOfReflexiveObjectProperty inference) {
		return combinedHashCode(
				hashCode(ElkClassInclusionOfReflexiveObjectProperty.class),
				hashCode(inference.getProperty()));
	}

	@Override
	public Integer visit(ElkClassInclusionOwlNothing inference) {
		return combinedHashCode(hashCode(ElkClassInclusionOwlNothing.class),
				hashCode(inference.getSuperClass()));
	}

	@Override
	public Integer visit(ElkClassInclusionOwlThing inference) {
		return combinedHashCode(hashCode(ElkClassInclusionOwlThing.class),
				hashCode(inference.getSubClass()));
	}

	@Override
	public Integer visit(
			ElkClassInclusionOwlThingEmptyObjectIntersectionOf inference) {
		return combinedHashCode(hashCode(
				ElkClassInclusionOwlThingEmptyObjectIntersectionOf.class));
	}

	@Override
	public Integer visit(ElkClassInclusionReflexivePropertyRange inference) {
		return combinedHashCode(
				hashCode(ElkClassInclusionReflexivePropertyRange.class),
				hashCode(inference.getSubClass()),
				hashCode(inference.getProperty()),
				hashCode(inference.getRange()));
	}

	@Override
	public Integer visit(
			ElkClassInclusionSingletonObjectUnionOfDecomposition inference) {
		return combinedHashCode(
				hashCode(
						ElkClassInclusionOwlThingEmptyObjectIntersectionOf.class),
				hashCode(inference.getDisjunct()));
	}

	@Override
	public Integer visit(ElkClassInclusionTautology inference) {
		return combinedHashCode(hashCode(ElkClassInclusionTautology.class),
				hashCode(inference.getExpression()));
	}

	@Override
	public Integer visit(ElkDisjointClassesOfDifferentIndividuals inference) {
		return combinedHashCode(
				hashCode(ElkDisjointClassesOfDifferentIndividuals.class),
				hashCode(inference.getDifferent()));
	}

	@Override
	public Integer visit(ElkDisjointClassesOfDisjointUnion inference) {
		return combinedHashCode(
				hashCode(ElkDisjointClassesOfDisjointUnion.class),
				hashCode(inference.getDefined()),
				hashCode(inference.getDisjoint()));
	}

	@Override
	public Integer visit(ElkEquivalentClassesCycle inference) {
		return combinedHashCode(hashCode(ElkEquivalentClassesCycle.class),
				hashCode(inference.getExpressions()));
	}

	@Override
	public Integer visit(ElkEquivalentClassesObjectHasValue inference) {
		return combinedHashCode(
				hashCode(ElkPropertyInclusionOfTransitiveObjectProperty.class),
				hashCode(inference.getProperty()),
				hashCode(inference.getValue()));
	}

	@Override
	public Integer visit(ElkEquivalentClassesObjectOneOf inference) {
		return combinedHashCode(hashCode(ElkEquivalentClassesObjectOneOf.class),
				hashCode(inference.getMembers()));
	}

	@Override
	public Integer visit(ElkEquivalentClassesOfDisjointUnion inference) {
		return combinedHashCode(
				hashCode(ElkEquivalentClassesOfDisjointUnion.class),
				hashCode(inference.getDefined()),
				hashCode(inference.getDisjoint()));
	}

	@Override
	public Integer visit(ElkEquivalentClassesOfSameIndividual inference) {
		return combinedHashCode(
				hashCode(ElkEquivalentClassesOfSameIndividual.class),
				hashCode(inference.getSame()));
	}

	@Override
	public Integer visit(ElkPropertyInclusionHierarchy inference) {
		return combinedHashCode(hashCode(ElkPropertyInclusionHierarchy.class),
				hashCode(inference.getSubExpression()),
				hashCode(inference.getExpressions()));
	}

	@Override
	public Integer visit(ElkPropertyInclusionOfEquivalence inference) {
		return combinedHashCode(
				hashCode(ElkPropertyInclusionOfEquivalence.class),
				hashCode(inference.getExpressions()),
				hashCode(inference.getSubPos()),
				hashCode(inference.getSuperPos()));
	}

	@Override
	public Integer visit(
			ElkPropertyInclusionOfTransitiveObjectProperty inference) {
		return combinedHashCode(
				hashCode(ElkPropertyInclusionOfTransitiveObjectProperty.class),
				hashCode(inference.getProperty()));
	}

	@Override
	public Integer visit(ElkPropertyInclusionTautology inference) {
		return combinedHashCode(hashCode(ElkPropertyInclusionTautology.class),
				hashCode(inference.getExpression()));
	}

	@Override
	public Integer visit(ElkPropertyRangePropertyExpansion inference) {
		return combinedHashCode(
				hashCode(ElkPropertyRangePropertyExpansion.class),
				hashCode(inference.getSubProperty()),
				hashCode(inference.getSuperProperty()),
				hashCode(inference.getRange()));
	}

	@Override
	public Integer visit(ElkToldAxiom inference) {
		return combinedHashCode(hashCode(ElkToldAxiom.class),
				hashCode(inference.getAxiom()));
	}

}
