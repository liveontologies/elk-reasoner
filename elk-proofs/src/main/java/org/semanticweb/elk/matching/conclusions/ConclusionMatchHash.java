package org.semanticweb.elk.matching.conclusions;

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

public class ConclusionMatchHash implements ConclusionMatch.Visitor<Integer> {

	private static final ConclusionMatchHash INSTANCE_ = new ConclusionMatchHash();

	private static int combinedHashCode(int... hashes) {
		return HashGenerator.combineListHash(hashes);
	}

	public static ConclusionMatch.Visitor<Integer> getInstance() {
		return INSTANCE_;
	}

	private static int hashCode(IndexedContextRootMatchChain chain) {
		int result = 0;
		for (;;) {
			if (chain == null) {
				return result;
			}
			// else
			result = combinedHashCode(result, hashCode(chain.getHead()));
			chain = chain.getTail();
		}
	}

	private static int hashCode(int i) {
		return i;
	}

	private static int hashCode(Object o) {
		return o.hashCode();
	}

	// forbid construction; only static methods should be used
	private ConclusionMatchHash() {

	}

	@Override
	public Integer visit(BackwardLinkMatch1 conclusionMatch) {
		return combinedHashCode(hashCode(BackwardLinkMatch1.class),
				hashCode(conclusionMatch.getParent()),
				hashCode(conclusionMatch.getSourceMatch()));
	}

	@Override
	public Integer visit(BackwardLinkMatch2 conclusionMatch) {
		return combinedHashCode(hashCode(BackwardLinkMatch2.class),
				hashCode(conclusionMatch.getParent()),
				hashCode(conclusionMatch.getRelationMatch()),
				hashCode(conclusionMatch.getDestinationMatch()));
	}

	@Override
	public Integer visit(ClassInconsistencyMatch1 conclusionMatch) {
		return combinedHashCode(hashCode(ClassInconsistencyMatch1.class),
				hashCode(conclusionMatch.getParent()),
				hashCode(conclusionMatch.getDestinationMatch()));
	}

	@Override
	public Integer visit(DisjointSubsumerMatch1 conclusionMatch) {
		return combinedHashCode(hashCode(DisjointSubsumerMatch1.class),
				hashCode(conclusionMatch.getParent()),
				hashCode(conclusionMatch.getDestinationMatch()));
	}

	@Override
	public Integer visit(DisjointSubsumerMatch2 conclusionMatch) {
		return combinedHashCode(hashCode(DisjointSubsumerMatch2.class),
				hashCode(conclusionMatch.getParent()),
				hashCode(conclusionMatch.getDisjointExpressionsMatch()));
	}

	@Override
	public Integer visit(ForwardLinkMatch1 conclusionMatch) {
		return combinedHashCode(hashCode(ForwardLinkMatch1.class),
				hashCode(conclusionMatch.getParent()),
				hashCode(conclusionMatch.getDestinationMatch()));
	}

	@Override
	public Integer visit(ForwardLinkMatch2 conclusionMatch) {
		return combinedHashCode(hashCode(ForwardLinkMatch2.class),
				hashCode(conclusionMatch.getParent()),
				hashCode(conclusionMatch.getChainStartPos()),
				hashCode(conclusionMatch.getFullChainMatch()));
	}

	@Override
	public Integer visit(ForwardLinkMatch3 conclusionMatch) {
		return combinedHashCode(hashCode(ForwardLinkMatch3.class),
				hashCode(conclusionMatch.getParent()),
				hashCode(conclusionMatch.getTargetMatch()),
				hashCode(conclusionMatch.getIntermediateRoots()));
	}

	@Override
	public Integer visit(IndexedDisjointClassesAxiomMatch1 conclusionMatch) {
		return combinedHashCode(
				hashCode(IndexedDisjointClassesAxiomMatch1.class),
				hashCode(conclusionMatch.getParent()));
	}

	@Override
	public Integer visit(IndexedDisjointClassesAxiomMatch2 conclusionMatch) {
		return combinedHashCode(
				hashCode(IndexedDisjointClassesAxiomMatch2.class),
				hashCode(conclusionMatch.getParent()),
				hashCode(conclusionMatch.getMemberMatches()));
	}

	@Override
	public Integer visit(IndexedEquivalentClassesAxiomMatch1 conclusionMatch) {
		return combinedHashCode(
				hashCode(IndexedEquivalentClassesAxiomMatch1.class),
				hashCode(conclusionMatch.getParent()));
	}

	@Override
	public Integer visit(IndexedEquivalentClassesAxiomMatch2 conclusionMatch) {
		return combinedHashCode(
				hashCode(IndexedEquivalentClassesAxiomMatch2.class),
				hashCode(conclusionMatch.getParent()),
				hashCode(conclusionMatch.getFirstMemberMatch()),
				hashCode(conclusionMatch.getSecondMemberMatch()));
	}

	@Override
	public Integer visit(
			IndexedObjectPropertyRangeAxiomMatch1 conclusionMatch) {
		return combinedHashCode(
				hashCode(IndexedObjectPropertyRangeAxiomMatch1.class),
				hashCode(conclusionMatch.getParent()));
	}

	@Override
	public Integer visit(
			IndexedObjectPropertyRangeAxiomMatch2 conclusionMatch) {
		return combinedHashCode(
				hashCode(IndexedObjectPropertyRangeAxiomMatch2.class),
				hashCode(conclusionMatch.getParent()),
				hashCode(conclusionMatch.getPropertyMatch()),
				hashCode(conclusionMatch.getRangeMatch()));
	}

	@Override
	public Integer visit(IndexedSubClassOfAxiomMatch1 conclusionMatch) {
		return combinedHashCode(hashCode(IndexedSubClassOfAxiomMatch1.class),
				hashCode(conclusionMatch.getParent()));
	}

	@Override
	public Integer visit(IndexedSubClassOfAxiomMatch2 conclusionMatch) {
		return combinedHashCode(hashCode(IndexedSubClassOfAxiomMatch2.class),
				hashCode(conclusionMatch.getParent()),
				hashCode(conclusionMatch.getSubClassMatch()),
				hashCode(conclusionMatch.getSuperClassMatch()));
	}

	@Override
	public Integer visit(
			IndexedSubObjectPropertyOfAxiomMatch1 conclusionMatch) {
		return combinedHashCode(
				hashCode(IndexedSubObjectPropertyOfAxiomMatch1.class),
				hashCode(conclusionMatch.getParent()));
	}

	@Override
	public Integer visit(
			IndexedSubObjectPropertyOfAxiomMatch2 conclusionMatch) {
		return combinedHashCode(
				hashCode(IndexedSubObjectPropertyOfAxiomMatch2.class),
				hashCode(conclusionMatch.getParent()),
				hashCode(conclusionMatch.getSubPropertyChainMatch()),
				hashCode(conclusionMatch.getSuperPropertyMatch()));
	}

	@Override
	public Integer visit(PropagationMatch1 conclusionMatch) {
		return combinedHashCode(hashCode(PropagationMatch1.class),
				hashCode(conclusionMatch.getParent()),
				hashCode(conclusionMatch.getDestinationMatch()),
				hashCode(conclusionMatch.getSubDestinationMatch()),
				hashCode(conclusionMatch.getCarryMatch()));
	}

	@Override
	public Integer visit(PropertyRangeMatch1 conclusionMatch) {
		return combinedHashCode(hashCode(PropertyRangeMatch1.class),
				hashCode(conclusionMatch.getParent()),
				hashCode(conclusionMatch.getPropertyMatch()));
	}

	@Override
	public Integer visit(PropertyRangeMatch2 conclusionMatch) {
		return combinedHashCode(hashCode(PropertyRangeMatch2.class),
				hashCode(conclusionMatch.getParent()),
				hashCode(conclusionMatch.getRangeMatch()));
	}

	@Override
	public Integer visit(SubClassInclusionComposedMatch1 conclusionMatch) {
		return combinedHashCode(hashCode(SubClassInclusionComposedMatch1.class),
				hashCode(conclusionMatch.getParent()),
				hashCode(conclusionMatch.getDestinationMatch()),
				hashCode(conclusionMatch.getSubsumerMatch()));
	}

	@Override
	public Integer visit(SubClassInclusionDecomposedMatch1 conclusionMatch) {
		return combinedHashCode(
				hashCode(SubClassInclusionDecomposedMatch1.class),
				hashCode(conclusionMatch.getParent()),
				hashCode(conclusionMatch.getDestinationMatch()));
	}

	@Override
	public Integer visit(SubClassInclusionDecomposedMatch2 conclusionMatch) {
		return combinedHashCode(
				hashCode(SubClassInclusionDecomposedMatch2.class),
				hashCode(conclusionMatch.getParent()),
				hashCode(conclusionMatch.getSubsumerMatch()));
	}

	@Override
	public Integer visit(SubPropertyChainMatch1 conclusionMatch) {
		return combinedHashCode(hashCode(SubPropertyChainMatch1.class),
				hashCode(conclusionMatch.getParent()),
				hashCode(conclusionMatch.getFullSuperChainMatch()),
				hashCode(conclusionMatch.getSuperChainStartPos()));
	}

	@Override
	public Integer visit(SubPropertyChainMatch2 conclusionMatch) {
		return combinedHashCode(hashCode(SubPropertyChainMatch2.class),
				hashCode(conclusionMatch.getParent()),
				hashCode(conclusionMatch.getFullSubChainMatch()),
				hashCode(conclusionMatch.getSubChainStartPos()));
	}

}
