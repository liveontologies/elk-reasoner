package org.semanticweb.elk.matching.inferences;

/*-
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

import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch4;
import org.semanticweb.elk.matching.conclusions.ConclusionMatchExpressionFactory;
import org.semanticweb.elk.matching.root.IndexedContextRootMatch;

public class BackwardLinkCompositionMatch8
		extends AbstractInferenceMatch<BackwardLinkCompositionMatch7> {

	private final IndexedContextRootMatch extendedConclusionSourceMatch_;

	BackwardLinkCompositionMatch8(BackwardLinkCompositionMatch7 parent,
			BackwardLinkMatch4 firstPremiseMatch) {
		super(parent);
		this.extendedConclusionSourceMatch_ = firstPremiseMatch
				.getExtendedSourceMatch();
		checkEquals(firstPremiseMatch, getFirstPremiseMatch(DEBUG_FACTORY));
	}

	public IndexedContextRootMatch getExtendedConclusionSourceMatch() {
		return extendedConclusionSourceMatch_;
	}

	BackwardLinkMatch4 getFirstPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getBackwardLinkMatch4(
				getParent().getFirstPremiseMatch(factory),
				getExtendedConclusionSourceMatch());
	}

	public BackwardLinkMatch4 getConclusionMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getBackwardLinkMatch4(
				getParent().getParent().getConclusionMatch(factory),
				getExtendedConclusionSourceMatch());
	}

	@Override
	public <O> O accept(InferenceMatch.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	public interface Visitor<O> {

		O visit(BackwardLinkCompositionMatch8 inferenceMatch8);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		BackwardLinkCompositionMatch8 getBackwardLinkCompositionMatch8(
				BackwardLinkCompositionMatch7 parent,
				BackwardLinkMatch4 firstPremiseMatch);

	}

}
