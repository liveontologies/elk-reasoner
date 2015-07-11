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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedComplexPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.BackwardLinkImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ForwardLinkImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.SubObjectProperty;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.SubPropertyChainImpl;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.ForwardLinkInferenceVisitor;

/**
 * A {@link ForwardLink} obtained by composition of a {@link BackwardLink} with
 * a {@link ForwardLink}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <R>
 *            The type of the forward relation
 */
public class ComposedForwardLink extends
		AbstractForwardLinkInference<IndexedComplexPropertyChain> implements
		ForwardLinkInference {

	private final IndexedObjectProperty backwardRelation_;

	private final IndexedContextRoot inferenceRoot_;

	private final IndexedPropertyChain forwardChain_;

	public ComposedForwardLink(IndexedContextRoot originRoot,
			IndexedObjectProperty backwardRelation,
			IndexedContextRoot inferenceRoot,
			IndexedPropertyChain forwardChain, IndexedContextRoot targetRoot,
			IndexedComplexPropertyChain composition) {
		super(originRoot, composition, targetRoot);
		this.backwardRelation_ = backwardRelation;
		this.inferenceRoot_ = inferenceRoot;
		this.forwardChain_ = forwardChain;
	}

	@Override
	public IndexedContextRoot getInferenceRoot() {
		return inferenceRoot_;
	}

	public BackwardLink getBackwardLink() {
		return new BackwardLinkImpl(getInferenceRoot(), backwardRelation_,
				getConclusionRoot());
	}

	public ForwardLink getForwardLink() {
		return new ForwardLinkImpl<IndexedPropertyChain>(getInferenceRoot(),
				forwardChain_, getTarget());
	}

	private IndexedComplexPropertyChain getComposition() {
		return getForwardChain();
	}

	public SubObjectProperty getLeftSubObjectProperty() {
		return new SubObjectProperty(backwardRelation_, getComposition()
				.getFirstProperty());
	}

	public SubPropertyChainImpl<?, ?> getRightSubObjectPropertyChain() {
		return new SubPropertyChainImpl<IndexedPropertyChain, IndexedPropertyChain>(
				forwardChain_, getComposition().getSuffixChain());
	}

	@Override
	public String toString() {
		return super.toString() + " (composition)";
	}

	@Override
	public <I, O> O accept(ForwardLinkInferenceVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

}
