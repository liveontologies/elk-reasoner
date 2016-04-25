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

import org.semanticweb.elk.owl.comparison.ElkObjectHash;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.util.hashing.HashGenerator;
import org.semanticweb.elk.util.hashing.Hasher;

public class ElkInferenceHash
		implements ElkInference.Visitor<Integer>, Hasher<ElkInference> {

	private static final ElkInferenceHash INSTANCE_ = new ElkInferenceHash();

	// forbid construction; only static methods should be used
	private ElkInferenceHash() {

	}

	public static int hashCode(ElkInference conclusion) {
		return conclusion == null ? 0 : conclusion.accept(INSTANCE_);
	}

	public static ElkInference.Visitor<Integer> getHashVisitor() {
		return INSTANCE_;
	}

	private static int combinedHashCode(int... hashes) {
		return HashGenerator.combineListHash(hashes);
	}

	private static int hashCode(Class<?> c) {
		return c.hashCode();
	}

	private static int hashCode(ElkObject elkObject) {
		return ElkObjectHash.hashCode(elkObject);
	}

	private static int hashCode(List<? extends ElkObject> elkObjects) {
		return ElkObjectHash.hashCode(elkObjects);
	}

	private static int hashCode(int n) {
		return n;
	}

	@Override
	public int hash(ElkInference inference) {
		return hashCode(inference);
	}

	@Override
	public Integer visit(
			ElkClassInclusionExistentialFillerExpansion inference) {
		return combinedHashCode(
				hashCode(ElkClassInclusionExistentialFillerExpansion.class),
				hashCode(inference.getSubClass()),
				hashCode(inference.getProperty()),
				hashCode(inference.getSubFiller()),
				hashCode(inference.getSuperFiller()));
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
	public Integer visit(
			ElkClassInclusionExistentialPropertyExpansion inference) {
		return combinedHashCode(
				hashCode(ElkClassInclusionExistentialPropertyExpansion.class),
				hashCode(inference.getClassExpressions()),
				hashCode(inference.getSubChain()),
				hashCode(inference.getSuperProperty()));
	}

	@Override
	public Integer visit(ElkClassInclusionHierarchy inference) {
		return combinedHashCode(hashCode(ElkClassInclusionHierarchy.class),
				hashCode(inference.getExpressions()));
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
				hashCode(inference.getSubExpression()),
				hashCode(inference.getConjuncts()),
				hashCode(inference.getConjunctPos()));
	}

	@Override
	public Integer visit(ElkClassInclusionObjectUnionOfComposition inference) {
		return combinedHashCode(
				hashCode(ElkClassInclusionObjectUnionOfComposition.class),
				hashCode(inference.getSubExpression()),
				hashCode(inference.getDisjuncts()),
				hashCode(inference.getDisjunctPos()));
	}

	@Override
	public Integer visit(ElkClassInclusionOfEquivalence inference) {
		return combinedHashCode(hashCode(ElkClassInclusionOfEquivalence.class),
				hashCode(inference.getExpressions()),
				hashCode(inference.getSubPos()),
				hashCode(inference.getSuperPos()));
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
	public Integer visit(ElkClassInclusionOwlThing inference) {
		return combinedHashCode(hashCode(ElkClassInclusionOwlThing.class),
				hashCode(inference.getSubClass()));
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
	public Integer visit(ElkClassInclusionTautology inference) {
		return combinedHashCode(hashCode(ElkClassInclusionTautology.class),
				hashCode(inference.getExpression()));
	}

	@Override
	public Integer visit(ElkPropertyInclusionHierarchy inference) {
		return combinedHashCode(hashCode(ElkPropertyInclusionHierarchy.class),
				hashCode(inference.getSubExpression()),
				hashCode(inference.getExpressions()));
	}

	@Override
	public Integer visit(ElkPropertyInclusionOfEquivalence inference) {
		return combinedHashCode(hashCode(ElkPropertyInclusionOfEquivalence.class),
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

}
