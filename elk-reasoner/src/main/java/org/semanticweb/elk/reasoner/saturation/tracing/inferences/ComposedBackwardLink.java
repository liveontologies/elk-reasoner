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
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;

/**
 * Represents a role composition inference. The premises are a backward link and
 * a forward link.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ComposedBackwardLink extends BackwardLink implements Inference {

	private final IndexedClassExpression inferenceContext_;
	
	private final IndexedPropertyChain backwardLinkRelation_;
	
	private final IndexedPropertyChain forwardLinkRelation_;
	
	private final IndexedClassExpression forwardLinkTarget_;
	
	public ComposedBackwardLink(IndexedPropertyChain chain,
			IndexedClassExpression inferenceContext, ForwardLink forwardLink,
			IndexedPropertyChain backwardLinkChain,
			IndexedClassExpression linkSource) {
		super(linkSource, chain);
		backwardLinkRelation_ = backwardLinkChain;
		forwardLinkRelation_ = forwardLink.getRelation();
		forwardLinkTarget_ = forwardLink.getTarget();
		inferenceContext_ = inferenceContext;
	}

	public ComposedBackwardLink(IndexedPropertyChain chain,
			IndexedClassExpression inferenceContext, BackwardLink backwardLink,
			IndexedPropertyChain forwardLinkChain,
			IndexedClassExpression forwardLinkTarget) {
		super(backwardLink.getSource(), chain);
		backwardLinkRelation_ = backwardLink.getRelation();
		forwardLinkRelation_ = forwardLinkChain;
		forwardLinkTarget_ = forwardLinkTarget;
		inferenceContext_ = inferenceContext;
	}

	@Override
	public <R, C> R acceptTraced(InferenceVisitor<R, C> visitor, C parameter) {
		return visitor.visit(this, parameter);
	}

	public BackwardLink getBackwardLink() {
		return new BackwardLink(getSource(), backwardLinkRelation_);
	}
	
	public ForwardLink getForwardLink() {
		return new ForwardLink(forwardLinkRelation_, forwardLinkTarget_);
	}
	
	@Override
	public IndexedClassExpression getInferenceContextRoot(IndexedClassExpression rootWhereStored) {
		return inferenceContext_;
	}

	@Override
	public String toString() {
		return super.toString() + " (composition)";
	}

	
}
