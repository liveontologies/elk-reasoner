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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionDecomposed;

/**
 * A {@link Contradiction} obtained from two {@link SubClassInclusionComposed} premises
 * having {@link IndexedObjectComplementOf} super-class and its negation respectively.
 * 
 * @see IndexedObjectComplementOf#getNegated()
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 *         
 * @author Yevgeny Kazakov        
 */
public class ContradictionOfObjectComplementOf extends AbstractContradictionInference {

	private final IndexedObjectComplementOf negation_;

	public ContradictionOfObjectComplementOf(IndexedContextRoot root,
			IndexedObjectComplementOf negatedSubsumer) {
		super(root);
		this.negation_ = negatedSubsumer;
	}

	@Override
	public <O> O accept(ClassConclusion.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	public SubClassInclusionComposed getFirstPremise(SubClassInclusionComposed.Factory factory) {
		return factory.getComposedSubClassInclusion(getInferenceRoot(),
				negation_.getNegated());
	}

	public SubClassInclusionDecomposed getSecondPremise(SubClassInclusionDecomposed.Factory factory) {
		return factory.getDecomposedSubClassInclusion(getInferenceRoot(), negation_);
	}

	@Override
	public String toString() {
		return "Contradiction from " + negation_.getNegated() + " and "
				+ negation_;
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
		
		public O visit(ContradictionOfObjectComplementOf inference, I input);
		
	}

}
