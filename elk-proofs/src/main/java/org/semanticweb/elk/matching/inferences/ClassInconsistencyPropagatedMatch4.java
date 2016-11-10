package org.semanticweb.elk.matching.inferences;

import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch4;

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

import org.semanticweb.elk.matching.conclusions.ClassInconsistencyMatch2;
import org.semanticweb.elk.matching.conclusions.ConclusionMatchExpressionFactory;
import org.semanticweb.elk.matching.root.IndexedContextRootMatch;

public class ClassInconsistencyPropagatedMatch4
		extends AbstractInferenceMatch<ClassInconsistencyPropagatedMatch3> {

	private final IndexedContextRootMatch extendedDestinationMatch_;

	ClassInconsistencyPropagatedMatch4(
			ClassInconsistencyPropagatedMatch3 parent,
			BackwardLinkMatch4 firstPremiseMatch) {
		super(parent);
		this.extendedDestinationMatch_ = firstPremiseMatch
				.getExtendedSourceMatch();
		checkEquals(firstPremiseMatch, getFirstPremiseMatch(DEBUG_FACTORY));
	}

	public IndexedContextRootMatch getExtendedDestinationMatch() {
		return extendedDestinationMatch_;
	}

	BackwardLinkMatch4 getFirstPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getBackwardLinkMatch4(
				getParent().getFirstPremiseMatch(factory),
				getExtendedDestinationMatch());
	}

	public ClassInconsistencyMatch2 getConclusionMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getClassInconsistencyMatch2(
				getParent().getParent().getParent().getConclusionMatch(factory),
				getExtendedDestinationMatch());
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

		O visit(ClassInconsistencyPropagatedMatch4 inferenceMatch4);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		ClassInconsistencyPropagatedMatch4 getClassInconsistencyPropagatedMatch4(
				ClassInconsistencyPropagatedMatch3 parent,
				BackwardLinkMatch4 firstPremiseMatch);

	}

}
