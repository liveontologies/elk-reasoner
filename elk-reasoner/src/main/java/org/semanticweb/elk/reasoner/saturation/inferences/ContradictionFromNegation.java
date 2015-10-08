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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ComposedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.DecomposedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.ContradictionInferenceVisitor;

/**
 * Represents a {@link Contradiction} produced when processing either a subsumer
 * or its negation.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ContradictionFromNegation extends AbstractContradictionInference {

	private final IndexedObjectComplementOf negation_;

	public ContradictionFromNegation(IndexedContextRoot root,
			IndexedObjectComplementOf negatedSubsumer) {
		super(root);
		this.negation_ = negatedSubsumer;
	}

	@Override
	public <I, O> O accept(ConclusionVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

	public ComposedSubsumer getPremise() {
		return new ComposedSubsumerImpl<IndexedClassExpression>(
				getInferenceRoot(), negation_.getNegated());
	}

	public DecomposedSubsumer getNegatedPremise() {
		return new DecomposedSubsumerImpl(getInferenceRoot(), negation_);
	}

	@Override
	public String toString() {
		return "Contradiction from " + negation_.getNegated() + " and "
				+ negation_;
	}

	@Override
	public <I, O> O accept(ContradictionInferenceVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

}
