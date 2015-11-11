package org.semanticweb.elk.reasoner.saturation.conclusions.model;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.comparison.ElkObjectHash;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObject;
import org.semanticweb.elk.util.hashing.HashGenerator;
import org.semanticweb.elk.util.hashing.Hasher;

public class ClassConclusionHash implements ClassConclusion.Visitor<Void, Integer>,
		Hasher<ClassConclusion> {

	private static final ClassConclusionHash INSTANCE_ = new ClassConclusionHash();

	// forbid construction; only static methods should be used
	private ClassConclusionHash() {

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

	private static int hashCode(ElkObject elkObject) {
		return ElkObjectHash.hashCode(elkObject);
	}
	
	private static int hashCode(int n) {
		return n;
	}

	public static int hashCode(ClassConclusion conclusion) {
		return conclusion == null ? 0 : conclusion.accept(INSTANCE_, null);
	}

	@Override
	public int hash(ClassConclusion conclusion) {
		return hashCode(conclusion);
	}

	@Override
	public Integer visit(BackwardLink subConclusion, Void input) {
		return combinedHashCode(hashCode(BackwardLink.class),
				hashCode(subConclusion.getConclusionRoot()),
				hashCode(subConclusion.getConclusionSubRoot()),
				hashCode(subConclusion.getOriginRoot()));
	}

	@Override
	public Integer visit(Propagation subConclusion, Void input) {
		return combinedHashCode(hashCode(Propagation.class),
				hashCode(subConclusion.getConclusionRoot()),
				hashCode(subConclusion.getConclusionSubRoot()),
				hashCode(subConclusion.getCarry()));
	}

	@Override
	public Integer visit(SubContextInitialization subConclusion, Void input) {
		return combinedHashCode(hashCode(SubContextInitialization.class),
				hashCode(subConclusion.getConclusionRoot()),
				hashCode(subConclusion.getConclusionSubRoot()));
	}

	@Override
	public Integer visit(ComposedSubsumer conclusion, Void input) {
		return combinedHashCode(hashCode(ComposedSubsumer.class),
				hashCode(conclusion.getConclusionRoot()),
				hashCode(conclusion.getExpression()));
	}

	@Override
	public Integer visit(ContextInitialization conclusion, Void input) {
		return combinedHashCode(hashCode(ContextInitialization.class),
				hashCode(conclusion.getConclusionRoot()));
	}

	@Override
	public Integer visit(Contradiction conclusion, Void input) {
		return combinedHashCode(hashCode(Contradiction.class),
				hashCode(conclusion.getConclusionRoot()));
	}

	@Override
	public Integer visit(DecomposedSubsumer conclusion, Void input) {
		return combinedHashCode(hashCode(DecomposedSubsumer.class),
				hashCode(conclusion.getConclusionRoot()),
				hashCode(conclusion.getExpression()));
	}

	@Override
	public Integer visit(DisjointSubsumer conclusion, Void input) {
		return combinedHashCode(hashCode(DisjointSubsumer.class),
				hashCode(conclusion.getConclusionRoot()),
				hashCode(conclusion.getDisjointExpressions()),
				hashCode(conclusion.getPosition()),
				hashCode(conclusion.getReason()));
	}

	@Override
	public Integer visit(ForwardLink conclusion, Void input) {
		return combinedHashCode(hashCode(ForwardLink.class),
				hashCode(conclusion.getConclusionRoot()),
				hashCode(conclusion.getForwardChain()),
				hashCode(conclusion.getTarget()));
	}

}
