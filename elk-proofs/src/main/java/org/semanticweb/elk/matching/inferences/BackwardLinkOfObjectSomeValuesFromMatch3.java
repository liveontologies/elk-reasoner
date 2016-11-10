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

import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch3;
import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch4;
import org.semanticweb.elk.matching.conclusions.ConclusionMatchExpressionFactory;
import org.semanticweb.elk.matching.root.IndexedContextRootMatch;

public class BackwardLinkOfObjectSomeValuesFromMatch3
		extends AbstractInferenceMatch<BackwardLinkOfObjectSomeValuesFromMatch2>
		implements InferenceMatch {

	private final IndexedContextRootMatch extendedDestinationMatch_;

	BackwardLinkOfObjectSomeValuesFromMatch3(
			BackwardLinkOfObjectSomeValuesFromMatch2 parent,
			BackwardLinkMatch3 conclusionMatch) {
		super(parent);
		this.extendedDestinationMatch_ = conclusionMatch
				.getExtendedDestinationMatch();
		checkEquals(conclusionMatch, getParentConclusionMatch(DEBUG_FACTORY));
	}

	public IndexedContextRootMatch getExtendedDestinationMatch() {
		return extendedDestinationMatch_;
	}

	BackwardLinkMatch3 getParentConclusionMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getBackwardLinkMatch3(
				getParent().getConclusionMatch(factory),
				getExtendedDestinationMatch());
	}

	public BackwardLinkMatch4 getConclusionMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getBackwardLinkMatch4(getParentConclusionMatch(factory),
				getParent().getExtendedOriginMatch());
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

		O visit(BackwardLinkOfObjectSomeValuesFromMatch3 inferenceMatch3);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		BackwardLinkOfObjectSomeValuesFromMatch3 getBackwardLinkOfObjectSomeValuesFromMatch3(
				BackwardLinkOfObjectSomeValuesFromMatch2 parent,
				BackwardLinkMatch3 conclusionMatch);

	}

}
