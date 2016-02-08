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

import org.semanticweb.elk.reasoner.indexing.model.IndexedComplexPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubPropertyChain;

/**
 * A {@link ForwardLink} obtained by composing a {@link BackwardLink} and
 * {@link ForwardLink} using {@link SubPropertyChain} premises.
 * 
 * @author "Yevgeny Kazakov"
 */
public class ForwardLinkComposition
		extends
			AbstractForwardLinkInference<IndexedComplexPropertyChain>
		implements
			LinkComposition {

	private final IndexedObjectProperty backwardRelation_;

	private final IndexedContextRoot inferenceRoot_;

	private final IndexedPropertyChain forwardChain_;

	public ForwardLinkComposition(IndexedContextRoot originRoot,
			IndexedObjectProperty backwardRelation,
			IndexedContextRoot inferenceRoot, IndexedPropertyChain forwardChain,
			IndexedContextRoot targetRoot,
			IndexedComplexPropertyChain composition) {
		super(originRoot, composition, targetRoot);
		this.backwardRelation_ = backwardRelation;
		this.inferenceRoot_ = inferenceRoot;
		this.forwardChain_ = forwardChain;
	}

	@Override
	public IndexedObjectProperty getPremiseBackwardRelation() {
		return backwardRelation_;
	}

	@Override
	public IndexedPropertyChain getPremiseForwardChain() {
		return forwardChain_;
	}

	@Override
	public IndexedComplexPropertyChain getComposition() {
		return getRelation();
	}

	@Override
	public IndexedContextRoot getOrigin() {
		return inferenceRoot_;
	}

	@Override
	public BackwardLink getFirstPremise() {
		return FACTORY.getBackwardLink(getOrigin(), backwardRelation_,
				getDestination());
	}

	@Override
	public SubPropertyChain getSecondPremise() {
		return FACTORY.getSubPropertyChain(backwardRelation_,
				getComposition().getFirstProperty());
	}

	@Override
	public ForwardLink getThirdPremise() {
		return FACTORY.getForwardLink(getOrigin(), forwardChain_, getTarget());
	}

	@Override
	public SubPropertyChain getFourthPremise() {
		return FACTORY.getSubPropertyChain(forwardChain_,
				getComposition().getSuffixChain());
	}

	@Override
	public String toString() {
		return super.toString() + " (composition)";
	}

	@Override
	public final <O> O accept(ForwardLinkInference.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public final <O> O accept(LinkComposition.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	/**
	 * Visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public static interface Visitor<O> {

		public O visit(ForwardLinkComposition inference);

	}

}
