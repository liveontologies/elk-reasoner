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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;

/**
 * Represents an existential composition inference. 
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class PropagatedSubsumer extends ComposedSubsumer implements Inference {

	private final IndexedPropertyChain linkRelation_;
	
	private final IndexedClassExpression linkSourceRoot_;
	
	private final IndexedClassExpression inferenceContextRoot_;
	
	public PropagatedSubsumer(IndexedClassExpression inferenceRoot, BackwardLink backwardLink, IndexedObjectSomeValuesFrom carry) {
		super(carry);
		linkSourceRoot_ = backwardLink.getSource();
		linkRelation_ = backwardLink.getRelation();
		inferenceContextRoot_ = inferenceRoot;
	}
	
	public PropagatedSubsumer(IndexedClassExpression context, Propagation propagation, IndexedPropertyChain linkRelation, IndexedClassExpression linkSource) {
		super(propagation.getCarry());
		linkSourceRoot_ = linkSource;
		linkRelation_ = linkRelation;
		inferenceContextRoot_ = context;
	}
	
	public Propagation getPropagation() {
		return new Propagation(linkRelation_, (IndexedObjectSomeValuesFrom) getExpression());
	}

	public BackwardLink getBackwardLink() {
		return new BackwardLink(linkSourceRoot_, linkRelation_);
	}	
	
	@Override
	public <R, C> R acceptTraced(InferenceVisitor<R, C> visitor, C parameter) {
		return visitor.visit(this, parameter);
	}

	@Override
	public IndexedClassExpression getInferenceContextRoot(IndexedClassExpression rootWhereStored) {
		return inferenceContextRoot_;
	}

	@Override
	public String toString() {
		return super.toString() + " (propagation)";
	}
}
