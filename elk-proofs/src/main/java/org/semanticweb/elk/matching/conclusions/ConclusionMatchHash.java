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

import java.util.List;

import org.semanticweb.elk.owl.comparison.ElkObjectHash;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.reasoner.tracing.Conclusion;
import org.semanticweb.elk.util.hashing.HashGenerator;
import org.semanticweb.elk.util.hashing.Hasher;

public class ConclusionMatchHash
		implements ConclusionMatch.Visitor<Integer>, Hasher<ConclusionMatch> {

	private static final ConclusionMatchHash INSTANCE_ = new ConclusionMatchHash();

	// forbid construction; only static methods should be used
	private ConclusionMatchHash() {

	}

	public static ConclusionMatch.Visitor<Integer> getHashVisitor() {
		return INSTANCE_;
	}

	@Override
	public int hash(ConclusionMatch match) {
		return hashCode(match);
	}

	private static int combinedHashCode(int... hashes) {
		return HashGenerator.combineListHash(hashes);
	}

	private static int hashCode(Class<?> c) {
		return c.hashCode();
	}

	public static int hashCode(ConclusionMatch conclusionMatch) {
		return conclusionMatch == null ? 0 : conclusionMatch.accept(INSTANCE_);
	}

	private static int hashCode(int i) {
		return i;
	}

	private static int hashCode(Conclusion conclusion) {
		return conclusion.hashCode();
	}

	private static int hashCode(ElkObject elkObject) {
		return elkObject.hashCode();
	}

	private static int hashCode(List<? extends ElkObject> list) {
		return ElkObjectHash.hashCode(list);
	}

	private static int hashCode(IndexedContextRootMatch match) {
		return match.hashCode();
	}

	private static int hashCode(SubsumerMatch match) {
		return match.hashCode();
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
	public Integer visit(IndexedDefinitionAxiomMatch1 conclusionMatch) {
		return combinedHashCode(hashCode(IndexedDefinitionAxiomMatch1.class),
				hashCode(conclusionMatch.getParent()));
	}

	@Override
	public Integer visit(IndexedDefinitionAxiomMatch2 conclusionMatch) {
		return combinedHashCode(hashCode(IndexedDefinitionAxiomMatch2.class),
				hashCode(conclusionMatch.getParent()),
				hashCode(conclusionMatch.getDefinedClassMatch()),
				hashCode(conclusionMatch.getDefinitionMatch()));
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
	public Integer visit(BackwardLinkMatch1 conclusionMatch) {
		return combinedHashCode(hashCode(BackwardLinkMatch1.class),
				hashCode(conclusionMatch.getParent()),
				hashCode(conclusionMatch.getRelationMatch()),
				hashCode(conclusionMatch.getSourceMatch()));
	}

	@Override
	public Integer visit(BackwardLinkMatch2 conclusionMatch) {
		return combinedHashCode(hashCode(BackwardLinkMatch2.class),
				hashCode(conclusionMatch.getParent()),
				hashCode(conclusionMatch.getDestinationMatch()));
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
	public Integer visit(ForwardLinkMatch1 conclusionMatch) {
		return combinedHashCode(hashCode(ForwardLinkMatch1.class),
				hashCode(conclusionMatch.getParent()),
				hashCode(conclusionMatch.getDestinationMatch()),
				hashCode(conclusionMatch.getChainStartPos()),
				hashCode(conclusionMatch.getFullChainMatch()));
	}

	@Override
	public Integer visit(ForwardLinkMatch2 conclusionMatch) {
		return combinedHashCode(hashCode(ForwardLinkMatch2.class),
				hashCode(conclusionMatch.getParent()),
				hashCode(conclusionMatch.getTargetMatch()),
				hashCode(conclusionMatch.getIntermediateRoots()));
	}

	@Override
	public Integer visit(PropagationMatch1 conclusionMatch) {
		return combinedHashCode(hashCode(PropagationMatch1.class),
				hashCode(conclusionMatch.getParent()),
				hashCode(conclusionMatch.getCarryMatch()));
	}

	@Override
	public Integer visit(PropagationMatch2 conclusionMatch) {
		return combinedHashCode(hashCode(PropagationMatch2.class),
				hashCode(conclusionMatch.getParent()),
				hashCode(conclusionMatch.getRelationMatch()));
	}

	@Override
	public Integer visit(PropagationMatch3 conclusionMatch) {
		return combinedHashCode(hashCode(PropagationMatch3.class),
				hashCode(conclusionMatch.getParent()),
				hashCode(conclusionMatch.getDestinationMatch()));
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
