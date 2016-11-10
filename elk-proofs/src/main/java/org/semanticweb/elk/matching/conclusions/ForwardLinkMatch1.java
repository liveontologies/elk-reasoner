package org.semanticweb.elk.matching.conclusions;

import org.semanticweb.elk.matching.root.IndexedContextRootMatch;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;

/*
 * #%L
 * ELK Proofs Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;

public class ForwardLinkMatch1
		extends AbstractClassConclusionMatch<ForwardLink> {

	private final IndexedContextRootMatch destinationMatch_;

	private final ElkSubObjectPropertyExpression fullForwardChainMatch_;

	private final int forwardChainStartPos_;

	ForwardLinkMatch1(ForwardLink parent,
			IndexedContextRootMatch destinationMatch,
			ElkSubObjectPropertyExpression fullForwardChainMatch,
			int forwardChainStartPos) {
		super(parent);
		checkChainMatch(fullForwardChainMatch, forwardChainStartPos);
		this.destinationMatch_ = destinationMatch;
		this.fullForwardChainMatch_ = fullForwardChainMatch;
		this.forwardChainStartPos_ = forwardChainStartPos;
	}

	public IndexedContextRootMatch getDestinationMatch() {
		return destinationMatch_;
	}

	public ElkSubObjectPropertyExpression getFullChainMatch() {
		return fullForwardChainMatch_;
	}

	public int getChainStartPos() {
		return forwardChainStartPos_;
	}

	@Override
	public <O> O accept(ClassConclusionMatch.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		ForwardLinkMatch1 getForwardLinkMatch1(ForwardLink parent,
				IndexedContextRootMatch destinationMatch,
				ElkSubObjectPropertyExpression fullForwardChainMatch,
				int forwardChainStartPos);

	}

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> {

		O visit(ForwardLinkMatch1 conclusionMatch);

	}

}
