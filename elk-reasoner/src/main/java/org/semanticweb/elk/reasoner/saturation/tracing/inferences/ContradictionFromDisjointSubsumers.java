/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointnessAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.AbstractConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.DisjointSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Subsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.InferenceVisitor;

/**
 * Represents a {@link Contradiction} as the result of processing a
 * {@link Subsumer} which occurs in the same {@link IndexedDisjointnessAxiom} as
 * some previously derived subsumer.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ContradictionFromDisjointSubsumers extends AbstractConclusion implements Contradiction, Inference {

	/**
	 * Subsumer for which the contradiction rule was applied
	 */
	private final IndexedClassExpression premise_;
	/**
	 * Previously derived disjoint subsumers occurring the same disjointness axiom.
	 */
	private final IndexedClassExpression[] disjointSubsumers_;
	
	private final IndexedDisjointnessAxiom axiom_;
	
	public ContradictionFromDisjointSubsumers(DisjointSubsumer ds, IndexedClassExpression[] disjointSubsumers) {
		premise_ = ds.getMember();
		axiom_ = ds.getAxiom();
		disjointSubsumers_ = disjointSubsumers;
	}
	
	@Override
	public <I, O> O accept(ConclusionVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

	@Override
	public String toString() {
		return "Contradiction from disjoint subsumer " + premise_;
	}

	@Override
	public <I, O> O acceptTraced(InferenceVisitor<I, O> visitor, I parameter) {
		return visitor.visit(this, parameter);
	}

	public DisjointSubsumer[] getPremises() {
		return new DisjointSubsumer[] {
				new DisjointSubsumerImpl(axiom_, premise_),
				new DisjointSubsumerImpl(axiom_, disjointSubsumers_[0]),
				new DisjointSubsumerImpl(axiom_, disjointSubsumers_[1]) };
		//return new DisjointSubsumerImpl(axiom_, premise_);
	}
	
	public IndexedDisjointnessAxiom getAxiom() {
		return axiom_;
	}
	
	@Override
	public IndexedClassExpression getInferenceContextRoot(
			IndexedClassExpression rootWhereStored) {
		return rootWhereStored;
	}

}
