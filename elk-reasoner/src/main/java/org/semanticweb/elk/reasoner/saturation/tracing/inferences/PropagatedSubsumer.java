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
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.BackwardLinkImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ComposedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.PropagationImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Propagation;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ClassInferenceVisitor;

/**
 * Represents an existential composition inference from a {@link BackwardLink}
 * and a {@link Propagation}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class PropagatedSubsumer extends
		ComposedSubsumerImpl<IndexedObjectSomeValuesFrom> implements
		ClassInference {

	private final IndexedObjectProperty linkRelation_;

	private final IndexedContextRoot inferenceRoot_;

	public PropagatedSubsumer(BackwardLink backwardLink,
			IndexedObjectSomeValuesFrom carry) {
		super(backwardLink.getSource(), carry);
		inferenceRoot_ = backwardLink.getRoot();
		linkRelation_ = backwardLink.getRelation();
	}

	public PropagatedSubsumer(IndexedContextRoot conclusionRoot,
			Propagation propagation) {
		super(conclusionRoot, propagation.getCarry());
		inferenceRoot_ = propagation.getRoot();
		linkRelation_ = propagation.getRelation();
	}

	@Override
	public IndexedContextRoot getInferenceContextRoot() {
		return inferenceRoot_;
	}

	public Propagation getPropagation() {
		return new PropagationImpl(getInferenceContextRoot(), linkRelation_,
				getExpression());
	}

	public BackwardLink getBackwardLink() {
		return new BackwardLinkImpl(getInferenceContextRoot(), getRoot(),
				linkRelation_);
	}

	@Override
	public String toString() {
		return super.toString() + " (propagation)";
	}

	/*
	 * public SubObjectProperty getSubPropertyPremise() { return new
	 * SubObjectProperty(linkRelation_, getExpression().getRelation()); }
	 */

	@Override
	public <I, O> O acceptTraced(ClassInferenceVisitor<I, O> visitor,
			I parameter) {
		return visitor.visit(this, parameter);
	}

}
