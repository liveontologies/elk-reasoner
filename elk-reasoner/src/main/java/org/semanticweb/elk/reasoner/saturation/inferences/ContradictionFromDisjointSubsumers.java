/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.inferences;

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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.DisjointSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Subsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.ContradictionInferenceVisitor;

/**
 * Represents a {@link Contradiction} as the result of processing a
 * {@link Subsumer} which occurs in the same {@link IndexedDisjointClassesAxiom}
 * as some previously derived subsumer.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ContradictionFromDisjointSubsumers extends
		AbstractContradictionInference {

	/**
	 * The axiom which causes the contradiction
	 */
	private final IndexedDisjointClassesAxiom axiom_;

	/**
	 * The positions of subsumers that violate the disjointness axiom;
	 * these positions must be different
	 */
	private final Integer firstPosition_, secondPosition_;
	
	/**
	 * The original {@link ElkAxiom} due to which this axiom was indexed
	 */
	private final ElkAxiom reason_;	

	public ContradictionFromDisjointSubsumers(DisjointSubsumer premise,
			Integer otherPos) {
		super(premise.getConclusionRoot());
		this.axiom_ = premise.getAxiom();
		this.firstPosition_ = premise.getPosition();
		this.secondPosition_ = otherPos;
		this.reason_ = premise.getReason();
	}

	@Override
	public IndexedContextRoot getInferenceRoot() {
		return getConclusionRoot();
	}

	public DisjointSubsumer[] getPremises() {
		return new DisjointSubsumer[]{
				new DisjointSubsumerImpl(getInferenceRoot(), axiom_,
						firstPosition_, reason_),
				new DisjointSubsumerImpl(getInferenceRoot(), axiom_,
						secondPosition_, reason_)};
	}

	public ElkAxiom getReason() {
		return reason_;
	}

	@Override
	public String toString() {
		return "Contradiction from disjoint subsumer " + axiom_.getMembers().get(firstPosition_)
				+ " using " + axiom_.getMembers().get(secondPosition_) + " due to " + reason_;
	}

	@Override
	public <I, O> O accept(ContradictionInferenceVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

}
