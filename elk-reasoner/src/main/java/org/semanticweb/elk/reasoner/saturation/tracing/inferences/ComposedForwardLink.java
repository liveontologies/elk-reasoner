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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.BackwardLinkImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ForwardLinkImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.InferenceVisitor;

/**
 * A {@link ForwardLink} obtained by composition of a {@link BackwardLink} with
 * a {@link ForwardLink}
 * 
 * @author "Yevgeny Kazakov"
 */
public class ComposedForwardLink extends ForwardLinkImpl implements Inference {

	private final IndexedClassExpression backwardLinkSource_;

	private final IndexedObjectProperty backwardLinkRelation_;

	private final IndexedClassExpression inferenceContext_;

	private final IndexedPropertyChain forwardLinkChain_;

	public ComposedForwardLink(IndexedClassExpression linkSource,
			IndexedObjectProperty backwardLinkRelation,
			IndexedClassExpression inferenceContext,
			IndexedPropertyChain forwardLinkChain,
			IndexedClassExpression linkTarget,
			IndexedBinaryPropertyChain composition) {
		super(composition, linkTarget);
		this.backwardLinkSource_ = linkSource;
		this.backwardLinkRelation_ = backwardLinkRelation;
		this.inferenceContext_ = inferenceContext;
		this.forwardLinkChain_ = forwardLinkChain;
	}

	@Override
	public <I, O> O acceptTraced(InferenceVisitor<I, O> visitor, I parameter) {
		return visitor.visit(this, parameter);
	}

	public BackwardLink getBackwardLink() {
		return new BackwardLinkImpl(backwardLinkSource_, backwardLinkRelation_);
	}

	public ForwardLink getForwardLink() {
		return new ForwardLinkImpl(forwardLinkChain_, getTarget());
	}

	@Override
	public IndexedClassExpression getInferenceContextRoot(
			IndexedClassExpression rootWhereStored) {
		return inferenceContext_;
	}

	@Override
	public String toString() {
		return super.toString() + " (composition)";
	}

}
