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
import org.semanticweb.elk.reasoner.indexing.factories.IndexedDisjointClassesAxiomFactory;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpressionList;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.DisjointSubsumer;

/**
 * A {@link Contradiction} obtained from two {@link DisjointSubsumer} premises
 * with different disjoint expressions of the same disjoint expression list 
 * 
 * @see DisjointSubsumer#getDisjointExpressions()
 *  
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 *    
 * @author Yevgeny Kazakov        
 */
public class ContradictionOfDisjointSubsumers extends
		AbstractContradictionInference {

	/**
	 * The disjoint {@link IndexedClassExpression}s that cause the contradiction
	 */
	private final IndexedClassExpressionList disjointExpressions_;

	/**
	 * The positions of subsumers that violate the disjointness axiom;
	 * these positions must be different
	 */
	private final Integer firstPosition_, secondPosition_;
	
	/**
	 * The original {@link ElkAxiom} due to which this axiom was indexed
	 */
	private final ElkAxiom reason_;	

	public ContradictionOfDisjointSubsumers(DisjointSubsumer premise,
			Integer otherPos) {
		super(premise.getConclusionRoot());
		this.disjointExpressions_ = premise.getDisjointExpressions();
		this.firstPosition_ = premise.getPosition();
		this.secondPosition_ = otherPos;
		this.reason_ = premise.getReason();
	}

	public ElkAxiom getReason() {
		return reason_;
	}
	
	@Override
	public IndexedContextRoot getInferenceRoot() {
		return getConclusionRoot();
	}
	
	public DisjointSubsumer getFirstPremise(DisjointSubsumer.Factory factory) {
		return factory.getDisjointSubsumer(getInferenceRoot(),
				disjointExpressions_, firstPosition_, reason_);
	}
	
	public DisjointSubsumer getSecondPremise(DisjointSubsumer.Factory factory) {
		return factory.getDisjointSubsumer(getInferenceRoot(),
				disjointExpressions_, secondPosition_, reason_);
	}
	
	public IndexedDisjointClassesAxiom getSideCondition(
			IndexedDisjointClassesAxiomFactory factory) {
		return factory.getIndexedDisjointClassesAxiom(reason_,
				disjointExpressions_);
	}
	
	@Override
	public String toString() {
		return "Contradiction from disjoint subsumer " + disjointExpressions_.getElements().get(firstPosition_)
				+ " using " + disjointExpressions_.getElements().get(secondPosition_) + " due to " + reason_;
	}

	@Override
	public <I, O> O accept(ContradictionInference.Visitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}
	
	/**
	 * Visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public static interface Visitor<I, O> {
		
		public O visit(ContradictionOfDisjointSubsumers inference, I input);
		
	}

}
