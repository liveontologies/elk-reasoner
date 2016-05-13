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

import org.semanticweb.elk.matching.conclusions.ConclusionMatchExpressionFactory;
import org.semanticweb.elk.matching.conclusions.DisjointSubsumerMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedDisjointClassesAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedDisjointClassesAxiomMatch1Watch;
import org.semanticweb.elk.matching.root.IndexedContextRootMatch;
import org.semanticweb.elk.reasoner.saturation.inferences.DisjointSubsumerFromSubsumer;

public class DisjointSubsumerFromSubsumerMatch1
		extends AbstractInferenceMatch<DisjointSubsumerFromSubsumer>
		implements IndexedDisjointClassesAxiomMatch1Watch {

	private final IndexedContextRootMatch originMatch_;

	private DisjointSubsumerFromSubsumerMatch1(
			DisjointSubsumerFromSubsumer parent,
			IndexedContextRootMatch originMatch) {
		super(parent);
		this.originMatch_ = originMatch;
	}

	DisjointSubsumerFromSubsumerMatch1(DisjointSubsumerFromSubsumer parent,
			DisjointSubsumerMatch1 conclusionMatch) {
		this(parent, conclusionMatch.getDestinationMatch());
	}

	public IndexedContextRootMatch getOriginMatch() {
		return originMatch_;
	}
	
	public DisjointSubsumerMatch1 getConclusionMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getDisjointSubsumerMatch1(
				getParent().getConclusion(factory), getOriginMatch());
	}

	public IndexedDisjointClassesAxiomMatch1 getSecondPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getIndexedDisjointClassesAxiomMatch1(
				getParent().getSecondPremise(factory));
	}

	@Override
	public <O> O accept(InferenceMatch.Visitor<O> visitor) {
		return visitor.visit(this);
	}
	
	@Override
	public <O> O accept(
			IndexedDisjointClassesAxiomMatch1Watch.Visitor<O> visitor) {
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

		O visit(DisjointSubsumerFromSubsumerMatch1 inferenceMatch1);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		DisjointSubsumerFromSubsumerMatch1 getDisjointSubsumerFromSubsumerMatch1(
				DisjointSubsumerFromSubsumer parent,
				DisjointSubsumerMatch1 conclusionMatch);

	}	

}
