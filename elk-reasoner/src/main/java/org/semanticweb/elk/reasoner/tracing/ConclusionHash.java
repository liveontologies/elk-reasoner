/*
 * #%L
 * ELK Reasoner
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
package org.semanticweb.elk.reasoner.tracing;

import org.semanticweb.elk.reasoner.indexing.model.IndexedDeclarationAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedEquivalentClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObject;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectPropertyRangeAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubObjectPropertyOfAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassInconsistency;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.PropertyRange;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionDecomposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubPropertyChain;
import org.semanticweb.elk.util.hashing.HashGenerator;
import org.semanticweb.elk.util.hashing.Hasher;

public class ConclusionHash
		implements Conclusion.Visitor<Integer>, Hasher<Conclusion> {

	private static final ConclusionHash INSTANCE_ = new ConclusionHash();

	// forbid construction; only static methods should be used
	private ConclusionHash() {

	}

	public static int hashCode(Conclusion conclusion) {
		return conclusion == null ? 0 : conclusion.accept(INSTANCE_);
	}
	
	public static Conclusion.Visitor<Integer> getHashVisitor() {
		return INSTANCE_;
	}

	private static int combinedHashCode(int... hashes) {
		return HashGenerator.combineListHash(hashes);
	}

	private static int hashCode(Class<?> c) {
		return c.hashCode();
	}
	
	private static int hashCode(IndexedObject o) {
		return o.hashCode();
	}

	private static int hashCode(int n) {
		return n;
	}

	@Override
	public int hash(Conclusion conclusion) {
		return hashCode(conclusion);
	}

	@Override
	public Integer visit(BackwardLink subConclusion) {
		return combinedHashCode(hashCode(BackwardLink.class),
				hashCode(subConclusion.getDestination()),
				hashCode(subConclusion.getSubDestination()),
				hashCode(subConclusion.getTraceRoot()));
	}

	@Override
	public Integer visit(ContextInitialization conclusion) {
		return combinedHashCode(hashCode(ContextInitialization.class),
				hashCode(conclusion.getDestination()));
	}

	@Override
	public Integer visit(ClassInconsistency conclusion) {
		return combinedHashCode(hashCode(ClassInconsistency.class),
				hashCode(conclusion.getDestination()));
	}

	@Override
	public Integer visit(DisjointSubsumer conclusion) {
		return combinedHashCode(hashCode(DisjointSubsumer.class),
				hashCode(conclusion.getDestination()),
				hashCode(conclusion.getDisjointExpressions()),
				hashCode(conclusion.getPosition()));
	}

	@Override
	public Integer visit(ForwardLink conclusion) {
		return combinedHashCode(hashCode(ForwardLink.class),
				hashCode(conclusion.getDestination()),
				hashCode(conclusion.getChain()),
				hashCode(conclusion.getTarget()));
	}

	@Override
	public Integer visit(IndexedDeclarationAxiom conclusion) {
		return combinedHashCode(hashCode(IndexedDeclarationAxiom.class),
				hashCode(conclusion.getEntity()));
	}

	@Override
	public Integer visit(IndexedEquivalentClassesAxiom conclusion) {
		return combinedHashCode(hashCode(IndexedEquivalentClassesAxiom.class),
				hashCode(conclusion.getFirstMember()),
				hashCode(conclusion.getSecondMember()));
	}

	@Override
	public Integer visit(IndexedDisjointClassesAxiom conclusion) {
		return combinedHashCode(hashCode(IndexedDisjointClassesAxiom.class),
				hashCode(conclusion.getMembers()));
	}

	@Override
	public Integer visit(IndexedObjectPropertyRangeAxiom conclusion) {
		return combinedHashCode(hashCode(IndexedObjectPropertyRangeAxiom.class),
				hashCode(conclusion.getProperty()),
				hashCode(conclusion.getRange()));
	}

	@Override
	public Integer visit(IndexedSubClassOfAxiom conclusion) {
		return combinedHashCode(hashCode(IndexedSubClassOfAxiom.class),
				hashCode(conclusion.getSubClass()),
				hashCode(conclusion.getSuperClass()));
	}

	@Override
	public Integer visit(IndexedSubObjectPropertyOfAxiom conclusion) {
		return combinedHashCode(hashCode(IndexedSubObjectPropertyOfAxiom.class),
				hashCode(conclusion.getSubPropertyChain()),
				hashCode(conclusion.getSuperProperty()));
	}

	@Override
	public Integer visit(Propagation subConclusion) {
		return combinedHashCode(hashCode(Propagation.class),
				hashCode(subConclusion.getDestination()),
				hashCode(subConclusion.getSubDestination()),
				hashCode(subConclusion.getCarry()));
	}

	@Override
	public Integer visit(PropertyRange conclusion) {
		return combinedHashCode(hashCode(PropertyRange.class),
				hashCode(conclusion.getProperty()),
				hashCode(conclusion.getRange()));
	}

	@Override
	public Integer visit(SubClassInclusionComposed conclusion) {
		return combinedHashCode(hashCode(SubClassInclusionComposed.class),
				hashCode(conclusion.getDestination()),
				hashCode(conclusion.getSubsumer()));
	}

	@Override
	public Integer visit(SubClassInclusionDecomposed conclusion) {
		return combinedHashCode(hashCode(SubClassInclusionDecomposed.class),
				hashCode(conclusion.getDestination()),
				hashCode(conclusion.getSubsumer()));
	}

	@Override
	public Integer visit(SubContextInitialization subConclusion) {
		return combinedHashCode(hashCode(SubContextInitialization.class),
				hashCode(subConclusion.getDestination()),
				hashCode(subConclusion.getSubDestination()));
	}

	@Override
	public Integer visit(SubPropertyChain conclusion) {
		return combinedHashCode(hashCode(SubPropertyChain.class),
				hashCode(conclusion.getSubChain()),
				hashCode(conclusion.getSuperChain()));
	}

}
